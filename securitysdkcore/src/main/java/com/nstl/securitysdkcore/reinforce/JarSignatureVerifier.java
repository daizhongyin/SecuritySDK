package com.nstl.securitysdkcore.reinforce;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by daizhongyin on 2018/5/7.
 */

/** 签名的jar的验证器 */
public class JarSignatureVerifier {
    public static void main(String[] args) throws IOException {
        String name1 = "D:\\Documents\\keystore\\commons-collections4-4.1_true.jar";
        System.out.println(verifyJar(name1));
    }
    public static boolean verifyJar(String jarPath) {
        boolean flag = false;
        try {
            flag = verify(jarPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;
    }

    private static boolean verify(String jarPath) throws IOException {
        boolean flag = true;
        JarFile jar = new JarFile(jarPath, true);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            InputStream is = null;
            try {
                byte[] buffer = new byte[8192];
                is = jar.getInputStream(entry);
                while ((is.read(buffer, 0, buffer.length)) != -1) {
                    // We just read. This will throw a SecurityException
                    // if a signature/digest check fails.
                }
            } catch (SecurityException se) {
                flag = false;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return flag;
    }
}