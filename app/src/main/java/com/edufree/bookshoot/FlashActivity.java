package com.edufree.bookshoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class FlashActivity extends AppCompatActivity {

    CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        mTimer=new CountDownTimer(3500,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent i=new Intent(FlashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        };

        mTimer.start();
    }


}
