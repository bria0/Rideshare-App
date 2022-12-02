package com.example.ridex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridex.models.Posts;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

/**
 Class Description:
 This class will allow the user to search postings based on locations
 */

public class SearchFragment extends Fragment {
    private static final String ACTIVITY_NAME = "SearchFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //UI
    EditText fromEditText, toEditText;
    TextView heading;
    Button searchBtn;
    RecyclerView postingsRecyclerView;
    Spinner searchAsDropdown;
    //Data
    String fromLocation = "", toLocation = "";
    Realm realm;
    RealmResults<Posts> allPosts;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }



    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        realm = MainActivity.getRealm(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //get all fields
        fromEditText = view.findViewById(R.id.from_editText);
        toEditText = view.findViewById(R.id.to_editText);
        heading = view.findViewById(R.id.heading);
        searchBtn = view.findViewById(R.id.search_btn);
        postingsRecyclerView = view.findViewById(R.id.postingsRecyclerView);
        searchAsDropdown = view.findViewById(R.id.searchAsDropdown);

        searchAsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //if locations are passed
        if (getArguments()!=null
                && !getArguments().getString("fromLocation").isEmpty()
                &&  !getArguments().getString("toLocation").isEmpty()) {
            fromLocation = getArguments().getString("fromLocation");
            toLocation = getArguments().getString("toLocation");
            fromEditText.setText(fromLocation);
            toEditText.setText(toLocation);
            heading.setText("Postings");
        }

        searchFilter();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFilter();
            }
        });
        return view;
    }

    public void searchFilter(){
        RealmResults<Posts> searchResults;

        if (fromEditText.getText().toString().isEmpty() && toEditText.getText().toString().isEmpty()){
            searchResults = realm.where(Posts.class)
                    .equalTo("postStatus", "Active")
                    .equalTo("postedAs",
                            searchAsDropdown.getSelectedItem().toString().equals("Rider") ? "Driver" : "Rider")
                    .sort("date", Sort.ASCENDING)
                    .findAll();
        } else if (fromEditText.getText().toString().isEmpty() &&
                !toEditText.getText().toString().isEmpty()){
            searchResults = realm.where(Posts.class)
                    .equalTo("toLocation", toEditText.getText().toString())
                    .equalTo("postStatus", "Active")
                    .equalTo("postedAs",
                            searchAsDropdown.getSelectedItem().toString().equals("Rider") ? "Driver" : "Rider")
                    .sort("date", Sort.ASCENDING)
                    .findAll();

        } else if (toEditText.getText().toString().isEmpty()
                && !fromEditText.getText().toString().isEmpty()){
            searchResults = realm.where(Posts.class)
                    .equalTo("fromLocation", fromEditText.getText().toString())
                    .equalTo("postStatus", "Active")
                    .equalTo("postedAs",
                            searchAsDropdown.getSelectedItem().toString().equals("Rider") ? "Driver" : "Rider")
                    .sort("date", Sort.ASCENDING)
                    .findAll();

        } else {
            searchResults = realm.where(Posts.class)
                    .equalTo("fromLocation", fromLocation)
                    .equalTo("toLocation", toLocation)
                    .equalTo("postedAs",
                            searchAsDropdown.getSelectedItem().toString().equals("Rider") ? "Driver" : "Rider")
                    .equalTo("postStatus", "Active")
                    .sort("date", Sort.ASCENDING)

                    .findAll();
        }

        if (searchResults.size() == 0){
            emptyPostingViews();
        } else {
            displayPostingViews(searchResults);
        }
    }


    public void displayPostingViews(RealmResults<Posts> posts){
        PostingsRealmAdapter postingsAdapter = new PostingsRealmAdapter(getContext(), posts);
        postingsRecyclerView.setAdapter(postingsAdapter);
        postingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postingsAdapter.notifyDataSetChanged();
    }

    public void emptyPostingViews(){
        EmptyPostingAdapter emptyPostingAdapter = new EmptyPostingAdapter(getContext(),
                "No postings for the search!");
        postingsRecyclerView.setAdapter(emptyPostingAdapter);
        postingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getArguments() != null){
            getArguments().clear();
        }
    }
}