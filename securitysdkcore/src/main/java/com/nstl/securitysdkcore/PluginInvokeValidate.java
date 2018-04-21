package com.nstl.securitysdkcore;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.config.IntentUriScheme;
import com.nstl.securitysdkcore.config.InterceptPluginInvoke;

/**
 * Created by ffthy on 2017/12/27.
 */

public class PluginInvokeValidate {

    private static Context context = null;

    public PluginInvokeValidate(Context context) {
        this.context = context;
    }

    /**
     * @param pluginName
     * @param className
     * @param methodSign
     * @param intent
     * @return
     */
    public static boolean validate(String pluginName, String className, String methodSign, Intent intent) {
        //  InterceptPluginInvoke interceptPluginInvoke=json.load()  根据配置生成实例类。
        // InterceptPluginInvoke interceptPluginInvoke=new InterceptPluginInvoke();
        SecuritySDKInit securitySDKInit = SecuritySDKInit.getInstance(context);
        String interceptString = securitySDKInit.getConfigStringValueByKey("intercept_plugin_invoke");
        InterceptPluginInvoke interceptPluginInvoke = (InterceptPluginInvoke) JSON.parse(interceptString);
        return validatePlugin(pluginName, className, methodSign, intent, interceptPluginInvoke);

    }

    /**
     * @param pluginName
     * @param className
     * @param methodSign
     * @param intent
     * @param interceptPluginInvoke1
     * @return
     */
    public static boolean validatePlugin(String pluginName, String className, String methodSign, Intent intent, InterceptPluginInvoke interceptPluginInvoke1) {
        int type = interceptPluginInvoke1.getType();
        if (pluginName == null) {
            return type == 0 ? false : true;
        } else {
            if (pluginName.equals(interceptPluginInvoke1.getPluginName())) {
                return validateclassName(className, methodSign, intent, interceptPluginInvoke1);
            }
            return type == 0 ? true : false;
        }
    }

    /**
     * @param className
     * @param methodSign
     * @param intent
     * @param interceptPluginInvoke1
     * @return
     */
    public static boolean validateclassName(String className, String methodSign, Intent intent, InterceptPluginInvoke interceptPluginInvoke1) {
        int type = interceptPluginInvoke1.getType();
        if (className == null) {
            return type == 0 ? false : true;
        } else {
            if (className.equals(interceptPluginInvoke1.getTargetClassName())) {
                return validateMethod(methodSign, intent, interceptPluginInvoke1);
            }
            return type == 0 ? true : false;
        }
    }

    /**
     *
     * @param methodSign
     * @param intent
     * @param interceptPluginInvoke1
     * @return
     */
    private static boolean validateMethod(String methodSign, Intent intent, InterceptPluginInvoke interceptPluginInvoke1) {
        int type = interceptPluginInvoke1.getType();
        if (methodSign == null) {
            return type == 0 ? false : true;
        } else {
            if (methodSign.equals(interceptPluginInvoke1.getMethod().getMethodSign())) {
                return validateIntent(intent, interceptPluginInvoke1);
            }
            return type == 0 ? true : false;
        }
    }

    /**
     * @param intent
     * @param interceptPluginInvoke1
     * @return
     */
    private static boolean validateIntent(Intent intent, InterceptPluginInvoke interceptPluginInvoke1) {
        IntentUriScheme intentUriScheme = interceptPluginInvoke1.getUriScheme();
        int methodSignType = interceptPluginInvoke1.getMethod().getType();
        if (intentUriScheme == null) {
            return methodSignType == 0 ? false : true;
        }
        for (String a : intentUriScheme.getActionStringList()) {
            if (intent.getAction().equals(a)) {
                return intentUriScheme.getType() == 0 ? false : true;
            }
        }
        //todo //这句逻辑需要重写
        return true;
    }
}

