package com.example.modakflixtv;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    static JSONObject jsonData = null, oldData = null;

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public View findViewById(int id)
    {
        View view = getActivity().findViewById(id);
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
    }

    private void initialise()
    {

        jsonData = EditProfileActivity.jsonData;
        oldData = EditProfileActivity.oldData;

        refreshData(jsonData.toString(), "0");
        TextView saveBtn = getActivity().findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(jsonData);
                Intent intent = new Intent(getActivity(), ProfilesActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
        MainActivity.setHighlightView(saveBtn, getActivity());

        TextView discard = getActivity().findViewById(R.id.discardBtn);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardProcess();
            }
        });
        MainActivity.setHighlightView(discard, getActivity());
    }

    public void discardProcess()
    {
        if(determineChanges(jsonData))
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
            alertDialogBuilder.setMessage("Do you want to discard changes?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), ProfilesActivity.class);
                    getActivity().finish();
                    startActivity(intent);
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
            Intent intent = new Intent(getActivity(), ProfilesActivity.class);
            getActivity().finish();
            startActivity(intent);
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
    public Boolean determineDuplicate(JSONObject data, String name)
    {
        try {
            JSONArray arr = data.getJSONArray("cards");
            for(int i = 0; i<arr.length(); i++)
            {
                JSONObject card = arr.getJSONObject(i);
                if((card.getString("first_name")+" "+card.getString("last_name")).trim().equals(name))
                {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject removeJsonObject(JSONObject data, String name)
    {
        try {
            JSONArray arr = data.getJSONArray("cards");
            for(int i = 0; i<arr.length(); i++)
            {
                JSONObject card = arr.getJSONObject(i);
                if((card.getString("first_name")+" "+card.getString("last_name")).trim().equals(name))
                {
                    arr.remove(i);
                    break;
                }
            }
            data.put("cards", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    public void deleteProfileFuntion(View view, JSONObject data)
    {
        try {
            oldData = new JSONObject(data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView profile = view.findViewById(R.id.accountName);
        String name = profile.getText().toString().trim();

        jsonData = removeJsonObject(data, name);
    }
    public void saveProfile(JSONObject data)
    {
        SaveData sd = new SaveData();
        sd.execute(MiscOperations.add_profile, data.toString());
    }

    public void refreshData(String data, String processFlag)
    {
        LoadCard ld = new LoadCard();
        ld.execute(data);
    }

    public JSONObject addProfile(String name, JSONObject data)
    {
        if(!name.isEmpty() && !determineDuplicate(data, name))
        {
            try {

                String firstName = name.split(" ")[0];
                String lastName1 = "";
                if (name.split(" ").length > 1)
                {
                    lastName1 = name.split(" ")[1];
                }
                else
                {
                    lastName1 = "";
                }
                final String lastName = lastName1;
                String username = firstName.toLowerCase() + lastName.toLowerCase();
                JSONObject object = new JSONObject();
                object.put("first_name", firstName);
                object.put("last_name", lastName);
                object.put("username", username);
                object.put("Serial", "-1");
                JSONArray arr = data.getJSONArray("cards");
                arr.put(object);
                data.put("cards", arr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Name is existing or not valid");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alertDialogBuilder.show();
        }
        return data;
    }
    private static String temp = "";
    public String DisplayProfileDialog(String Message, JSONObject jsonObject)
    {
        final JSONObject finalJsonData = jsonObject;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
        alertDialogBuilder.setMessage(Message);
        EditText input = new EditText(getActivity());
        input.setHint("Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(input);


        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString().trim();
                if(!name.isEmpty())
                {
                    JSONObject finalJsonData1= addProfile(name, finalJsonData);
                    jsonData = finalJsonData1;
                    refreshData(finalJsonData1.toString(), "1");
                }
            }
        });
        alertDialogBuilder.show();
        return null;
    }

    private class SaveData extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... data) {
            MiscOperations.pingDataServerPost(MiscOperations.handleUrl(data[0]), data[1]);
            return null;
        }
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... json) {

            String processFlag = "0";
            if(json.length > 1)
                processFlag = json[1];
            if(processFlag.equals(0))
            {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(json[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject resumeData = null;

                JSONObject finalJsonData = jsonData;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray show = null;
                        try {
                            show = finalJsonData.getJSONArray("cards");
                        } catch (Exception e) {
                            e.printStackTrace();
                            //showServerDialog("Server not found! Want to input local IP?");
                        }
                        LinearLayout c = getActivity().findViewById(R.id.linearLayoutEdit1);
                        //c.removeAllViews();
                        if(show!=null)
                        {
                            for (int i = 0; i < show.length(); ) {
                                LinearLayout linearLayout2 = new LinearLayout(getActivity());

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                                        tv.setText(card.getString("first_name") + " " + card.getString("last_name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    linearLayout2.addView(view);

                                }
                                // c.removeView(loading);
                                c.addView(linearLayout2);
                            }
                        }
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profiles, null);
                        @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                        view.setId(uniqueId);
                        TextView tv = view.findViewById(R.id.accountName);
                        tv.setText("+");
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override

                            public void onClick(View v) {
                                DisplayProfileDialog("Enter the details", finalJsonData);
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

                        c.addView(view);
                    }
                });
            }
            else
            {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(json[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject resumeData = null;

                final JSONObject finalJsonData = jsonData;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray show = null;
                        try {
                            show = finalJsonData.getJSONArray("cards");
                        } catch (Exception e) {
                            e.printStackTrace();
                            //showServerDialog("Server not found! Want to input local IP?");
                        }
                        LinearLayout c = getActivity().findViewById(R.id.linearLayoutEdit1);
                        c.removeAllViews();
                        if(show!=null)
                        {
                            for (int i = 0; i < show.length(); ) {
                                LinearLayout linearLayout2 = new LinearLayout(getActivity());

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                                        tv.setText(card.getString("first_name") + " " + card.getString("last_name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    linearLayout2.addView(view);

                                }
                                // c.removeView(loading);
                                c.addView(linearLayout2);
                            }
                        }
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profiles, null);
                        @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                        view.setId(uniqueId);
                        TextView tv = view.findViewById(R.id.accountName);
                        tv.setText("+");
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override

                            public void onClick(View v) {
                                DisplayProfileDialog("Enter the details", finalJsonData);
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
                        c.addView(view);
                    }
                });
            }
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
}