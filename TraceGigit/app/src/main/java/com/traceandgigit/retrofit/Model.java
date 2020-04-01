package com.traceandgigit.retrofit;



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


    }

    public static class Lyrics {
        public String word;

        public Lyrics(String word) {
            this.word = word;
        }
    }
}