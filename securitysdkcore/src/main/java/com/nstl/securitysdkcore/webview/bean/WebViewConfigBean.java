package com.nstl.securitysdkcore.webview.bean;

import java.util.List;

/**
 * Created by plldzy on 2017/4/27.
 */

public class WebViewConfigBean {
    List<UrlControlsBean> urlList = null;
    List<BlockMethodInvokeBean> blockMethodInvokeBeanList = null;

    public List<BlockMethodInvokeBean> getBlockMethodInvokeBeanList() {
        return blockMethodInvokeBeanList;
    }

    public void setBlockMethodInvokeBeanList(List<BlockMethodInvokeBean> blockMethodInvokeBeanList) {
        this.blockMethodInvokeBeanList = blockMethodInvokeBeanList;
    }

    public List<UrlControlsBean> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<UrlControlsBean> urlList) {
        this.urlList = urlList;
    }
}
