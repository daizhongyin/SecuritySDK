//
// Created by Lin on 2017/11/13.
//
#include <string.h>
#include "SimulatorDetected.h"

/*
 * 获取模拟器特殊文件检测的权值
 * @return 失败返回0
 */
int getWeightByCpu(){
    FILE *fp = fopen(CPU_FILE_PATH,"r");
    if( fp == NULL ){
        return 0;
    }
    char line[1024] = "\0";
    ///文件包含函数
    while (fgets(line, 1024, fp)) {
        if (strstr(line,"Intel") || strstr(line,"ranchud") || strstr(line,"amd")) {
            //转化为数值型
            fclose(fp);
            return 15;
        }
    }
    fclose(fp);
    return 0;
}

/*
 * 获取模拟器特殊文件检测的权值
 * @return 失败返回0
 */
int getWeightByUniqueFile()
{
    //检测特殊文件
    char *qemu_path[3]={
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
    };
    for(int i = 0; i < 3; i ++){
        FILE *fp = fopen((const char *) qemu_path[i], "r");
        if(fp != NULL){
            fclose(fp);
            return 15;
        }
    }
    return 0;
}
/*
 * 获取模拟器驱动文件检测的权值
 * @return 失败返回0
 */
int getWeightByQeumDreiver()
{
    //模拟器驱动文件
    FILE *fp = fopen("/proc/tty/drivers","r");
    if( fp == NULL ){
        return 0;
    }
    char line[1024] = "\0";
    ///文件包含函数
    while (fgets(line, 1024, fp)) {
        if (strstr(line,"goldfish")) {
            //转化为数值型
            fclose(fp);
            return 15;
        }
    }
    fclose(fp);
    return 0;

}
/*
 * 获取pipe文件检测的权值
 * @return 失败返回0
 */
int getWeightByPipeFile()
{
    //检测pipe
    char *pipe[2] = {
            "/dev/socket/qemud","/dev/qemu_pipe"
    };
    for(int i = 0; i < 2; i ++){
        FILE *fp = fopen((const char *) pipe[i], "r");
        if(fp != NULL){
            fclose(fp);
            return 15;
        }
    }
    free(pipe);
    return 0;
}
/*
 * 获取蓝牙文件检测的权值
 * @return 失败返回0
 */
int getWeightByBlueStack()
{
    char *bluestacks_path[22]={
            "/data/app/com.bluestacks.appmart-1.apk", "/data/app/com.bluestacks.BstCommandProcessor-1.apk",
            "/data/app/com.bluestacks.help-1.apk", "/data/app/com.bluestacks.home-1.apk", "/data/app/com.bluestacks.s2p-1.apk",
            "/data/app/com.bluestacks.searchapp-1.apk", "/data/bluestacks.prop", "/data/data/com.androVM.vmconfig",
            "/data/data/com.bluestacks.accelerometerui", "/data/data/com.bluestacks.appfinder", "/data/data/com.bluestacks.appmart",
            "/data/data/com.bluestacks.appsettings", "/data/data/com.bluestacks.BstCommandProcessor", "/data/data/com.bluestacks.bstfolder",
            "/data/data/com.bluestacks.help", "/data/data/com.bluestacks.home", "/data/data/com.bluestacks.s2p", "/data/data/com.bluestacks.searchapp",
            "/data/data/com.bluestacks.settings", "/data/data/com.bluestacks.setup", "/data/data/com.bluestacks.spotlight", "/mnt/prebundledapps/bluestacks.prop.orig"
    };
    for(int i = 0; i < 22; i ++){
        FILE *fp = fopen((const char *) bluestacks_path[i], "r");
        if(fp != NULL){
            fclose(fp);
            return 0;
        }
    }
    return 15;
}
/*
 * 获取模拟器IMEI检测的权值
 * @param env
 * @param mContext
 * @return 失败返回0
 */
int getWeightByIMEI(JNIEnv *env,jobject mContext){
    jstring ret_str = getIMEI(env,mContext);
    if(strcmp(jstringToChar(env,ret_str),"000000000000000")){
        return -1;
    }else{
        return 5;
    }
}
/*
 * 获取模拟器IMSI检测的权值
 * @param env
 * @param mContext
 * @return 失败返回0
 */
int getWeightByIMSI(JNIEnv *env,jobject mContext){
    jstring ret_str = getIMSI(env,mContext);
    if(strcmp(jstringToChar(env,ret_str),"310260000000000")){
        return -1;
    }else{
        return 5;
    }
}
/*
 * 获取模拟器TelephoneNum检测的权值
 *  * @param env
 * @param mContext
 * @return 失败返回0
 */
