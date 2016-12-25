package com.awesome.alikhundmiri.PopularMovie_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (findViewById(R.id.activity_detail) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.activity_detail, new DetailFragment()).commit();
        }
    }

    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final String LOG_TAG = DetailActivity.class.getSimpleName();

            View rootview = inflater.inflate(R.layout.content_detail, container, false);

            Intent intent = getActivity().getIntent();
            //Log.v(LOG_TAG, "Intent intent: " + intent);

            if (intent != null) {
                String title = intent.getStringExtra(getString(R.string.title));
                final TextView titleText = (TextView) rootview.findViewById(R.id.title);
                titleText.setText(title);
                //Log.v(LOG_TAG, "Intent recieved: " + title);
                String releaseDate = intent.getStringExtra(getString(R.string.release_date));
                final TextView release = (TextView) rootview.findViewById(R.id.release_date);
                release.setText(releaseDate);

                String rating = intent.getStringExtra(getString(R.string.rating));
                final TextView movieRating = (TextView) rootview.findViewById(R.id.rating);
                movieRating.setText(rating);

                String plot = intent.getStringExtra(getString(R.string.plot));
                final TextView moviePlot = (TextView) rootview.findViewById(R.id.plot);
                moviePlot.setText(plot);

                String poster = intent.getStringExtra(getString(R.string.poster));
                ImageView imageView = (ImageView) rootview.findViewById(R.id.detail_poster);
                Picasso.with(getContext()).load(poster).into(imageView);

                String banner = intent.getStringExtra(getString(R.string.banner));
                final LinearLayout bannerPic = (LinearLayout) rootview.findViewById(R.id.linear_banner);
                Picasso.with(getContext())
                        .load(banner)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bannerPic.setBackground(new BitmapDrawable(bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                //Default place holder
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                //Loading image
                            }
                        });
            }
            return rootview;
        }
    }

}