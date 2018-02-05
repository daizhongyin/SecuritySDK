package com.nstl.securitysdkcore.reinforce;

import android.content.Context;
import android.os.Build;

import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.NativeCoreUtil;
import com.nstl.securitysdkcore.reinforce.bean.InstallPackageInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by plldzy on 17-11-15.
 * 检测用户手机是否root,单利模式
 */

public class DetectRootUtil {
    private List<String> rootAppNameList = new LinkedList<String>();
    private static Context context = null;
    private static DetectRootUtil instance = null;
    private DetectRootUtil(Context context){
        this.context = context;
        //初始化常用的root应用和root管理应用的包名
        rootAppNameList.add("com.mgyun.shua.su");                //root大师
        rootAppNameList.add("com.qihoo.permmgr");                //360一键root
        rootAppNameList.add("eu.chainfire.supersu");             //supersu(root授权管理)
        rootAppNameList.add("com.shuame.rootgenius");            //root精灵
        rootAppNameList.add("com.kingroot.kinguser");            //kingroot
    }
    public static synchronized DetectRootUtil getInstance(Context context){
        if (instance == null){
            instance = new DetectRootUtil(context);
        }
        return instance;
    }
    //查看系统是正式版还是测试版，测试版可以运行root能力
    private static boolean checkDeviceDebuggable(){
        String buildTags = Build.TAGS;
        if(buildTags != null && buildTags.contains("test-keys")){
            return true;
        }
        return false;
    }
    //检测root授权管理和进行root的应用APK
    private static boolean checkRootApk(){
        boolean flag = false;
        List<InstallPackageInfo> installPackageInfoList = HelpUtil.getInstallPackageAndSig(context);
        //查找已知的广泛使用的root工具和root管理工具:1.kingroot,2.root精灵,3.supersu(对已经root进行授权管理的软件),4.360一键root,5.root大师
        for(InstallPackageInfo pkg : installPackageInfoList){
            if (instance.rootAppNameList.contains(pkg.getPkgName())){
                flag = true;
                break;
            }
        }
        return flag;
    }
    //检测是否存在su,并确定su是否是可执行的提权程序
    private static boolean checkExitSUAndIsExecute(){
        boolean flag = false;
        NativeCoreUtil nativeCoreUtil = new NativeCoreUtil();
        if(nativeCoreUtil.isExisSUAndExecute() > 0)
            flag = true;
        return flag;
    }
    public boolean isRoot(){
        boolean flag = false;
        flag = checkDeviceDebuggable();
        if(!flag){
            flag = checkRootApk();
        }
        if(!flag){
            flag = checkExitSUAndIsExecute();
        }
        return flag;
    }
    //最后一种root检测方法：就是直接进行root授权请求，但是用户交互体验不好，另外用户拒绝后，无法判断用户是否root。

}
