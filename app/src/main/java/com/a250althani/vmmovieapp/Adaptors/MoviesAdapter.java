package com.a250althani.vmmovieapp.Adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a250althani.vmmovieapp.Data.MovieModel;
import com.a250althani.vmmovieapp.R;
import com.a250althani.vmmovieapp.UI.DetailsActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private Bundle bundle;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String EXTRA_POSITION = "ID";
    private Serializable serializable;
    private ArrayList<MovieModel> movies;
    private Context context;

    public MoviesAdapter(Context context, ArrayList<MovieModel> source) {
        this.context = context;
        this.movies = source;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.text_title.setText(movies.get(position).getOriginal_title());

        Picasso.with(context)
                .load(movies.get(position).getMoviePoster())
                .error(R.drawable.loading_imag)
                .into(holder.poster_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetailActivity(serializable = movies.get(position));
            }
        });

        Log.i("msg", "link is " + movies.get(position).getMoviePoster());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster_img;
        TextView text_title;

        MovieViewHolder(View itemView) {
            super(itemView);
            poster_img = itemView.findViewById(R.id.poster_thumbnail);
            text_title = itemView.findViewById(R.id.text_title);
        }
    }

    private void launchDetailActivity(Serializable position) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }
}
