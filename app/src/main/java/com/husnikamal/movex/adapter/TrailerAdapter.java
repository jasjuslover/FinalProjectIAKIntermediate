package com.husnikamal.movex.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.husnikamal.movex.R;
import com.husnikamal.movex.model.Trailer;

import java.util.List;

/**
 * Created by husni on 30/01/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private Context context;
    private List<Trailer> results;

    public TrailerAdapter(Context context, List<Trailer> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_structure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.trailerTitle.setText(results.get(position).getName());
        holder.trailerDesc.setText(results.get(position).getType());
        holder.trailerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtube.com/watch?v=" + results.get(position).getKey()));
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trailerTitle;
        public TextView trailerDesc;
        public CardView trailerCard;

        public ViewHolder(View itemView) {
            super(itemView);

            trailerTitle = (TextView) itemView.findViewById(R.id.title);
            trailerDesc = (TextView) itemView.findViewById(R.id.desc);
            trailerCard = (CardView) itemView.findViewById(R.id.trailerCard);
        }
    }
}
