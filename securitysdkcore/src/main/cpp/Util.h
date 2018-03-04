//
// Created by plldzy on 17-11-15.
//基础工具类:1)字符串jstring处理；2)生成错误信息；3)byte数组和char数组转换;4）log信息输出
//
#include <jni.h>
#include <string>
#include <stdlib.h>
#include <android/log.h>
#ifndef SECURITYSDK_BASICUTIL_H
#define SECURITYSDK_BASICUTIL_H

#define LOG_TAG    "security-sdk-core" // 这个是自定义的LOG的标识
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)

char* jstringToChar(JNIEnv* env, jstring jstr);
jstring charsTojstring(JNIEnv* env, char* str);
jstring getErrorInfo(JNIEnv* env,char *file, const char *func, int line, char *msg);
char* ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray);

#endif //SECURITYSDK_BASICUTIL_H

