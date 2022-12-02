package com.example.ridex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Messages;
import com.example.ridex.models.Posts;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

/**
 Class Description:
 This class will show all the users you have messages with. You can access the chatrooms from here.
 */

public class InboxPageFragment extends Fragment {
    private static final String ACTIVITY_NAME = "InboxPageFragment";
    // UI variables
    RecyclerView messageInboxRecyclerView;
    // MongoDB Realm
    Realm realm;
    App app;
    RealmResults<ChatRoom> myInbox;

    public InboxPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
        realm = MainActivity.getRealm(getActivity());

        myInbox = realm.where(ChatRoom.class)
                .equalTo("userOneUID", app.currentUser().getId())
                .or()
                .equalTo("userTwoUID", app.currentUser().getId())
                .findAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_page, container, false);
        messageInboxRecyclerView = view.findViewById(R.id.messagesRecyclerView);

        if (myInbox.size() == 0){
            emptyPostingViews();
        } else {
//            Log.i(ACTIVITY_NAME, "Inbox: " + String.valueOf(myInbox));
            displayPostingViews(myInbox);
        }

        // Inflate the layout for this fragment
        return view;
    }

    public void displayPostingViews(RealmResults<ChatRoom> myInbox){
        InboxRealmAdapter inboxRealmAdapter =
                new InboxRealmAdapter(getContext(), myInbox, realm, app.currentUser().getId());
        messageInboxRecyclerView.setAdapter(inboxRealmAdapter);
        messageInboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inboxRealmAdapter.notifyDataSetChanged();
    }

    public void emptyPostingViews(){
        EmptyPostingAdapter emptyPostingAdapter = new EmptyPostingAdapter(getContext(),
                "No messages to show yet!");
        messageInboxRecyclerView.setAdapter(emptyPostingAdapter);
        messageInboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}