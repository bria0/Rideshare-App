package com.example.ridex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.ridex.models.Posts;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

/**
 Class Description:
 This class will save all the listings that are created by the user
 */

public class MyListingsFragment extends Fragment {
    public static final String ACTIVITY_NAME = "MyListingsFragment";
    // UI
    RecyclerView myListingsRecyclerView;
    // MongoDB
    App app;
    Realm realm;
    RealmResults<Posts> myPostings;
    BottomNavigationView menu;
    FrameLayout frameLayout;
    ImageButton backImage;

    public MyListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
        realm = MainActivity.getRealm(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_listings, container, false);

        //get fields
        menu = getActivity().findViewById(R.id.bottomNavigation);
        frameLayout = getActivity().findViewById(R.id.frameLayout);
        backImage = view.findViewById(R.id.backImage);

        menu.setVisibility(View.GONE);

        myListingsRecyclerView = view.findViewById(R.id.myListingsRecyclerView);

        myPostings = realm.where(Posts.class)
                .equalTo("posterUID",
                        app.currentUser().getId())
                .sort("date", Sort.ASCENDING)
                .findAll();

        if (myPostings.size() == 0){
            emptyPostingViews();
        } else {
            displayPostingViews(myPostings);
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AccountPageFragment()).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void displayPostingViews(RealmResults<Posts> posts){
        PostingsRealmAdapter myPostings = new PostingsRealmAdapter(getContext(), posts);
        myListingsRecyclerView.setAdapter(myPostings);
        myListingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myPostings.notifyDataSetChanged();
    }
    public void emptyPostingViews(){
        EmptyPostingAdapter emptyPostingAdapter = new EmptyPostingAdapter(getContext(),
                "No postings for the search!");
        myListingsRecyclerView.setAdapter(emptyPostingAdapter);
        myListingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        menu.setVisibility(View.VISIBLE);
    }
}