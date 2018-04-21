package com.nstl.securitysdkcore.webview;


import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;

/**
 * Created by Lin on 2017/12/14.
 */

public class SafeWebView extends BridgeWebView implements BridgeHandler {

    private IMethodInvokeInterface invokeInterface = null;          //业务方根据JS传递进来的参数，进行实际处理的类
    private WebSettings settings = null;
    private WebViewClient client = null;

    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SafeWebView(Context context) {
        super(context);
    }

    /**
     * 客户端调用SafeWebView后，传递相关参数进行初始化
     * 根据系统的版本关闭具有漏洞的接口
     *
     * @param
     * @return void
     */
    public void init(Context context, IMethodInvokeInterface miInterface) {
        settings = this.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        this.invokeInterface = miInterface;
        client = new BridgeWebViewClient(this) {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                //停止对错误的https证书的继续加载
                handler.cancel();
            }
        };

        if (Build.VERSION.SDK_INT <= 16) {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        }
        this.setWebViewClient(client);
    }

    /**
     * 重写BridgeWebView 的 java向js注册对象的方法，里面主要是进行过滤
     * @param handlerName
     */
    public void registerHandler(String handlerName) {
        super.registerHandler(handlerName, this);
    }

    /**
     * 重写loadUrl,目的是为了判断url的准入
     * @param url 加载的url
     */
    @Override
    public void loadUrl(String url) {
        if (verifyUrl(url.trim()) < 0) {
            return;
        }
        super.loadUrl(url);
    }

    /**
     * 判断当前url是否合法，业务方可以根据业务场景，增加url的白名单匹配过程；如禁止webview加载非白名单中的url
     */
    private int verifyUrl(String url) {
        //file协议只允许assert相关目录下的html文件
        if (fileUrlISSafe(url)) {
            enableFileCrossAccess();
        } else {
            disableFileCrossAccess();
        }
        return 1;
    }

    /**
     * 关闭了file协议的跨域访问，防止webview加载外部file文件读取应用内的私有文件，造成信息泄露
     */
    private void disableFileCrossAccess() {
        settings.setAllowFileAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
    }

    /**
     * 开启file域访问能力,确保加载file协议文件，是在assert目录下，或者信任的私有目录下
     */
    private void enableFileCrossAccess() {
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
    }

    /**
     * 返回到js的数据
     * 双方约定一种数据交换格式：json,
     * @param data
     * @param function
     */
    @Override
    public void handler(String data, CallBackFunction function) {
        String resultJson = "error";
        //获得当前加载的url，如果url不是本地目录的文件，或者是百度域下的url，则不进行handle的处理【禁止JS和java本地代码进行交互，从而调用危险接口】
        if (this.getUrl() != null && (urlISSafe(this.getUrl().trim()))) {
            //执行实现的接口，相当于js代码调用了java对象的方法，通过json来控制
            resultJson = invokeInterface.dispatch(data);
            function.onCallBack(resultJson);
        }
        function.onCallBack(resultJson);
    }

    /**
     * 判断url是否安全:http开头的协议，只允许加载百度域下的url，file协议，只允许加载当前应用资源目录下的html文件
     * @param urlStr
     * @return
     */
    private boolean urlISSafe(String urlStr) {
        boolean isSafeFlag = false;
        if (urlStr.startsWith("http") || urlStr.startsWith("https")) {
            //匹配url是否是百度域下或者可信域
            isSafeFlag = urlStr.matches("((http://)|(https://)){0,1}(/w/d)*.baidu.com");
        }
        isSafeFlag = fileUrlISSafe(urlStr);
        return isSafeFlag;
    }

    /**
     * //file协议的url是合法的判断
     *
     * @param fileUrl
     * @return
     */
    private boolean fileUrlISSafe(String fileUrl) {
        boolean isSafeFlag = false;
        if (fileUrl.startsWith("file")) {
            //file路径截取
            String urlPath = fileUrl.substring(fileUrl.indexOf("file:") + 7);
            //可信的file协议的目录文件
            if (!TextUtils.isEmpty(((CharSequence) urlPath)) && !urlPath.contains("..") && !urlPath.contains("\\") && !urlPath.contains("%")) {
                if (urlPath.startsWith("/android_asset") || urlPath.startsWith("/android_res")) {
                    isSafeFlag = true;
                }
            }
        }
        return isSafeFlag;
    }
}
