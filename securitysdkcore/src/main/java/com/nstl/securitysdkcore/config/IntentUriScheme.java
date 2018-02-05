package com.nstl.securitysdkcore.config;

import java.util.List;
import java.util.Map;

/**
 * Created by plldzy on 17-12-6.
 * Intent Uri Scheme的拦截规则：分别对intent中的action和type以及intent uri中的scheme、host、port、path以及uri中的参数进行过滤和拦截,
 */

public class IntentUriScheme {
    private int type = 0;                                   //拦截调用的配置方式：黑白名单，0是黑名单，非0是白名单
    private List<String> actionStringList = null;           //需要过滤的intent action
    private List<String> intentTypeList = null;             //需要过滤的intent中的type
    private List<String> uriSchemeList = null;
    private List<String> uriHostList = null;
    private List<Integer> uriPortList = null;
    private List<String> uriPathList = null;
    private Map<String, String> uriParaMaps = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getUriSchemeList() {
        return uriSchemeList;
    }

    public void setUriSchemeList(List<String> uriSchemeList) {
        this.uriSchemeList = uriSchemeList;
    }

    public List<String> getUriHostList() {
        return uriHostList;
    }

    public void setUriHostList(List<String> uriHostList) {
        this.uriHostList = uriHostList;
    }

    public List<Integer> getUriPortList() {
        return uriPortList;
    }

    public void setUriPortList(List<Integer> uriPortList) {
        this.uriPortList = uriPortList;
    }

    public List<String> getUriPathList() {
        return uriPathList;
    }

    public void setUriPathList(List<String> uriPathList) {
        this.uriPathList = uriPathList;
    }

    public Map<String, String> getUriParaMaps() {
        return uriParaMaps;
    }

    public void setUriParaMaps(Map<String, String> uriParaMaps) {
        this.uriParaMaps = uriParaMaps;
    }

    public List<String> getActionStringList() {
        return actionStringList;
    }

    public void setActionStringList(List<String> actionStringList) {
        this.actionStringList = actionStringList;
    }

    public List<String> getIntentTypeList() {
        return intentTypeList;
    }

    public void setIntentTypeList(List<String> intentTypeList) {
        this.intentTypeList = intentTypeList;
    }
}
