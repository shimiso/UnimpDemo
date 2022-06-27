package com.example.unimpdemo;

import android.content.Intent;
import android.os.Bundle;

import com.example.unimpdemo.base.BaseActivity;
import com.example.unimpdemo.widget.CountDownTextView;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullFullscreen();//全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        CountDownTextView countDownTextView = findViewById(R.id.tv_skip);
        countDownTextView.start(() -> {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        });
    }
}