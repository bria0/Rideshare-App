package com.example.ridex;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ridex.databinding.ActivityMainBinding;
import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Messages;
import com.example.ridex.models.Posts;
import com.example.ridex.models.Ride;
import com.example.ridex.models.Users;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.AppException;
import io.realm.mongodb.ErrorCode;
import io.realm.mongodb.sync.MutableSubscriptionSet;
import io.realm.mongodb.sync.Subscription;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

/**
 Class Description:
 This class will allow the user to navigate through multiple fragments in the menubar
 */

public class MainActivity extends AppCompatActivity {
    private static final String ACTIVITY_NAME = "MainActivity";
    private static final String REALM_TAG = "__REALM__";

    //Realm Variables
    Realm realm;
    App app;
    SyncConfiguration configuration;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "onCreate()");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomePageFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            //change fragments depending on menubar
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomePageFragment());
                    break;
                case R.id.account:
                    replaceFragment(new AccountPageFragment());
                    break;
                case R.id.post:
                    replaceFragment(new CreatePostingsFragment());
                    break;
                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.inbox:
                    replaceFragment(new InboxPageFragment());
                    break;
            }

            return true;
        });

        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());

        configuration =
                new SyncConfiguration.Builder(app.currentUser())
                        .errorHandler(new SyncSession.ErrorHandler() {
                            @Override
                            public void onError(SyncSession session, AppException error) {
                                if (error.getErrorCode() == ErrorCode.BAD_AUTHENTICATION){
                                    app.currentUser().logOut();
                                }
                            }
                        })
                        .initialSubscriptions(new SyncConfiguration.InitialFlexibleSyncSubscriptions() {
                            @Override
                            public void configure(@NonNull Realm realm,
                                                  @NonNull MutableSubscriptionSet subscriptions) {
                                // Add a subscription with a name
                                subscriptions.addOrUpdate(Subscription.create("userQuery",
                                        realm.where(Users.class)
                                ));
                                subscriptions.addOrUpdate(Subscription.create("postsQuery",
                                        realm.where(Posts.class)));
                                subscriptions.addOrUpdate(Subscription.create("messageQuery",
                                        realm.where(Messages.class)));
                                subscriptions.addOrUpdate(Subscription.create("chatRoomQuery",
                                        realm.where(ChatRoom.class)));
                                subscriptions.addOrUpdate(Subscription.create("rideQuery",
                                        realm.where(Ride.class)));
                            }
                        })
                        .build();

        realm = Realm.getInstance(configuration);
    }

    //to change fragments
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    public static Realm getRealm(Context context) {
        // noinspection ResourceType
        return (Realm)context.getSystemService(REALM_TAG);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if(REALM_TAG.equals(name)) {
            return realm;
        }
        return super.getSystemService(name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "onDestroy()");
        realm.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "onStart()");
    }
}