package com.nstl.securitysdkcore.webview;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.nstl.securitysdkcore.config.ConfigFileToObject;
import com.nstl.securitysdkcore.config.InterceptMethod;
import com.nstl.securitysdkcore.config.WebviewConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by Lin on 2017/12/14.
 */

public class SafeWebView extends BridgeWebView implements BridgeHandler{

    //private Map<String, String> methodMap = null;                   //禁止JS和本地代码进行交互的方法名和相关参数
    private IMethodInvokeInterface invokeInterface = null;          //业务方根据JS传递进来的参数，进行实际处理的类
    private WebviewConfig webViewConfig = null;
    private WebSettings settings = null;
    private WebViewClient client = null;

    /**
     * 继承的构造方法
     * @param context
     */
    public SafeWebView(Context context){
        super(context);
        init(context);
    }

    /**
     *继承的构造方法
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SafeWebView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 继承的构造方法
     * @param context
     * @param attrs
     */
    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 自身的构造方法
     * @param context
     * @param miInterface
     */
    public SafeWebView(Context context, IMethodInvokeInterface miInterface) {
        super(context);
        settings = this.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //this.methodMap = methodMap;
        this.invokeInterface = miInterface;
        client = new BridgeWebViewClient(this){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                //停止对错误的https证书的继续加载
                handler.cancel();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                super.onUnhandledKeyEvent(view, event);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        };
        this.setDefaultHandler(new DefaultHandler());
        init(context);
    }
    /**
     * 客户端调用SafeWebView后，传递相关参数进行初始化
     * 根据系统的版本关闭具有漏洞的接口
     * @param
     * @return void
     */
    public void init(Context context){
        //从配置文件中读取webview的配置策略：url黑、白名单，需要阻止JS执行的方法名和方法参数。
        //反序列化到webviewConfigbean
        this.webViewConfig = ConfigFileToObject.getSecuritySDKConfig(context).getWebviewConfig();

        if(Build.VERSION.SDK_INT >=16){
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowFileAccessFromFileURLs(true);
        }
        if(Build.VERSION.SDK_INT <= 16){
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
        super.registerHandler(handlerName,this);
    }
    /**
     * 重写loadUrl,目的是为了判断url的准入
     * @param url 加载的url
     */
    @Override
    public void loadUrl(String url) {
        if( verifyUrl(url) < 0){
            return;
        }
        super.loadUrl(url);
    }
    /**
     * //判断要加载的Url是否在白名单或黑名单内，只加载可信域的https
     * ? 需要增加正则匹配问题
     * @param url
     * @return
     *      白名单返回1
     *      黑名单返回-1
     *      不在名单内，根据url为http链接返回3
     *                        https链接返回2
     */
    private int verifyUrl(String url){
        List<String> urlWhiteList = this.webViewConfig.getUrlWhiteList();
        List<String> urlBlackList = this.webViewConfig.getUrlBlackList();
        for( String urlWhiteStr : urlWhiteList){
            if( url.endsWith(urlWhiteStr) ){
                return 1;
            }
        }
        for (String urlBlackStr : urlBlackList){
            if( url.endsWith(urlBlackStr)){
                return -1;
            }
        }
        if(url.startsWith("https")){
            return 2;
        }
        return 3;
    }

    /**
     * //校验JS此次调用的方法名和参数，根据WebViewConfigBean相关变量进行判断
     * 从配置文件中读取数据判断是否允许加载
     * @param methodName
     * @param argJson
     * @return
     */
    private boolean verifyMethodNameOrArg(String methodName,Map<Integer,String> argJson){
        List<InterceptMethod> methodList = this.webViewConfig.getMethodList();
        for (InterceptMethod interceptMethod : methodList){
            if( interceptMethod.getMethodNmae().equals(methodName)){
                if( interceptMethod.getType() > 0){
                    return true;
                }
                if( interceptMethod.getType() == 0){
                    return false;
                }
                if( interceptMethod.getType() == -1){
                    //取出参数的位置
                    Map<Integer,String> map = interceptMethod.getMethodArgMap();
                    for( Map.Entry<Integer, String> entry : map.entrySet()){
                        if(argJson.get(entry.getKey()).equals(entry.getValue())){
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * 返回到js的数据
     * @param data
     * @param function
     */
    @Override
    public void handler(String data, CallBackFunction function) {
        String resultJson = "error";
        //对data进行反序列化，格式为method name , map
        MethodAndArgJson methodAndArgJson = JSON.parseObject( data, MethodAndArgJson.class);
        if( methodAndArgJson == null){
            function.onCallBack(resultJson);
        }
        String methodName = methodAndArgJson.getMethodName();
        Map<Integer,String> argJson = methodAndArgJson.getArgMapJson();
        //调用verify方法验证是否允许调用
        if(verifyMethodNameOrArg(methodName,argJson)){
            //执行实现的接口
            resultJson = invokeInterface.dispatch(methodName,argJson);
        }
        function.onCallBack(resultJson);
    }
}
