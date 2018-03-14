package com.nstl.securitysdkcore.config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by plldzy on 17-12-6.
 * webview中需要拦截的方法名和该方法的参数
 */

public class InterceptMethod {
    private int type = 0;                                   //拦截调用的配置方式：黑白名单，0是黑名单，-1拦截该方法，不用比较方法的参数内容，其他值都是白名单
    private String methodNmae;                              //需要拦截的方法名
    private Map<Integer,String> methodArgMap = null;        //需要拦截的方法的参数列表集合key是第几个参数，value是参数内容
    private String MethodSign=null;                             //需要拦截的方法签名
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMethodNmae() {
        return methodNmae;
    }

    public void setMethodNmae(String methodNmae) {
        this.methodNmae = methodNmae;
    }

    public Map<Integer, String> getMethodArgMap() {
        return methodArgMap;
    }

    public void setMethodArgMap(Map<Integer, String> methodArgMap) {
        this.methodArgMap = methodArgMap;
    }
    public String getMethodSign(){
        class ClassA{};
        Method method=(new ClassA()).getClass().getEnclosingMethod();
        String methodStr = method.toString();
        String methodName = methodStr.substring(0, methodStr.lastIndexOf("("));
        String methodNamePre = methodName.substring(0,
                methodName.lastIndexOf("."));
        return   methodStr.substring(methodNamePre.length() + 1);
//        return  MethodSign;
    }
    public void setMethodSign(String methodSign){
        this.MethodSign=methodSign;
    }
}
