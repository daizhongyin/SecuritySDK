package com.nstl.securitysdkcore.config;

import android.content.SharedPreferences;
import android.preference.Preference;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.SecuritySDKInit;

import java.util.List;

/**
 * Created by plldzy on 17-12-6.
 * 安全SDK的配置文件对webview、url scheme、应用下载和插件调用进行拦截的配置规则
 */

public class SecuritySDKConfig {
    private String version = null;                                          //版本号
    private String updateTime = null;                                       //本次更新时间
    private long timeout = 3600;                                            //下次更新间隔时间
    private WebviewConfig webviewConfig = null;                             //webview的拦截规则
    private List<IntentUriScheme> intentUriList = null;                     //intent uri拦截规则
    private List<String> updateAndDownloadUrlWhiteList = null;              //应用和插件更新的url白名单
    private List<InterceptPluginInvoke> interceptPluginInvokeList = null;   //应用插件调用的过滤拦截规则


    /*private SecuritySDKConfig(SharedPreferences p){
        this.preferences = p;
        setVersion(p.getString(SecuritySDKInit.VERSION, null));
        setUpdateTime(p.getString(SecuritySDKInit.UPDATETIME, null));
        setTimeout(p.getLong(SecuritySDKInit.TIMEOUT, -1));

    }
    public static SecuritySDKConfig getInstance(SharedPreferences p){
        if(sdkConfig == null){
            sdkConfig = new SecuritySDKConfig(p);
        }
        return sdkConfig;
    }*/

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public WebviewConfig getWebviewConfig() {
        /*String str = preferences.getString(SecuritySDKInit.WEBVIEWCONFIG, null);
        if(str != null){
            //fastjson
            webviewConfig = JSON.parseObject(str, WebviewConfig.class);
        }*/
        return webviewConfig;
    }


    public List<IntentUriScheme> getIntentUriList() {
        /*String str = preferences.getString(SecuritySDKInit.INTENTURISCHEMELIST, null);
        if(str != null){
            intentUriList = JSON.parseArray(str, IntentUriScheme.class);
        }*/
        return intentUriList;
    }


    public List<String> getUpdateAndDownloadUrlWhiteList() {
        /*String str = preferences.getString(SecuritySDKInit.URLWHITELIST, null);
        if(str != null){
            updateAndDownloadUrlWhiteList = JSON.parseArray(str, String.class);
        }*/
        return updateAndDownloadUrlWhiteList;
    }


    public List<InterceptPluginInvoke> getInterceptPluginInvokeList() {
        /*String str = preferences.getString(SecuritySDKInit.INTERCEPTPLUGININVOKE, null);
        if(str != null){
            interceptPluginInvokeList = JSON.parseArray(str, InterceptPluginInvoke.class);
        }*/
        return interceptPluginInvokeList;
    }

    public void setWebviewConfig(WebviewConfig webviewConfig) {
        this.webviewConfig = webviewConfig;
    }

    public void setIntentUriList(List<IntentUriScheme> intentUriList) {
        this.intentUriList = intentUriList;
    }

    public void setUpdateAndDownloadUrlWhiteList(List<String> updateAndDownloadUrlWhiteList) {
        this.updateAndDownloadUrlWhiteList = updateAndDownloadUrlWhiteList;
    }

    public void setInterceptPluginInvokeList(List<InterceptPluginInvoke> interceptPluginInvokeList) {
        this.interceptPluginInvokeList = interceptPluginInvokeList;
    }
}
