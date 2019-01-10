package com.example.souhi.moodtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.gson.Gson;

import java.util.Calendar;


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

//onPause : save actual mood object (via json/gson) in SharedPref key ["memory"+index] (index 0-8)
// first need to know if same day than last key shared. if different : index+1
    @Override
    protected void onPause() {
        super.onPause();
        index = prefs.getInt("memoryIndex", 1);
        String json0 = prefs.getString("memory" + index, "");

        if (json0 != null && !json0.equals("")) {
            lastMood = gson.fromJson(json0, Mood.class);
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());//today's date
            calendar2.setTimeInMillis(lastMood.getTodaysDate());//date from lastMood

            if (calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) || calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) {
                index++;// different date-> index+1
                if (index == 9)
                    index = 1; // index= 1 to 8
            }
        }
//now we save the mood at SharedPref key ["memory" +index]
        currentMood.setTodaysDate(System.currentTimeMillis()); //actualize date before saving
        String json = gson.toJson(currentMood); // currentMood -> json format
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("memory" + index, json);// writing json in key ["memory" + index]
        editor.putInt("memoryIndex", index);//writing last index in key memoryIndex
        editor.apply();
    }  // end on pause

//onResume : restore lastmode registered if date = todays date (else, reinit smiley/background)
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        index = prefs.getInt("memoryIndex", 1);
        String json = prefs.getString("memory" + index, "");//json contains lastmood registered
        if (json != null && !json.equals("")) {
            currentMood = gson.fromJson(json, Mood.class);//currentMood contains lastmood registered
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());//date du jour
            calendar2.setTimeInMillis(currentMood.getTodaysDate());//date de currentMood
            if (calendar1.get(Calendar.DAY_OF_YEAR) != calendar2.get(Calendar.DAY_OF_YEAR) || calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)) {
// if currentMood(=last moood registerde) is not from today, then reinit the mood (smiley3, green background, no comment, date=now)
                moodNumber = 3;
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
