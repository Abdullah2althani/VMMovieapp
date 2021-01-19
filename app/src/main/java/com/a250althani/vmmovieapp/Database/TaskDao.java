package com.a250althani.vmmovieapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.a250althani.vmmovieapp.Data.MovieModel;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ")
    List<MovieModel> loadAllModel();

    @Insert
    void insertMovieModel(MovieModel movieModel);

    @Delete
    void deleteMovieModel(MovieModel movieModel);

    @Query("SELECT * FROM task where movieId=:movieId")
    MovieModel loadMovie(int movieId);
}
