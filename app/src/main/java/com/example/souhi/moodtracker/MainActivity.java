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

// if clicking button noteAdd

// if clicking button History





    } //end onCreate
} //end MainActivity
