#include <jni.h>
#include <string>

extern "C" {
#include "include/libavcodec/avcodec.h"
#include "mediaInfoCodec.h"
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_getCodecInfo(
        JNIEnv *env,
        jclass) {
    BaseMediaInfoCodec *baseMediaInfoCodec = new BaseMediaInfoCodec();
    baseMediaInfoCodec->getCodecConfig();
    return env->NewStringUTF(baseMediaInfoCodec->getCodecConfig());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_parseVideoSource(
        JNIEnv *env,
        jclass, jstring videoSorce) {
    if (videoSorce == NULL) {
        return NULL;
    }
    jboolean *isCopy = JNI_FALSE;
    const char *source = env->GetStringUTFChars(videoSorce, isCopy);

    std::string result = "";
    result.append(source);
    SimpleMediaInfoCodec *codec = new SimpleMediaInfoCodec();
    codec->parseVideoSorce(&result);
    return env->NewStringUTF(result.c_str());
}
