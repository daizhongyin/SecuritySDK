package com.nstl.securitysdkcore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by Lin on 2017/11/20.
 */
/**
 * 为了保证ZIPFile读取APK文件的安全性，确保只有一个Dex，此外还需要确保APK中的签名和其自生代码的信息一致
 */
public class SafeZipFile{

    private String apkFilePath = null;
    private boolean zipFileIsIllegal = false;
    private Context context;
    private String md5Sig;

    /**
     *
     * @param file
     * @param context Context上下文
     * @param md5Sig 要校验的签名证书的MD5值(可以通过keytool工具来查看)
     */
    public SafeZipFile(File file, Context context, String md5Sig){
        apkFilePath = file.getAbsolutePath();
        context = context;
        md5Sig = md5Sig;
    }

    /**
     * true表示apk或者签名zip为非法的,false表示zip或者apk合法
     * @return
     */
    public boolean isLegalZipFile(){
        try {
            if( this.dexCheck() && this.apkSignCheck(this.context,this.md5Sig)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断zipfile的合法性，防止../ 及 重复的dex文件
     * @return
     * @throws IOException
     */
    private boolean dexCheck() throws IOException {
        //检查APK中的所有dex文件，确保不存在相同的姓名，注意多个dex文件的情况-classes.dex,classes2.dex
        File srcFile = new File(this.apkFilePath);
        FileInputStream fileInputStream = new FileInputStream(srcFile);
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
        StringBuilder fileNameBuilder = new StringBuilder();
        ZipEntry zipEntry = null;
        while ((zipEntry = zipInputStream.getNextEntry()) != null){
            if(zipEntry.isDirectory()){
                continue;
            }else{
                String entryName = zipEntry.getName();
                if( entryName.endsWith(".dex") ){
                    if( fileNameBuilder.toString().contains( entryName )){
                        return false;
                    }else{
                        fileNameBuilder.append(entryName);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 检查apk的签名
     * @param ct
     * @param sig
     * @return
     */
    private boolean apkSignCheck(Context ct, String sig){
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
    private  String encryptionMD5(byte[] byteStr) {
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
