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
        String title[] = getListsFromJSONObject(showJSONList, titleIndex, "title");
        NUM_COLS = title.length;

        String description[] = getListsFromJSONObject(showJSONList, "des", "");;
        String videoUrl[] = getListsFromJSONObject(showJSONList, "url", "");
        String bgImageUrl[] = getListsFromJSONObject(showJSONList, "album_art_path", "");
        String cardImageUrl[] = getListsFromJSONObject(showJSONList, "album_art_path", "");
        String resumePos[] = getListsFromJSONObject(showJSONList, "position", "");
        String duration[] = getListsFromJSONObject(showJSONList, "duration", "");

        for (int index = 0; index < cardImageUrl.length; ++index) {
            list.add(
                    buildMovieInfo(
                            title[index],
                            description[index],
                            videoUrl[index],
                            cardImageUrl[index],
                            bgImageUrl[index],
                            Integer.parseInt(resumePos[index]),
                            Integer.parseInt(duration[index])));
        }

        return list;


    }

    private static Movie buildMovieInfo(
            String title,
            String description,
            String videoUrl,
            String cardImageUrl,
            String backgroundImageUrl,
            int resumePos,
            int duration) {
        Movie movie = new Movie();
        movie.setId(count++);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(backgroundImageUrl);
        movie.setVideoUrl(videoUrl);
        movie.setResumePos(resumePos);
        movie.setDuration(duration);
        return movie;
    }

    private static String[] getListsFromJSONObject(JSONObject jsonObject, String index, String posFlag)
    {
        try {
            JSONArray cards = jsonObject.getJSONArray("cards");
            String[] output = new String[cards.length()];

            for(int i = 0; i < cards.length(); i++)
            {
                JSONObject temp = (JSONObject) cards.get(i);

                if(temp.has(index))
                {
                    if(index.equals(MiscOperations.position))
                    {
                        int pos = temp.getInt(MiscOperations.position);
                        int dur = temp.getInt(MiscOperations.duration);
                        if(posFlag.equals("title"))
                        {
                            String title = temp.getString(MiscOperations.title_index_show);
                            output[i] = MiscOperations.resumeString(pos, dur, title);
                        }
                        else
                        {
                            output[i] = String.valueOf(pos);
                        }

                    }
                    else
                        output[i] = temp.getString(index);
                }
                else
                    output[i] = "0";
            }
            return output;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}