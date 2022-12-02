package com.example.ridex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.airbnb.lottie.LottieAnimationView;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

/**
 Class Description:
 This will be the first thing that opens when a new user opens the app. It allows a swiping feture for the user
 */

public class LandingPageActivity extends AppCompatActivity {

    private static final String ACTIVITY_NAME = "LandingPageActivity";
    Button getStartedBtn;
    ImageSwitcher imageSwitcher, imageSwitcherDots;
    private int position = 0;
    private int[] image = {R.drawable.infocard1_formatter, R.drawable.infocard2_formatter, R.drawable.infocard3_formatter};
    private int[] dots = {R.drawable.dot1, R.drawable.dot2, R.drawable.dot3};
    //for swipe gesture
    ConstraintLayout myLayout;
    SwipeListener swipeListener;
    LottieAnimationView carAnimation;
    ImageView bground;
    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfSignedIn();
        setContentView(R.layout.activity_landing_page);

        getStartedBtn = findViewById(R.id.getStartedButton);
        imageSwitcher = findViewById(R.id.imageSwitcher);
        myLayout = findViewById(R.id.myLayout);
        carAnimation = findViewById(R.id.carAnimation);
        swipeListener = new SwipeListener(myLayout);
        bground = findViewById(R.id.bground);
        welcome = findViewById(R.id.welcome);
        imageSwitcherDots = findViewById(R.id.imageSwitcherDots);

        //animate the background
        bground.animate().translationY(-1900).setDuration(800).setStartDelay(1000);
        welcome.animate().translationY(-650).setDuration(800).setStartDelay(1000);

        //set default view
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(LandingPageActivity.this);
                imageView.setImageResource(image[position]);
                return imageView;
            }
        });

        imageSwitcherDots.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(LandingPageActivity.this);
                imageView.setImageResource(dots[position]);
                return imageView;
            }
        });



        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
                intent.putExtra("initiallyLoaded", String.valueOf(true));
                startActivity(intent);
            }
        });
    }

    private void checkIfSignedIn(){
        App app = new App(new AppConfiguration.Builder(MongoDb.appId).build());
        User user = app.currentUser();
        if (user != null){
            Log.i(ACTIVITY_NAME, "The user is already logged in!");
            Intent intent = new Intent(LandingPageActivity.this, MainActivity.class);
            startActivity(intent);
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "onStart()");
    }

    private class SwipeListener implements View.OnTouchListener {

        GestureDetector gestureDetector;

        //constructor
        SwipeListener(View view){
            //threshold value
            int threshold = 100;
            int velocity_threshold = 100;
            imageSwitcher = findViewById(R.id.imageSwitcher);
            imageSwitcherDots = findViewById(R.id.imageSwitcherDots);
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try{
                        //check condition
                        if(Math.abs(xDiff) > Math.abs(yDiff)){ //when x is greater than y
                            if (Math.abs(xDiff) > threshold
                                && Math.abs(velocityX) > velocity_threshold ){

                                if(xDiff>0){
                                    //when swiped right
                                    imageSwitcher.setInAnimation(getApplicationContext(),R.anim.slide_in_right);
                                    imageSwitcher.setOutAnimation(getApplicationContext(),R.anim.slide_out_right);
                                    if (position>=0 && position<3){
                                        position--;
                                        if (position<0){position =2;}
                                        imageSwitcher.setImageResource(image[position]);
                                        imageSwitcherDots.setImageResource((dots[position]));
                                    }

                                }else{
                                    //when swiped left
                                    imageSwitcher.setInAnimation(getApplicationContext(),R.anim.slide_in_left);
                                    imageSwitcher.setOutAnimation(getApplicationContext(),R.anim.slide_out_left);
                                    if (position>=0 && position<3){
                                        position++;
                                        if(position>2){position = 0;}
                                        imageSwitcher.setImageResource(image[position]);
                                        imageSwitcherDots.setImageResource((dots[position]));
                                    }

                                }
                                return true;
                            }
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            };

            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }


}