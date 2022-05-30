#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_ottu_payment_network_RetrofitClientInstance_getLink(JNIEnv *env, jobject thiz) {
    // TODO: implement getLink()
    std::string hello = "https://ksa.ottu.dev/b/";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_ottu_payment_network_RetrofitClientInstance_getLinkPg(JNIEnv *env, jobject thiz) {

    std::string hello = "https://pg.ottu.dev/pg/";
    return env->NewStringUTF(hello.c_str());
}