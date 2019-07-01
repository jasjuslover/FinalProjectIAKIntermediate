package com.husnikamal.movex.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.husnikamal.movex.BuildConfig;
import com.husnikamal.movex.R;
import com.husnikamal.movex.adapter.RecyclerAdapter;
import com.husnikamal.movex.model.Movie;
import com.husnikamal.movex.model.MovieResponse;
import com.husnikamal.movex.network.Client;
import com.husnikamal.movex.network.Service;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final int SEARCH_TASK = 0, POPULAR_TASK = 1, NOW_PLAYING_TASK = 2, UPCOMING_TASK = 3;
    private CompositeDisposable rxDisposable;

    public LinearLayoutManager layoutManager;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressSearch)
    ProgressBar progressSearch;
    @BindView(R.id.toolbarTitle)
    LinearLayout toolbarTitle;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    private List<Movie> movieList;
    private String type;
    public RecyclerAdapter adapter;

    MaterialSearchView searchView;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        init();

        loadMovies(POPULAR_TASK, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.popular) {
            loadMovies(POPULAR_TASK, null);
            return true;
        } else if (id == R.id.now_playing) {
            loadMovies(NOW_PLAYING_TASK, null);
            return true;
        } else if (id == R.id.upcoming) {
            loadMovies(UPCOMING_TASK, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        movieList = new ArrayList<>();
        rxDisposable = new CompositeDisposable();
        adapter = new RecyclerAdapter(this, movieList);
        type = "popular";
        progressSearch.setVisibility(View.INVISIBLE);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_main);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        initSwipeListener();
        initSearchListener();
    }

    private void initSwipeListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                loadMovies(POPULAR_TASK, null);
            }
        });
    }

    private void initSearchListener() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit", "" + query);
                searchMovies(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("onQueryTextChange", "" + newText);
                searchMovies(newText);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                toolbarTitle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                toolbarTitle.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadMovies(int taskId, String text) {
        switch (taskId) {
            case SEARCH_TASK:
                searchMovies(text);
                break;
            case POPULAR_TASK:
                refreshList();
                loadMovies("popular");
                break;
            case NOW_PLAYING_TASK:
                refreshList();
                loadMovies("now_playing");
                break;
            case UPCOMING_TASK:
                refreshList();
                loadMovies("upcoming");
                break;
            default:
                refreshList();
                loadMovies("popular");
        }
    }

    private void refreshList() {
        movieList.clear();
        adapter.notifyDataSetChanged();
    }

    private void loadMovies(String type) {
        Client.getService().getMovie(type, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxDisposable.add(d);
                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        if (movieResponse != null) {
                            if (movieResponse.getResults() != null) {
                                if (movieResponse.getResults().size() == 0) {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinator, "Movie not found", Snackbar.LENGTH_LONG)
                                            .setAction("OK", null);
                                    snackbar.show();
                                } else {
                                    for (Movie m : movieResponse.getResults()) {
                                        movieList.add(m);
                                    }
                                }

                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                progressSearch.setVisibility(View.GONE);
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

    private void searchMovies(String text) {
        Client.getService().searchMovie(BuildConfig.API_KEY, text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxDisposable.add(d);
                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        if (movieResponse != null) {
                            if (movieResponse.getResults() != null) {
                                if (movieResponse.getResults().size() == 0) {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinator, "Movie not found", Snackbar.LENGTH_LONG)
                                            .setAction("OK", null);
                                    snackbar.show();
                                } else {
                                    movieList.clear();
                                    for (Movie m : movieResponse.getResults()) {
                                        movieList.add(m);
                                    }
                                }

                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                progressSearch.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        rxDisposable.clear();
        super.onDestroy();
    }
}
