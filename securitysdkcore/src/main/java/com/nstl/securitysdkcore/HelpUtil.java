package com.nstl.securitysdkcore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.util.Log;

import com.nstl.securitysdkcore.crypt.CryptAndHttps;
import com.nstl.securitysdkcore.reinforce.bean.InstallPackageInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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

    /**
     * 获取文件的MD5的值
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取sd卡的路径信息
     * @return
     */
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
    /**
     * 通过反射的方法获得refererField
     * @return
     */
    public static String reflectGetReferrer(Context context) {
        Class activityClass = null;
        try {
            activityClass = Class.forName("android.app.Activity");
            Field refererField = activityClass.getDeclaredField("mReferrer");
            refererField.setAccessible(true);
            String referrer = (String) refererField.get(context);
            return referrer;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }
    /**
     *  测试https链接 assets文件夹中必须提前配有服务器对应的cer证书
     * @return
     */
    public static void initSSLWithHttpClient(Context context) {
        try {
            String CertName="zhihu.cer";
            InputStream cerInput=new BufferedInputStream(context.getAssets().open(CertName));
            List<String> IpAndHosts= new ArrayList();
            IpAndHosts.add("192.168.0.32");
            HttpsURLConnection conn= CryptAndHttps.getHttpsUrlConnection("https://zhuanlan.zhihu.com/p/22816331",IpAndHosts,cerInput);
            conn.connect();
            InputStream  in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer result = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("TTTT", result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
