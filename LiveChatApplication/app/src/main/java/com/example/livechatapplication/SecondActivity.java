package com.example.livechatapplication;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class SecondActivity extends RecyclerView.Adapter<SecondActivity.LViewHolder> {
    private Model mel;

    public SecondActivity(Model model) {
        super();
        this.mel = model;
    }

    @NonNull
    @Override
    public LViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout lay = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_second, parent, false);

        LViewHolder viewh = new LViewHolder(lay);
        return viewh;
    }

    @Override
    public void onBindViewHolder(@NonNull LViewHolder holder, int position) {
        String lyric = mel.lyricsList.get(position).word;
        TextView lyricText = holder.itemView.findViewById(R.id.lyricText);
        lyricText.setText(lyric);
    }

    @Override
    public int getItemCount() {
        Log.d("size",": "+ mel.lyricsList.size());


        return mel.lyricsList.size();

    }

    public static class LViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout layoutlinear;
        public LViewHolder(LinearLayout lay) {

            super(lay);

            layoutlinear = lay;
        }

        public LViewHolder publicvh = this;

        public ItemDetailsLookup.ItemDetails getItemDetails(){
            return new ItemDetailsLookup.ItemDetails() {
                @Override
                public int getPosition() {
                    return publicvh.getAdapterPosition();
                }
                @Nullable
                @Override
                public Object getSelectionKey() {
                    return publicvh.getItemId();
                }
            };
        }
    }
}