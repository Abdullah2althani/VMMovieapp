package com.a250althani.vmmovieapp;

import android.annotation.SuppressLint;
import android.app.Application;

import android.net.Uri;
import android.os.AsyncTask;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.a250althani.vmmovieapp.Data.MovieModel;
import com.a250althani.vmmovieapp.Database.AppDatabase;
import com.a250althani.vmmovieapp.Utils.JsonUtils_details;
import com.a250althani.vmmovieapp.Utils.NetworkUtils;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private AppDatabase database;
    private MutableLiveData<List<MovieModel>> mutableLiveData;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(boolean favoriteMovies, String filter) {
        if (favoriteMovies) {
            new FavoriteMoviesAsyncTask().execute();
            return mutableLiveData;
        } else {
            new MoviesFromServerAsyncTask().execute(filter);
            return mutableLiveData;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class MoviesFromServerAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... filter) {
            List<MovieModel> movies = null;
            String url = buildURL(filter[0]);
            String response = NetworkUtils.fetchResponse(url);
            movies = JsonUtils_details.extractFeatureFromJson(response);
            mutableLiveData.postValue(movies);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class FavoriteMoviesAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mutableLiveData.postValue(database.taskDao().loadAllModel());
            return null;
        }
    }

    private String buildURL(String s) {

        final String BASE_URL = "http://api.themoviedb.org/3/movie";
        final String API_KEY_QUERY_PARAMETER = "api_key";
        final String API_KEY = "474ef283dc3c293fc855a8e09ad74a52";

        Uri uri = Uri.parse(BASE_URL);
        Uri.Builder builder = uri.buildUpon();

        builder.appendPath(s);

        builder.appendQueryParameter(API_KEY_QUERY_PARAMETER, API_KEY);
        return builder.toString();
    }

}
