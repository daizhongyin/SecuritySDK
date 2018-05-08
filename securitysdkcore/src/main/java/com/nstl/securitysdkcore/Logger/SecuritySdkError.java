package com.nstl.securitysdkcore.Logger;

import android.app.admin.*;

/**
 * Created by Lin on 2018/5/7.
 */

public enum SecuritySdkError{

    FILE_OPEN_ERROR(10000, "文件打开错误"),
    NETWORK_ERROR(10001,"网络异常错误"),

    //签名算法相关
    NO_SUCCH_ALGORITHM(10002, "没有此类算法"),
    SIGNATURE_ERROR(10002, "计算签名错误"),
    INVALID_KEY(10003, "无效的密钥");


    private String msg;
    private int code;

    private SecuritySdkError(int code,String msg)
    {
        this.code=code;
        this.msg=msg;
    }

    public String getMsg()
    {
        return this.msg;
    }
    public int getCode() {
        return this.code;
    }
}
