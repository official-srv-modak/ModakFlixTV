package com.example.modakflixtv;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splashscreen extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.splash_screen, new SplashscreenFragment())
                    .commitNow();
        }
        setSplashscreenAnimation();
    }

    private void setSplashscreenAnimation()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this, ProfilesActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);


        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        ImageView logo = findViewById(R.id.logo);
        logo.setAnimation(slideIn);

        TextView tv = findViewById(R.id.tag);
        Animation slideLt = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        tv.setAnimation(slideLt);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(R.id.tag);
                Animation slideRt = AnimationUtils.loadAnimation(Splashscreen.this, R.anim.slide_down);
                tv.setAnimation(slideRt);
            }
        }, 3500);
    }
}