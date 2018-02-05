package com.nstl.securitysdkcore.webview.bean;

import java.util.List;

/**
 * Created by plldzy on 2017/4/27.
 */

public class BlockMethodInvokeBean {
    private String methodName;
    private List<String> methodArg = null;

    public List<String> getMethodArg() {
        return methodArg;
    }

    public void setMethodArg(List<String> methodArg) {
        this.methodArg = methodArg;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
