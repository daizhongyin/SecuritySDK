package com.nstl.securitysdkcore.webview;

import java.util.Map;

/**
 * Created by plldzy on 2017/4/27.
 */

public interface IMethodInvokeInterface {
    String  dispatch(String methodName, Map<Integer,String> argJson);
}
