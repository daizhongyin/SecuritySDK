package com.nstl.securitysdkcore.config;

import java.util.List;

/**
 * Created by plldzy on 17-12-6.
 * webview的拦截配置规则
 */

public class WebviewConfig {

    private List<String> urlWhiteList = null;               //webview中url白名单列表
    private List<String> urlBlackList = null;               //url黑名单列表
    private List<InterceptMethod> methodList = null;          //需要拦截的方法的结合

    public List<String> getUrlWhiteList() {
        return urlWhiteList;
    }

    public void setUrlWhiteList(List<String> urlWhiteList) {
        this.urlWhiteList = urlWhiteList;
    }

    public List<String> getUrlBlackList() {
        return urlBlackList;
    }

    public void setUrlBlackList(List<String> urlBlackList) {
        this.urlBlackList = urlBlackList;
    }

    public List<InterceptMethod> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<InterceptMethod> methodList) {
        this.methodList = methodList;
    }
}
