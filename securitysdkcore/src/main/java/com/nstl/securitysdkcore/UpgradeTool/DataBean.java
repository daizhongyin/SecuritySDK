package com.nstl.securitysdkcore.UpgradeTool;

/**
 * Created by Lin on 2017/12/27.
 */

public class DataBean {
    private String description;
    private String downUrl;
    private String isForce;
    private String version;
    private int vercode;
    private String verifyCode;              //下载内容的MD5等验证值
    private String singnedVerifyCode;       //RSA私钥签名后的验证值(需要业务方在应用中传递公钥验证改值是否合法)

    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDownUrl(String downUrl){
        this.downUrl = downUrl;
    }
    public String getDownUrl(){
        return downUrl;
    }
    public void setIsForce(String isForce){
        this.isForce = isForce;
    }
    public String getIsForce(){
        return this.isForce;
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return this.version;
    }
    public int getVercode(){
        return this.vercode;
    }
    public void setVercode(int vercode){
        this.vercode = vercode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getSingnedVerifyCode() {
        return singnedVerifyCode;
    }

    public void setSingnedVerifyCode(String singnedVerifyCode) {
        this.singnedVerifyCode = singnedVerifyCode;
    }
}
