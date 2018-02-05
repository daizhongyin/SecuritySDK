#include <jni.h>
#include <string>
#include <unistd.h>
#include "Util.h"
#include "SimulatorDetected.h"
#include "getSign.h"
#include "InjectDetected.h"
extern "C"
JNIEXPORT jint JNICALL
Java_com_nstl_securitysdkcore_NativeCoreUtil_debugPresent(JNIEnv *env, jobject instance) {

    // TODO
    int BUF_SIZE = 1024;
    char file_path[1024] = "\0";
    char buffer[1024] = "\0";
    //get app pid
    int pid = getpid();
    sprintf(file_path, "/proc/%d/status", pid);
    FILE *fp = fopen(file_path,"r");
    if(fp == NULL){
        return -2;
    }else{
        while(fgets(buffer,BUF_SIZE,fp)){
            if (strncmp(buffer, "TracerPid", 9) == 0) {
                int statue = atoi(&buffer[10]);
                if (statue != 0) {
                    fclose(fp);
                    //返回错误的返回值
                    return -1;
                }
                break;
            }
        }
        fclose(fp);
    }
    return 0;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_nstl_securitysdkcore_NativeCoreUtil_runInEmulator(JNIEnv *env, jobject instance,
                                                           jobject mContext) {

    // TODO
    return simulatorDetected(env,mContext,30);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_nstl_securitysdkcore_NativeCoreUtil_rePackage(JNIEnv *env, jobject instance,jobject mContext,
                                                       jobject verifyListener) {

    // TODO
    char *sha1 = getAppSignSha1(env, mContext);
    jboolean result = checkValidity(env,sha1);
    jclass jhandlerClass = env->GetObjectClass(verifyListener);
    jmethodID SuccessMethodId = env->GetMethodID(jhandlerClass, "onVerifySuccess", "()V");
    jmethodID FailMethodId = env->GetMethodID(jhandlerClass, "onVerifyFail", "()V");

    if(result){
        env->CallVoidMethod(verifyListener, SuccessMethodId);

    }else{
        env->CallVoidMethod(verifyListener, FailMethodId);
    }



}

extern "C"
JNIEXPORT jint JNICALL
Java_com_nstl_securitysdkcore_NativeCoreUtil_detectInject(JNIEnv *env, jobject instance,jobject mContext) {

    // TODO
       int i =getimagebase();

        return i;


}
extern "C"
JNIEXPORT jint JNICALL Java_com_nstl_securitysdkcore_NativeCoreUtil_isExisSUAndExecute(JNIEnv *env, jobject instance){
    int result = 0;
    char* path[5] = {"/system/bin/su", "/system/xbin/su", "/system/xbin/busybox","/system/bin/busybox", "/data/local/tmp/busybox"};
    for (int i = 0; i < 5; ++i) {
        if (access(path[i],F_OK|X_OK) == 0)             //判断上述文件是否存在并且拥有可执行的权限
            result ++;
    }
    return result;

}