package com.probypro.mario99ukdw.popularmovies.utilities;

import com.probypro.mario99ukdw.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mario99ukdw on 28.06.2017.
 */

public class MovieDBResultParser {
    final static String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    final static String TAG_RESULTS = "results";
    final static String TAG_ORIGINAL_TITLE = "original_title";
    final static String TAG_POSTER_PATH = "poster_path";
    final static String TAG_OVERVIEW = "overview";
    final static String TAG_VOTE_AVERAGE = "vote_average";
    final static String TAG_RELEASE_DATE = "release_date";
    final static String POSTER_SIZE = "w185";

    public static String constructPosterUrlString(String posterPath, String size) {
        String url = MOVIEDB_POSTER_BASE_URL;
        String posterSize = size;
        if (posterSize.isEmpty()) posterSize = POSTER_SIZE; // use default size if empty

        url = url + posterSize + posterPath;
        return url;
    }
    public static ArrayList<Movie> parseResult(String datastring) {
        ArrayList<Movie> result = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(datastring);
            if (data.has(TAG_RESULTS)) {
                JSONArray resultsJSONArray = data.getJSONArray(TAG_RESULTS);
                String posterPath = null;
                String originalTitle = null;
                String overview = null;
                double voteAverage = 0;
                String releaseDate = null;
                for (int i = 0; i < resultsJSONArray.length(); i++) {
                    JSONObject row = resultsJSONArray.getJSONObject(i);

                    if (row.has(TAG_POSTER_PATH)) posterPath = constructPosterUrlString(row.getString(TAG_POSTER_PATH), POSTER_SIZE);
                    if (row.has(TAG_ORIGINAL_TITLE)) originalTitle = row.getString(TAG_ORIGINAL_TITLE);
                    if (row.has(TAG_OVERVIEW)) overview = row.getString(TAG_OVERVIEW);
                    if (row.has(TAG_VOTE_AVERAGE)) voteAverage = row.getDouble(TAG_VOTE_AVERAGE);
                    if (row.has(TAG_RELEASE_DATE)) releaseDate = row.getString(TAG_RELEASE_DATE);

                    result.add(new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
