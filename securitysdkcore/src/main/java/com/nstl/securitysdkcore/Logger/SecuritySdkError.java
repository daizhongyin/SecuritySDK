package com.nstl.securitysdkcore.Logger;

/**
 * Created by Lin on 2018/5/7.
 */

public enum SecuritySdkError {

    FILE_OPEN_ERROR(10000, "文件打开错误"),
    NETWORK_ERROR(10001,"网络异常错误");


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
