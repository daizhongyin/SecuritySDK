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
    private String md5SignCode;

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
    public void setMd5SignCode(String md5SignCode){
        this.md5SignCode = md5SignCode;
    }
    public String getMd5SignCode(){
        return this.md5SignCode;
    }
}
