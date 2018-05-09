package com.cog.arcaneRider.adapter;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cog.arcaneRider.R;
import com.cog.arcaneRider.YourTripsActivity;

import java.util.ArrayList;
import java.util.List;

public class YourTripsListAdapter extends RecyclerView.Adapter {
    private List<YourTrips> tripsListItems;
    YourTripsActivity activity;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public YourTripsListAdapter(YourTripsActivity activity, ArrayList<YourTrips> tripsListItems,RecyclerView recyclerView) {
        this.activity = activity;
        this.tripsListItems= tripsListItems;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        if(viewType == VIEW_ITEM){
            View convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_your_trips_list, parent, false);
            FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), activity.getString(R.string.app_font));
            fontChanger.replaceFonts((ViewGroup)convertView);
            viewHolder = new MyViewHolder(convertView);
        }else{
            View convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.loadmore_progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(convertView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyViewHolder){
            YourTrips trips = tripsListItems.get(position);

            try {

                Glide.with(activity).load(trips.getDriverImage()).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget( ((MyViewHolder) holder).driverImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ((MyViewHolder) holder).driverImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

               /* Glide.with(getApplicationContext()).load(trips.getCarImage()).asBitmap().centerCrop().placeholder(R.drawable.trip_car)
                        .skipMemoryCache(true).into(((MyViewHolder) holder).carImage);*/

//            driverName.setText(trips.getDriverName());
                ((MyViewHolder) holder).tripAmount.setText("$ "+trips.getTripAmount());
                ((MyViewHolder) holder).tripDate.setText(trips.getTripDate());
                ((MyViewHolder) holder).carType.setText(trips.getCarType());
                ((MyViewHolder) holder).pickupLocation.setText(trips.getPickupLocation());
                ((MyViewHolder) holder).dropLocation.setText(trips.getDropLocation());
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage,driverImage;
        TextView driverName,tripAmount,tripDate,carType,pickupLocation,dropLocation;

        public MyViewHolder(View convertView) {
            super(convertView);
            carImage=(ImageView)convertView.findViewById(R.id.car_image);
            driverImage=(ImageView)convertView.findViewById(R.id.driver_image);
            driverName=(TextView)convertView.findViewById(R.id.driver_name);
            tripAmount=(TextView)convertView.findViewById(R.id.trip_amount);
            tripDate=(TextView)convertView.findViewById(R.id.trip_date);
            carType=(TextView)convertView.findViewById(R.id.trip_car_trype);
            pickupLocation=(TextView)convertView.findViewById(R.id.pick_location);
            dropLocation=(TextView)convertView.findViewById(R.id.drop_location);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public int getItemCount() {
        return tripsListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return tripsListItems.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

}
