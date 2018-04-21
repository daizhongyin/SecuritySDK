//
// Created by Lin on 2017/11/13.
//
#include "BasicInfor.h"
#include <string>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include "Util.h"

/*
 * 获取TelephoneManager
 * @param env
 * @param mContext 传入的Context信息
 * @return 失败返回NULL
 */
jobject getTelephonyManager(JNIEnv *env,jobject mContext)
{
    //获取系统的context
    jclass system_context = env->FindClass("android/content/Context");
    if(env->ExceptionCheck()){
        env->ExceptionDescribe();
        env->ExceptionClear();
        return NULL;
    }
    if(system_context == 0){
        return NULL;
    }
    jmethodID METHOD_getSystemService = env->GetMethodID(system_context,"getSystemService","(Ljava/lang/String;)Ljava/lang/Object;");
    if(env->ExceptionCheck()){
        env->ExceptionDescribe();
        env->ExceptionClear();
        return NULL;
    }
    if(METHOD_getSystemService == 0){
        return NULL;
    }
    jfieldID FIELD_TELEPHONY_SERVICE = env->GetStaticFieldID(system_context,"TELEPHONY_SERVICE","Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionDescribe();
        env->ExceptionClear();
        return NULL;
    }
    if(FIELD_TELEPHONY_SERVICE == 0){
        return NULL;
    }

    jstring Value_TELEPHONY_SERVICE = (jstring) env->GetStaticObjectField(system_context, FIELD_TELEPHONY_SERVICE );
    if(env->ExceptionCheck()){
        env->ExceptionDescribe();
        env->ExceptionClear();
        return NULL;
    }
    if(Value_TELEPHONY_SERVICE == 0 ){
        return NULL;
    }

    jobject Object_telephone_manager = env->CallObjectMethod(mContext, METHOD_getSystemService ,Value_TELEPHONY_SERVICE);
    if(env->ExceptionCheck()){
        env->ExceptionDescribe();
        env->ExceptionClear();
        return NULL;
    }
    if(Object_telephone_manager == 0 ){
        return NULL;
    }

    return Object_telephone_manager;

}

/*
 * 获取获取IMEI 模拟器为000000000000000
 * @param env
 * @param mContext 传入的Context信息
 * @return 失败返回error信息
 */
jstring getIMEI(JNIEnv *env,jobject mContext)
{

    jobject OBJECT_telephone_manager = getTelephonyManager(env,mContext);
    if(OBJECT_telephone_manager == NULL){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_telephone_manager get failed");
    }
    jclass CLASS_TelephonyManager = env->FindClass("android/telephony/TelephonyManager");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed Excption");
    }
    if(CLASS_TelephonyManager == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed");
    }
    jmethodID METHOD_getDeviceId = env->GetMethodID(CLASS_TelephonyManager,"getDeviceId","()Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getDeviceId get failed Excption");
    }
    if(METHOD_getDeviceId == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getDeviceId get failed");
    }
    jstring retStr = (jstring) env->CallObjectMethod(OBJECT_telephone_manager, METHOD_getDeviceId);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"IMEI get failed Excption");
    }
    return retStr;

}
/*
 * 获取IMSI 模拟器为：310260000000000
 * @param env
 * @param mContext 传入的Context信息
 * @return 失败返回error信息
 */
jstring getIMSI(JNIEnv *env,jobject mContext)
{

    jobject OBJECT_telephone_manager = getTelephonyManager(env,mContext);
    if(OBJECT_telephone_manager == NULL ){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_telephone_manager get failed");
    }
    jclass CLASS_TelephonyManager = env->FindClass("android/telephony/TelephonyManager");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed Excption");
    }
    if(CLASS_TelephonyManager == 0 ){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed ");
    }

    jmethodID METHOD_getSubscriberId = env->GetMethodID(CLASS_TelephonyManager,"getSubscriberId","()Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getSubscriberId get failed Excption");
    }
    if(METHOD_getSubscriberId == 0 ){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getSubscriberId get failed");
    }

    jstring retStr = (jstring) env->CallObjectMethod(OBJECT_telephone_manager, METHOD_getSubscriberId);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"IMEI Value get failed Excption");
    }
    return retStr;
}
/*
 * 获取本机号码
 * @param env
 * @param mContext 传入的Context信息
 * @return 失败返回errror信息
 */
