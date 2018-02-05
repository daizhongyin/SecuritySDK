package com.nstl.securitysdkcore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.nstl.securitysdkcore.reinforce.bean.InstallPackageInfo;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by plldzy on 17-11-17.
 * java层基础帮助工具类
 */

public class HelpUtil {
    //获得手机上安装的应用及其相关信息
    public static List<InstallPackageInfo> getInstallPackageAndSig(Context context){
        List<InstallPackageInfo> installPkgList = new LinkedList<InstallPackageInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        for (PackageInfo p : packageList) {
            InstallPackageInfo installPkg = new InstallPackageInfo();
            installPkg.setPkgName(p.packageName);
            installPkg.setVersionCode(p.versionCode);
            installPkg.setVersionName(p.versionName);
            installPkg.setActivityInfos(p.activities);
            installPkg.setFirstInstallTime(p.firstInstallTime);
            installPkg.setLastUpdateTime(p.lastUpdateTime);
            installPkg.setPermissionInfos(p.permissions);
            installPkg.setReceivers(p.receivers);
            installPkg.setServiceInfos(p.services);
            //获得签名
            installPkg.setPkgSig(p.signatures[0].toCharsString());
            Signature[] arrSignatures = p.signatures;
            installPkgList.add(installPkg);

        }
        return installPkgList;
    }
}
