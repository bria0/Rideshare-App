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
import com.example.ridex.models.Posts;
import com.example.ridex.models.Users;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

/**
 Class Description:
 This class will collect data to display on the inbox page
 */

public class InboxRealmAdapter
        extends RealmRecyclerViewAdapter<ChatRoom,
        InboxRealmAdapter.MyViewHolder> {
    public static final String ACTIVITY_NAME = "InboxRealmAdapter";

    Context context;
    RealmResults<ChatRoom> inboxList;
    String currentUserUID;
    Realm realm;

    public InboxRealmAdapter(Context context_taken, RealmResults<ChatRoom> inbox, Realm realm_taken, String userID){
        super(inbox, true, true);
        context = context_taken;
        inboxList = inbox;
        currentUserUID = userID;
        realm = realm_taken;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inbox_message_layout, parent, false);
        return new InboxRealmAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String fullName = getOtherUserFullName(position);
        String lastMessage = getLastMessage(position);
        holder.fullName.setText(fullName);
        holder.lastMessage.setText(lastMessage);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_NAME, "Clicked on the chatroom!");

                Bundle bundle = new Bundle();
                bundle.putString("chatRoomID", inboxList.get(position).getId().toString());
                bundle.putString("senderName", fullName);
                ChatWindowFragment chatWindowFragment = new ChatWindowFragment();
                chatWindowFragment.setArguments(bundle);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frameLayout,
                                chatWindowFragment).commit();
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, lastMessage;
        ImageButton moreBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI Variables
            fullName = itemView.findViewById(R.id.userFullName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            moreBtn = itemView.findViewById(R.id.messageMoreBtn);
        }
    }

    public String getOtherUserFullName(int position){
        String name;
        Users user;
        if (inboxList.get(position).getUserOneUID().equals(currentUserUID)){
            // This means we need to get name for the userTwo.
            user = realm.where(Users.class)
                    .equalTo("uid", inboxList.get(position).getUserTwoUID()).findFirst();
        } else {
            user = realm.where(Users.class)
                    .equalTo("uid", inboxList.get(position).getUserOneUID()).findFirst();
        }
        name = String.format("%s %s", user.getFirstName(), user.getLastName());
        return name;
    }

    public String getLastMessage(int position){
        Messages lastMessage =
                realm.where(Messages.class)
                        .equalTo("_id",
                                new ObjectId(inboxList.get(position).getMessages()
                                        .get(inboxList.get(position).getMessages().size() - 1))).findFirst();
        Log.i(ACTIVITY_NAME, "lastMessage: " + String.valueOf(lastMessage));
        try {
            return lastMessage.getMessageSent();
        } catch (NullPointerException e){
            return "No messages yet!";
        }
    }
}
