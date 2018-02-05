//
// Created by Lin on 2017/11/13.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include "Util.h"
#ifndef SECURITYSDK_BASICINFO_H
#define SECURITYSDK_BASICINFO_H

#define CPU_FILE_PATH "/proc/cpuinfo"


jstring getIMEI(JNIEnv *env,jobject mContext);
jstring getIMSI(JNIEnv *env,jobject mContext);
jstring getTelephoneNum(JNIEnv *env,jobject mContext);
jstring getSerial(JNIEnv *env);
jstring getBoard(JNIEnv *env);
jstring getBootLoader(JNIEnv *env);
jstring getBrand(JNIEnv *env);
jstring getDevice(JNIEnv *env);
jstring getHardware(JNIEnv *env);
jstring getModel(JNIEnv *env);
jstring getProduct(JNIEnv *env);
jstring getCpuInfo(JNIEnv *env);
jstring getWlan0Mac(JNIEnv *env,jobject mContext);


#endif //SECURITYSDK_BASICINFO_H
