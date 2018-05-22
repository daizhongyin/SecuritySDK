package com.nstl.securitysdkcore.reinforce;

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
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 为了保证ZIPFile读取APK文件的安全性：防止zipfile的目录穿越漏洞，同时确保只有一个Dex，此外还需要确保APK中的签名和其自生代码的信息一致
 */
public class SafeZipFile {

    private String apkFilePath = null;
    private boolean zipFileIsIllegal = false;
    private Context context;
    private String md5Sig;

    /**
     * @param file
     * @param context Context上下文
     * @param md5Sig  要校验的签名证书的MD5值(可以通过keytool工具来查看)
     */
    public SafeZipFile(File file, Context context, String md5Sig) {
        apkFilePath = file.getAbsolutePath();
        context = context;
        md5Sig = md5Sig;
    }

    /**
     * true表示apk或者签名zip为合法的,false表示zip或者apk非法
     *
     * @param validateSig 是否对zipfile文件进行签名校验，true表示进行，false表示不用进行zip文件的签名校验。
     * @return true表示zipfile文件合法，false非法
     */
    public boolean isZipFileValid(boolean validateSig) {
        boolean flag = false;
        if (validateSig) {
            flag = this.dexCheck() && this.apkSignCheck(this.context, this.md5Sig);
        } else {
            flag = this.dexCheck();
        }

        return flag;
    }

    /**
     * 判断zipfile的合法性，防止../ 及 重复的dex文件
     *
     * @return true表示合法，false非法
     * @throws IOException
     */
    private boolean dexCheck() {
        // 检查zip压缩文件中，是否会包含../，以及zip中的所有dex文件，确保不存在相同的姓名，注意多个dex文件的情况-classes.dex,classes2.dex
        boolean flag = true;
        if (TextUtils.isEmpty(apkFilePath)) {
            return false;
        }
        File srcFile = new File(this.apkFilePath);
        FileInputStream fileInputStream = null;
        ZipEntry zipEntry = null;
        StringBuilder fileNameBuilder = new StringBuilder();
        ZipInputStream zipInputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                } else {
                    String entryName = zipEntry.getName();
                    if (entryName.contains("../")) {
                        flag = false;
                        return flag;
                    }
                    if (entryName.endsWith(".dex")) {
                        if (fileNameBuilder.toString().contains(entryName)) {
                            flag = false;
                            return flag;
                        } else {
                            fileNameBuilder.append(entryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return flag;
    }

    /**
     * 检查apk的签名
     *
     * @param ct
     * @param sig
     * @return true表示apk签名合法，false表示签名非法
     */
    private boolean apkSignCheck(Context ct, String sig) {
        //2.读取APK中的META-INF目录下的签名文件，验证一致性
        boolean flag = false;
        PackageManager pm = ct.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_SIGNATURES);
        if (pi != null) {
            Signature[] signatures = pi.signatures;
            if (signatures != null && signatures.length > 0) {
                String signature = encryptionMD5(signatures[0].toByteArray());
                if (signature.equals(sig))
                    flag = true;
            }
        }
        return flag;
    }

    /**
     * md5签名
     *
     * @param byteStr
     * @return
     */
    private String encryptionMD5(byte[] byteStr) {
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