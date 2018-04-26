package com.nstl.securitysdkcore.UpgradeTool;

/**
 * Created by Lin on 2018/4/26.
 */

public class UserConfig {
    private String save_app_name;
    private String save_app_location;
    private boolean enableNetWrok;
    private boolean isShowInTitle;
    private String show_name;
    private String show_description;

    public String getSave_app_name(){
        return save_app_name;
    }
    public String getSave_app_location(){
        return save_app_location;
    }
    public boolean getEnableNetWork(){
        return enableNetWrok;
    }
    public boolean getIsShowInTitle(){
        return isShowInTitle;
    }


}
