package in.assignmentsolutions.momskart;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;
import in.assignmentsolutions.momskart.utils.Typewriter;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;
    //private static String MY_PREFS_NAME = "";
    String isOverLay = "false";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;

        Typewriter tv =  findViewById(R.id.tv_description);
        tv.setCharacterDelay(150);
        tv.animateText("Handcrafted by Moms of India");

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

//                if(SharedPreferencesHelper.getAttb(getApplicationContext(), Constants.SHARED_PREF, "FIRST_TIME").equals("-1")) {
//                    SharedPreferencesHelper.setAttb(getApplicationContext(), Constants.SHARED_PREF, "FIRST_TIME", "TRUE");
//                    startActivity(new Intent(mContext, IntroActivity.class));
//                    finish();
//                }

                TaskStackBuilder.create(getApplicationContext())
                        .addNextIntent(new Intent(mContext, MainActivity.class))
                        .addNextIntent(new Intent(mContext, IntroActivity.class))
                        .startActivities();

//                Intent intent = new Intent(mContext, MainActivity.class);
//                startActivity(intent);
//                finishAffinity();
            }
        }, SPLASH_TIME_OUT);

    }

    public void gotoHomeActivity() {
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
    }
}
