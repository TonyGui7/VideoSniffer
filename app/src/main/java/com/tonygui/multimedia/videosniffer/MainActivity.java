package com.tonygui.multimedia.videosniffer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tonygui.multimedia.io.AssetFileUtils;
import com.tonygui.multimedia.jnihub.CodecListener;
import com.tonygui.multimedia.jnihub.NativeMultiMediaProcessor;
import com.tonygui.multimedia.player.PlayerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(NativeMultiMediaProcessor.getCodecInfo());

        final PlayerView playerView = findViewById(R.id.anim_palyer_view);
//        NativeMultiMediaProcessor.initCodec();
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = AssetFileUtils.getAssetVideoPath(MainActivity.this, "video_source.mp4");
                playerView.startPlay(filePath);
            }
        });
    }
}
