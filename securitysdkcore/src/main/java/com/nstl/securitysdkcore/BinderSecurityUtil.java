package com.nstl.securitysdkcore;

import android.content.Context;
import android.os.Binder;
import android.util.Log;

/**
 * Created by ffthy on 13/12/2017.
 */

public class BinderSecurityUtil {

    /**
     * callingAPP， RemoteAppSign两个变量为用户提前配置，callingAPP 为调用者的包名，RemoteAppSign为包签名
     */
    public static  Boolean checkClientSig(Context context){
        String  TAG="BinderSecurityUtil";
        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        Log.d(TAG, "checkClientSig: "+callingApp);
        NativeCoreUtil nativeCoreUtil=new NativeCoreUtil();
        String RemoteAppSign=nativeCoreUtil.getRemoteAppSign(context,callingApp);
        Log.d(TAG, "checkClientSig: "+RemoteAppSign);
        if(callingApp=="xxx"&&RemoteAppSign=="xxx"){
            return true;
        }
        return  false;

    }
}
