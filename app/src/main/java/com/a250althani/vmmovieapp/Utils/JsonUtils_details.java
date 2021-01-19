package com.a250althani.vmmovieapp.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.a250althani.vmmovieapp.Data.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils_details {

    private static final String LOG_TAG = "JsonUtils";
    private static final String RESULTS_KEY = "results";
    private static final String POSTER_KEY = "poster_path";
    private static final String Id_KEY = "id";
    private static final String Name_KEY = "original_title";
    private static final String Overview_KEY = "overview";
    private static final String Vote_Average_KEY = "vote_average";
    private static final String Release_Date_KEY = "release_date";
    public static ArrayList<MovieModel> extractFeatureFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        ArrayList<MovieModel> movieModels = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            if (baseJsonResponse.has(RESULTS_KEY)) {

                JSONArray moviesArray = baseJsonResponse.getJSONArray(RESULTS_KEY);

                for (int i = 0; i < moviesArray.length(); i++) {

                    JSONObject currentmovie = moviesArray.getJSONObject(i);

                    String id = currentmovie.optString(Id_KEY);
                    int object_id = Integer.parseInt(id);

                    String poster = currentmovie.optString(POSTER_KEY);
                    String name = currentmovie.optString(Name_KEY);
                    String overview = currentmovie.optString(Overview_KEY);
                    String vote_average = currentmovie.optString(Vote_Average_KEY);
                    String release_date = currentmovie.optString(Release_Date_KEY);

                    MovieModel movies = new MovieModel(poster, name, overview, vote_average, release_date, object_id);

                    movieModels.add(movies);

                }//loop

            } else {
                movieModels = null;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
        }

        if (movieModels == null) {
            Log.i(LOG_TAG, "movies is null");
        } else {
            Log.i(LOG_TAG, "movies is NOT null");
        }
        return movieModels;
    }
}
