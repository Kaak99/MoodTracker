package com.example.souhi.moodtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {

    //initialization////////
    RelativeLayout mainLayout;
    ImageView ivSmiley;
    Button btnNoteAdd, btnHistory;
    Gson gson = new Gson();
    SharedPreferences prefs;
    MediaPlayer media;

    int moodNumber = 3; //start with mood number 3
    Mood currentMood, lastMood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById
        ivSmiley = findViewById(R.id.ivSmiley);
        btnNoteAdd = findViewById(R.id.btnNoteAdd);
        btnHistory = findViewById(R.id.btnHistory);
        mainLayout = findViewById(R.id.mainLayout);


// if  swiping
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

            public void onSwipeTop() {          // moodNumber increases, but =<4 (5 moods)
                if (moodNumber < Constants.tabSmiley.length - 1) {
                    moodNumber++;
                    swipeDisplay();
                }
            }

            public void onSwipeRight() {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                //Log.v("MoodTracker", "++" + currentMood.getTodaysDate() + "++" + currentMood.getTodaysNote() + "++" + currentMood.getTodaysMood());
            }

            public void onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {  // moodNumber decreases, but => 0

                if (moodNumber > 0) {
                    moodNumber--;
                    swipeDisplay();
                }
            }
        });


// if clicking button noteAdd

// if clicking button History





    } //end onCreate

    private void swipeDisplay() {  //actualize display and sound when swipping
        ivSmiley.setImageResource(Constants.tabSmiley[moodNumber]);
        mainLayout.setBackgroundResource(Constants.tabColorBackground[moodNumber]);
        currentMood.setTodaysMood(moodNumber);
        if (media != null && media.isPlaying()) {
            media.stop();
        }
        media = MediaPlayer.create(getApplicationContext(), Constants.tabSound[moodNumber]);
        media.start();
    }

} //end MainActivity
