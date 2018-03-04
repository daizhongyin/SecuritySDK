package com.nstl.securitysdkcore.webview;

/**
 * Created by plldzy on 2017/4/27.
 */

public interface IMethodInvokeInterface {
    /**
     * 处理js传递过来的参数，然后把本地函数的处理结果，返回给js页面
     * @param data
     * @return
     */
    String  dispatch(String data);
}
