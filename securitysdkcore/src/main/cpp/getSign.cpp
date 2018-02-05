#include "getSign.h"
using namespace std;

//签名信息
const char *UserSign_sha1="E83868175D274D31A62D3BC667D1289748E3779D";
const char hexcode[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
extern "C"
char* getAppSignSha1(JNIEnv *env, jobject context_object){
    //上下文对象
    jclass context_class = env->GetObjectClass(context_object);

    //反射获取PackageManager
    //context.getPackageManager()
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(context_object, methodId);
    if (package_manager == NULL) {
        LOGD("package_manager is NULL!!!");
        return NULL;
    }

    //反射获取包名
    //context.getPackageName()
    methodId = env->GetMethodID(context_class, "getPackageName", "()Ljava/lang/String;");
    jstring package_name = (jstring)env->CallObjectMethod(context_object, methodId);
    if (package_name == NULL) {
        LOGD("package_name is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(context_class);

    //获取PackageInfo对象
    //PackageManager.getPackageInfo(Sting, int)
    jclass pack_manager_class = env->GetObjectClass(package_manager);
    methodId = env->GetMethodID(pack_manager_class, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    env->DeleteLocalRef(pack_manager_class);
    jobject package_info = env->CallObjectMethod(package_manager, methodId, package_name, 0x40);
    if (package_info == NULL) {
        LOGD("getPackageInfo() is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(package_manager);

    //获取签名信息
    //PackageInfo.signatures[0]
    jclass package_info_class = env->GetObjectClass(package_info);
    jfieldID fieldId = env->GetFieldID(package_info_class, "signatures", "[Landroid/content/pm/Signature;");
    env->DeleteLocalRef(package_info_class);
    jobjectArray signature_object_array = (jobjectArray)env->GetObjectField(package_info, fieldId);
    if (signature_object_array == NULL) {
        LOGD("signature is NULL!!!");
        return NULL;
    }
    jobject signature_object = env->GetObjectArrayElement(signature_object_array, 0);
    env->DeleteLocalRef(package_info);

    //签名信息转换成sha1值
    //Signature.toByteArray()
    jclass signature_class = env->GetObjectClass(signature_object);
    methodId = env->GetMethodID(signature_class, "toByteArray", "()[B");
    env->DeleteLocalRef(signature_class);
    jbyteArray signature_byte = (jbyteArray) env->CallObjectMethod(signature_object, methodId);

//    //new ByteArrayInputStream
//    jclass byte_array_input_class=env->FindClass("java/io/ByteArrayInputStream");
//    methodId=env->GetMethodID(byte_array_input_class,"<init>","([B)V");
//    jobject byte_array_input=env->NewObject(byte_array_input_class,methodId,signature_byte);
//
//    // /CertificateFactory.getInstance("X.509")
//
//    jclass certificate_factory_class=env->FindClass("java/security/cert/CertificateFactory");
//    methodId=env->GetStaticMethodID(certificate_factory_class,"getInstance","(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;");
//    jstring x_509_jstring=env->NewStringUTF("X.509");
//    jobject cert_factory=env->CallStaticObjectMethod(certificate_factory_class,methodId,x_509_jstring);
//
//    //certFactory.generateCertificate(byteIn);
//    methodId=env->GetMethodID(certificate_factory_class,"generateCertificate",("(Ljava/io/InputStream;)Ljava/security/cert/Certificate;"));
//    jobject x509_cert=env->CallObjectMethod(cert_factory,methodId,byte_array_input);
//    env->DeleteLocalRef(certificate_factory_class);
//
//    //cert.getEncoded()
//    jclass x509_cert_class=env->GetObjectClass(x509_cert);
//    methodId=env->GetMethodID(x509_cert_class,"getEncoded","()[B");
//    jbyteArray cert_byte=(jbyteArray)env->CallObjectMethod(x509_cert,methodId);
//    env->DeleteLocalRef(x509_cert_class);


    //MessageDigest.getInstance("SHA1")
    jclass message_digest_class=env->FindClass("java/security/MessageDigest");
    methodId=env->GetStaticMethodID(message_digest_class,"getInstance","(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jstring sha1_jstring=env->NewStringUTF("SHA1");
    jobject sha1_digest=env->CallStaticObjectMethod(message_digest_class,methodId,sha1_jstring);

    //sha1.digest (signature_byte)
    methodId=env->GetMethodID(message_digest_class,"digest","([B)[B");
    jbyteArray sha1_byte=(jbyteArray)env->CallObjectMethod(sha1_digest,methodId,signature_byte);
    env->DeleteLocalRef(message_digest_class);

    //转换成char
    jsize array_size=env->GetArrayLength(sha1_byte);
    jbyte* sha1 =env->GetByteArrayElements(sha1_byte,NULL);

    char *hex_sha=new char[array_size*2+1];
    for (int i = 0; i <array_size ; ++i) {
        hex_sha[2*i]=hexcode[((unsigned char)sha1[i])/16];
        hex_sha[2*i+1]=hexcode[((unsigned char)sha1[i])%16];
    }
    hex_sha[array_size*2]='\0';

    LOGD("hex_sha %s ",hex_sha);
    return hex_sha;
}


extern "C"
jboolean checkValidity(JNIEnv *env, char *Appsha1){
    //比较签名
    if (strcmp(Appsha1,UserSign_sha1)==0)
    {
        LOGD("验证成功");
        return true;
    }
    LOGD("验证失败");
    return false;
}

//jboolean checkHooking(){
//
//}
//void getimagebase() {
//    pid_t pid =  getpid();
//    char fileName[256] = {0};
//    sprintf(fileName, "/proc/%d/maps", pid);
////    FILE* fd = fopen(fileName, "r");
//////    FILE *fw = fopen("/sdcard/maps.txt", "w");
////    if (fd != NULL)
////    {
////        char buff[2048+1];
////        while(fgets(buff, 2048, fd) != NULL)
////        {
//////            int ilen = strlen(buff);
//////            fwrite(buff, ilen, 1, fw);
////            LOGD("get Module : %s", buff);
////        }
////
////
////    }
//////    fclose(fw);
////    fclose(fd);
//    ifstream in(fileName);
//    string line;
//    char* charline;
//    if(in) // 有该文件
//    {
//        while (getline (in, line)) // line中不包括每行的换行符
//        {
//            LOGD(line.data());
//            int length=strlen(line.data());
//            charline=new char(length+1);
//            strcpy(charline,line.data());
//            strtok(charline, "");
//        }
//    }
//
//}