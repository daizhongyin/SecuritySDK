//
// Created by Lin on 2017/11/13.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include "BasicInfor.h"
#ifndef SECURITYSDK_SIMULATORDETECTED_H
#define SECURITYSDK_SIMULATORDETECTED_H

int getWeightByPipeFile();
int getWeightByUniqueFile();
int getWeightByQeumDreiver();
int getWeightByBlueStack();
int getWeightByWlan0Mac(JNIEnv *env,jobject mContext);
int getWeightByCpu();

int getWeightByIMEI(JNIEnv *env,jobject mContext);
int getWeightByIMSI(JNIEnv *env,jobject mContext);
int getWeightByTelephoneNum(JNIEnv *env,jobject mContext);
int getWeightBySerial(JNIEnv *env);
int getWeightByBoard(JNIEnv *env);
int getWeightByBootLoader(JNIEnv *env);
int getWeightByHardware(JNIEnv *env);
int getWeightByProduct(JNIEnv *env);
int getWeightByModel(JNIEnv *env);
int getWeightByBrand(JNIEnv *env);
int getWeightByDevice(JNIEnv *env);

int simulatorDetected(JNIEnv *env,jobject mContext,const int threshold);

#endif //SECURITYSDK_SIMULATORDETECTED_H


