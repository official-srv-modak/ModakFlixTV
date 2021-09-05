package com.example.modakflixtv;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.DetailsSupportFragmentBackgroundController;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsSupportFragment {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_RENT = 2;
    private static final int ACTION_BUY = 3;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;

    static  int durFromMx=0, posFromMx=0;

    static boolean resumeFlag = false;

    static String username = "Sourav Modak", title = "";

    private Movie mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private DetailsSupportFragmentBackgroundController mDetailsBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsSupportFragmentBackgroundController(this);


        mSelectedMovie =
                (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        if(mSelectedMovie.getTitle().contains("-"))
        {
            title = mSelectedMovie.getTitle().split("-")[1].trim();
            posFromMx = mSelectedMovie.getResumePos();
            durFromMx = mSelectedMovie.getDuration();
        }
        else
            title = mSelectedMovie.getTitle();

        intialiseUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(resumeFlag)
        {
            intialiseUI();
            resumeFlag = false;
        }

    }

    private void intialiseUI()
    {
        if(resumeFlag)
        {
            mSelectedMovie.setTitle(MiscOperations.resumeString(posFromMx, durFromMx, title));
        }
        if (mSelectedMovie != null) {
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupRelatedMovieListRow();
            setAdapter(mAdapter);
            initializeBackground(mSelectedMovie);
            setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
    private void initializeBackground(Movie data) {
        mDetailsBackground.enableParallax();
        Glide.with(getActivity())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .load(data.getBackgroundImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {
                        mDetailsBackground.setCoverBitmap(bitmap);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.default_background));
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(mSelectedMovie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable,
                                                @Nullable Transition<? super Drawable> transition) {
                        Log.d(TAG, "details overview card image url ready: " + drawable);
                        row.setImageDrawable(drawable);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        String title = mSelectedMovie.getTitle();

        if(title.contains("Resume") && (title.contains("Hr") || title.contains("min")))
        {
            actionAdapter.add(
                    new Action(
                            ACTION_WATCH_TRAILER,
                            getResources().getString(R.string.resume)
                    ));
            row.setActionsAdapter(actionAdapter);
        }
        else
        {
            actionAdapter.add(
                    new Action(
                            ACTION_WATCH_TRAILER,
                            getResources().getString(R.string.play)
                    ));
        /*actionAdapter.add(
                new Action(
                        ACTION_RENT,
                        getResources().getString(R.string.rent_1),
                        getResources().getString(R.string.rent_2)));
        actionAdapter.add(
                new Action(
                        ACTION_BUY,
                        getResources().getString(R.string.buy_1),
                        getResources().getString(R.string.buy_2)));*/
            row.setActionsAdapter(actionAdapter);
        }


        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.selected_background));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    /*Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
                    startActivity(intent);*/
                    int pos = mSelectedMovie.getResumePos();
                    if(pos >= 60000)
                        startMxPlayer(mSelectedMovie.getVideoUrl(), pos);
                    else
                        startMxPlayer(mSelectedMovie.getVideoUrl(), 1000);
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void startMxPlayer(String videoUrl, int pos1)
    {
        String appPackageName = "com.mxtech.videoplayer.ad";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.mxtech.videoplayer.ad");
            intent.setClassName("com.mxtech.videoplayer.ad", "com.mxtech.videoplayer.ad.ActivityScreen");
            Uri videoUri = Uri.parse(videoUrl);
            intent.setDataAndType(videoUri, "application/x-mpegURL");


            //intent.putExtra("position", 0000);
            intent.setPackage("com.mxtech.videoplayer.ad"); // com.mxtech.videoplayer.pro
            intent.putExtra("position", pos1);
            byte decoder = 2;
            //intent.putExtra("decode_mode", decoder);
            intent.putExtra("fast_mode", true);
            intent.putExtra("return_result", true);
            startActivityForResult(intent, 1);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "MX Player not installed. Install MX Player" , Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)  // -1 RESULT_OK : Playback was completed or stopped by user request.
        {
            //Activity.RESULT_CANCELED: User canceled before starting any playback.
            //RESULT_ERROR (=Activity.RESULT_FIRST_USER): Last playback was ended with an error.

            if (data.getAction().equals("com.mxtech.intent.result.VIEW")) {
                //data.getData()
                PostProcess p = new PostProcess();
                p.execute(data);
            } else if (data.getAction().equals("modakflix_player_current_pos")) {
                //data.getData()
                PostProcess p = new PostProcess();
                p.execute(data);
            }
        }
    }

    private void setupRelatedMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies)};
        List<Movie> list = MovieList.getList();

        Collections.shuffle(list);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int j = 0; j < list.size(); j++) {
            listRowAdapter.add(list.get(j));
        }

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {

            if (item instanceof Movie) {
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie);

                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                DetailsActivity.SHARED_ELEMENT_NAME)
                                .toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }

    private class PostProcess extends AsyncTask<Intent, Void, Integer> {
        protected Integer doInBackground(Intent... data) {
            doPostProcess(data[0]);

            return 0;
        }

        /*ProgressDialog progressDialog = new ProgressDialog(Description.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
        }*/
    }
    private void doPostProcess(Intent data)
    {
        int pos = data.getIntExtra("position", -1); // Last playback position in milliseconds. This extra will not exist if playback is completed.
        int dur = data.getIntExtra("duration", -1); // Duration of last played video in milliseconds. This extra will not exist if playback is completed.
        String cause = data.getStringExtra("end_by"); //  Indicates reason of activity closure.
        Uri uri = data.getData();
        String name = "";
        durFromMx = dur;
        posFromMx = pos;
        mSelectedMovie.setDuration(durFromMx);
        mSelectedMovie.setResumePos(posFromMx);
        resumeFlag = true;
        if(pos != -1 && dur != -1)
        {
            try {
                name = URLDecoder.decode(uri.toString().split("/")[uri.toString().split("/").length - 2], "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                double rem = 0;
                rem = dur - pos;
                rem = (rem/dur)*100;
                if (rem >= 5)
                {
                    MiscOperations.pingDataServer(MiscOperations.record_position_path+"?username="+username+"&show="+ URLDecoder.decode(uri.toString(), "UTF-8")+"&pos="+pos+"&duration="+dur+"&cause="+cause+"&name="+name);
                }

                else
                    MiscOperations.pingDataServer(MiscOperations.delete_position_path+"?username="+username+"&show="+name);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            MainFragment.resumeFlag = true;
            /*Button openWith = getActivity().findViewById(R.id.playWithBtn);
            int rem = dur - pos;
            rem /= 1000;
            int mins = rem/60;
            int hrs = mins/60;
            if(hrs > 0)
            {
                mins = mins%60;
                openWith.setText("Resume "+hrs+" hour "+mins+" min(s) left");
            }
            else
            {
                openWith.setText("Resume "+mins+" min(s) left");
            }*/
        }
    }
}