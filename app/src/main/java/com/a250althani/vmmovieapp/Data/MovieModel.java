package com.a250althani.vmmovieapp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task")
public class MovieModel implements Serializable {

    @PrimaryKey
    private int movieId;
    private String moviePoster;
    private String original_title;
    private String overview;
    private String vote_average;
    private String release_date;

    public MovieModel(String moviePoster, String original_title, String overview, String vote_average, String release_date, int movieId) {
        this.moviePoster = moviePoster;
        this.original_title = original_title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.movieId = movieId;
    }

    public String getOriginal_title() {
        return original_title;
    }


    public String getOverview() {
        return overview;
    }


    public String getVote_average() {
        return vote_average;
    }


    public String getRelease_date() {
        return release_date;
    }


    public int getMovieId() {
        return movieId;
    }

    public String getMoviePoster() {
        return buildFullPosterPath();
    }

    private String buildFullPosterPath() {
        String FIRST_PART_OF_POSTER_URL = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/";
        return FIRST_PART_OF_POSTER_URL + moviePoster;
    }
}
