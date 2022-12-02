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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Messages;
import com.example.ridex.models.Users;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;
/**
 Class Description:
 This class will contain information for each message in the inbox
 */

public class MessagesRealmAdapter
        extends RealmRecyclerViewAdapter<Messages,
        RecyclerView.ViewHolder> {
    public static final String ACTIVITY_NAME = "MessagesRealmAdapter";

    Context context;
    RealmResults<Messages> inboxList;
    String currentUserUID;

    public MessagesRealmAdapter(Context context_taken, RealmResults<Messages> inbox, String currentUser){
        super(inbox, true, true);
        context = context_taken;
        inboxList = inbox;
        currentUserUID = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        if (inboxList.get(position).getSenderUID().equals(currentUserUID)){
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.sent_message_container, parent, false);
                return new SenderViewHolder(view);
            case 1:
                View view2 = inflater.inflate(R.layout.received_message_container, parent, false);
                return new ReceiverViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                // Sending
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.sentMessage.setText(inboxList.get(position).getMessageSent());
                senderViewHolder.sentMessageTimeDate.setText(inboxList.get(position).getTime());
                break;
            case 1:
                // Receiving
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.receivedMessage.setText(inboxList.get(position).getMessageSent());
                receiverViewHolder.receivedMessageTimeDate.setText(inboxList.get(position).getTime());
                break;
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessage, sentMessageTimeDate;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI Variables
            sentMessage = itemView.findViewById(R.id.sentMessage);
            sentMessageTimeDate = itemView.findViewById(R.id.sentMessageTimeDate);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessage, receivedMessageTimeDate;
        ImageView profilePhoto;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI Variables
            receivedMessage = itemView.findViewById(R.id.receivedMessage);
            receivedMessageTimeDate = itemView.findViewById(R.id.receivedMessageTimeDate);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
        }
    }
}
