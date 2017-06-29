package com.probypro.mario99ukdw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.probypro.mario99ukdw.popularmovies.data.Movie;
import com.probypro.mario99ukdw.popularmovies.data.MovieAdapter;
import com.probypro.mario99ukdw.popularmovies.utilities.MovieDBResultParser;
import com.probypro.mario99ukdw.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayList<Movie> movieArrayList = null;
    private GridView movieListGridView = null;

    final static String SORT_METHOD_KEY = "SORT_METHOD";
    final static String VAR_NAME_MOVIE_ARRAY_LIST = "movieArrayList";
    final static String VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION = "firstVisiblePosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieListGridView = (GridView) findViewById(R.id.gv_movie_list);
        movieListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
                Movie movie = (Movie) parent.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(Movie.INTENT_PARCEL_NAME, movie);

                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            loadMovieByDefault();
        } else {
            // Get data from local resources
            movieArrayList = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST);

            if (movieArrayList != null) {
                loadMovieListToGridView(movieArrayList);
                Log.d(LOG_TAG, "onCreate : movieArrayList is null");
            } else {
                loadMovieByDefault();
                Log.d(LOG_TAG, "onCreate : movieArrayList is loaded by default");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            setSortMethod(NetworkUtils.PARAM_SORT_POPULAR);
            loadMovieFromServer(NetworkUtils.PARAM_SORT_POPULAR);
            return true;
        } else if (id == R.id.action_sort_top_rated) {
            setSortMethod(NetworkUtils.PARAM_SORT_TOP_RATED);
            loadMovieFromServer(NetworkUtils.PARAM_SORT_TOP_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        int firstVisiblePosition = movieListGridView.getFirstVisiblePosition();

        state.putParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST, movieArrayList);
        state.putInt(VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION, firstVisiblePosition);

        Log.d(LOG_TAG, "onSaveInstanceState : movieArrayList is saved");
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Loading Saved data
        movieArrayList = savedInstanceState.getParcelableArrayList(VAR_NAME_MOVIE_ARRAY_LIST);
        loadMovieListToGridView(movieArrayList);

        int firstVisiblePosition = savedInstanceState.getInt(VAR_NAME_MOVIE_FIRST_VISIBLE_POSITION);
        movieListGridView.setSelection(firstVisiblePosition);
        Log.d(LOG_TAG, "onRestoreInstanceState : movieArrayList is restored");
    }

    /**
     *  get sort method from local resources
     */
    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(SORT_METHOD_KEY, NetworkUtils.PARAM_SORT_POPULAR);
    }

    /**
     *  get sort method selection into local resources
     */
    private void setSortMethod(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SORT_METHOD_KEY, sortMethod);
        editor.apply();
    }

    /**
     *  parse JSON text from server
     */
    private void parseResults(String result) {
        movieArrayList = MovieDBResultParser.parseResult(result);
        loadMovieListToGridView(movieArrayList);
    }

    /**
     *  load movie list from server with default sort method or based on SharedPreferences
     */
    private void loadMovieByDefault() {
        String sortMethod = getSortMethod();
        loadMovieFromServer(sortMethod);
    }

    /**
     *  load movie list from server with selected sort method
     */
    private void loadMovieFromServer(String sortMethod) {
        if (isNetworkAvailable()) {
            URL searchUrl = NetworkUtils.buildUrl(sortMethod);
            Log.d(LOG_TAG, searchUrl.toString());
            new MovieDBQueryTask().execute(searchUrl);
        } else {
            // show no internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection. Please check", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  load movie list into grid view
     */
    private void loadMovieListToGridView(ArrayList<Movie> movieList) {
        //if (movieList == null) {
        //
        //}
        MovieAdapter movieAdapter = new MovieAdapter(this, movieList);
        movieListGridView.setAdapter(movieAdapter);
    }

    /**
     * check if internet connection is available
     * Based on a stackoverflow snippet
     * URL : https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return true if there is Internet. false if not.
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class MovieDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                parseResults(searchResults);
                Log.d(LOG_TAG, "Search Result : " + searchResults);
            }
        }
    }
}
