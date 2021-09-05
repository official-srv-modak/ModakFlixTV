package com.example.modakflixtv;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MiscOperations {

    public static String ip = "modakflix.com", ipInfoFilePath = "";

    public static String position = "position", duration = "duration", title_index_show = "name";

    public static String domain_name = "http://"+ip+"/";
    public static String record_position_path = domain_name+"record_position.php";
    public static String delete_position_path = domain_name+"delete_from_shows_watched.php";
    public static String get_shows_watched_path = domain_name+"get_shows_watched.php?username=Sourav Modak";
    public static String reset_profile = domain_name+"reset_profile.php?username=admin";
    public static String get_movies_list = domain_name+"get_movies_list_json.php";
    public static String reload_shows_watched = domain_name+"reload_shows_watched.php";
    public static String search_shows = domain_name+"search_show.php";
    public static String get_profiles = domain_name+"get_profiles.php";
    public static String reload_description = domain_name+"reload_description.php";
    public static String get_description = domain_name+"get_description.php";
    public static String add_profile = domain_name+"add_profile.php";

    public static JSONObject getDataFromServer(String URL)
    {
        URL = handleUrl(URL);
        String output = "";
        Log.e("URL", URL);
        try{
            java.net.URL url = new URL(URL);
            Map params = new LinkedHashMap<>();
            StringBuilder postData = new StringBuilder();
            Set<Map.Entry> s = params.entrySet();
            for (Map.Entry param : s) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode((String) param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                {
                    output += inputLine;
                }
            }
            in.close();
            JSONObject jsonObj = new JSONObject(output);
            return jsonObj;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e("URL", URL);
            return null;
        }
    }
    public static String handleUrl(String URL)
    {
        if(!URL.isEmpty())
        {
            String output = "http:";
            //Log.e("YY", URL);
            String [] splitList = URL.split(output);
            if(splitList.length>1)
            {
                String temp = splitList[1];
                temp = temp.replace("/", "forwardslash");
                temp = temp.replace(" ", "spacebarspace");
                temp = temp.replace("?", "questionmarkquestion");
                temp = temp.replace("&", "emparsandemparsand");
                temp = temp.replace("=", "equaltoequal");
                try {
                    temp= URLEncoder.encode(temp, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                temp = temp.replace("forwardslash", "/");
                temp = temp.replace("spacebarspace", "%20");
                temp = temp.replace("questionmarkquestion", "?");
                temp = temp.replace("emparsandemparsand", "&");
                temp = temp.replace("equaltoequal", "=");

                output += temp;
                return output;
            }
            else
                return URL;
        }
        else
            return URL;

    }
    public static ArrayList<Integer> getClockValues(int timeInMill)
    {
        int rem = timeInMill;
        rem /= 1000;
        int secs = rem%60;
        int mins = rem/60;
        int hrs = mins/60;
        mins = mins%60;
        ArrayList<Integer> output = new ArrayList<>();
        output.add(hrs);
        output.add(mins);
        output.add(secs);
        return output;
    }

    public static String pingDataServer(String URL)
    {
        String output = "";
        try{
            java.net.URL url = new URL(URL);
            Map params = new LinkedHashMap<>();
            StringBuilder postData = new StringBuilder();
            Set<Map.Entry> s = params.entrySet();
            for (Map.Entry param : s) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode((String) param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                {
                    output += inputLine;
                }
            }
            in.close();
            //JSONObject jsonObj = new JSONObject(output);
            return output;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
