package com.example.ridex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ridex.models.Posts;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

/**
 Class Description:
 This class will let the user create a posting as a rider
 */

public class RiderCreatePostingFragment extends Fragment {
    public final static String ACTIVITY_NAME = "RiderCreatePostingFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // MongoDB Realm Variables
    App app;
    Realm realm;
    Posts riderPost;

    // UI Variables
    EditText fromLocation,
            toLocation,
            date,
            postDescription;
    Spinner seatsRequired, time;
    Button postBtn;


    public RiderCreatePostingFragment() {
        // Required empty public constructor
    }



    public static RiderCreatePostingFragment newInstance(String param1, String param2) {
        RiderCreatePostingFragment fragment = new RiderCreatePostingFragment();
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

        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
        realm = MainActivity.getRealm(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rider_create_posting,
                container, false);

        // Assign UI elements
        fromLocation = view.findViewById(R.id.riderFromLocation);
        toLocation = view.findViewById(R.id.riderToLocation);
        date = view.findViewById(R.id.riderDateForCarpool);
        time = view.findViewById(R.id.riderTimeForCarpool);
        seatsRequired = view.findViewById(R.id.seatsRequiredDropdown);
        postDescription = view.findViewById(R.id.postDescription);
        postBtn = view.findViewById(R.id.riderPostBtn);
        // End of assigning UI elements

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromLocation.getText().toString().isEmpty() ||
                        toLocation.getText().toString().isEmpty() ||
                        date.getText().toString().isEmpty()
                ) {
                    // Please fill in all the details before creating all the information.
                    Toast.makeText(requireContext(),
                            "Please fill in all the details before creating all the information.",
                            Toast.LENGTH_LONG).show();
                } else {
                    realm.executeTransactionAsync(r -> {
                        Posts riderPost = r.createObject(
                                Posts.class,
                                new ObjectId()
                        );
                        riderPost.setPostedAs("Rider");
                        riderPost.setPostStatus("Active");
                        riderPost.setPosterUID(app.currentUser().getId());
                        riderPost.setFromLocation(fromLocation.getText().toString());
                        riderPost.setToLocation(toLocation.getText().toString());
                        riderPost.setDate(date.getText().toString());
                        riderPost.setTime(time.getSelectedItem().toString());
                        riderPost.setNumberOfSeats(
                                Integer.parseInt(seatsRequired.getSelectedItem().toString()));
                        riderPost.setPostDescription(postDescription.getText().toString());

                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(),
                                    "Inserted the post into the db!", Toast.LENGTH_LONG).show();

                            fromLocation.setText("");
                            toLocation.setText("");
                            date.setText("");
                            time.setSelection(0);
                            postDescription.setText("");
                            seatsRequired.setSelection(0);
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Log.i(ACTIVITY_NAME, "Error inserting: " + error.toString());
                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}