package saurav.chandra.listentothis;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import saurav.chandra.listentothis.R;

public class activity_splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (!((ListenToThis)getApplicationContext()).isNameSet()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent Intent = new Intent(getApplicationContext(), activity_setup.class);
                    startActivity(Intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            Intent Intent = new Intent(getApplicationContext(), activity_main.class);
            startActivity(Intent);
            finish();
        }

    }

}