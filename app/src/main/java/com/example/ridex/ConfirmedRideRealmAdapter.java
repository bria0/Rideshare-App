package com.example.ridex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Messages;
import com.example.ridex.models.Posts;
import com.example.ridex.models.Users;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 Class Description:
 This class will add the confirmed ride to the list of confirmed rides in the database
 */

public class ConfirmedRideRealmAdapter
        extends RealmRecyclerViewAdapter<Posts,
        ConfirmedRideRealmAdapter.MyViewHolder> {
    public static final String ACTIVITY_NAME = "InboxRealmAdapter";

    Context context;
    RealmResults<Posts> confirmedRidePosts;
    Realm realm;

    public ConfirmedRideRealmAdapter(Context context_taken, Realm realm_taken, RealmResults<Posts> confirmedRidePosts_taken){
        super(confirmedRidePosts_taken, true);
        context = context_taken;
        realm = realm_taken;
        confirmedRidePosts = confirmedRidePosts_taken;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirmed_post_listing_layout, parent, false);
        return new ConfirmedRideRealmAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Users driverUser = realm.where(Users.class)
                .equalTo("uid", confirmedRidePosts.get(position)
                        .getPosterUID()).findFirst();

        holder.date_time.setText(String.format("%s · %s",
                confirmedRidePosts.get(position).getDate(),
                confirmedRidePosts.get(position).getTime()));
        holder.seats_available.setText("Number of Seats: " + String.valueOf(confirmedRidePosts
                .get(position).getNumberOfSeats()));
        holder.driver_name.setText(String.format("Driver: %s %s", driverUser.getFirstName(),
                driverUser.getLastName()));
        holder.car_info.setText(String.format("Car: %s %s",
                confirmedRidePosts.get(position).getCarModel(),
                confirmedRidePosts.get(position).getCarYear()));
        holder.destination_name.setText(confirmedRidePosts.get(position).getToLocation());

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = confirmedRidePosts.get(position).getId().toString();
                Bundle bundle = new Bundle();
                bundle.putString("postId", postID);
                PostDetailsFragment postDetailsFragment = new PostDetailsFragment();
                postDetailsFragment.setArguments(bundle);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frameLayout, postDetailsFragment).commit();
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date_time, seats_available, driver_name, car_info, destination_name;
        ImageButton moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI Variables
            date_time = itemView.findViewById(R.id.date_time);
            seats_available = itemView.findViewById(R.id.seats_available);
            driver_name = itemView.findViewById(R.id.driver_name);
            car_info = itemView.findViewById(R.id.car_info);
            destination_name = itemView.findViewById(R.id.destination_name);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }

}
