package com.nstl.securitysdkcore.Logger;
import java.util.HashMap;
import java.util.Map;

public class Track {
    public static final String METHOD_SAFEZIP = "safezip";
    public static final String METHOD_SAFEWEBVIEW = "safeWebView";
    public static final String METHOD_SAFEUPDATE = "update";
    public static final String METHOD_NATIVECODE_DEBUGPRESENT = "debugPresent";
    public static final String METHOD_NATIVECODE_RUNINEMU = "runInEmulator";
    public static final String METHOD_NATIVECODE_REPACKAGE = "repackage";
    public static final String METHOD_NATIVECODE_DETECTINJECT = "detectInject";
    public static final String METHOD_NATIVECODE_ISROOT = "isRoot";


    public static  int METHOD_SAFE_ZIP = 0;


    public static Map<String, Integer> trace(){
        Map<String,Integer> map = new HashMap<>();
        map.put(METHOD_SAFEZIP,METHOD_SAFE_ZIP);
        return map;
    }

}