jstring getTelephoneNum(JNIEnv *env,jobject mContext)
{

    jobject OBJECT_telephone_manager = getTelephonyManager(env,mContext);
    if(OBJECT_telephone_manager == NULL){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_telephone_manager get failed");
    }
    jclass CLASS_TelephonyManager = env->FindClass("android/telephony/TelephonyManager");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed Excption");
    }
    if(CLASS_TelephonyManager == 0 ){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_Telephonymanager get failed");
    }
    jmethodID METHOD_getLine1Number = env->GetMethodID(CLASS_TelephonyManager,"getLine1Number","()Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getLine1Number get failed Excption");
    }
    if(METHOD_getLine1Number == 0 ){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getLine1Number get failed");
    }
    jstring retStr = (jstring)env->CallObjectMethod(OBJECT_telephone_manager,METHOD_getLine1Number);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"LineNumber value get failed Excption");
    }
    return retStr;

}


/*
 * 获取基本信息所需的Build
 * @param env
 * @param value 传入的属性值
 * @return 失败返回error信息
 */
jstring getBuildBasicInfo(JNIEnv *env,const char *value)
{
    jclass CLASS_ANDROID_OS_BUILD = env->FindClass("android/os/Build");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"android/os/Build get failed Excption");
    }
    if(CLASS_ANDROID_OS_BUILD == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"android/os/Build get failed");
    }

    jfieldID FIELD_VALUE = env->GetStaticFieldID(CLASS_ANDROID_OS_BUILD,value,"Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"FIELD_VALUE get failed Excption");
    }
    if(FIELD_VALUE == 0 ){
        char msg[100] = "\0";
        sprintf(msg,"android/os/Build key = %s get Field failed",value);
        jstring msgRetStr = getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,msg);
        return msgRetStr;
    }
    jstring retStr = (jstring) env->GetStaticObjectField(CLASS_ANDROID_OS_BUILD, FIELD_VALUE);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        char msg[100] = "\0";
        sprintf(msg,"android/os/Build key = %s get value failed",value);
        jstring msgRetStr = getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,msg);
        return msgRetStr;
    }
    return retStr;
}
/*
 * 获取Serial
 * @param env
 * @return 失败返回error信息
 */
jstring getSerial(JNIEnv *env)
{
    return getBuildBasicInfo(env,"SERIAL");
}
/*
 * 获取Board
 * @param env
 * @return 失败返回error信息
 */
jstring getBoard(JNIEnv *env)
{
    return getBuildBasicInfo(env,"BOARD");
}
/*
 * 获取BootLoader
 * @param env
 * @return 失败返回error信息
 */
jstring getBootLoader(JNIEnv *env)
{
    return getBuildBasicInfo(env,"BOOTLOADER");
}
/*
 * 获取Brand
 * @param env
 * @return 失败返回error信息
 */
jstring getBrand(JNIEnv *env)
{
    return getBuildBasicInfo(env,"BRAND");
}
/*
 * 获取Device
 * @param env
 * @return 失败返回error信息
 */
jstring getDevice(JNIEnv *env)
{
    return getBuildBasicInfo(env,"DEVICE");
}
/*
 * 获取HardWare
 * @param env
 * @return 失败返回error信息
 */
jstring getHardware(JNIEnv *env)
{
    return getBuildBasicInfo(env,"HARDWARE");
}
/*
 * 获取Model
 * @param env
 * @return 失败返回error信息
 */
jstring getModel(JNIEnv *env)
{
    return getBuildBasicInfo(env,"MODEL");
}
/*
 * 获取Product
 * @param env
 * @return 失败返回error信息
 */
jstring getProduct(JNIEnv *env)
{
    return getBuildBasicInfo(env,"PRODUCT");
}
/*
 * 获取Board
 * @param env
 * @return 失败返回error信息
 */
