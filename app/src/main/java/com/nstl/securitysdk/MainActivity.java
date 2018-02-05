package com.nstl.securitysdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.SecuritySDKInit;
import com.nstl.securitysdkcore.config.IntentUriScheme;
import com.nstl.securitysdkcore.config.InterceptMethod;
import com.nstl.securitysdkcore.config.InterceptPluginInvoke;
import com.nstl.securitysdkcore.config.SecuritySDKConfig;
import com.nstl.securitysdkcore.config.WebviewConfig;
import com.nstl.securitysdkcore.reinforce.DetectRootUtil;
import com.nstl.securitysdkcore.reinforce.bean.InstallPackageInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText("hello“");
        /*HelpUtil helpUtil = new HelpUtil();
        List<InstallPackageInfo> installPackageInfoList = helpUtil.getInstallPackageAndSig(this);
        StringBuilder builder = new StringBuilder();
        for(InstallPackageInfo pkg : installPackageInfoList){
            builder.append(pkg.getPkgName());
        }*/
        DetectRootUtil detectRootUtil = DetectRootUtil.getInstance(this);
        tv.setText("设备是否root：" + detectRootUtil.isRoot());
        SecuritySDKConfig sdkConfig = new SecuritySDKConfig();

        //webview的过滤配置规则
        WebviewConfig webviewConfig = new WebviewConfig();
        List<String> urlWhiteList = new LinkedList<String>();
        urlWhiteList.add("baidu.com");
        urlWhiteList.add("google.com");
        List<String> urlBlackList = new LinkedList<String>();
        urlBlackList.add("test.com");
        urlBlackList.add("attack.com");
        List<InterceptMethod> methodList = new LinkedList<InterceptMethod>();

        InterceptMethod method = new InterceptMethod();
        Map<Integer,String> methodArgMap = new HashMap<Integer, String>();
        methodArgMap.put(1, "baidu.com");
        method.setType(1);          //白名单方式过滤openurl函数的第一个参数，只允许host是baidu.com的url
        method.setMethodNmae("openurl");
        method.setMethodArgMap(methodArgMap);
        methodList.add(method);

        webviewConfig.setUrlBlackList(urlWhiteList);
        webviewConfig.setUrlBlackList(urlBlackList);
        webviewConfig.setMethodList(methodList);

        //intent uri调用拦截
        List<IntentUriScheme> uriSchemeList = new LinkedList<IntentUriScheme>();
        IntentUriScheme uriScheme1 = new IntentUriScheme();
        uriScheme1.setType(1);          //此uri是白名单,只允许http开头host是baidu.com下的两个相关action
        List<String> actionList = new LinkedList<String>();
        actionList.add("com.action.view");
        actionList.add("com.action.browser");
        uriScheme1.setActionStringList(actionList);
        List<String> hostList = new LinkedList<String>();
        hostList.add("baidu.com");
        uriScheme1.setUriHostList(hostList);
        List<String> schemeList = new LinkedList<String>();
        schemeList.add("http");
        uriSchemeList.add(uriScheme1);

        //应用和插件更新的url白名单
        List<String> updateAndDownloadUrlWhiteList = new LinkedList<String>();
        updateAndDownloadUrlWhiteList.add("safedownload.com");

        //插件调用配置规则
        List<InterceptPluginInvoke> interceptPluginInvokeList = new LinkedList<InterceptPluginInvoke>();
        InterceptPluginInvoke pluginInvoke = new InterceptPluginInvoke();
        pluginInvoke.setType(0);            //黑名单，拦截查件中的短信发送方法
        pluginInvoke.setPluginName("com.sendsms.plugin");
        InterceptMethod pluginMethod = new InterceptMethod();
        pluginMethod.setType(0);            //黑名单方式拦截函数sendsmsbydefault，不用过滤参数，直接拦截
        pluginMethod.setMethodNmae("sendsmsbydefault");
        pluginInvoke.setMethod(pluginMethod);
        //插件中=====对intent 中uri增加params过滤，比如禁止某个关键字的http和file字符
        Map<String, String> uriParaMaps = new HashMap<String, String>();
        uriParaMaps.put("keywords", "http");
        uriParaMaps.put("keywords", "file");
        uriScheme1.setUriParaMaps(uriParaMaps);
        pluginInvoke.setUriScheme(uriScheme1);
        interceptPluginInvokeList.add(pluginInvoke);

        sdkConfig.setVersion("1.0");
        sdkConfig.setTimeout(100000);
        sdkConfig.setWebviewConfig(webviewConfig);
        sdkConfig.setIntentUriList(uriSchemeList);
        sdkConfig.setUpdateAndDownloadUrlWhiteList(updateAndDownloadUrlWhiteList);
        sdkConfig.setInterceptPluginInvokeList(interceptPluginInvokeList);

        String jsonStr = JSON.toJSONString(sdkConfig);
        Log.i(TAG, jsonStr);
        sdkConfig = JSON.parseObject(jsonStr, SecuritySDKConfig.class);
        tv.setText(sdkConfig.getIntentUriList().get(0).getUriHostList().get(0));

        //配置文件的初始化
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "test");
        params.put("token", "123456");
        SecuritySDKInit.getInstance(this).syncConfig("http://192.168.199.164:8080/mytest/test.txt", params);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
