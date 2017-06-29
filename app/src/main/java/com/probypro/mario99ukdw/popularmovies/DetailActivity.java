package com.probypro.mario99ukdw.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.probypro.mario99ukdw.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        ImageView posterImageView = (ImageView) findViewById(R.id.iv_poster);
        TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);
        TextView voteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(Movie.INTENT_PARCEL_NAME);

        titleTextView.setText(movie.getTitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
//                .resize(getResources().getInteger(R.integer.poster_w185_width),
//                        getResources().getInteger(R.integer.poster_w185_height))
                .into(posterImageView);

        String overview = movie.getOverview();
        if (overview == null || overview == "") {
            overview = getResources().getString(R.string.message_no_overview_found);
        }
        overviewTextView.setText(overview);

        voteAverageTextView.setText(movie.getFormatedVoteAverage());

        String releaseDate = movie.getFormatedReleaseDate();
        if(releaseDate == null || releaseDate == "") {
            releaseDate = getResources().getString(R.string.message_no_release_date_found);
        } else {
            try {
                releaseDate = getFormatedDate(releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
                releaseDate = "";
            }
        }
        releaseDateTextView.setText(releaseDate);
    }

    private String getFormatedDate(String date, String format) throws ParseException {
        Log.d(LOG_TAG, "parsing movie release date" + date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this.getApplicationContext());
        return dateFormat.format(simpleDateFormat.parse(date));
    }
}
