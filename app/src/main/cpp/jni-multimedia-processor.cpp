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

extern "C" JNIEXPORT void JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_softdecode(
        JNIEnv *env,
        jclass, jstring videoSource, jobject listener
        ) {

    char* filePath = const_cast<char *>(env->GetStringUTFChars(videoSource, NULL));
    SimpleMediaInfoCodec *codec = new SimpleMediaInfoCodec();
    codec->decodeH264(env, filePath, listener);
}

extern "C" JNIEXPORT void JNICALL
Java_com_tonygui_multimedia_jnihub_NativeMultiMediaProcessor_initCodec(JNIEnv *env,jclass){
    int ret = SimpleMediaInfoCodec::register_Codec_listener(env);
    if (ret == 0) {
        fprintf(stderr, "initCodec succeed!\n");
    } else{
        fprintf(stderr, "initCodec failed!\n");
    }
}
