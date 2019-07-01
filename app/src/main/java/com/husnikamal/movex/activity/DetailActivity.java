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

import com.husnikamal.movex.BuildConfig;
import com.husnikamal.movex.R;
import com.husnikamal.movex.adapter.CastAdapter;
import com.husnikamal.movex.adapter.TrailerAdapter;
import com.husnikamal.movex.model.Cast;
import com.husnikamal.movex.model.CastResponse;
import com.husnikamal.movex.model.Trailer;
import com.husnikamal.movex.model.TrailerResponse;
import com.husnikamal.movex.network.Client;
import com.husnikamal.movex.network.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    private static final String API_KEY = "dfe1a9fa143f0e48abfd687fbc950e49";
    private CompositeDisposable rxDisposable;

    @BindView(R.id.recyclerViewTrailer)
    RecyclerView recyclerViewTrailer;
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

    private List<Cast> castList;
    private List<Trailer> trailerList;
    private CastAdapter castAdapter;
    private TrailerAdapter trailerAdapter;
    private LinearLayoutManager layoutManagers, layoutTrailerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        castList = new ArrayList<>();
        trailerList = new ArrayList<>();
        rxDisposable = new CompositeDisposable();

        initAppBarScrollListener();

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

        loadCast(id);
        loadTrailer(id);

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

        castAdapter = new CastAdapter(this, castList);
        layoutManagers = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCast.setLayoutManager(layoutManagers);
        recyclerViewCast.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCast.setAdapter(castAdapter);

        trailerAdapter = new TrailerAdapter(DetailActivity.this, trailerList);

        layoutTrailerManager = new GridLayoutManager(DetailActivity.this, 1);
        recyclerViewTrailer.setLayoutManager(layoutTrailerManager);
    }

    private void initAppBarScrollListener() {
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBar.getTotalScrollRange()) {
                    posterPath.setVisibility(View.INVISIBLE);
                } else if (verticalOffset == 0) {
                    posterPath.setVisibility(View.VISIBLE);
                } else {
                    posterPath.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void loadCast(int id) {
        Client.getService().getCast(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CastResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxDisposable.add(d);
                    }

                    @Override
                    public void onNext(CastResponse castResponse) {
                        if (castResponse != null) {
                            if (castResponse.getCasts() != null) {
                                if (castResponse.getCasts().size() == 0) {
                                    // if cast size is 0
                                } else {
                                    for (Cast c : castResponse.getCasts()) {
                                        castList.add(c);
                                    }

                                    castAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadTrailer(int id) {
        Client.getService().getTrailers(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TrailerResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxDisposable.add(d);
                    }

                    @Override
                    public void onNext(TrailerResponse trailerResponse) {
                        if (trailerResponse != null) {
                            if (trailerResponse.getResults() != null) {
                                if (trailerResponse.getResults().size() == 0) {
                                    // if size is 0
                                } else {
                                    for (Trailer t : trailerResponse.getResults()) {
                                        trailerList.add(t);
                                    }

                                    trailerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
