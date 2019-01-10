package com.example.souhi.moodtracker;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.example.souhi.moodtracker.R;
//import com.example.souhi.moodtracker.Constants;
//import com.example.souhi.moodtracker.Mood;
import com.google.gson.Gson;

import java.util.Calendar;

import static com.example.souhi.moodtracker.Constants.HISTORY_NUMBER;
import static com.example.souhi.moodtracker.Constants.MOOD_NUMBER;
import static com.example.souhi.moodtracker.Constants.SAVEDPREFKEY_NUMBER;

public class HistoryActivity extends AppCompatActivity {

    //initialization
    RelativeLayout historyLayout;
    int moodNumber; //from 0 to MOOD_NUMBER=5 (5 diferent mood)
    String moodComment = "";

    // view of 7=HISTORY_NUMBER different emoods in history
    // SharedPref contains 8= SAVEDPREFKEY_NUMBER (=HISTORY_NUMBER+1) keys for mood(7registered+today)
    String[] json = new String[SAVEDPREFKEY_NUMBER];
    Gson gson = new Gson();
    Mood[] moods = new Mood[SAVEDPREFKEY_NUMBER];
    Mood lastMood;
    int[] tabSize = new int[MOOD_NUMBER]; //this tab will contain width-size in pixel for moods in history
    int index0; //from 0 to SAVEDPREFKEY_NUMBER
    int[] moodTime = new int[SAVEDPREFKEY_NUMBER];

    RelativeLayout[] relativeTab = new RelativeLayout[HISTORY_NUMBER];
    ImageButton[] imgButton = new ImageButton[HISTORY_NUMBER];
    TextView[] tvMood = new TextView[HISTORY_NUMBER];
    SharedPreferences prefs;

// OnCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


// findViewById

        historyLayout = findViewById(R.id.mainLayout);

        for (int i = 0; i < HISTORY_NUMBER; i++) {
            int id1 = getResources().getIdentifier("imgButton" + i, "id", getPackageName());
            imgButton[i] = findViewById(id1);
            int id2 = getResources().getIdentifier("relLayout" + i, "id", getPackageName());
            relativeTab[i] = findViewById(id2);
            int id3 = getResources().getIdentifier("tvMood" + i, "id", getPackageName());
            tvMood[i] = findViewById(id3);
        }

//Create TabSize depending on hardware used
//find screen real width, divide by MOOD_NUMBER=5 (number of different mood) -> put in array "tabSize"
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int width = (point.x) / MOOD_NUMBER;
        for (int i = 0; i < MOOD_NUMBER; i++) {
            tabSize[i] = width * (i + 1); //put width for each mood choice in a tab
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        index0 = prefs.getInt("memoryIndex", 0);// last index-> index0
        // we will copy in json[0->7] all the SharedPref's keys (from memory1->memory8)
        //json[0] will get the last registered mood; json[7] the older
        for (int i = 0; i < SAVEDPREFKEY_NUMBER; i++) {  // 8 elements in this tab
            json[i] = prefs.getString("memory" + index0, "");
            index0--;
            if (index0 == 0) {  //  because index0 = 1 to 8
                index0 = SAVEDPREFKEY_NUMBER;
            }
        }
        // then we look at json[0], the more recent mood registered, and check his date
        lastMood = gson.fromJson(json[0], Mood.class);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());//todays date
        calendar2.setTimeInMillis(lastMood.getTodaysDate());//lastMood' s date
//same date as today-> ignore it and display the 7 following
        if (calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR) && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
            for (int i = 0; i < HISTORY_NUMBER; i++) {
                json[i] = json[i + 1];
            }
//display the history from json 0 to 7
            for (int i = 0; i < HISTORY_NUMBER; i++) { //(7 items to display)
                if (!json[i].equals("")) { //if (json[i] != "") {
                    moods[i] = new Gson().fromJson(json[i], Mood.class);//tableau de Gson
                    moodNumber = moods[i].getTodaysMood();
                    moodComment = moods[i].getTodaysNote();
                    calendar2.setTimeInMillis(moods[i].getTodaysDate());
                    moodTime[i] = ((calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR)) * 365) + (calendar1.get(Calendar.DAY_OF_YEAR) - calendar2.get(Calendar.DAY_OF_YEAR));
//moodtime=number of day from the mood till now
                    ViewGroup.LayoutParams params = relativeTab[i].getLayoutParams();
                    params.width = tabSize[moodNumber];
                    relativeTab[i].setBackgroundResource(Constants.tabColorBackground[moodNumber]);
                    relativeTab[i].setLayoutParams(params);

                    tvMood[i].setText(getDiffDays(moodTime[i]));

                    if (!moodComment.equals("")) { //if there's a comment, show the icon
                        imgButton[i].setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private String getDiffDays(int moodTime) {
        String[] numberLetterTab = getResources().getStringArray(R.array.numbers);
        // display textView ("yesterday"..."a week ago", x days ago")
        switch (moodTime) {
            case 1: //yesterday
                return getString(R.string.yesterday);
            case 2: // day before yesterday
                return getString(R.string.daybefore);
            case 3:
            case 4:
            case 5:
            case 6:
                return getString(R.string.itwasletter, numberLetterTab[moodTime - 3]);
            case 7: //a week ago
                return getString(R.string.oneweek);
            default:
                return getString(R.string.itwas, moodTime);
        }
    }

    // display comment if clicking a imageButton-com in history
    public void comDisplay(View v) {
        for (int i = 0; i < HISTORY_NUMBER; i++) {
            if (v == imgButton[i] && moods[i] != null) {
                final String comment = moods[i].getTodaysNote();
                Toast.makeText(HistoryActivity.this, "" + comment, Toast.LENGTH_SHORT).show();
            }
        }
    }


}   //end MainActivity

