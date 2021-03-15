//
// Created by Tony Gui on 2021-02-25.
//
#include <string>
#include "mediaInfoCodec.h"

extern "C" {
#include "include/libavcodec/avcodec.h"
#include "include/libavutil/avutil.h"
#include "libavformat/avformat.h"
#include "libavdevice/avdevice.h"
#include "libswscale/swscale.h"
}

BaseMediaInfoCodec::BaseMediaInfoCodec() {

}

BaseMediaInfoCodec::~BaseMediaInfoCodec() {

}

char *BaseMediaInfoCodec::getCodecConfig() {
    std::string codecInfo = "FFmpeg config info: \n";
    codecInfo.append(avcodec_configuration());
    return const_cast<char *>(codecInfo.c_str());
}

SimpleMediaInfoCodec::SimpleMediaInfoCodec() {

}

SimpleMediaInfoCodec::~SimpleMediaInfoCodec() {

}

void SimpleMediaInfoCodec::parseVideoSorce(std::string *videoSource) {
    if(videoSource == NULL) {
        return;
    }

    avcodec_register_all();
}




