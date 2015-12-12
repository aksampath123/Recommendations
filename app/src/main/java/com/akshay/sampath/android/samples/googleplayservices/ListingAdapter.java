package com.akshay.sampath.android.samples.googleplayservices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshay.sampath.android.samples.googleplayservices.api.Etsy;
import com.akshay.sampath.android.samples.googleplayservices.google.GoogleServicesHelper;
import com.akshay.sampath.android.samples.googleplayservices.model.ActiveListings;
import com.akshay.sampath.android.samples.googleplayservices.model.Listing;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Akshay on 2015-11-22.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
        implements Callback<ActiveListings>, GoogleServicesHelper.GoogleServicesListener {

    public static final int REQUEST_CODE_PLUS_ONE = 10;
    public static final int REQUEST_CODE_SHARE = 11;

    private MainActivity mActivity;
    private LayoutInflater mInflater;
    private ActiveListings mActiveListings;

    private boolean mIsGooglePlayServicesAvailable;

    public ListingAdapter(MainActivity activity){
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mIsGooglePlayServicesAvailable = false;
    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListingHolder(mInflater.inflate(R.layout.layout_listing, parent,false));
    }

    @Override
    public void onBindViewHolder(ListingHolder holder, int position) {
        final Listing listing = mActiveListings.results[position];
        holder.mTitleView.setText(listing.title);
        holder.mPriceView.setText(listing.price);
        holder.mShopNameView.setText(listing.Shop.shop_name);

        if(mIsGooglePlayServicesAvailable){
            holder.mPlusOneButton.setVisibility(View.VISIBLE);
            holder.mPlusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
            holder.mPlusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);

            holder.mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new PlusShare.Builder(mActivity)
                            .setType("text/plain")
                            .setText("Check this item out on Etsy " + listing.title)
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();

                    mActivity.startActivityForResult(intent, REQUEST_CODE_SHARE);
                }
            });
        }
        else{
            holder.mPlusOneButton.setVisibility(View.GONE);

            holder.mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Checkout this item on Etsy " + listing.title + " " + listing.url);
                    intent.setType("text/plain");

                    mActivity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
                }
            });
        }

        Picasso.with(holder.mImageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if(mActiveListings == null){
            return 0;
        }
        if(mActiveListings.results == null){
            return 0;
        }
        return mActiveListings.results.length;
    }

    @Override
    public void success(ActiveListings activeListings, Response response) {
        mActiveListings = activeListings;
        notifyDataSetChanged();
        mActivity.showList();
    }

    @Override
    public void failure(RetrofitError error) {
        mActivity.showError();
    }

    public ActiveListings getActiveListings(){
        return mActiveListings;
    }

    @Override
    public void onConnected() {

        if(getItemCount() == 0){
            Etsy.getActiveListings(this);
        }

        mIsGooglePlayServicesAvailable = true;
        notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {

        if(getItemCount() == 0){
            Etsy.getActiveListings(this);
        }

        mIsGooglePlayServicesAvailable = false;
        notifyDataSetChanged();
    }

    public class ListingHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTitleView;
        public TextView mShopNameView;
        public TextView mPriceView;
        public PlusOneButton mPlusOneButton;
        public ImageButton mShareButton;

        public ListingHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.listing_image);
            mTitleView = (TextView) itemView.findViewById(R.id.listing_title);
            mShopNameView = (TextView) itemView.findViewById(R.id.listing_shop_name);
            mPriceView = (TextView) itemView.findViewById(R.id.listing_price);
            mPlusOneButton = (PlusOneButton) itemView.findViewById(R.id.listing_plus_one_btn);
            mShareButton = (ImageButton) itemView.findViewById(R.id.listing_share_btn);
        }
    }
}
