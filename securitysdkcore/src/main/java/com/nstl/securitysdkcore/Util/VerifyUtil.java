package com.nstl.securitysdkcore.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import com.nstl.securitysdkcore.HelpUtil;

import java.io.File;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Lin on 2018/4/26.
 */

public class VerifyUtil {
    /**
     * 使用公钥用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止下载被非法劫持
     * @param signCode 签值
     * @param code
     * @return
     */
    public static boolean verifySignedVerifyCode(String publicKey,String signCode, String code) {
        if ( publicKey ==  null || publicKey.isEmpty()){
            return false;
        }
        if( signCode == null || signCode.isEmpty()){
            return false;
        }
        if( code == null ||  code.isEmpty()){
            return false;
        }

        boolean flag = false;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getBytes());
            PublicKey pKey = kf.generatePublic(keySpec);
            java.security.Signature signature = java.security.Signature.getInstance("SHA1withRSA");
            signature.initVerify(pKey);
            signature.update(code.getBytes());
            flag = signature.verify(signCode.getBytes());
        } catch (Exception e) {
            //todo 打印日志
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 检查下载的apk或者文件的MD5信息是否跟文件自身匹配
     * @param md5Code 下载文件的MD5值
     * @return true, 下载内容MD5验证合法，false MD5验证非法
     */
    public static boolean fileMd5Check(String apkFilePath, String md5Code) {
        boolean flag = false;
        if ( TextUtils.isEmpty(apkFilePath) )
            return flag;
        if (md5Code == null || md5Code.isEmpty()){
            return false;
        }
        try{
            File downloadFile = new File(apkFilePath);
            String downloadFileMD5 = HelpUtil.getFileMD5(downloadFile);
            flag = md5Code.equals(downloadFileMD5);
        }catch (Exception e){
            //todo 打印log
            flag = false;
        }
        return flag;
    }

    /**
     * 检查apk的签名
     * @param ct
     * @param sig
     * @return
     */
    public static boolean apkSignCheck(Context ct, String sig, String apkFilePath){
        //2.读取APK中的META-INF目录下的签名文件，验证一致性
        boolean flag = true;
        if (TextUtils.isEmpty(apkFilePath))
            return true;
        PackageManager pm = ct.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_SIGNATURES);
        if(pi != null){
            Signature[] signatures = pi.signatures;
            if(signatures != null && signatures.length > 0){
                String signature =  encryptionMD5(signatures[0].toByteArray());
                if(signature.equals(sig))
                    flag = false;
            }
        }
        return flag;
    }

    /**
     * md5签名
     * @param byteStr
     * @return
     */
    private static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }
}
