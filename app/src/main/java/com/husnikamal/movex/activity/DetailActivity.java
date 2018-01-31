package com.husnikamal.movex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.husnikamal.movex.R;
import com.husnikamal.movex.adapter.CastAdapter;
import com.husnikamal.movex.adapter.TrailerAdapter;
import com.husnikamal.movex.model.Cast;
import com.husnikamal.movex.model.CastResponse;
import com.husnikamal.movex.model.TrailerResponse;
import com.husnikamal.movex.network.Client;
import com.husnikamal.movex.network.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String API_KEY = "dfe1a9fa143f0e48abfd687fbc950e49";
    @BindView(R.id.recyclerViewTrailer)
    RecyclerView recyclerViewTrailer;
    private List<Cast> castList;
    private CastAdapter castAdapter;
    private LinearLayoutManager layoutManagers, layoutTrailerManager;

    @BindView(R.id.backdrop)
    ImageView backdrop;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.iconRelease)
    ImageView iconRelease;
    @BindView(R.id.textRelease)
    TextView textRelease;
    @BindView(R.id.iconPopularity)
    ImageView iconPopularity;
    @BindView(R.id.textPopularity)
    TextView textPopularity;
    @BindView(R.id.iconOverview)
    ImageView iconOverview;
    @BindView(R.id.textOverview)
    TextView textOverview;
    @BindView(R.id.fab)
    ImageView posterPath;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.ratingVal)
    RatingBar ratingVal;
    @BindView(R.id.recyclerViewCast)
    RecyclerView recyclerViewCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        imageView.startAnimation(fadeoutAnim);

//        Setting layout for Cast RecyclerView
        layoutManagers = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCast.setLayoutManager(layoutManagers);

        layoutTrailerManager = new GridLayoutManager(DetailActivity.this, 1);
        recyclerViewTrailer.setLayoutManager(layoutTrailerManager);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBar.getTotalScrollRange()) {
//                    Collapsed
                    posterPath.setVisibility(View.INVISIBLE);
                } else if (verticalOffset == 0) {
//                    Expanded
                    posterPath.setVisibility(View.VISIBLE);
                } else {
//                    Between
                    posterPath.setVisibility(View.INVISIBLE);
                }
            }
        });

        Intent intentGetter = getIntent();
        int id;
        String movieName, thumbnail, poster, overView, release;
        Double popularity;
        Float rateStars;

        double stars = getIntent().getExtras().getDouble("voteAverages");
        rateStars = (float) (stars / 10f * 5f);

        id = getIntent().getExtras().getInt("id");
        movieName = getIntent().getExtras().getString("titleMovie");
        thumbnail = getIntent().getExtras().getString("imageBackground");
        poster = getIntent().getExtras().getString("posterPath");
        popularity = getIntent().getExtras().getDouble("textPopularity");
        release = getIntent().getExtras().getString("textRelease");
        overView = getIntent().getExtras().getString("overview");
//        rateStars = getIntent().getExtras().getFloat("voteAverages");

        Log.i("Backdrop ", "" + thumbnail);

        Picasso.with(DetailActivity.this)
                .load("https://image.tmdb.org/t/p/w500" + thumbnail)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(backdrop);

        Picasso.with(DetailActivity.this)
                .load(poster)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(posterPath);

        toolbarLayout.setTitle(movieName);
        textPopularity.setText("" + popularity);
        textRelease.setText(release);
        textOverview.setText(overView);
        ratingVal.setRating(rateStars);
        initCast();
        loadCast(id);
        loadTrailer(id);
    }

    private void initCast() {
        castList = new ArrayList<>();
//        RecyclerAdapter constructor need two identifier. Context and List
        castAdapter = new CastAdapter(this, castList);
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewCast.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCast.setAdapter(castAdapter);
        castAdapter.notifyDataSetChanged();
    }

    private void loadCast(int id) {
        Client client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<CastResponse> castResponse = apiService.getCast(id, API_KEY);
        castResponse.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("Response ", "" + response.code());
                    Log.d("Data ", "" + response.body().getCasts());
                    recyclerViewCast.setAdapter(new CastAdapter(DetailActivity.this, response.body().getCasts()));
//                    recyclerView.setAdapter(new RecyclerAdapter(MainActivity.this, response.body().getResults()));
                }
            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {

            }
        });
    }

    private void loadTrailer(int id) {
        Client client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<TrailerResponse> trailerResponse = apiService.getTrailers(id, API_KEY);
        trailerResponse.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful()) {
                    int status = response.code();
                    Log.d("Response Trailer ", "" + status);
                    Log.d("Data ", "" + response.body().getResults());
                    recyclerViewTrailer.setAdapter(new TrailerAdapter(DetailActivity.this, response.body().getResults()));
                    Log.d("Adapter ", "RecyclerView Adapter Seted");
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {

            }
        });
    }
}
