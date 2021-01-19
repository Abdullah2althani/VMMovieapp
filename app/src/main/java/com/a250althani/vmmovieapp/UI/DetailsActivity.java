package com.a250althani.vmmovieapp.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a250althani.vmmovieapp.Adaptors.MoveAutherAdaptor;
import com.a250althani.vmmovieapp.Data.MovieModel;
import com.a250althani.vmmovieapp.Database.AppDatabase;
import com.a250althani.vmmovieapp.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics = null;
    FavoriteMoviesWorker moviesWorker;
    MovieModel movieModel250;
    int moviesID;
    private MoveAutherAdaptor adapter;
    public static final String EXTRA_POSITION = "ID";
    //    private static final int DEFAULT_POSITION = -1;
    final static String PARAM_QUERY = "api_key";
    public Intent movies_id;
    String API_KEY;
    RequestQueue mQueue;
    ArrayList<MovieModel> movieModel;
    String VedioID;
    int flag = 0;
    TextView noInternetMsg,
            original_tv_title, original_tv,
            overview_tv_title, overview_tv,
            rating_tv_title, rating_tv,
            release_date_tv_title, release_date_tv,
            Reviewed_adapter, selected_button_title;
    ImageView image_iv;
    ImageButton toBefavorite_img, removefavorite_img;
    LinearLayout linearLayout;

    // Member variable for the Database
    private AppDatabase mDB;

    @Override
    protected void onRestart() {
        super.onRestart();
        Connectivity();
    }

    private void initialize() {
        //Define the TextView of no internet connection
        noInternetMsg = findViewById(R.id.no_internet_tv);

        original_tv = findViewById(R.id.original_tv);
        overview_tv = findViewById(R.id.overview_tv);
        rating_tv = findViewById(R.id.rating_tv);
        release_date_tv = findViewById(R.id.release_date_tv);
        image_iv = findViewById(R.id.image_iv);
        linearLayout = findViewById(R.id.contentView);
        toBefavorite_img = findViewById(R.id.favorite_img);
        removefavorite_img = findViewById(R.id.favorite_img2);
        Reviewed_adapter = findViewById(R.id.Reviewed_adapter);
    }

    private void populateUI() {
        Picasso.with(this)
                .load(movieModel250.getMoviePoster())
                .error(R.drawable.loading_imag)
                .into(image_iv);
        original_tv.setText(movieModel250.getOriginal_title());
        overview_tv.setText(movieModel250.getOverview());
        rating_tv.setText(movieModel250.getVote_average());
        release_date_tv.setText(movieModel250.getRelease_date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setTitle(getResources().getString(R.string.Details_Activity_title));
        API_KEY = getResources().getString(R.string.API_KEY);
        movieModel = new ArrayList<>();
        moviesWorker = new FavoriteMoviesWorker(this);
        initialize();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDB = AppDatabase.getDatabase(getApplicationContext());
        if (getting_passed_id()) return;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, moviesID + "");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Connectivity();

    }

    private boolean getting_passed_id() {
        movies_id = getIntent();
        movieModel250 = (MovieModel) movies_id.getSerializableExtra(EXTRA_POSITION);
        assert movieModel250 != null;
        moviesID = movieModel250.getMovieId();
        moviesWorker.isFavoriteMovie(movieModel250);
        populateUI();

        return false;
    }


    protected void Connectivity() {
        if (isConnected()) {
            noInternetMsg.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            image_iv.setVisibility(View.VISIBLE);
        } else {
            noInternetMsg.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            image_iv.setVisibility(View.GONE);
        }
    }

    private Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public void DisplayReviews(View view) {
        mQueue = Volley.newRequestQueue(this);
        Log.i("DisplayReviews= ", "Clicked");
        Reviewed_adapter.setVisibility(View.VISIBLE);

        String url = "https://api.themoviedb.org/3/movie/" + moviesID + "/reviews?" + PARAM_QUERY + "=" + API_KEY;
        Log.i("url", url);

        JsonObjectRequest requestQ = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("onResponse= ", "Called");
                            // JSONObject  Object = response.getJSONObject("id");
                            JSONArray jsonArray = response.getJSONArray("results");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject resultObject = jsonArray.getJSONObject(i);

                                    String author = resultObject.getString("author");
                                    String content = resultObject.getString("content");
                                    Reviewed_adapter.append("\n" + author + "\n\n" + content + "\n\n");
                                    adapter = new MoveAutherAdaptor(author, content);
                                    Toast.makeText(DetailsActivity.this, "Scroll down", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Reviewed_adapter.setText(getResources().getString(R.string.no_reviews));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse= ", "Called");
                    }
                });
        mQueue.add(requestQ);
    }

    public void DisplayVideo(View view) {
        Log.i("DisplayVideo = ", "Clicked");
        selected_button_title.setVisibility(View.GONE);
        Reviewed_adapter.setVisibility(View.GONE);
        mQueue = Volley.newRequestQueue(this);
        String url = "https://api.themoviedb.org/3/movie/" + moviesID + "/videos?" + PARAM_QUERY + "=" + API_KEY;
        Log.i("url", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("onResponse= ", "Called");
                            JSONArray jsonArray = response.getJSONArray("results");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            VedioID = jsonObject1.getString("key");
                            Log.e("VedioID= ", VedioID + "");
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + VedioID)));
                            Toast.makeText(DetailsActivity.this, "YouTube App will startup", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse= ", "Called");
                    }
                }
        );
        mQueue.add(jsonObjectRequest);
    }

    public void setIFMovieFavorite(boolean ifMovieFavorite) {
        if (ifMovieFavorite) {
            flag = 1;
            toBefavorite_img.setVisibility(View.GONE);
            removefavorite_img.setVisibility(View.VISIBLE);
        } else {
            flag = 0;
            toBefavorite_img.setVisibility(View.GONE);
            removefavorite_img.setVisibility(View.VISIBLE);
        }
    }

    public void AddToBeFavorite(View view) {
        moviesWorker.addFavoriteMovie(movieModel250);
        Toast.makeText(this, "Added to be Favorite Movie", Toast.LENGTH_SHORT).show();

        toBefavorite_img.setVisibility(View.GONE);
        removefavorite_img.setVisibility(View.VISIBLE);
    }

    public void RemoveFromFavorite(View view) {
        moviesWorker.deleteFavoriteMovie(movieModel250);
        Toast.makeText(this, "Removed from Favorite Movie", Toast.LENGTH_SHORT).show();
        toBefavorite_img.setVisibility(View.VISIBLE);
        removefavorite_img.setVisibility(View.GONE);
    }

    public class FavoriteMoviesWorker {
        private boolean isFavorite = true;
        private AppDatabase database;

        FavoriteMoviesWorker(Context context) {
            database = AppDatabase.getDatabase(context);
        }

        void addFavoriteMovie(MovieModel movieModel) {
            isFavorite = true;
            new FavoriteMoviesAsyncTask().execute(movieModel);
        }

        void deleteFavoriteMovie(MovieModel movieModel) {
            isFavorite = false;
            new FavoriteMoviesAsyncTask().execute(movieModel);
        }

        void isFavoriteMovie(MovieModel movieModel) {
            new IsFavoriteMoviesAsyncTask().execute(movieModel);
        }

        @SuppressLint("StaticFieldLeak")
        private class IsFavoriteMoviesAsyncTask extends AsyncTask<MovieModel, Void, MovieModel> {
            @Override
            protected MovieModel doInBackground(MovieModel... movieModel) {
                return database.taskDao().loadMovie(movieModel[0].getMovieId());
            }

            @Override
            protected void onPostExecute(MovieModel movieModel) {
                super.onPostExecute(movieModel);
                setIFMovieFavorite(movieModel != null ? true : false);
            }
        }

        @SuppressLint("StaticFieldLeak")
        private class FavoriteMoviesAsyncTask extends AsyncTask<MovieModel, Void, Void> {
            @Override
            protected Void doInBackground(MovieModel... movieModel) {
                if (isFavorite) {
                    database.taskDao().insertMovieModel(movieModel[0]);
                } else {
                    database.taskDao().deleteMovieModel(movieModel[0]);
                }
                return null;
            }
        }
    }
}
