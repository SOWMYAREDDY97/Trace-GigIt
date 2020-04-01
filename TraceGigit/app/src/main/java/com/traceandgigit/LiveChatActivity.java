package com.traceandgigit;


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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.traceandgigit.retrofit.Model;

public class LiveChatActivity extends AppCompatActivity {

    private Model mod;
    private SecondActivity lyricsecond = null;
    private RecyclerView lyricRec = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);



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

        SelectionTracker.Builder build = new SelectionTracker.Builder<>("planet-selection-id",
                lyricRec,
                new StableIdKeyProvider(lyricRec),
                new LyricDetailsLookup(lyricRec),
                StorageStrategy.createLongStorage());
        build.withOnItemActivatedListener(new OnItemActivatedListener() {
            @Override
            public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails item,
                                           @NonNull MotionEvent e) {

                TextView displayTV = findViewById(R.id.addLyricText);
                Model.Lyrics selected = mod.lyricsList.get(item.getPosition());
                displayTV.setText(displayTV.getText().toString()+" " + selected.word);
                return false;
            }
        });
        SelectionTracker t = build.build();

        Button addBTN=findViewById(R.id.addButton);
       
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText wordsEditText = findViewById(R.id.wordsEditText);
                TextView addLyricText = findViewById(R.id.addLyricText);
                EditText temp = findViewById(R.id.wordsEditText);

                addLyricText.setText(addLyricText.getText().toString()+" "+wordsEditText.getText().toString());
            }
        });



    }
}