int getWeightByTelephoneNum(JNIEnv *env,jobject mContext){
    jstring ret_str = getTelephoneNum(env,mContext);
    char *simulatorTelephoneNum[16] = {
        "15555215554","15555215556","15555215558","15555215560",
        "15555215562","15555215564", "15555215566","15555215568",
        "15555215570","15555215572", "15555215574","15555215576",
        "15555215578","15555215580","15555215582","15555215584"
    };
    for(int i = 0; i < 16;i ++){
        if(strcmp(simulatorTelephoneNum[i],jstringToChar(env,ret_str))){
            return 5;
        }
    }
    return 0;
}
/*
 * 获取模拟器Serial检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightBySerial(JNIEnv *env){
    jstring ret_str = getSerial(env);
    if(strcmp(jstringToChar(env,ret_str),"unknown") == 0){
        return 5;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Board检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByBoard(JNIEnv *env){
    jstring ret_str = getBoard(env);
    if(strcmp(jstringToChar(env,ret_str),"unknown") == 0){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Bootloader检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByBootLoader(JNIEnv *env){
    jstring ret_str = getBootLoader(env);
    if(strcmp(jstringToChar(env,ret_str),"unknown") == 0){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Hardware检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByHardware(JNIEnv *env){
    jstring ret_str = getHardware(env);
    if(strcmp(jstringToChar(env,ret_str),"goldfish") || strcmp(jstringToChar(env,ret_str),"ranchu")){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Hardware检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByDevice(JNIEnv *env){
    jstring ret_str = getDevice(env);
    if(strstr(jstringToChar(env,ret_str),"generic")){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Product检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByProduct(JNIEnv *env){
    jstring ret_str = getProduct(env);
    if(strstr(jstringToChar(env,ret_str),"sdk")){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Model检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByModel(JNIEnv *env){
    jstring ret_str = getModel(env);
    if(strstr(jstringToChar(env,ret_str),"sdk") || strstr(jstringToChar(env,ret_str),"SDK")){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Brand检测的权值
 * @param env
 * @return 失败返回0
 */
int getWeightByBrand(JNIEnv *env){
    jstring ret_str = getBrand(env);
    if(strstr(jstringToChar(env,ret_str),"generic") || strstr(jstringToChar(env,ret_str),"Android")){
        return 2;
    }else{
        return 0;
    }
}
/*
 * 获取模拟器Brand检测的权值
 * @param env
 * @param mContext
 * @return 失败返回0
 */
int getWeightByWlan0Mac(JNIEnv *env,jobject mContext){
    jstring retStr = getWlan0Mac(env,mContext);
    if(strcmp(jstringToChar(env,retStr),"no data")){
        return 10;
    }
    return 0;
}
/*
 * 判断是否为模拟器
 * @param env
 * @param mContext
 * @param 模拟器的阈值， >30 为模拟器，30>x>10 为疑似模拟器，<10为不是模拟器
 * @return  1 为 模拟器
 *          -1 为不是模拟器
 *          0 为疑似模拟器
 */
int simulatorDetected(JNIEnv *env,jobject mContext,const int threshold)
{
    //先判断蓝牙文件
    int weight_total = 0;
    weight_total += getWeightByBlueStack();
    if( weight_total >= threshold){
        return 1;
    }
    //pipe文件
    weight_total += getWeightByPipeFile();
    if(weight_total >= threshold){
        return 1;
    }
    //特殊文件
    weight_total += getWeightByUniqueFile();
    if(weight_total >= threshold){
        return 1;
    }
    //驱动文件
    weight_total += getWeightByQeumDreiver();
    if(weight_total >= threshold){
        return 1;
    }
    //Cpu信息
    weight_total += getWeightByCpu();
    if(weight_total >= threshold){
        return 1;
    }
    //waln0mac
    weight_total += getWeightByWlan0Mac(env,mContext);
    if(weight_total >= threshold){
        return 1;
    }
    //IMEI
    weight_total += getWeightByIMEI(env,mContext);
    if(weight_total >= threshold){
        return 1;
    }
    //IMSI
    weight_total += getWeightByIMSI(env,mContext);
    if(weight_total >= threshold){
        return 1;
    }
    //TELEPHONENUM
    weight_total += getWeightByTelephoneNum(env,mContext);
    if(weight_total >= threshold){
        return 1;
    }
    //SERIAL
    weight_total += getWeightBySerial(env);
    if(weight_total >= threshold){
        return 1;
    }
    //BOARD
    weight_total += getWeightByBoard(env);
    if(weight_total >= threshold){
        return 1;
    }
    //MODEL
    weight_total += getWeightByModel(env);
    if(weight_total >= threshold){
        return 1;
    }
    //product
    weight_total += getWeightByProduct(env);
    if(weight_total >= threshold){
        return 1;
    }
    //HARDWARE
    weight_total += getWeightByHardware(env);
    if(weight_total >= threshold){
        return 1;
    }
    //Brand
    weight_total += getWeightByBrand(env);
    if(weight_total >= threshold){
        return 1;
    }
    //Device
    weight_total += getWeightByDevice(env);
    if(weight_total >= threshold){
        return 1;
    }
    //Brand
    weight_total += getWeightByBootLoader(env);
    if(weight_total >= threshold){
        return 1;
    }
    if(threshold > weight_total > 10){
        return 0;
    }
    return -1;

}