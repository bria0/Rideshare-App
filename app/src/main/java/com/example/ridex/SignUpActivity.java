package com.example.ridex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

/**
 Class Description:
 This class will allow the user to sign up
 */

public class SignUpActivity extends AppCompatActivity {
    private final static String ACTIVITY_NAME = "SignUpActivity";

    EditText firstNameInputField, lastNameInputField, emailAddressInputField, passwordInputField;
    Button signUpBtn, signUpWithGoogleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the contentView
        setContentView(R.layout.activity_sign_up);
        // Getting a reference for the variables
        firstNameInputField = findViewById(R.id.firstName_inputField);
        lastNameInputField = findViewById(R.id.lastName_inputField);
        emailAddressInputField = findViewById(R.id.email_inputField);
        passwordInputField = findViewById(R.id.password_inputField);
        signUpBtn = findViewById(R.id.signUp_btn);
        signUpWithGoogleBtn = findViewById(R.id.signUp_with_google_btn);
        // End of the reference for all the variable.

    }

    public void signUpBtnListener(View view) {
        // Clicked on the sign up btn
        String firstName = firstNameInputField.getText().toString();
        String lastName = lastNameInputField.getText().toString();
        String email = emailAddressInputField.getText().toString().toLowerCase();
        String password = passwordInputField.getText().toString();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()
                && !password.isEmpty()){
            // Provided all the information.

            App app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
            app.getEmailPassword().registerUserAsync(email.toLowerCase(), password, result -> {
                if (result.isSuccess()){
                    Toast.makeText(getApplicationContext(), "Successfully signed up!", Toast.LENGTH_SHORT).show();
                    // Log the newly signed up user!
                    Credentials credentials = Credentials.emailPassword(email, password);
                    app.loginAsync(credentials, result1 -> {
                        if (result1.isSuccess()){
                            User user = app.currentUser();

                            MongoClient mongoClient = user.getMongoClient(MongoDb.mongoClient);
                            MongoDatabase mongoDatabase = mongoClient.getDatabase(MongoDb.databaseName);
                            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(MongoDb.usersCollection);

                            RealmList<String> confirmedRide = new RealmList<>();
                            confirmedRide.add("");

                            mongoCollection.insertOne(new Document("_id", new ObjectId(user.getId()))
                                    .append("firstName", firstName)
                                    .append("lastName", lastName)
                                    .append("email", email)
                                    .append("takingRide", "")
                                    .append("confirmedRide", confirmedRide)
                                    .append("numberOfRidesDriven", 0)
                                    .append("numberOfRidesTaken", 0)
                                    .append("overAllReview", 0)
                                    .append("photoUrl","")
                                    .append("uid", user.getId())).getAsync(insertResult -> {
                                if (insertResult.isSuccess()){
                                    Intent intent = new Intent(SignUpActivity.this, ProfilePictureActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.i(ACTIVITY_NAME, "Error: "+ insertResult.getError());
                                }
                            });
                        }
                        else {
                            Log.i(ACTIVITY_NAME, "Unable to login after signing up! " + result1.getError());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "A user with this email address is already signed up!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Please make sure to provide all the information.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void signUpWithGoogleBtnListener(View view){
        // Clicked on the sign up with google btn
        Toast.makeText(getApplicationContext(), "Sign up with Google!",
                Toast.LENGTH_SHORT).show();
    }

    public void signInBtnTransition(View view){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "onStart()");
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
    }
}