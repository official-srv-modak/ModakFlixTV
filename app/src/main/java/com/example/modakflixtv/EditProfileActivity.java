package com.example.modakflixtv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends FragmentActivity {

    static JSONObject jsonData = null, oldData = null;
    EditProfileFragment child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        child = new EditProfileFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_profile_act, child)
                    .commitNow();
        }
        initialise();
    }
    private void initialise()
    {
        try {
            jsonData = new JSONObject(getIntent().getStringExtra("profileData"));
            oldData = new JSONObject(getIntent().getStringExtra("profileData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        determineChangesAlert();
    }

    public void determineChangesAlert()
    {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
            alertDialogBuilder.setMessage("Do you want to discard changes?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(EditProfileActivity.this, ProfilesActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialogBuilder.show();
    }
}