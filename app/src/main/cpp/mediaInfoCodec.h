//
// Created by Tony Gui on 2021-02-25.
//

extern "C" {
#include "include/libavcodec/avcodec.h"
}

#ifndef VIDEOSNIFFER_MEDIAINFOCODEC_H
#define VIDEOSNIFFER_MEDIAINFOCODEC_H

class BaseMediaInfoCodec {
public:
    BaseMediaInfoCodec();

    ~BaseMediaInfoCodec();

    char *getCodecConfig();
};

class SimpleMediaInfoCodec : BaseMediaInfoCodec {
public:
    SimpleMediaInfoCodec();
    ~SimpleMediaInfoCodec();
    void parseVideoSorce(std::string *videoSource);
    void decodeH264(JNIEnv *env, char *filePath, jobject listener);
    static int register_Codec_listener(JNIEnv* env);
};

#endif //VIDEOSNIFFER_MEDIAINFOCODEC_H
