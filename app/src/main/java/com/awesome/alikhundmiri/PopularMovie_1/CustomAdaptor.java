package com.awesome.alikhundmiri.PopularMovie_1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alikhundmiri on 20/12/16.
 */

public class CustomAdaptor extends ArrayAdapter<CustomList>{
    public static final String LOG_CAT = CustomAdaptor.class.getSimpleName();
    private ArrayList<CustomList> gridItems = new ArrayList<>();

    public CustomAdaptor(Activity context, ArrayList<CustomList> cList) {
        super(context, 0, cList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView moviePosterView;
        TextView movieTitleView;
        TextView movieReleaseView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listitem_view, parent, false);//XML file with the look of each listitem
            moviePosterView = (ImageView) convertView.findViewById(R.id.movie_image);
            convertView.setTag(moviePosterView);
        }else {
            moviePosterView = (ImageView) convertView.getTag();
        }

        CustomList item = getItem(position);

        Picasso.with(getContext()).load(item.getmPoster()).into(moviePosterView);

        movieTitleView = (TextView) convertView.findViewById(R.id.movie_text_1);
        movieTitleView.setText(item.getmTitle());

        movieReleaseView = (TextView) convertView.findViewById(R.id.movie_text_2);
        movieReleaseView.setText(item.getmReleaseDate());

        return convertView;
    }
}
