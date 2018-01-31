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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final int SEARCH_TASK = 0, POPULAR_TASK = 1, NOW_PLAYING_TASK = 2, UPCOMING_TASK = 3;
    private final String API_KEY = "dfe1a9fa143f0e48abfd687fbc950e49";

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
    public RecyclerAdapter adapter;

    MaterialSearchView searchView;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        progressSearch.setVisibility(View.INVISIBLE);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

//        Set Layout to GridLayout and set column to 2.
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

//        When screen swiping
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_main);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                loadMovies(POPULAR_TASK, null);
            }
        });

//        Initialize the recyclerview when portrait and landscape
        init();

//        Load Popular Movies as default recyclerView
        loadMovies(POPULAR_TASK, null);

//        Search Function (In Menu Bar)
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                When Text Submitted. Logging for test, is the text changed or not.
                Log.d("onQueryTextSubmit", "" + query);
                loadMovies(SEARCH_TASK, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                When text changed. Logging for test, is the text changed or not.
                Log.d("onQueryTextChange", "" + newText);
                loadMovies(SEARCH_TASK, newText);
                return false;
            }
        });

//        Set View when MaterialSearch is show and close
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
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

//        Initialize reciclerview when portrait and landscape. If screen is portrait, recyclerview will set to 2 columns, and set to 4 columns when landscape
    private void init() {
        movieList = new ArrayList<>();
//        RecyclerAdapter constructor need two identifier. Context and List
        adapter = new RecyclerAdapter(this, movieList);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

//        Method for load the movies. SEARCH_TASK for call searchMovie with apiKey and query, POPULAR_TASK for getPopular and NOW_PLAYING_TASK for getNowPlaying
    private void loadMovies(int taskId, String text) {
        Client client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<MovieResponse> responseCall;
        switch (taskId) {
            case SEARCH_TASK:
                responseCall = apiService.searchMovie(API_KEY, text);
                break;
            case POPULAR_TASK:
                responseCall = apiService.getMovie("popular", API_KEY);
                break;
            case NOW_PLAYING_TASK:
                responseCall = apiService.getMovie("now_playing", API_KEY);
                break;
            case UPCOMING_TASK:
                responseCall = apiService.getMovie("upcoming", API_KEY);
                break;
            default:
                responseCall = apiService.getMovie("popular", API_KEY);
        }
        responseCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
//                    If app get response, progressBar will show
                    progressSearch.setVisibility(View.VISIBLE);

//                    If number of movie is zero, SnackBar will show
                    if (response.body().getResults().size() == 0) {
                        Snackbar snackbar = Snackbar
                                .make(coordinator, "Movie not found", Snackbar.LENGTH_LONG)
                                .setAction("OK", null);
                        snackbar.show();
                    } else {
                        recyclerView.setAdapter(new RecyclerAdapter(MainActivity.this, response.body().getResults()));
                        swipeRefreshLayout.setRefreshing(false);
                        progressSearch.setVisibility(View.INVISIBLE);
                    }
//                    recyclerView.setAdapter(new RecyclerAdapter(MainActivity.this, response.body().getResults()));
//                    swipeRefreshLayout.setRefreshing(false);
//                    progressSearch.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Data", "" + t.getMessage());
            }
        });
    }


//        responseCall.enqueue(new Callback<MovieResponse>() {
//            @Override
//            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
//                if (response.isSuccessful()) {
////                    If app get response, progressBar will show
//                    progressSearch.setVisibility(View.VISIBLE);
//                    recyclerView.setAdapter(new RecyclerAdapter(MainActivity.this, response.body().getResults()));
//                    swipeRefreshLayout.setRefreshing(false);
//                    progressSearch.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieResponse> call, Throwable t) {
//                Log.d("Data", "" + t.getMessage());
//            }
//        });
}
