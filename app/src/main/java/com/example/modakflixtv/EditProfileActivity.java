package com.example.modakflixtv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_profile_act, new EditProfileFragment())
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
        ImageButton discard = findViewById(R.id.discardBtn);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardProcess();
            }
        });
    }
    @Override
    public void onBackPressed() {
        discardProcess();
    }

    public void discardProcess()
    {
        if(determineChanges(jsonData))
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
        else
        {
            Intent intent = new Intent(this, ProfilesActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public Boolean determineChanges(JSONObject data)
    {
        try {
            JSONArray arrNew = data.getJSONArray("cards");
            JSONArray arrOld = oldData.getJSONArray("cards");
            if(arrNew.length()!=arrOld.length())
                return true;
            else
                return false;
            /*for(int i = 0; i<arr.length(); i++)
            {
                JSONObject card = arr.getJSONObject(i);
                if(card.getString("Serial").trim().equals("-1"))
                {
                    return true;
                }
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}