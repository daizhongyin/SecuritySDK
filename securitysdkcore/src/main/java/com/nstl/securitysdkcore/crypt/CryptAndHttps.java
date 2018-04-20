package com.nstl.securitysdkcore.crypt;

import android.util.Base64;
import android.util.Log;

import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.crypt.bean.EncryptData;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by plldzy on 17-11-15.
 */

public class CryptAndHttps {
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA"; //签名算法
    public static String TAG="CryptAndHttps";

    /**
     *     //初始化AES的key
     * @return
     */
    private static byte[] getAESKey(){
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, new SecureRandom(password.getBytes())); 这种方式按password指定的字符串生成AES密钥。
            // SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以生成的秘钥就一样。
            kg.init(128);//要生成多少位，只需要修改这里即可128, 192或256，第二个参数为空，密钥为随机生成
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            return b;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("没有此算法。");
        }
        return null;
    }

    /**
     * AES加密 ,提供初始密钥种子
     * @param data
     * @param keyByte
     * @return
     */
    private static byte[] encryptDataByAES(String data, byte[] keyByte){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(keyByte));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = data.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *     //AES加密,不提供初始密钥
     * @param data
     * @return
     */
    private static byte[] encryptDataByAES(String data){
        try {
            byte[] enCodeFormat =getAESKey();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = data.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *     //用AES加密内容，然后非对称公钥加密AES的key
     * @param sourceData
     * @param publicKey
     * @param type
     * @return
     * @throws Exception
     */
    public static EncryptData aesEocdeBodyAsymmetricEncodeKey(String sourceData, String publicKey, int type) throws Exception {
        byte aesKey[] = getAESKey();
        EncryptData encryptData = new EncryptData();
        encryptData.setEncryContent(encryptDataByAES(sourceData, aesKey));
        if(type == 1){  //type=1,表示RSA加密key
            encryptData.setEncryKey(encryptByRSAPublicKey(aesKey, publicKey));
        }else{
            encryptData.setEncryKey(encryptByECCPublicKey(aesKey, publicKey));
        }
        return encryptData;
    }

    /**
     * //RSA公钥加密
     * @param data
     * @param rsaPublicKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByRSAPublicKey(byte[] data, String rsaPublicKey) throws Exception  {
        RSAPublicKey publicKey=loadPublicKeyByStr(rsaPublicKey);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(1, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 117) {  //RSA最大加密明文大小117
            byte[] cache;
            if(inputLen - offSet > 117) {
                cache = cipher.doFinal(data, offSet, 117);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;

    }

    /**
     * //ECC公钥加密
     * @param data
     * @param eccPublicKey
     * @return
     */
    public static byte[] encryptByECCPublicKey(byte[] data, String eccPublicKey){
//        byte[] keyBytes = Base64.decode(eccPublicKey,Base64.DEFAULT);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = ECKeyFactory.INSTANCE;
//
//        ECPublicKey pubKey = (ECPublicKey) keyFactory
//                .generatePublic(x509KeySpec);
//
//        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(pubKey.getW(),
//                pubKey.getParams());
        return null;
    }

    /**
     * //RSA签名校验
     * @param data
     * @param rsaPublicKey
     * @param sign
     * @return
     */
    public static boolean verifyByRSA(byte[] data, String rsaPublicKey, String sign){
        //RSAPublicKey publicKey=loadPublicKeyByStr(rsaPublicKey);
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(rsaPublicKey,Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update( data);

            boolean bverify = signature.verify( Base64.decode(sign,Base64.DEFAULT) );
            return bverify;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;

    }


    /**
     *     //ECC签名校验
     * @param data
     * @param eccPublicKey
     * @param sign
     * @return
     */
    public static boolean verifyByECC(byte[] data, String eccPublicKey, String sign){
        return false;
    }


    /**
     *    //生成信息摘要算法，type=1表示sha256,type=2表示sha512
     * @param sourceStr
     * @param type
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getHashBySHA(String sourceStr, int type) throws NoSuchAlgorithmException {
        String algorithm = type == 1 ? "SHA-256" : "SHA-512";
        MessageDigest md= MessageDigest.getInstance(algorithm);
        return HelpUtil.bytesToHexString(md.digest(sourceStr.getBytes()));

    }
    /**
     * 根据客户端自定义证书，进行Https安全通信
     * @param urlString             需要通信的url
     * @param ipAndHosts            证书所签发的域名或者IP地址列表,进行hostnameverify的验证
     * @param certInputstream      证书的输入流
     */
    public static HttpsURLConnection getHttpsUrlConnection(String urlString, List<String> ipAndHosts, InputStream certInputstream) {
        InputStream is = null;
        SSLSocketFactory ssf=null;
//        if (!ipAndHosts.contains(urlString)){
//            return null;
//        }
        // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
        HttpsURLConnection httpsConn = null;
        try {
            URL myURL = new URL(urlString);
            httpsConn = (HttpsURLConnection) myURL.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ssf=generateSSLSocketFactory(certInputstream);
        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv=HttpsURLConnection.getDefaultHostnameVerifier();

                Boolean result=hv.verify(hostname,session);
                return result;
            }
        });
        httpsConn.setSSLSocketFactory(ssf);
        return httpsConn;
    }

    /**
     *
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr,Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     *
     * @param certInputstream
     * @return
     */
    public static SSLSocketFactory generateSSLSocketFactory(InputStream certInputstream) {
        try {
            // is = new FileInputStream("anchor.crt");
            CertificateFactory cf = CertificateFactory.getInstance("X.509"); //cert格式的文件需要再加个参数provider值为"BC"
            Certificate ca = cf.generateCertificate(certInputstream);
            //Log.d(TAG, ((X509Certificate)ca).getPublicKey().toString());

            // 创建 Keystore 包含我们的证书
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null);
            keyStore.setCertificateEntry("anchor", ca);

            // 创建一个 TrustManager 仅把 Keystore 中的证书 作为信任的锚点
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            // 用 TrustManager 初始化一个 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            return sslContext.getSocketFactory();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }
}
