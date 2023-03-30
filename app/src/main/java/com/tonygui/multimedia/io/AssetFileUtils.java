package com.tonygui.multimedia.io;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetFileUtils {
    private static String assetPath = "";

    public static String getAssetVideoPath(Context context, String assetName) {
        if (!TextUtils.isEmpty(assetPath)) {
            return assetPath;
        }
        BufferedInputStream isBuffer = null;
        BufferedOutputStream osBuffer = null;
        try {
            InputStream inputStream = context.getResources().getAssets().open(assetName);
            assetPath = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/" + assetName;
            File file = new File(assetPath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            isBuffer = new BufferedInputStream(inputStream);
            osBuffer = new BufferedOutputStream(fileOutputStream);
            byte[] data = new byte[1024];
            for (int len; (len = isBuffer.read(data)) != -1; ) {
                fileOutputStream.write(data, 0, len);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isBuffer != null) {
                    isBuffer.close();
                }
                if (osBuffer != null) {
                    osBuffer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return assetPath;
    }
}
