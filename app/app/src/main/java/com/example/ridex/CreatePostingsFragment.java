package com.example.ridex;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.example.ridex.models.Posts;
import com.example.ridex.models.Users;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
 This class will be used to select whether user wants to create a posting as a rider or a driver
 */

public class CreatePostingsFragment extends Fragment {
    private static final String ACTIVITY_NAME = "CreatePostingsFragment";
    private static final String REALM_TAG = "__REALM__";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Global UI variables
    Spinner searchAs;

    // End global UI variables

    public CreatePostingsFragment() {
        // Required empty public constructor
    }

    public static CreatePostingsFragment newInstance(String param1, String param2) {
        CreatePostingsFragment fragment = new CreatePostingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(ACTIVITY_NAME, "onCreate()");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    // TODO: Add pricing to the schema.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_create_postings, container, false);

        // Initialize UI variables
        searchAs = view.findViewById(R.id.createPostDropdown);
        // End initialize UI variables

        String selectedSearchAs = searchAs.getSelectedItem().toString();
        replaceCreatePostingView(selectedSearchAs);

        searchAs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getSelectedItem().toString();
                replaceCreatePostingView(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void replaceCreatePostingView(String searchAs){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (searchAs.equals("Rider")){
            fragmentTransaction.replace(R.id.createPostingFrameLayout, new RiderCreatePostingFragment());
        } else {
            fragmentTransaction.replace(R.id.createPostingFrameLayout, new DriverCreatePostingFragment());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(ACTIVITY_NAME, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "onDestroy()");
    }
}