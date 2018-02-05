package com.nstl.securitysdkcore.webview;


import java.util.Map;

/**
 * Created by Lin on 2017/12/17.
 */

public class MethodAndArgJson {     //拦截调用的配置方式：黑白名单，0是黑名单，非0是白名单

    private String methodName;
    private Map<Integer,String>argMapJson = null;

    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    public String getMethodName(){
        return methodName;
    }
    public void setArgMapJson(Map<Integer,String> argMapJson){
        this.argMapJson = argMapJson;
    }
    public Map<Integer,String> getArgMapJson(){
        return argMapJson;
    }
}
