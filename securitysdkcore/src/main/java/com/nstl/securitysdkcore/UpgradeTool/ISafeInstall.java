package com.nstl.securitysdkcore.UpgradeTool;

import android.content.Context;

/**
 * Created by Lin on 2017/12/27.
 */

public interface ISafeInstall {

    public int getVerCode();
    public void install(Context context, String savePath, String fileName, String isForce);
    public String getErrMsg(String errMsg);

}
