package com.nstl.securitysdkcore.UpgradeTool.UpgradeToolTest;

import android.content.Context;
import android.util.Log;

import com.nstl.securitysdkcore.UpgradeTool.ISafeInstall;

/**
 * Created by LIN on 2017/12/31.
 */

public class SafeInstall implements ISafeInstall {
    @Override
    public int getVerCode() {
        return 7;
    }

    @Override
    public void install(Context context, String savePath, String fileName, String isForce) {
        Log.d("TEST","this is install apk interface");
    }

    @Override
    public String getErrMsg(String errMsg) {
        return null;
    }
}
