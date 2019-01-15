package com.example.souhi.moodtracker.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import com.example.souhi.moodtracker.R;
import com.example.souhi.moodtracker.model.Constants;
import com.example.souhi.moodtracker.model.Mood;
import com.google.gson.Gson;

import static com.example.souhi.moodtracker.model.Constants.DEFAULT_MOOD_INDEX;

public class MainActivity extends AppCompatActivity {


    //initialization////////
    private RelativeLayout mainLayout;
    private ImageView ivSmiley;
    private Button btnNoteAdd, btnHistory;
    private Gson gson = new Gson();
    private SharedPreferences prefs;
    private MediaPlayer media;

    private int moodNumber = DEFAULT_MOOD_INDEX; //start with mood number 3
    private String moodComment = ""; //start with no mood comment
    private long moodDate = System.currentTimeMillis(); //start with today's'date
    private Mood currentMood, lastMood;     //each mood contains a moodDate, a moodNumber, a moodComment
    private int moodIndex; //we will memorize 8 json, moodIndex point the key to the last one


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentMood = new Mood(moodDate, moodNumber, moodComment); //new mood=currentMood

        initViews();
        initGestureListener();
        initButtons();
    } //end onCreate

    private void initViews() {
        // findViewById
        ivSmiley = findViewById(R.id.ivSmiley);
        btnNoteAdd = findViewById(R.id.btnNoteAdd);
        btnHistory = findViewById(R.id.btnHistory);
        mainLayout = findViewById(R.id.mainLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGestureListener() {
        // if  swiping
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {          // moodNumber increases, but =<4 (5 moods)
                if (moodNumber < Constants.tabSmiley.length - 1) {
                    moodNumber++;
                    swipeDisplay();
                }
            }

            public void onSwipeBottom() {  // moodNumber decreases, but => 0
                if (moodNumber > 0) {
                    moodNumber--;
                    swipeDisplay();
                }
            }
        });
    }

    private void initButtons() {
        // if clicking button noteAdd
        btnNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View promptsView = li.inflate(R.layout.alertdiag_noteadd, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = promptsView
                        .findViewById(R.id.etAlertdiagNoteadd);

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
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    //onPause : save actual mood object (via json/gson) in SharedPref key ["memory"+moodIndex] (moodIndex 0-8)
// first need to know if same day than last key shared. if different : moodIndex+1
    @Override
    protected void onPause() {
        super.onPause();
        moodIndex = prefs.getInt("memoryIndex", 1);
        String lastJson = prefs.getString("memory" + moodIndex, "");
        if (lastJson != null && !lastJson.equals("")) {
            lastMood = gson.fromJson(lastJson, Mood.class);
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());//today's date
            calendar2.setTimeInMillis(lastMood.getTodaysDate());//date from lastMood

            if (calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) || calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) {
                moodIndex++;// different date-> moodIndex+1
                if (moodIndex == 9)
                    moodIndex = 1; // moodIndex= 1 to 8
            }
        }
//now we save the mood at SharedPref key ["memory" +moodIndex]
        currentMood.setTodaysDate(System.currentTimeMillis()); //actualize date before saving
        String json = gson.toJson(currentMood); // currentMood -> json format
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("memory" + moodIndex, json);// writing json in key ["memory" + moodIndex]
        editor.putInt("memoryIndex", moodIndex);//writing last moodIndex in key memoryIndex
        editor.apply();
    }  // end on pause

    //onResume : restore lastmode registered if date = todays date (else, reinit smiley/background)
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        moodIndex = prefs.getInt("memoryIndex", 1);
        String json = prefs.getString("memory" + moodIndex, "");//json contains lastmood registered
        if (json != null && !json.equals("")) {
            currentMood = gson.fromJson(json, Mood.class);//currentMood contains lastmood registered
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());//date du jour
            calendar2.setTimeInMillis(currentMood.getTodaysDate());//date de currentMood
            if (calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) || calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) {
// if currentMood(=last moood registerde) is not from today, then reinit the mood (smiley3, green background, no comment, date=now)
                moodNumber = DEFAULT_MOOD_INDEX;
                moodComment = "";
                long moodDate = System.currentTimeMillis();
                currentMood.setTodaysMood(moodNumber);//
                currentMood.setTodaysNote(moodComment);
                currentMood.setTodaysDate(moodDate);
            } else { // if this last mood is from today, then restore
                moodNumber = currentMood.getTodaysMood();
                moodComment = currentMood.getTodaysNote();
                moodDate = currentMood.getTodaysDate();
            }
// Now : display the right smiley and background color
            ivSmiley.setImageResource(Constants.tabSmiley[moodNumber]);
            mainLayout.setBackgroundResource(Constants.tabColorBackground[moodNumber]);
        }
    } //end onResume

    private void swipeDisplay() {  //actualize display and sound when swiping
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
