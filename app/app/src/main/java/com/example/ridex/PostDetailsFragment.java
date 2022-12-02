package com.example.ridex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridex.models.ChatRoom;
import com.example.ridex.models.Posts;
import com.example.ridex.models.Users;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

/**
 Class Description:
 This class will display the details of the post
 */

public class PostDetailsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ACTIVITY_NAME = "PostDetailsFragment";

    Realm realm;
    Posts currentPosting;
    Users posterUser;
    ChatRoom chatRoom, newChatRoom;
    String posterUID;
    LinearLayout priceLayout, carDetailsLayout, additionalComments;
    App app;
    String postId = "";
    TextView driverInfo,
            driverName,
            driverRating,
            pickupLocation,
            dropoffLocation,
            rideDate,
            ridePrice,
            seatsAvailable,
            carModel,
            carLicensePlate,
            carColor,
            rideAddComments,
            seatsAvailableText;
    Button messageBtn, confirmRideBtn, deletePosting;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    public static PostDetailsFragment newInstance(String param1, String param2) {
        PostDetailsFragment fragment = new PostDetailsFragment();
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
        if (getArguments() != null && !getArguments().getString("postId").isEmpty()){
            postId = getArguments().getString("postId");
        }
        Log.i(ACTIVITY_NAME, "PostID: " +postId);
        currentPosting = realm.where(Posts.class)
                .equalTo("_id", new ObjectId(postId)).findFirst();
        posterUser = realm.where(Users.class)
                .equalTo("uid", currentPosting.getPosterUID()).findFirst();
        app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        driverInfo = view.findViewById(R.id.driver_info);
        driverName = view.findViewById(R.id.driver_name);
        driverRating = view.findViewById(R.id.driver_rating);
        pickupLocation = view.findViewById(R.id.pickup_location);
        dropoffLocation = view.findViewById(R.id.dropoff_location);
        additionalComments = view.findViewById(R.id.additionalComments);
        rideDate = view.findViewById(R.id.ride_date);
        ridePrice = view.findViewById(R.id.ride_price);
        seatsAvailable = view.findViewById(R.id.seats_available);
        carModel = view.findViewById(R.id.car_model);
        carLicensePlate = view.findViewById(R.id.car_license_plate);
        carColor = view.findViewById(R.id.car_color);
        rideAddComments = view.findViewById(R.id.ride_add_comments);
        priceLayout = view.findViewById(R.id.price_layout);
        seatsAvailableText = view.findViewById(R.id.seats_available_text);
        carDetailsLayout = view.findViewById(R.id.car_details_layout);
        messageBtn = view.findViewById(R.id.messageBtn);
        confirmRideBtn = view.findViewById(R.id.confirmRideBtn);
        deletePosting = view.findViewById(R.id.deletePosting);
        posterUID = currentPosting.getPosterUID();


        if (posterUID.equals(app.currentUser().getId())){
            messageBtn.setVisibility(View.GONE);
            confirmRideBtn.setVisibility(View.GONE);
            deletePosting.setVisibility(View.VISIBLE);
            deletePosting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Custom dialog to confirm deleting
                    MongoClient mongoClient = app.currentUser().getMongoClient(MongoDb.mongoClient);
                    MongoDatabase mongoDatabase = mongoClient.getDatabase(MongoDb.databaseName);
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(MongoDb.postsCollection);
                    Document postToDelete = new Document("_id", currentPosting.getId());

                    mongoCollection.deleteOne(postToDelete).getAsync(task -> {
                        if (task.isSuccess()){
                            long count = task.get().getDeletedCount();
                            if (count == 1){
                                Log.i(ACTIVITY_NAME, "Successfully deleted a document!");
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.frameLayout, new SearchFragment()).commit();
                            } else {
                                Log.i(ACTIVITY_NAME, "Did not to delete a document!");
                            }
                        } else {
                            Log.i(ACTIVITY_NAME,
                                    "Failed to delete document with: ", task.getError());
                        }
                    });
                }
            });
        } else {
            deletePosting.setVisibility(View.GONE);
            if (currentPosting.getAvailableSeats() != null && currentPosting.getAvailableSeats() != 0){
                confirmRideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View view = inflater.inflate(R.layout.custom_dialog, null);

                        final AlertDialog dialog = new AlertDialog.Builder(requireContext(), AlertDialog.THEME_HOLO_LIGHT)
                                .setView(view)
                                .setTitle("Confirm Ride")
                                .setPositiveButton("Confirm", null) //Set to null. We override the onclick
                                .setNegativeButton(android.R.string.cancel, null)

                                .create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view1) {
                                        EditText edit = view.findViewById(R.id.dialog_message_box);
                                        TextView errorText = view.findViewById(R.id.errorText);

                                        realm.executeTransactionAsync(r->{
                                            Posts currentPost=r.where(Posts.class)
                                                    .equalTo("_id",new ObjectId(postId))
                                                    .findFirst();
                                            Users currentUser = r.where(Users.class)
                                                    .equalTo("uid", app.currentUser().getId())
                                                    .findFirst();

                                            Integer availableSeats = currentPost.getAvailableSeats();
                                            String seatsRequired = edit.getText().toString();

                                            if (!seatsRequired.equals("")){
                                                if(currentPost.getAvailableSeats() - Integer.parseInt(seatsRequired) >= 0){
                                                    if(currentPost.getConfirmedUsers().get(0).equals("")){
                                                        currentPost.getConfirmedUsers().set(
                                                                0,app.currentUser().getId());
                                                    }else{
                                                        currentPost.getConfirmedUsers().add(
                                                                app.currentUser().getId());
                                                    }

                                                    currentPost.setAvailableSeats(
                                                            currentPost.getAvailableSeats() -
                                                                    Integer.parseInt(seatsRequired));

                                                    if (currentUser.getConfirmedRide().get(0).equals("")){
                                                        currentUser.getConfirmedRide().set(0, postId);
                                                    } else {
                                                        currentUser.getConfirmedRide().add(postId);
                                                    }

                                                    //Dismiss once everything is OK.
                                                    dialog.cancel();
                                                }else{
                                                    errorText.setVisibility(View.VISIBLE);
                                                    errorText.setText(
                                                            String.format("There are only %s available seats.",
                                                                    availableSeats)
                                                    );
                                                }
                                            } else {
                                                errorText.setVisibility(View.VISIBLE);
                                                //please provide number of seats you require!
                                                errorText.setText(
                                                        "Error: Please provide number of seats you require!");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                confirmRideBtn.setVisibility(View.GONE);
            }
        }

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User clicked on the message.
                // Check if the chatroom already exists with the two user.
                if (!posterUID.equals(app.currentUser().getId())){
                    chatRoom = realm.where(ChatRoom.class)
                            .equalTo("userOneUID", app.currentUser().getId())
                            .and()
                            .equalTo("userTwoUID", posterUser.getUid())
                            .or()
                            .equalTo("userOneUID", posterUser.getUid())
                            .and()
                            .equalTo("userTwoUID", app.currentUser().getId())
                            .findFirst();

                    Log.i(ACTIVITY_NAME, String.valueOf(chatRoom));
                    if (chatRoom == null){
                        // there is no chatRoom for the two users!
                        realm.executeTransactionAsync(r -> {
                            ChatRoom newChatRoom = r.createObject(ChatRoom.class, new ObjectId());
                            RealmList<String> messages = new RealmList<>();
                            messages.add("63829531a7f68278c574b688");
                            newChatRoom.setMessages(messages);
                            newChatRoom.setUserOneUID(posterUID);
                            newChatRoom.setUserTwoUID(app.currentUser().getId());
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Log.i(ACTIVITY_NAME, "Successfully created chatroom!");
                                newChatRoom = realm.where(ChatRoom.class)
                                        .equalTo("userOneUID", app.currentUser().getId())
                                        .and()
                                        .equalTo("userTwoUID", posterUser.getUid())
                                        .or()
                                        .equalTo("userOneUID", posterUser.getUid())
                                        .and()
                                        .equalTo("userTwoUID", app.currentUser().getId())
                                        .findFirst();
                                sendUserToMessageRoom(newChatRoom.getId().toString());
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Log.i(ACTIVITY_NAME, "Unable to make a chatroom!");
                                Log.i(ACTIVITY_NAME, "Error: "+ error);
                            }
                        });
                    } else {
                        sendUserToMessageRoom(chatRoom.getId().toString());
                    }
                } else {
                    Toast.makeText(getContext(), "This is your post!", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (currentPosting.getPostedAs().equals("Driver")){
            //driverName.setText(currentPosting.get);
            Log.i(ACTIVITY_NAME, String.valueOf(currentPosting));
            //driverRating
            driverName.setText(String.format("%s %s",
                    posterUser.getFirstName(),posterUser.getLastName()));
            driverRating.setText("⭐" + String.valueOf(posterUser.getOverAllReview()));
            pickupLocation.setText(currentPosting.getFromLocation());
            dropoffLocation.setText(currentPosting.getToLocation());
            rideDate.setText(currentPosting.getDate());
            ridePrice.setText(String.valueOf(currentPosting.getPrice()));
            seatsAvailable.setText(String.valueOf(currentPosting.getNumberOfSeats()));
            carModel.setText(currentPosting.getCarModel());
            carLicensePlate.setText(currentPosting.getLicensePlate());
            carColor.setText(currentPosting.getCarColor());
            rideAddComments.setText(currentPosting.getPostDescription());
            if (currentPosting.getPostDescription().equals("")){
                additionalComments.setVisibility(View.GONE);
            }
        }
        else{
            driverInfo.setText("Rider Information");
            driverName.setText(String.format("%s %s",
                    posterUser.getFirstName(),posterUser.getLastName()));
            driverRating.setText("⭐" + String.valueOf(posterUser.getOverAllReview()));
            pickupLocation.setText(currentPosting.getFromLocation());
            dropoffLocation.setText(currentPosting.getToLocation());
            rideDate.setText(currentPosting.getDate());
            priceLayout.setVisibility(View.GONE);
            if (currentPosting.getPostDescription().equals("")){
                additionalComments.setVisibility(View.GONE);
            }
            seatsAvailableText.setText("Seats Required");
            seatsAvailable.setText(String.valueOf(currentPosting.getNumberOfSeats()));
            carDetailsLayout.setVisibility(View.GONE);
            rideAddComments.setText(currentPosting.getPostDescription());
        }
        return view;
    }

    public void sendUserToMessageRoom(String chatRoomId){
        Bundle bundle = new Bundle();
        bundle.putString("chatRoomID", chatRoomId);
        bundle.putString("senderName", String.format("%s %s",
                posterUser.getFirstName(),posterUser.getLastName()));
        ChatWindowFragment chatWindowFragment = new ChatWindowFragment();
        chatWindowFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, chatWindowFragment).commit();
    }
}