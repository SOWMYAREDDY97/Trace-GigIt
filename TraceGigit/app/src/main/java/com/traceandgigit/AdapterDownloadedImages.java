package com.traceandgigit;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

public class AdapterDownloadedImages extends RecyclerView.Adapter<AdapterDownloadedImages .DownloadedImagesViewHolder> {


    private Context mContext;
    private List<ParseObject> mParseObjects;


    public AdapterDownloadedImages(Context mContext, List<ParseObject> objects){
        this.mContext = mContext;
        this.mParseObjects = objects;
    }

    @NonNull
    @Override
    public DownloadedImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_downloaded_images, parent, false);

        return new DownloadedImagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedImagesViewHolder holder, int position) {

        ParseFile imageFile = mParseObjects.get(position).getParseFile("ImageFile");
        if (imageFile != null) {
            imageFile.getUrl();
            Glide.with(mContext).load(imageFile.getUrl()).into(holder.mImageView);
        }

    }

    @Override
    public int getItemCount() {
        return mParseObjects.size();
    }

    public class DownloadedImagesViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public DownloadedImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
        }
    }
}
