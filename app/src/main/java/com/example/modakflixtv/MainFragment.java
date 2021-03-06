package com.example.modakflixtv;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BrowseSupportFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 2;
    public static int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    public static BackgroundManager mBackgroundManager;
    public static boolean resumeFlag = false;

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        startUI();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        startUI();
        /*if(resumeFlag)
        {
            resumeFlag = false;
            LoadCard ld = new LoadCard();
            ld.execute();
        }*/
    }

    private void startUI()
    {
        if(resumeFlag)
        {
            resumeFlag = false;
            LoadCard ld = new LoadCard();
            ld.execute();
        }
        else
        {
            prepareBackgroundManager();

            setupUIElements();

            LoadCard ld = new LoadCard();
            ld.execute();

            setupEventListeners();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {

        List<Movie> moviesList = MovieList.setupMovies(MiscOperations.get_movies_list, MiscOperations.title_index_show);
        List<Movie> resumeList = MovieList.setupMovies(MiscOperations.get_shows_watched_path, MiscOperations.position);

        //Collections.shuffle(moviesList);

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        // Resume

        if(resumeList.size() > 0)
        {
            ArrayObjectAdapter listRowAdapterResume = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < resumeList.size(); j++) {
                listRowAdapterResume.add(resumeList.get(j));
            }
            HeaderItem headerResume = new HeaderItem(0, MovieList.MOVIE_CATEGORY[0]);
            rowsAdapter.add(new ListRow(headerResume, listRowAdapterResume));
        }

        // Movies
        //Collections.shuffle(moviesList);

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
        for (int j = 0; j < moviesList.size(); j++) {
            listRowAdapter.add(moviesList.get(j));

        }
        HeaderItem header = new HeaderItem(1, MovieList.MOVIE_CATEGORY[1]);
        rowsAdapter.add(new ListRow(header, listRowAdapter));


        //TV Shows
        HeaderItem gridHeader = new HeaderItem(2, "TV Shows");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.coming_soon));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter(rowsAdapter);
            }
        });

        // Menu
        HeaderItem gridHeader1 = new HeaderItem(2, "Menu");

        GridItemPresenter mGridPresenter1 = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter1 = new ArrayObjectAdapter(mGridPresenter1);
        gridRowAdapter1.add(getResources().getString(R.string.profiles));
        gridRowAdapter1.add(getString(R.string.reset_profile));
        gridRowAdapter1.add(getResources().getString(R.string.contact_us));
        rowsAdapter.add(new ListRow(gridHeader1, gridRowAdapter1));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter(rowsAdapter);
            }
        });
    }

    private void prepareBackgroundManager() {

        if(mBackgroundManager != null)
            mBackgroundManager.release();
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getContext(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));

        setTitle(MiscOperations.username); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getContext(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getContext(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent searchAct = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchAct);
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable,
                                                @Nullable Transition<? super Drawable> transition) {
                        mBackgroundManager.setDrawable(drawable);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
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
                    menuActions((String) item);
                }
            }
        }
    }

    private void menuActions(String option)
    {
        if(option.equalsIgnoreCase(getResources().getString(R.string.profiles)))
        {
            Intent intent = new Intent(getActivity(), ProfilesActivity.class);
            intent.putExtra("startFlag", "1");
            getActivity().finish();
            startActivity(intent);
        }
        else if(option.equalsIgnoreCase(getResources().getString(R.string.reset_profile)))
        {
            resetProfile("Do you really want to reset all you watching history?");
        }
        else if(option.equalsIgnoreCase(getResources().getString(R.string.contact_us)))
        {
            showContactUs("Developer - Sourav Modak\nContact Number - +91 9500166574\nE-Mail - official.srv.modak@gmail.com");
        }

    }

    public void resetProfile(String Message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
        alertDialogBuilder.setMessage(Message);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResetProfile resetProfile = new ResetProfile();
                resetProfile.execute(MiscOperations.reset_profile);

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }

    private class ResetProfile extends AsyncTask<String, Void, Integer> {

        @SuppressLint("ResourceType")
        @Override
        protected Integer doInBackground(String... url) {
            MiscOperations.pingDataServer(MiscOperations.handleUrl(url[0]));
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();


        }
    }

    public void showContactUs(String Message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
        alertDialogBuilder.setMessage(Message);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof Movie) {
                mBackgroundUri = ((Movie) item).getBackgroundImageUrl();
                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void setOnClickListener(ViewHolder holder, View.OnClickListener listener) {
            Toast.makeText(getContext(), "Sourav Modak", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            loadRows();
            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

}