package com.nstl.securitysdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.NativeCoreUtil;
import com.nstl.securitysdkcore.reinforce.SafeZipFile;
import com.nstl.securitysdkcore.UpgradeTool.ISafeInstall;
import com.nstl.securitysdkcore.UpgradeTool.UpgradeModel;
import com.nstl.securitysdkcore.UpgradeTool.UpgradeTool;
import com.nstl.securitysdkcore.config.IntentUriScheme;
import com.nstl.securitysdkcore.config.InterceptMethod;
import com.nstl.securitysdkcore.config.InterceptPluginInvoke;
import com.nstl.securitysdkcore.config.SecuritySDKConfig;
import com.nstl.securitysdkcore.config.WebviewConfig;
import com.nstl.securitysdkcore.reinforce.DetectRootUtil;
import com.nstl.securitysdkcore.reinforce.IVerifyListener;
import com.nstl.securitysdkcore.webview.IMethodInvokeInterface;
import com.nstl.securitysdkcore.webview.SafeWebView;

import java.io.File;
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
        /*HelpUtil helpUtil = new HelpUtil();
        List<InstallPackageInfo> installPackageInfoList = helpUtil.getInstallPackageAndSig(this);
        StringBuilder builder = new StringBuilder();
        for(InstallPackageInfo pkg : installPackageInfoList){
            builder.append(pkg.getPkgName());
        }*/

        //****************基础功能测试****************
        //查看设备是否root
        DetectRootUtil detectRootUtil = DetectRootUtil.getInstance(this);
        Toast.makeText(this, "设备是否root：" + detectRootUtil.isRoot(), Toast.LENGTH_SHORT).show();

        //查看是否在调试应用
        NativeCoreUtil nativeCoreUtil = new NativeCoreUtil();
        Toast.makeText(this, "设备是否在调试阶段：" + nativeCoreUtil.debugPresent(), Toast.LENGTH_SHORT).show();

        //查看设备是否是模拟器
        Toast.makeText(this, "设备是否是模拟器：" + nativeCoreUtil.runInEmulator(this), Toast.LENGTH_SHORT).show();

        //查看应用是否被重打包
        IVerifyListener iVerifyListener = new IVerifyListener() {
            @Override
            public void onVerifySuccess() {
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerifyFail() {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }
        };
        nativeCoreUtil.rePackage(this, iVerifyListener);

        //是否注入
        Toast.makeText(this, "应用是否被注入：" + nativeCoreUtil.detectInject(this), Toast.LENGTH_SHORT).show();


        //****************安全检测zip文件,提供路径****************
        File zipFile = new File("");
        String md5Str = "";
        SafeZipFile safeZipFile = new SafeZipFile(this, zipFile, md5Str);
        Toast.makeText(this, "文件：" + zipFile.getName() + "是否是安全的: " + safeZipFile.isLegalZipFile(), Toast.LENGTH_SHORT).show();


        //****************upgradetool 升级测试****************
        String public_key = "";
        String upgrade_model_json = ""; //此处应该是从服务器获取json串，然后反json为Upgrade
        UpgradeModel upgradeModel = JSON.parseObject(upgrade_model_json, UpgradeModel.class);
        String sava_path = "";
        String file_nme = "";
        ISafeInstall iSafeInstall = new ISafeInstall() {
            @Override
            public int getVerCode() {
                return 0;
            }

            @Override
            public void install(Context context, String savePath, String fileName, String isForce) {

            }

            @Override
            public String getErrMsg(String errMsg) {
                return null;
            }
        };
        UpgradeTool upgradeTool = new UpgradeTool(this, public_key, upgradeModel, sava_path, file_nme, iSafeInstall);


        //****************webview 安全测试****************
        SafeWebView safeWebView = (SafeWebView) this.findViewById(R.id.my_webview);
        IMethodInvokeInterface iMethodInvokeInterface = new IMethodInvokeInterface() {
            @Override
            public String dispatch(String data) {
                //业务方在此处处理JS回调
                return null;
            }
        };
        safeWebView.init(this, iMethodInvokeInterface);
        //向js注册一个hander，js调用
        safeWebView.registerHandler("safeWebViewInterface");
        safeWebView.loadUrl("http://www.test.com");

        //todo****************插件调用安全测试****************

        //todo****************binder机制通信安全测试****************


        //todo****************配置文件读取测试****************
        SecuritySDKConfig sdkConfig = new SecuritySDKConfig();

        //todo****************https安全通信测试****************

        //todo****************加密、解密组建测试****************

        //todo****************移动威胁情报分析测试****************
        //查看
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
        Map<Integer, String> methodArgMap = new HashMap<Integer, String>();
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
        //tv.setText(sdkConfig.getIntentUriList().get(0).getUriHostList().get(0));

        //配置文件的初始化
       /* Map<String, String> params = new HashMap<String, String>();
        params.put("username", "test");
        params.put("token", "123456");
        SecuritySDKInit.getInstance(this).syncConfig("http://192.168.199.164:8080/mytest/test.txt", params);*/
        SafeWebView webView = (SafeWebView) this.findViewById(R.id.my_webview);
        IMethodInvokeInterface methodInvokeInterface = new IMethodInvokeInterface() {
            @Override
            public String dispatch(String data) {
                //业务方在此处处理JS回调
                return null;
            }
        };
        webView.init(this, methodInvokeInterface);
        webView.loadUrl("http://www.test.com");
        //远程调用
        final TextView tv3 = (TextView) findViewById(R.id.IPC_check);
        Intent service = new Intent(MainActivity.this, MyService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if ((service instanceof IMyAidlInterface.Stub) == false) {
                    Log.d(TAG, "service " + (service instanceof IMyAidlInterface.Stub));
                }
                IMyAidlInterface myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                try {
                    String s = myAidlInterface.getInfoFromCli("hello from Cli");
                    tv3.setText(s);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);


        //远程调用 包名查看
        String referrerStr = HelpUtil.reflectGetReferrer(this);
        final TextView tv4 = (TextView) findViewById(R.id.caller_name);
        tv4.setText("caller namer:" + referrerStr);

        //https访问
        Button button = (Button) findViewById(R.id.myButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            HelpUtil.initSSLWithHttpClient(MainActivity.this);
                        } catch (Exception e) {
                            Log.e("HTTPS TEST", e.getMessage());
                        }
                    }
                }).start();
            }
        });

        /**
         * A native method that is implemented by the 'native-lib' native library,
         * which is packaged with this application.
         */
    }
}
