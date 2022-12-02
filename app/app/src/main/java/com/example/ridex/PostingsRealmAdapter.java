package com.example.ridex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridex.models.Posts;

import java.util.zip.Inflater;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

/**
 Class Description:
 This class will save the post details in the database
 */

public class PostingsRealmAdapter
        extends RealmRecyclerViewAdapter<Posts,
        PostingsRealmAdapter.MyViewHolder> {
    public static final String ACTIVITY_NAME = "PostingsRealmAdapter";

    Context context;
    RealmResults<Posts> postsList;

    public PostingsRealmAdapter(Context context_taken, RealmResults<Posts> allPosts){
        super(allPosts, true, true);
        context = context_taken;
        postsList = allPosts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_listing_layout, parent, false);
        return new PostingsRealmAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.postedAs.setText(postsList.get(position).getPostedAs());
        holder.postDate.setText(postsList.get(position).getDate());
        holder.fromLocation.setText(postsList.get(position).getFromLocation());
        holder.toLocation.setText(postsList.get(position).getToLocation());
        if (postsList.get(position).getPostedAs().equals("Driver")){
            holder.seatsOrPriceImage.setImageResource(R.drawable.money);
            holder.seatsOrPriceText.setText("$"+
                    String.valueOf(postsList.get(position).getPrice()));
        } else {
            holder.seatsOrPriceText.setText(
                    String.valueOf(postsList.get(position).getNumberOfSeats())
            );
        }
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postID = postsList.get(position).getId().toString();
                Bundle bundle = new Bundle();
                bundle.putString("postId", postID);
                PostDetailsFragment postDetailsFragment = new PostDetailsFragment();
                postDetailsFragment.setArguments(bundle);

                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, postDetailsFragment).commit();
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView postedAs, postDate, fromLocation, toLocation, seatsOrPriceText;
        ImageView seatsOrPriceImage, fromLocationImage, toLocationImage;
        View divider;
        ImageButton moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI Variables
            postedAs = itemView.findViewById(R.id.postedAs);
            postDate = itemView.findViewById(R.id.postDate);
            fromLocation = itemView.findViewById(R.id.fromLocationText);
            toLocation = itemView.findViewById(R.id.toLocationText);
            seatsOrPriceImage = itemView.findViewById(R.id.seatsOrPriceImage);
            seatsOrPriceText = itemView.findViewById(R.id.seatsOrPriceText);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
