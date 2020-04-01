package com.example.livechatapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Model mod;
    private SecondActivity lyricsecond = null;
    private RecyclerView lyricRec = null;
    private Handler mHandler;
    TextView addLyricText;
    public int row_count = 0;

    public Runnable onEveryTimeInterval = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(onEveryTimeInterval,1000);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("livechat");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (row_count < objects.size()) {
                        int temp = row_count;
                        row_count = objects.size();
                        for (int i = temp; i < objects.size(); i++) {


                            ParseObject user = objects.get(i);
                            if (addLyricText != null) {
                                if (user.get("user_object_id") != null) {
                                    addLyricText.setText(addLyricText.getText().toString() + "\n-------------------------------\n User:-" + user.get("message"));
                                }
                                else{
                                    addLyricText.setText(addLyricText.getText().toString() + "\n-------------------------------\n Admin:-" + user.get("message"));
                                }
                            }


                        }
                    }

                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.appID))
                // if defined
                .clientKey(getString(R.string.clientKey))
                .server(getString(R.string.serverUrl))
                .build()
        );
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        addLyricText = findViewById(R.id.addLyricText);
        mod = Model.getModel();

        lyricsecond = new SecondActivity(mod);


        lyricRec = findViewById(R.id.lyricRecycler);
        lyricRec.setAdapter(lyricsecond);


        final LinearLayoutManager myManager = new LinearLayoutManager(this);
        lyricRec.setLayoutManager(myManager);

        final class LyricDetailsLookup extends ItemDetailsLookup {

            private final RecyclerView recview;
            LyricDetailsLookup(RecyclerView recyclerView) {
                recview = recyclerView;
            }
            public ItemDetails getItemDetails(MotionEvent e) {

                View v = lyricRec.findChildViewUnder(e.getX(), e.getY());


                if (v != null) {
                    RecyclerView.ViewHolder h = lyricRec.getChildViewHolder(v);
                    if (h instanceof SecondActivity.LViewHolder) {
                        return ((SecondActivity.LViewHolder) h).getItemDetails();
                    }
                }
                return null;
            }
        }

//        SelectionTracker.Builder build = new SelectionTracker.Builder<>("planet-selection-id",
//                lyricRec,
//                new StableIdKeyProvider(lyricRec),
//                new LyricDetailsLookup(lyricRec),
//                StorageStrategy.createLongStorage());
//        build.withOnItemActivatedListener(new OnItemActivatedListener() {
//            @Override
//            public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails item,
//                                           @NonNull MotionEvent e) {
//
//                TextView displayTV = findViewById(R.id.addLyricText);
//                Model.Lyrics selected = mod.lyricsList.get(item.getPosition());
//
//                displayTV.setText(displayTV.getText().toString()+" " + selected.word);
//                return false;
//            }
//        });
//        SelectionTracker t = build.build();

        Button addBTN=findViewById(R.id.addButton);
        mHandler.postDelayed(onEveryTimeInterval,1000);

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText wordsEditText = findViewById(R.id.wordsEditText);

                EditText temp = findViewById(R.id.wordsEditText);

                final ParseObject livechat = new ParseObject("livechat");
                livechat.put("message", wordsEditText.getText().toString());
                if (wordsEditText.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Message", Toast.LENGTH_LONG).show();

                }
                else {

                    livechat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                row_count = row_count + 1;
                                addLyricText.setText(addLyricText.getText().toString() + "\n-------------------------------\n Admin :- " + wordsEditText.getText().toString());
                                wordsEditText.getText().clear();

                            } else {
                                Toast.makeText(MainActivity.this, "unable to add menu details " + e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });



    }
}