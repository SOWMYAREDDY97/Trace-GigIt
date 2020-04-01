package com.example.livechatapplication;



import java.util.ArrayList;

public class Model {

    private static Model theModel = null;

    public ArrayList<Lyrics> lyricsList;

    private Model() {
        lyricsList = new ArrayList<Lyrics>();
        loadModel();
    }

    public static Model getModel() {
        if (theModel == null)
            theModel = new Model();
        return theModel;
    }

    private void loadModel() {

        Lyrics listlyr = new Lyrics("she");
        lyricsList.add(listlyr);
        listlyr = new Lyrics("he");
        lyricsList.add(listlyr);
        listlyr = new Lyrics("loves you");
        lyricsList.add(listlyr);
        listlyr = new Lyrics("Yeah, yeah, yeah");
        lyricsList.add(listlyr);
        listlyr = new Lyrics("you know");
        lyricsList.add(listlyr);
        listlyr = new Lyrics("should be");
        lyricsList.add(listlyr);
    }

    public static class Lyrics {
        public String word;

        public Lyrics(String word) {
            this.word = word;
        }
    }
}