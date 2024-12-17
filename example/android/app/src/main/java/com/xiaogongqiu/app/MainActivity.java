package com.xiaogongqiu.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取 Intent 数据
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null){
            Log.e("com.xiaogongqiu.app", "-------onCreate: "+extras.toString());
        }

    }
}
