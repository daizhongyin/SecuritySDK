package com.nstl.securitysdkcore.crypt;

import android.util.Base64;

import com.nstl.securitysdkcore.crypt.bean.EncryptData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
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
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by plldzy on 17-11-15.
 */

public class CryptAndHttps {
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA"; //签名算法
    //初始化AES的key
    private static byte[] getAESKey(){
        return null;
    }
    // AES加密
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

    //用AES加密内容，然后非对称公钥加密AES的key
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
    //RSA公钥加密
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
    //ECC公钥加密
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
    //RSA签名校验
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
    //ECC签名校验
    public static boolean verifyByECC(byte[] data, String eccPublicKey, String sign){
        return false;
    }
    //生成信息摘要算法，type=1表示sha256,type=2表示sha512
    public static String getHashBySHA(String sourceStr, int type){
        return null;
    }
    /**
     * 根据客户端自定义证书，进行Https安全通信
     * @param urlString             需要通信的url
     * @param ipAndHosts            证书所签发的域名或者IP地址列表,进行hostnameverify的验证
     * @param certInputstream      证书的输入流
     */
    public static HttpsURLConnection getHttpsUrlConnection(String urlString, List<String> ipAndHosts, InputStream certInputstream){
        return null;
    }

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

}
