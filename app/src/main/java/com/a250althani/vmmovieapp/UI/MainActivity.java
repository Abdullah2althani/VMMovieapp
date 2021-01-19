package com.a250althani.vmmovieapp.UI;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a250althani.vmmovieapp.Adaptors.MoviesAdapter;
import com.a250althani.vmmovieapp.Data.MovieModel;
import com.a250althani.vmmovieapp.MovieViewModel;
import com.a250althani.vmmovieapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    public static final String POPULAR_FILTER = "popular";
    public static final String TOP_RATED_FILTER = "top_rated";
    public static final String FAVORITE_MOVIES = "favorite_movies";
    private String selectedFilter = POPULAR_FILTER;

    TextView noInternetMsg;
    private ArrayList<MovieModel> movies;
    private MoviesAdapter adapter;

    private FirebaseRemoteConfig mRemoteConfig;
    private TextView txt_remote_config;

    RecyclerView recyclerView;
    GridLayoutManager layoutManager;

    MovieViewModel movieViewModel;

    final int NUMBER_OF_COLUMNS = 2;

    @Override
    protected void onRestart() {
        super.onRestart();
        Connectivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        noInternetMsg = findViewById(R.id.no_internet_tv);
        recyclerView = findViewById(R.id.recycler_view);

        movies = new ArrayList<>();

        layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        adapter = new MoviesAdapter(this, movies);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        Connectivity();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Initializing the RemoteConfig instance
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        // Define default configuration values. It can be used in case where
        // config not fetched due to any issue
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("remote_test_parameter", 20);
        // Apply the configuration settings and default values of remote config.
        mRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mRemoteConfig.setDefaults(defaultConfigMap);
        txt_remote_config = findViewById(R.id.txt_remote_config);
        //calling the loadConfig Method to fetch the remote configuration
        loadConfig();
    }

    private void loadConfig() {
        long cacheExpiration = 3600; // we set here 1 hours in seconds
        // If developer mode is enabled we need to reduce cacheExpiration to 0 so that
        // every time our app fetch the config from remote server.
        // remove the below line of if condition code in release version
        if (mRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRemoteConfig.activateFetched();
                        //calling the ApplyConfig method to apply the fetch configuration
                        applyConfig();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RemoteConfig", "Error fetching config: " +
                                e.getMessage());
                        // On error we can apply different method or we can use the same as well
                        // As we have already set the default value
                        applyOnFailure();
                    }
                });
    }

    private void applyOnFailure() {
        String remote_value = mRemoteConfig.getString("remote_test_parameter");
        txt_remote_config.setText("Remote Config fetch failed, Setting Default value is:" + remote_value);
    }

    private void applyConfig() {
        String remote_value = mRemoteConfig.getString("remote_test_parameter");
        txt_remote_config.setText("Remote Config fetch, value is:" + remote_value);
    }


    protected void Connectivity() {
        if (isConnected()) {
            noInternetMsg.setVisibility(View.GONE);
            getMoviesList();
        } else {
            //The technique used for showing this msg is taken from: https://stackoverflow.com/questions/28217436/how-to-show-an-empty-view-with-a-recyclerview
            noInternetMsg.setVisibility(View.VISIBLE);
        }
    }

    private Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_menu_item:
                selectedFilter = FAVORITE_MOVIES;
                getMoviesList();
                break;

            case R.id.most_popular:
                selectedFilter = POPULAR_FILTER;
                getMoviesList();
                break;

            case R.id.highest_rated:
                selectedFilter = TOP_RATED_FILTER;
                getMoviesList();

//            default:
//                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void getMoviesList() {
        if (selectedFilter.equals(POPULAR_FILTER) || selectedFilter.equals(TOP_RATED_FILTER)) {
            movieViewModel.getMovies(false, selectedFilter).observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    movies.clear();
                    movies.addAll(movieModels);
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (selectedFilter.equals(FAVORITE_MOVIES)) {
            movieViewModel.getMovies(true, selectedFilter).observe(this, new Observer<List<MovieModel>>() {
                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    movies.clear();
                    movies.addAll(movieModels);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}