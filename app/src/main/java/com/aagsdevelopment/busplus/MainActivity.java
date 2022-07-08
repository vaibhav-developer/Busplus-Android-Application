package com.aagsdevelopment.busplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN=3000;
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo,slogan,sponsor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();
//      Animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

//      Hooks
        image=findViewById(R.id.splashImage);
        logo=findViewById(R.id.tvlogo);
        slogan=findViewById(R.id.tvslogan);
        sponsor=findViewById(R.id.sponsorAbhinav);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);
        sponsor.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(MainActivity.this,LoginAs.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}