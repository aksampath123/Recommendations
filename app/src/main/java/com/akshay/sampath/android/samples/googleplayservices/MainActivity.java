package com.akshay.sampath.android.samples.googleplayservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.akshay.sampath.android.samples.googleplayservices.api.Etsy;
import com.akshay.sampath.android.samples.googleplayservices.google.GoogleServicesHelper;
import com.akshay.sampath.android.samples.googleplayservices.model.ActiveListings;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_ACTIVE_LISTINGS = "StateActiveListings";

    private RecyclerView mRecyclerView;
    private View mProgressBar;
    private TextView errorView;

    private GoogleServicesHelper mGoogleServicesHelper;
    private ListingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = findViewById(R.id.progressbar);
        errorView = (TextView) findViewById(R.id.error_view);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new ListingAdapter(this);

        mRecyclerView.setAdapter(mAdapter);

        mGoogleServicesHelper = new GoogleServicesHelper(this, mAdapter);

        showLoading();

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(STATE_ACTIVE_LISTINGS)) {
                mAdapter.success((ActiveListings) savedInstanceState.getParcelable(STATE_ACTIVE_LISTINGS), null);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleServicesHelper.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleServicesHelper.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleServicesHelper.handleActivityResult(requestCode, resultCode, data);

        if(requestCode == ListingAdapter.REQUEST_CODE_PLUS_ONE) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActiveListings activeListings = mAdapter.getActiveListings();
        if(activeListings != null){
            outState.putParcelable(STATE_ACTIVE_LISTINGS, activeListings);
        }
        else{
            showLoading();
            Etsy.getActiveListings(mAdapter);
        }
    }

    public void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    public void showList(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    public void showError(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }
}
