package com.example.souhi.moodtracker;

public class Mood {

    // var
    private long todaysDate;
    private int todaysMood;
    private String todaysNote;

    //constructor
    public Mood (long todaysDate , int todaysMood , String todaysNote ) {
        this.todaysDate = todaysDate;
        this.todaysMood = todaysMood;
        this.todaysNote = todaysNote;
    }
    //getter/setters
    public long getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(long todaysDate) {
        this.todaysDate = todaysDate;
    }

    public int getTodaysMood() {
        return todaysMood;
    }

    public void setTodaysMood(int todaysMood) {
        this.todaysMood = todaysMood;
    }

    public String getTodaysNote() {
        return todaysNote;
    }

    public void setTodaysNote(String todaysNote) {
        this.todaysNote = todaysNote;
    }

    //action...


}
