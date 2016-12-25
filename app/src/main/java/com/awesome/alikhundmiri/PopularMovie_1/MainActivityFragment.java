package com.awesome.alikhundmiri.PopularMovie_1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivityFragment extends Fragment {


    private CustomAdaptor newadaptor;

    private ArrayList<CustomList> mCustomList;

    String BASE_URL;

    public MainActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                updateMoviesList();
                //Toast.makeText(getContext(), "Device is connected to the internet", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList() {
        FetchMoviesList getNewList = new FetchMoviesList();
        getNewList.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String LOG_TAG = MainActivityFragment.class.getSimpleName();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        mCustomList = new ArrayList<>();
        newadaptor = new CustomAdaptor(getActivity(), mCustomList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridLayout);
        gridView.setAdapter(newadaptor);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CustomList poster = newadaptor.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(getString(R.string.title), poster.getmTitle());
                intent.putExtra(getString(R.string.poster), poster.getmPoster());
                intent.putExtra(getString(R.string.banner), poster.getmBackDrop());
                intent.putExtra(getString(R.string.plot), poster.getmPlot());
                intent.putExtra(getString(R.string.rating), poster.getmRating().toString());//since it is in Double format
                intent.putExtra(getString(R.string.release_date), poster.getmReleaseDate());
                // get the id of the movie of item selected.
                // divert the program to a different do in background which fetches data we need
                // like Run time & Slogan.
                startActivity(intent);
                Log.v(LOG_TAG, "Launched Detail Activity: " + poster.getmTitle());
            }
        });
        return rootView;
    }

    private ArrayList<CustomList> getDataFromJson(String movieJsonStr)
            throws JSONException {

        final String LOG_TAG = ArrayList.class.getSimpleName();


        final String TMDB_MAIN_BRANCH = "results";
        final String TMDB_NAME = "title";
        final String TMDB_IMAGE_URL = "poster_path";
        final String TMDB_COVER_IMAGE = "backdrop_path";
        final String TMDB_RATING = "vote_average";
        final String TMDB_DETAIL = "overview";
        final String TMDB_RELEASEDATE = "release_date";
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
        final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780";

        JSONObject moviesJSON = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJSON.getJSONArray(TMDB_MAIN_BRANCH);

        ArrayList<CustomList> gridItems = new ArrayList<>();
        CustomList item;
        Log.v(LOG_TAG, "" + moviesArray.length());

        //starting a loop here for the number of movies parsed
        for (int i = 0; i < moviesArray.length(); i++) {

            String movieTitle;
            Double movieRating;

            String posterPath;
            String POSTER_URL;

            String backdropPath;
            String BACKDROP_URL;

            String movieDetail;
            String releaseDate;


            JSONObject oneMovie = moviesArray.getJSONObject(i);

            movieTitle = oneMovie.getString(TMDB_NAME);
            movieRating = oneMovie.getDouble(TMDB_RATING);
            movieDetail = oneMovie.getString(TMDB_DETAIL);
            releaseDate = oneMovie.getString(TMDB_RELEASEDATE);

            posterPath = oneMovie.getString(TMDB_IMAGE_URL);
            POSTER_URL = POSTER_BASE_URL + posterPath;

            backdropPath = oneMovie.getString(TMDB_COVER_IMAGE);
            BACKDROP_URL = BACKDROP_BASE_URL + backdropPath;
            item = new CustomList();

            //set title
            item.setmTitle(movieTitle);
            //Set Rating
            item.setmRating(movieRating);
            //set Context
            item.setmPlot(movieDetail);
            //Set release Date
            item.setmReleaseDate(releaseDate);
            //set Poster
            item.setmPoster(POSTER_URL);
            //set back drop
            item.setmBackDrop(BACKDROP_URL);

            gridItems.add(item);

            //Log.v(LOG_TAG, "Movie List generated: " + movieTitle);
        }
        return gridItems;
    }

    private String selectBaseUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.popular_value));

        if (sort.equals(getString(R.string.popular_value))) {
            BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
        } else if (sort.equals(getString(R.string.top_rated_value))) {
            BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
        } else if (sort.equals(getString(R.string.upcoming_value))) {
            BASE_URL = "http://api.themoviedb.org/3/movie/upcoming?";
        }
        return BASE_URL;
    }

    public class FetchMoviesList extends AsyncTask<Void, Void, ArrayList<CustomList>> {

        private final String LOG_TAG = FetchMoviesList.class.getSimpleName();

        @Override
        protected void onPostExecute(ArrayList<CustomList> gridItems) {
            super.onPostExecute(gridItems);
            newadaptor.clear();
            if (gridItems != null) {
                newadaptor.addAll(gridItems);
            }
        }

        @Override
        protected ArrayList<CustomList> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr;
            try {
                selectBaseUrl();
                final String API_PARAM = "api_key";

                Uri BuildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.THEMOVIES_DB_API_KEY)
                        .build();

                URL url = new URL(BuildUri.toString());

                Log.v(LOG_TAG, "Built url: " + BuildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Closing Stream, last line mainFragment", e);
                    }
                }
            }
            try {
                return getDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            //this will happen only if there was any error parsing JSON data
            return null;
        }
    }
}
