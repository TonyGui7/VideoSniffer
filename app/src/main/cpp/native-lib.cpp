#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_getCodecInfo(
        JNIEnv *env,
        jclass) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
