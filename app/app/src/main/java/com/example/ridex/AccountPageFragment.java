package com.example.ridex;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridex.models.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

/**
Class Description:
 This class will display the user account information. We can logout, go to help page, or view my listings page

 */
public class AccountPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ACTIVITY_NAME = "AccountPageFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI Variables
    ImageButton logoutBtn, helpBtn, myListingsBtn;
    TextView fullName, numberOfRidesDriven, numberOfRidesTaken, overAllRating;

    // Realm variables.
    Users currUsersInfo;
    Realm realm;
    RealmObjectChangeListener<Users> userListener;
    App app;

    public AccountPageFragment() {
        // Required empty public constructor
    }



    public static AccountPageFragment newInstance(String param1, String param2) {
        AccountPageFragment fragment = new AccountPageFragment();
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

        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());

        realm = MainActivity.getRealm(getActivity());

        currUsersInfo = realm.where(Users.class)
                .equalTo("uid", app.currentUser().getId()).findFirst();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_page, container, false);
        // Initialize the variables
        logoutBtn = view.findViewById(R.id.logoutBtn);
        helpBtn = view.findViewById(R.id.helpBtn);
        fullName = view.findViewById(R.id.userFullName);
        numberOfRidesDriven = view.findViewById(R.id.numberOfRidesDriven);
        numberOfRidesTaken = view.findViewById(R.id.numberOfRidesTaken);
        overAllRating = view.findViewById(R.id.overAllRating);
        myListingsBtn = view.findViewById(R.id.myListingsImageBtn);
        // End of the initialization of the variable

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
                User user = app.currentUser();
                assert user != null;
                user.logOutAsync(result -> {
                    if (result.isSuccess()){
                        Toast.makeText(getActivity(),
                                "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),
                                LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(),
                                "Unable to logout successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getFragmentManager().beginTransaction().replace(R.id.frameLayout, new HelpPageFragment()).commit();

            }
        });

        myListingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyListingsFragment()).commit();
            }
        });

        userListener = (changedUser, changeSet) -> {
            if (changeSet.isDeleted()){
                Log.i(ACTIVITY_NAME, "User is deleted!");
                Toast.makeText(view.getContext(),
                        "There is seems to be some problem with your account!",
                        Toast.LENGTH_SHORT).show();
                app.currentUser().logOutAsync(result -> {
                    if (result.isSuccess()){
                        Intent intent = new Intent(getActivity(),
                                LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }

            for (String fieldName : changeSet.getChangedFields()){
                Log.i(ACTIVITY_NAME, "Field '" + fieldName + "' changed.");
                switch (fieldName) {
                    case "lastName":
                    case "firstName":
                        fullName.setText(String.format("%s %s",
                                currUsersInfo.getFirstName(), currUsersInfo.getLastName()));
                        break;
                    case "email":
                        Log.i(ACTIVITY_NAME, "Email is changed!");
                        break;
                    case "numberOfRidesDriven":
                        numberOfRidesDriven.setText(String.valueOf(currUsersInfo.getNumberOfRidesDriven()));
                        break;
                    case "numberOfRidesTaken":
                        numberOfRidesTaken.setText(String.valueOf(currUsersInfo.getNumberOfRidesTaken()));
                        break;
                    case "overAllRating":
                        overAllRating.setText(String.format("⭐️ %d", currUsersInfo.getOverAllReview()));
                        break;
                    default:
                        break;
                }
            }
        };

        currUsersInfo.addChangeListener(userListener);

        fullName.setText(String.format("%s %s",
                currUsersInfo.getFirstName(), currUsersInfo.getLastName()));

        numberOfRidesDriven.setText(String.valueOf(currUsersInfo.getNumberOfRidesDriven()));
        numberOfRidesTaken.setText(String.valueOf(currUsersInfo.getNumberOfRidesTaken()));
        overAllRating.setText(String.format("⭐️ %d", currUsersInfo.getOverAllReview()));


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currUsersInfo.removeChangeListener(userListener);
    }
}