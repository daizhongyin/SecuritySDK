package com.nstl.securitysdkcore.crypt.bean;

/**
 * Created by plldzy on 17-11-15.
 */
//加密后的key和内容
public class EncryptData {
    private byte[] encryKey;
    private byte[] encryContent;

    public byte[] getEncryKey() {
        return encryKey;
    }

    public void setEncryKey(byte[] encryKey) {
        this.encryKey = encryKey;
    }

    public byte[] getEncryContent() {
        return encryContent;
    }

    public void setEncryContent(byte[] encryContent) {
        this.encryContent = encryContent;
    }
}
