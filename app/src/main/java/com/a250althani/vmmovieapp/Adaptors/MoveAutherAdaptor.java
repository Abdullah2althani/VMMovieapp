package com.a250althani.vmmovieapp.Adaptors;

import android.content.Context;
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
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MoveAutherAdaptor extends RecyclerView.Adapter<MoveAutherAdaptor.MovieViewHolder>{
    private String AutherName;
    private String AutherContent;
    private Context context;

    public MoveAutherAdaptor(String autherName, String autherContent) {
        AutherName = autherName;
        AutherContent = autherContent;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            holder.review_author.setText(AutherName);
            holder.review_content.setText(AutherContent);
            Log.i("msg", " review_author clicked" + AutherName );
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView review_author;
        TextView review_content;

        MovieViewHolder(View itemView) {
            super(itemView);
            review_author = itemView.findViewById(R.id.poster_thumbnail);
            review_content = itemView.findViewById(R.id.text_title);
        }
    }
}
