package com.tonygui.multimedia.videosniffer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tonygui.multimedia.io.AssetFileUtils;
import com.tonygui.multimedia.jnihub.CodecListener;
import com.tonygui.multimedia.jnihub.NativeMultiMediaProcessor;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(NativeMultiMediaProcessor.getCodecInfo());
        NativeMultiMediaProcessor.initCodec();
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String filePath = AssetFileUtils.getAssetVideoPath(MainActivity.this, "video_source.mp4");
                        NativeMultiMediaProcessor.softdecode(filePath, new CodecListener() {
                            @Override
                            public void onFrameAvailable(int width, int height, byte[] yPixel, byte[] uPixel, byte[] vPixel) {
                                if (width != height) {
                                    int y = 9;
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
