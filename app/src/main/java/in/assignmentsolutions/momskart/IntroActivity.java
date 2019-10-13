package in.assignmentsolutions.momskart;

import android.Manifest;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro);

        if(SharedPreferencesHelper.getAttb(getApplicationContext(), Constants.SHARED_PREF, "FIRST_TIME").equals("-1")) {
            Toast.makeText(getApplicationContext(), SharedPreferencesHelper.getAttb(getApplicationContext(), Constants.SHARED_PREF, "FIRST_TIME"), Toast.LENGTH_SHORT).show();
            SharedPreferencesHelper.setAttb(getApplicationContext(), Constants.SHARED_PREF, "TRUE", "FIRST_TIME");
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimary)
                    .buttonsColor(R.color.colorAccent)
                    .image(agency.tango.materialintroscreen.R.drawable.ic_next)
                    .title("title 1")
                    .description("Description 1")
                    .build());

        /*
            ,
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("We provide solutions to make you love your work");
                    }
                }, "Work with love")
         */

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.endblue)
                    .buttonsColor(R.color.colorAccent)
                    .image(R.drawable.avatar)
                    .title("title 2")
                    .description("Description 2")
                    .build());

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorGrey)
                    .buttonsColor(R.color.colorAccent)
                    .image(R.drawable.momskart_logo)
                    .title("title 3")
                    .description("Description 3")
                    .build());

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorGrey)
                    .buttonsColor(R.color.colorAccent)
                    .image(R.drawable.momskart_logo)
                    .title("title 4")
                    .description("Description 4")
                    .build());
        }

        finishActivity(100);


    }
}
