package com.example.modakflixtv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class ProfilesActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profiles_act, new ProfileFragment())
                    .commitNow();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String startFlag = "0";
        if(getIntent().hasExtra("startFlag"))
        {
            startFlag = getIntent().getStringExtra("startFlag");
        }
        if(startFlag.equals("0"))
            overridePendingTransition(0, 0);
        else
            overridePendingTransition(R.anim.fade_in, 0);


        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 27, 0, 0);
        AppBarLayout ap = findViewById(R.id.appBarLayout);
        ap.setLayoutParams(lp);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}