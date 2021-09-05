package com.example.modakflixtv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    static String username = "Sourav Modak";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();

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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    private void setupUI()
    {
        ImageView backButton = getActivity().findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        EditText searchTextBox = getActivity().findViewById(R.id.searchTextBox);
        searchTextBox.requestFocus();
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if(!searchTextBox.getText().toString().isEmpty())
                    {
                        LoadCard ld = new LoadCard();
                        ld.execute(MiscOperations.search_shows+"?query="+searchTextBox.getText()+"&username="+username);
                    }
                    else
                    {
                        LinearLayout linearLayout1 = getActivity().findViewById(R.id.linearLayout1);
                        linearLayout1.removeAllViews();
                    }

                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject result = null, resumeData = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                result = MiscOperations.getDataFromServer(urls[0]);
            }
            JSONObject finalResult = result;
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI

                    if(finalResult != null) {   //json object of search is not null
                        /*resultV.setText("");
                        try {
                            resultV.setText(finalResult.getString("cards"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        LinearLayout linearLayout1 = getActivity().findViewById(R.id.linearLayout1);
                        linearLayout1.removeAllViews();
                        try {
                            JSONArray show = finalResult.getJSONArray("cards");
                            List<Integer> idList = new ArrayList<Integer>();
                            for(int i = 0; i < show.length(); i++)
                            {
                                JSONObject card = show.getJSONObject(i);
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_result_elements, null);
                                @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                                view.setId(uniqueId);
                                idList.add(uniqueId);

                                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                                String album_art_path = card.getString("album_art_path");
                                if(!album_art_path.isEmpty())
                                    Glide.with(getActivity()).load(album_art_path).into(imageView);

                                TextView tv = (TextView) view.findViewById(R.id.showNameSearch);
                                CharSequence name = card.getString("name");
                                if(!name.toString().isEmpty())
                                    tv.setText(name);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        /*Intent intent = new Intent(getActivity(), Description.class);
                                        intent.putExtra("description", card.toString());
                                        intent.putExtra("username", username);
                                        //intent.putExtra("url", get_shows_watched_path);
                                        int pos = 0;
                                        String resumeFlag = "0";
                                        if(card.has("position"))
                                            resumeFlag = "1";
                                        else
                                            resumeFlag = "0";
                                        intent.putExtra("resumeFlag", resumeFlag);
                                        SearchActivity.this.startActivityForResult(intent, 1);*/
                                    }
                                });


                                linearLayout1.addView(view);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        // show nothing found
                    }
                }
            });


            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}