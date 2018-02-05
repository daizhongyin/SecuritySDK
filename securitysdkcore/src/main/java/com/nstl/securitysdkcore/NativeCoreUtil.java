package com.nstl.securitysdkcore;

import com.nstl.securitysdkcore.reinforce.IVerifyListener;

/**
 * Created by plldzy on 17-11-15.
 */
/*
核心能力的本地代码实现
 */
public class NativeCoreUtil {
    static {
        System.loadLibrary("native-lib");
    }
    //public native String stringFromJNI();
    public native int debugPresent();		// 调试
    public native int runInEmulator(Object mContext);	// 模拟器
    public native void rePackage(Object mContext,IVerifyListener verifyListener);		// 重打包
    public native int detectInject(Object mcontext);       //注入检测
    public native int isExisSUAndExecute();             //是否存在su文件并且是可执行的


}
