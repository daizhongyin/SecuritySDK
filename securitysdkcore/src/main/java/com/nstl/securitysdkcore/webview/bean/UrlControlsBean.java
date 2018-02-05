package com.nstl.securitysdkcore.webview.bean;

/**
 * Created by plldzy on 2017/4/27.
 */

public class UrlControlsBean {
    private int control;
    private String urlValue;            //形如baidu.com,taobao.com.匹配时，先获取url的host，然后通过字符串的endswith来判断，url是否可信

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(String urlValue) {
        this.urlValue = urlValue;
    }
}
