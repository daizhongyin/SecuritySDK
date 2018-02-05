package com.nstl.securitysdkcore.config;

/**
 * Created by plldzy on 17-12-6.
 * 插件调用的拦截规则，属性type只能控制pluginname和targetClassName这两个属性
 * InterceptMethod 和IntentUriScheme属性是白名单还是黑名单分别有各自属性控制；
 * InterceptPluginInvoke-->type和InterceptMethod、IntentUriScheme的type控制关系如下：
 * InterceptPluginInvoke-->type黑名单，那么后两者只能是黑名单；如果InterceptPluginInvoke-->type是白名单，后两者黑，白名单都可以
 */

public class InterceptPluginInvoke {
    private int type = 0;                   //插件拦截调用的配置方式：黑白名单，0是黑名单，非0是白名单；
    private String pluginName;              //需要拦截调用的插件名
    private InterceptMethod method;         //需要拦截的插件中的方法以及方法参数
    private String targetClassName;         //需要拦截调用的目标类名
    private IntentUriScheme uriScheme;      //需要拦截的intent uri的scheme规则

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public InterceptMethod getMethod() {
        return method;
    }

    public void setMethod(InterceptMethod method) {
        this.method = method;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public IntentUriScheme getUriScheme() {
        return uriScheme;
    }

    public void setUriScheme(IntentUriScheme uriScheme) {
        this.uriScheme = uriScheme;
    }
}
