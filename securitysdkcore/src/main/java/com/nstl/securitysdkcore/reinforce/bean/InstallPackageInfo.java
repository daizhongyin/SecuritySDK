package com.nstl.securitysdkcore.reinforce.bean;

import android.content.pm.ActivityInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ServiceInfo;

/**
 * Created by plldzy on 17-11-17.
 */

public class InstallPackageInfo {
    private String pkgName;                             //应用包名
    private String pkgSig;                              //应用签名
    private int versionCode;                            //应用版本号
    private String versionName;                         //应用版本名称
    private PermissionInfo[] permissionInfos = null;    //应用申请权限列表
    private ActivityInfo[] activityInfos = null;        //应用activity列表，下同
    private ServiceInfo[] serviceInfos = null;
    private ActivityInfo[] receivers = null;
    private long firstInstallTime;
    private long lastUpdateTime;

    //private

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgSig() {
        return pkgSig;
    }

    public void setPkgSig(String pkgSig) {
        this.pkgSig = pkgSig;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int version) {
        this.versionCode = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public PermissionInfo[] getPermissionInfos() {
        return permissionInfos;
    }

    public void setPermissionInfos(PermissionInfo[] permissionInfos) {
        this.permissionInfos = permissionInfos;
    }

    public ActivityInfo[] getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(ActivityInfo[] activityInfos) {
        this.activityInfos = activityInfos;
    }

    public ServiceInfo[] getServiceInfos() {
        return serviceInfos;
    }

    public void setServiceInfos(ServiceInfo[] serviceInfos) {
        this.serviceInfos = serviceInfos;
    }

    public ActivityInfo[] getReceivers() {
        return receivers;
    }

    public void setReceivers(ActivityInfo[] receivers) {
        this.receivers = receivers;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
