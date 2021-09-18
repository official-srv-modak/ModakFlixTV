package com.example.modakflixtv;

import static com.example.modakflixtv.MiscOperations.ipInfoFilePath;
import static com.example.modakflixtv.MiscOperations.ip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        intialise();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public static String fetchIpDataFromFile(String ipInfoFilePath)
    {
        File file = null;
        String ipFromFile = "modakflix.com";
        if(!ipInfoFilePath.isEmpty())
        {
            file = new File(ipInfoFilePath);
        }
        if(file != null)
        {
            try {
                ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
                ipFromFile = (String) objIn.readObject();
                objIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.e("IP", ipFromFile);
        return ipFromFile;
    }

    public static void writeIpData(String ipInfoFilePath, String ipData)
    {
        File file = new File(ipInfoFilePath);
        if(ipData.contains("http://"))
            ipData = ipData.split("http://")[1];
        else if(ipData.contains("https://"))
            ipData = ipData.split("https://")[1];
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(file));
            objOut.writeObject(ipData);
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void intialise()
    {
        ipInfoFilePath = getContext().getFilesDir().getAbsolutePath() + "/ipInfo.dat";
        ip = fetchIpDataFromFile(ipInfoFilePath);

        LoadCard ld = new LoadCard();
        ld.execute(MiscOperations.get_profiles);
    }
    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject jsonData = null, resumeData = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                jsonData = MiscOperations.getDataFromServer(urls[0]);
            }
            final JSONObject finalJsonData = jsonData;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray show = null;
                    try {
                        show = finalJsonData.getJSONArray("cards");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showServerDialog("Server not found! Want to input local IP?");
                    }
                    LinearLayout c = getActivity().findViewById(R.id.linearLayout2);
                    if(show!=null)
                    {
                        for (int i = 0; i < show.length(); ) {
                            LinearLayout linearLayout2 = new LinearLayout(getActivity());

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            linearLayout2.setLayoutParams(params);
                            linearLayout2.setWeightSum(2f);
                            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout2.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

                            for (int j = 0; j < 2 && i<show.length(); j++) {
                                JSONObject card = null;
                                try {
                                    card = show.getJSONObject(i++);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.profiles, null);
                                @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                                view.setId(uniqueId);

                                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @SuppressLint("UseCompatLoadingForDrawables")
                                    @Override
                                    public void onFocusChange(View view, boolean b) {
                                        if(view.getBackground()==null)
                                        {
                                            view.setBackground(getResources().getDrawable(R.drawable.block_white));
                                        }
                                        else
                                        {
                                            view.setBackground(null);
                                        }
                                    }
                                });

                                TextView tv = view.findViewById(R.id.accountName);
                                try {
                                    MiscOperations.username = card.getString("first_name") + " " + card.getString("last_name");
                                    tv.setText(MiscOperations.username);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                linearLayout2.addView(view);


                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().overridePendingTransition(0, R.anim.fade_out);
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra("username", tv.getText());
                                        intent.putExtra("ip", ip);
                                        getActivity().finish();
                                        startActivity(intent);
                                    }
                                });
                            }
                            // c.removeView(loading);
                            c.addView(linearLayout2);
                        }
                    }
                    /*View view = LayoutInflater.from(getActivity()).inflate(R.layout.profiles, null);
                    @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                    view.setId(uniqueId);
                    TextView tv = view.findViewById(R.id.accountName);
                    tv.setText("Edit Profiles");

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().overridePendingTransition(0, R.anim.fade_out);
                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                            intent.putExtra("profileData", finalJsonData.toString());
                            startActivity(intent);
                        }
                    });
                    view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if(view.getBackground()==null)
                            {
                                view.setBackground(getResources().getDrawable(R.drawable.block_white));
                            }
                            else
                            {
                                view.setBackground(null);
                            }
                        }
                    });
                    c.addView(view);*/
                    TextView loading = getActivity().findViewById(R.id.loading);
                    loading.setText("Use mobile app to edit profiles.");
                }
            });

            return null;
        }
        ProgressDialog progressDialog = new ProgressDialog(getActivity());;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();


            int temp = 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
                progressDialog.dismiss();
        }*/

    }

    public void showServerDialog(String Message)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(Message);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Movies.writeIpData("192.168.0.4");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Enter Server's Local IP Address");
                final EditText input = new EditText(getActivity());
                input.setHint("IP Address");
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeIpData(ipInfoFilePath, input.getText().toString().trim());
                        ip = input.getText().toString().trim();
                        Intent intent = new Intent(getActivity(), Splashscreen.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        alertDialogBuilder.show();

    }
}