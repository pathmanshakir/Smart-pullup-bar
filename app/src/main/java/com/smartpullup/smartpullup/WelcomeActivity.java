package com.smartpullup.smartpullup;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class WelcomeActivity extends LoginActivity {

    Button login_bttn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        BackgroundAnimation();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


    }

    private void BackgroundAnimation(){
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.welcome_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    public void GoToLogin(View view){
        Intent intentConnect = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intentConnect);
        //finish();

    }

    public void GoToCrateAccount(View view){
        Intent intentConnect = new Intent(WelcomeActivity.this, RegisterActivity.class);
        startActivity(intentConnect);
        //finish();

    }
}
