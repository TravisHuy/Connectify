package com.nhathuy.connectify;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView animationView;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth=FirebaseAuth.getInstance();

        animationView=findViewById(R.id.splash_anim);
        Window w=getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mAuth.getCurrentUser()!=null){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
                else{
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    super.onAnimationEnd(animation);
                    View sharedView=animationView;
                    String transName="slap_anim";
                    ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this,sharedView,transName);
                    startActivity(intent,activityOptions.toBundle());
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
    }
}