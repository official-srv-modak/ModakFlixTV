package com.example.modakflixtv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new MainFragment())
                    .commitNow();
        }

        setUser();
    }

    private void setUser() {
        MiscOperations.username = getIntent().getStringExtra("username");
    }

    public static void setHighlightView(View view, Activity activity)
    {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view.getBackground()==null)
                {
                    view.setBackground(activity.getResources().getDrawable(R.drawable.block_white));
                }
                else
                {
                    view.setBackground(null);
                }
            }
        });
    }
}