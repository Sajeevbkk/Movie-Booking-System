package com.moviebooking.app.cache;

import com.moviebooking.app.client.ApiClient;
import com.moviebooking.app.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class DataCache {
    private static DataCache instance;
    private List<Movie> moviesCache = null;

    private DataCache() {}

    public static synchronized DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public synchronized void invalidate() {
        moviesCache = null;
    }

    public synchronized List<Movie> getMovies(boolean force) throws Exception {
        if (force || moviesCache == null) {
            moviesCache = ApiClient.getInstance().getMovies();
        }
        return new ArrayList<>(moviesCache);
    }
}
