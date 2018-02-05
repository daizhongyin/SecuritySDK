//
// Created by ffthy on 21/11/2017.
//
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <sys/ptrace.h>
#include <unistd.h>
#include <sys/types.h>
#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include "Util.h"
#include <jni.h>
#ifndef SECURITYSDK_GETSIGN_H
#define SECURITYSDK_GETSIGN_H



extern "C"
char* getAppSignSha1(JNIEnv *env, jobject context_object);
extern "C"
jboolean checkValidity(JNIEnv *env, char *Appsha1);

#endif //SECURITYSDK_GETSIGN_H
