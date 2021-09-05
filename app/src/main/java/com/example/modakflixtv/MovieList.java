package com.example.modakflixtv;

import static com.example.modakflixtv.MainFragment.NUM_COLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class MovieList {
    public static final String MOVIE_CATEGORY[] = {
            "Resume watching",
            "Movies",
            "TV Shows",
    };

    private static List<Movie> list;
    private static long count = 0;

    public static List<Movie> getList() {
        if (list == null) {
            list = setupMovies(MiscOperations.get_movies_list, "name");
        }
        return list;
    }

    public static List<Movie> setupMovies(String url, String titleIndex) {
        list = new ArrayList<>();

        JSONObject showJSONList = MiscOperations.getDataFromServer(url);
        String title[] = getListsFromJSONObject(showJSONList, titleIndex);
        NUM_COLS = title.length;

        String description[] = getListsFromJSONObject(showJSONList, "des");;
        String videoUrl[] = getListsFromJSONObject(showJSONList, "url");
        String bgImageUrl[] = getListsFromJSONObject(showJSONList, "album_art_path");
        String cardImageUrl[] = getListsFromJSONObject(showJSONList, "album_art_path");

        for (int index = 0; index < cardImageUrl.length; ++index) {
            list.add(
                    buildMovieInfo(
                            title[index],
                            description[index],
                            videoUrl[index],
                            cardImageUrl[index],
                            bgImageUrl[index]));
        }

        return list;


    }

    private static Movie buildMovieInfo(
            String title,
            String description,
            String videoUrl,
            String cardImageUrl,
            String backgroundImageUrl) {
        Movie movie = new Movie();
        movie.setId(count++);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(backgroundImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }

    private static String[] getListsFromJSONObject(JSONObject jsonObject, String index)
    {
        try {
            JSONArray cards = jsonObject.getJSONArray("cards");
            String[] output = new String[cards.length()];

            for(int i = 0; i < cards.length(); i++)
            {
                JSONObject temp = (JSONObject) cards.get(i);

                if(index.equals(MiscOperations.position))
                {
                    int pos = temp.getInt(MiscOperations.position);
                    int dur = temp.getInt(MiscOperations.duration);
                    int rem = dur - pos;
                    String tmpStr = "Resume ";

                    ArrayList<Integer> clockList = MiscOperations.getClockValues(rem);
                    tmpStr += clockList.get(0)+" Hr "+clockList.get(1)+" min remaining - ";

                    tmpStr += temp.getString(MiscOperations.title_index_show);

                    output[i] = tmpStr;
                }
                else
                    output[i] = temp.getString(index);
            }
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}