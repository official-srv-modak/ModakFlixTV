package com.example.modakflixtv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

                                        Movie movie = new Movie();
                                        movie.setBackgroundImageUrl(album_art_path);
                                        movie.setCardImageUrl(album_art_path);
                                        movie.setTitle(name.toString());

                                        try {
                                            movie.setResumePos(Integer.parseInt(card.getString("position")));
                                            movie.setDuration(Integer.parseInt(card.getString("duration")));
                                            movie.setVideoUrl(card.getString("url").toString());
                                            movie.setDescription(card.getString("des"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        searchElementClick(movie, imageView);
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

        private void searchElementClick(Movie movie, ImageView imageView)
        {
            Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
            detailsIntent.putExtra(DetailsActivity.MOVIE, movie);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    imageView,
                    DetailsActivity.SHARED_ELEMENT_NAME)
                    .toBundle();
            getActivity().startActivity(detailsIntent, bundle);
        }

        private final class ItemViewClickedListener implements OnItemViewClickedListener {

            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {

                if (item instanceof Movie) {
                    Movie movie = (Movie) item;
                    Log.d("Search Item", "Item: " + item.toString());
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.MOVIE, movie);

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsActivity.SHARED_ELEMENT_NAME)
                            .toBundle();
                    getActivity().startActivity(intent, bundle);
                } else if (item instanceof String) {
                    if (((String) item).contains(getString(R.string.error_fragment))) {
                        Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}