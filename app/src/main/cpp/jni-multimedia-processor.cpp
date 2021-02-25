#include <jni.h>
#include <string>

extern "C" {
#include "include/libavcodec/avcodec.h"
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_getCodecInfo(
        JNIEnv *env,
        jclass) {
    std::string codecInfo = "FFmpeg codecConfig \n";
    const char *codecConfig = avcodec_configuration();
    codecInfo.append(codecConfig);
    return env->NewStringUTF(codecInfo.c_str());
}
