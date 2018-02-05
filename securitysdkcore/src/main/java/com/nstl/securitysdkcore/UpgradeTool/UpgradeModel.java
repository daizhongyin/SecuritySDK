package com.nstl.securitysdkcore.UpgradeTool;

/**
 * Created by LIN on 2017/12/27.
 */

public class UpgradeModel {
    private int code; //
    private String msg; //反序列化之后的信息
    private DataBean dataBean; //apk的相关信息

    public int getCode(){
        return code;
    }
    public void setCode(int code){
        this.code = code;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public DataBean getDataBean(){
        return this.dataBean;
    }
    public void setDataBean(DataBean dataBean){
        this.dataBean = dataBean;
    }
}
