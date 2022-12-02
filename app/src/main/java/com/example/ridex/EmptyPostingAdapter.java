package com.example.ridex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 Class Description:
 To display when there are no posts available to display
 */

public class EmptyPostingAdapter extends RecyclerView.Adapter<EmptyPostingAdapter.MyViewHolder> {

    Context context;
    String message;

    public EmptyPostingAdapter(Context context_taken, String message_taken) {
        context = context_taken;
        message = message_taken;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.empty_post_listing_layout, parent, false);
        return new EmptyPostingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.message.setText(message);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.listingTitle);
        }
    }
}
