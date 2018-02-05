package com.nstl.securitysdkcore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.config.SecuritySDKConfig;

import java.util.Date;
import java.util.Map;

/**
 * Created by plldzy on 17-11-15.
 */

/**
 * SDK的配置文件更新和获取指定key的value
 */
public class SecuritySDKInit {
    private String configName = "securitysdk_config.json";
    private Context context = null;
    private Handler updateHandler = new Handler();
    private static SecuritySDKInit sdkInit = null;
    public final static String VERSION = "version";
    public final static String UPDATETIME = "update_time";
    public final static String TIMEOUT = "time_out";
    public final static String WEBVIEWCONFIG = "Webview_config";
    public final static String INTENTURISCHEMELIST = "intent_uri";
    public final static String URLWHITELIST = "url_white";
    public final static String INTERCEPTPLUGININVOKE = "intercept_plugin_invoke";
    private SharedPreferences preferences = null;

    private SecuritySDKInit(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(configName, Context.MODE_PRIVATE);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public void syncConfig(final String updateUrl, final Map<String, String> updateConfigJsonParams){
        String jsonString = HttpUtil.doPost(updateUrl, updateConfigJsonParams);
        SecuritySDKConfig sdkConfig = JSON.parseObject(jsonString, SecuritySDKConfig.class);
        //根据根目录下的config.json,对配置文件进行进行key-value格式编写，后续直接根据key读出相关value，然后序列化成SecuritySDKConfig中相关属性

        if((getConfigStringValueByKey(VERSION) != null && sdkConfig.getVersion().compareTo(getConfigStringValueByKey(VERSION))>0)){
            //发现新版本的配置文件
            preferences.edit().putString(VERSION, JSON.toJSONString(sdkConfig.getVersion()));
            preferences.edit().putString(UPDATETIME, new Date().toString());
            preferences.edit().putLong(TIMEOUT, sdkConfig.getTimeout());
            preferences.edit().putString(WEBVIEWCONFIG, JSON.toJSONString(sdkConfig.getWebviewConfig()));
            preferences.edit().putString(INTENTURISCHEMELIST, JSON.toJSONString(sdkConfig.getIntentUriList()));
            preferences.edit().putString(URLWHITELIST, JSON.toJSONString(sdkConfig.getUpdateAndDownloadUrlWhiteList()));
            preferences.edit().putString(INTERCEPTPLUGININVOKE, JSON.toJSONString(sdkConfig.getInterceptPluginInvokeList()));
        }
        //执行定期更新任务查询
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                syncConfig(updateUrl, updateConfigJsonParams);
            }
        };
        updateHandler.postDelayed(runnable, sdkConfig.getTimeout());
    }
    //获得配置文件中指定key的值，如webview的参数
    public String getConfigStringValueByKey(String keyName){
        return preferences.getString(keyName,null);
    }

    //获得配置文件中指定key的值，如更新间隔时间
    public long getConfigLongValueByKey(String keyName){
        return preferences.getLong(keyName,-1);
    }
    public static synchronized SecuritySDKInit getInstance(Context context){
        if (sdkInit == null)
            sdkInit = new SecuritySDKInit(context);
        return sdkInit;
    }

}
