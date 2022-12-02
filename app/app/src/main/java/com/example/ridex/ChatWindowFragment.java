package com.example.ridex;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Messages;
import com.example.ridex.models.Posts;
import com.example.ridex.models.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

/**
Class Description:
 This class will open up the chatroom with another user. It will load all the previous messages and send new messages.
 */
public class ChatWindowFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ACTIVITY_NAME = "ChatWindowFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Data taken:
    String chatRoomTaken;
    String chatRoomTitle;

    // MongoDB Realm
    App app;
    Realm realm;
    RealmResults<Messages> allMessages;
    MessagesRealmAdapter messagesRealmAdapter;

    //UI
    BottomNavigationView menu;
    ImageButton backBtn;
    FrameLayout frameLayout;
    TextView chatName;
    ProgressBar chatProgressBar;
    EditText inputMessage;
    ImageButton sendMessageBtn;
    RecyclerView chatRecyclerView;



    public ChatWindowFragment() {
        // Required empty public constructor
    }


    public static ChatWindowFragment newInstance(String param1, String param2) {
        ChatWindowFragment fragment = new ChatWindowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatRoomTaken = getArguments().getString("chatRoomID");
            chatRoomTitle = getArguments().getString("senderName");
        }
        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
        realm = MainActivity.getRealm(getActivity());
        allMessages = realm.where(Messages.class)
                .equalTo("chatRoomID", chatRoomTaken).findAllAsync();
        allMessages.load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_window, container, false);

        //get fields
        menu = getActivity().findViewById(R.id.bottomNavigation);
        frameLayout = getActivity().findViewById(R.id.frameLayout);
        // UI Variables
        backBtn = view.findViewById(R.id.backImage);
        chatName = view.findViewById(R.id.chatName);
        chatProgressBar = view.findViewById(R.id.chatProgressBar);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendMessageBtn = view.findViewById(R.id.sendMessageBtn);
        chatRecyclerView = view.findViewById(R.id.chatRoomMessagesRecyclerView);

        chatName.setText(chatRoomTitle);

        if (allMessages.isLoaded()){
            Log.i(ACTIVITY_NAME, String.valueOf(allMessages));
            chatProgressBar.setVisibility(View.INVISIBLE);
            displayMessages(allMessages);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new InboxPageFragment()).commit();
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputMessage.getText().toString().isEmpty()){
                    realm.executeTransactionAsync(messageRealm -> {
                        Users currentUser = messageRealm.where(Users.class)
                                .equalTo("uid", app.currentUser().getId()).findFirst();
                        ChatRoom chatRoom = messageRealm.where(ChatRoom.class)
                                .equalTo("_id", new ObjectId(chatRoomTaken)).findFirst();

                        // Add message
                        ObjectId messageID = new ObjectId();
                        Messages newMessage = messageRealm.createObject(Messages.class, messageID);
                        Log.i(ACTIVITY_NAME, "executing!!");
                        newMessage.setChatRoomID(chatRoomTaken);
                        newMessage.setFirstName(currentUser.getFirstName());
                        newMessage.setLastName(currentUser.getLastName());
                        newMessage.setMessageSent(inputMessage.getText().toString());
                        newMessage.setSenderUID(currentUser.getUid());
                        newMessage.setTime("8:00 PM");

                        // Update chatRoom to include the last Message idx
                        chatRoom.getMessages().add(String.valueOf(messageID));

                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            inputMessage.setText("");
                            try {
                                chatRecyclerView.smoothScrollToPosition(messagesRealmAdapter.getItemCount() - 1);
                            } catch (Exception e) {
                                displayMessages(allMessages);
                            }
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Log.i(ACTIVITY_NAME, "Error: "+ error.toString());
                        }
                    });
                }
            }
        });

        //hide menubar
        menu.setVisibility(View.GONE);
        return view;
    }

    public void displayMessages(RealmResults<Messages> messages){
        Log.i(ACTIVITY_NAME, "displayMessages()");
        if (messages.size() != 0){
            messagesRealmAdapter=
                    new MessagesRealmAdapter(getContext(), messages, app.currentUser().getId());
            chatRecyclerView.setAdapter(messagesRealmAdapter);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatRecyclerView.smoothScrollToPosition(messagesRealmAdapter.getItemCount() - 1);
            messagesRealmAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(ACTIVITY_NAME, "OnDestroyView");
        menu.setVisibility(View.VISIBLE);
    }
}