jstring getCpuInfo(JNIEnv *env)
{
    FILE *fp = fopen(CPU_FILE_PATH,"r");
    if(fp == NULL){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CPU_FILE open failed");
    }
    char ret[1025] = "\0";
    int size = fread(ret,1024,1,fp);
    if(size == -1){
        fclose(fp);
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CPU_FILE fread failed");
    }
    jstring retStr = charsTojstring(env,ret);
    fclose(fp);
    return retStr;
}
/*
 * 获取wlan的MAC地址
 * @param env
 * @return 失败返回error信息
 */
jstring getWlan0Mac(JNIEnv *env,jobject mContext)
{
    jclass system_context = env->FindClass("android/content/Context");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"Context get failed Excption");
    }
    if(system_context == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"Context get failed");
    }

    jfieldID FIELD_WIFI_SERVICE = env->GetStaticFieldID(system_context,"WIFI_SERVICE","Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"FIELD_WIFI_SERVICE get failed Excption");
    }
    if(FIELD_WIFI_SERVICE == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"FIELD_WIFI_SERVICE get failed");
    }

    jstring VALUE_WIFI_SERVICE = (jstring) env->GetStaticObjectField(system_context, FIELD_WIFI_SERVICE);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"VALUE_WIFI_SERVER get failed Excption");
    }
    if(VALUE_WIFI_SERVICE == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"VALUE_WIFI_SERVICE get failed ");
    }

    jmethodID METHOD_getSystemService = env->GetMethodID(system_context,"getSystemService","(Ljava/lang/String;)Ljava/lang/Object;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getSystemService get failed Excption");
    }
    if(METHOD_getSystemService == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_getSystemService get failed" );
    }
    jobject OBJECT_WIFI_MANAGER = env->CallObjectMethod(mContext,METHOD_getSystemService,VALUE_WIFI_SERVICE);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_WIFI_MANAGER get failed Excption");
    }
    if(OBJECT_WIFI_MANAGER == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_WIFI_MANAGER get failed ");
    }
    jclass CLASS_WIFI_MANAGER = env->GetObjectClass(OBJECT_WIFI_MANAGER);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_WIFI_MANAGER get failed Excption");
    }
    if(CLASS_WIFI_MANAGER == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_WIFI_MANAGER get failed");
    }
    jmethodID METHOD_WIFI_INFO = env->GetMethodID(CLASS_WIFI_MANAGER,"getConnectionInfo","()Landroid/net/wifi/WifiInfo;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_WIFI_INFO get failed Excption");
    }
    if(METHOD_WIFI_INFO == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_WIFI_INFO get failed ");
    }
    jobject OBJECT_WIFI_INFO = env->CallObjectMethod(OBJECT_WIFI_MANAGER,METHOD_WIFI_INFO);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_WIFI_INFO  get failed Excption");
    }
    if(OBJECT_WIFI_INFO  == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"OBJECT_WIFI_INFO  get failed ");
    }

    jclass CLASS_WIFI_INFO = env->GetObjectClass(OBJECT_WIFI_INFO );
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_WIFI_INFO get failed Excption");
    }
    if(CLASS_WIFI_INFO == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"CLASS_WIFI_INFO get failed ");
    }
    jmethodID METHOD_MAC_getMacAddress = env->GetMethodID(CLASS_WIFI_INFO , "getMacAddress", "()Ljava/lang/String;");
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_MAC_getMacAddress get failed Excption");
    }
    if(METHOD_MAC_getMacAddress == 0){
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"METHOD_MAC_getMacAddress get failed ");
    }
    jstring mac_str = (jstring) env->CallObjectMethod(OBJECT_WIFI_INFO , METHOD_MAC_getMacAddress);
    if(env->ExceptionCheck()){
        env->ExceptionClear();
        return getErrorInfo(env,__FILE__, __FUNCTION__, __LINE__,"mac_str get failed Excption");
    }
    if(mac_str ==  NULL){
        return charsTojstring(env,"no_data");
    }
    return mac_str;
}

