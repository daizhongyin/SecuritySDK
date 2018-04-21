//
// Created by ffthy on 21/11/2017.
//

#include <jni.h>
#ifndef SECURITYSDK_GETSIGN_H
#define SECURITYSDK_GETSIGN_H



extern "C"
char* getAppSignSha1(JNIEnv *env, jobject context_object);
char* getAppSignSha1(JNIEnv *env, jobject context_object,jstring pkgname);
extern "C"
jboolean checkValidity(JNIEnv *env, char *Appsha1);

#endif //SECURITYSDK_GETSIGN_H
