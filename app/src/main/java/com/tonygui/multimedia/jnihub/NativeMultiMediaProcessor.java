package com.tonygui.multimedia.jnihub;

public class NativeMultiMediaProcessor {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-video-sniffer-lib");
    }

    public native static String getCodecInfo();

    public native static String parseVideoSource(String source);


    public native static void initCodec();

    public native static void softdecode(String source, CodecListener listener);
}
