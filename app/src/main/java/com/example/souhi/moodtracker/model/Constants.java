package com.example.souhi.moodtracker.model;

import com.example.souhi.moodtracker.R;

public class Constants {

    final public static int[] tabColorBackground = {R.color.faded_red, R.color.warm_grey, R.color.cornflower_blue_65, R.color.light_sage, R.color.banana_yellow};
    final public static int[] tabSmiley = {R.drawable.smiley_sad, R.drawable.smiley_disappointed, R.drawable.smiley_normal, R.drawable.smiley_happy, R.drawable.smiley_super_happy};
    final public static int[] tabSound = {R.raw.sound0, R.raw.sound1, R.raw.sound2, R.raw.sound3, R.raw.sound4};


    final public static int MOOD_NUMBER = 5;  //you can choose 5 different moods
    final public static int HISTORY_NUMBER = 7;  //you can display 7 different moods in historyView
    final public static int SAVEDPREFKEY_NUMBER = 8;//you save 8 different mood in SharedPref's keys +today's one
    public static final int DEFAULT_MOOD_INDEX =3 ;

}

