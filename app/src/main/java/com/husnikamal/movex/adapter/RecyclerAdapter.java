package com.husnikamal.movex.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.husnikamal.movex.R;
import com.husnikamal.movex.activity.DetailActivity;
import com.husnikamal.movex.activity.MainActivity;
import com.husnikamal.movex.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by husni on 28/01/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Movie> results;

    public RecyclerAdapter(Context context, List<Movie> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.structure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        double star = results.get(position).getVoteAverage();
        float rate = (float) (star / 10f * 5f);
        holder.filmTitle.setText("" + results.get(position).getTitle());
        holder.ratingBar.setRating(rate);
        Picasso.with(context)
                .load(results.get(position).getPosterPath())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageList);
        Log.d("Image ", "" + results.get(position).getPosterPath());
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("titleMovie", results.get(position).getTitle())
                        .putExtra("id", results.get(position).getId())
                        .putExtra("textRelease", results.get(position).getReleaseDate())
                        .putExtra("imageBackground", results.get(position).getBackdropPath())
                        .putExtra("posterPath", results.get(position).getPosterPath())
                        .putExtra("overview", results.get(position).getOverview())
                        .putExtra("textPopularity", results.get(position).getPopularity())
                        .putExtra("voteAverages", results.get(position).getVoteAverage());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView filmTitle;
        public ImageView imageList;
        public CardView cardItem;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            filmTitle = (TextView) itemView.findViewById(R.id.textNamaFilm);
            imageList = (ImageView) itemView.findViewById(R.id.imageList);
            cardItem = (CardView) itemView.findViewById(R.id.cardLayout);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingMovie);
        }
    }
}
