package com.husnikamal.movex.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.husnikamal.movex.R;
import com.husnikamal.movex.model.Cast;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by husni on 29/01/18.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private Context context;
    private List<Cast> results;

    public CastAdapter(Context context, List<Cast> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_structure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(results.get(position).getName());
        holder.character.setText(results.get(position).getCharacter());
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w500" + results.get(position).getCloseUp())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.closeUp);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView character;
        public ImageView closeUp;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.textName);
            character = (TextView) itemView.findViewById(R.id.textCharacter);
            closeUp = (ImageView) itemView.findViewById(R.id.closeUp);
        }
    }
}
