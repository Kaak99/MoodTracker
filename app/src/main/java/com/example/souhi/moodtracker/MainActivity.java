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
    String moodComment = ""; //start with no mood coment
    long moodDate = System.currentTimeMillis(); //start with today's'date
    Mood currentMood, lastMood;     //each mood contains a moodDate, a moodNumber, a moodComment
    int index; //we will memorize 8 json, index point the key to the last one

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
        btnNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.alertdiag_noteadd, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.etAlertdiagNoteadd); //to our alertdiagNoteadd.xml

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.noteadd_pos_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        moodComment = userInput.getText().toString();
                                        currentMood.setTodaysNote(moodComment);
                                    }
                                })
                        .setNegativeButton(R.string.noteadd_neg_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog & show
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


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
