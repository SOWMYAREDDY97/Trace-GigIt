package com.traceandgigit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class AdapterSaloons extends RecyclerView.Adapter<AdapterSaloons .DownloadedImagesViewHolder> {

    private Context mContext;
    private List<ParseUser> mParseObjects;
    private OnItemClickListener mOnItemClickListener;

    public AdapterSaloons(Context mContext, List<ParseUser> objects){
        this.mContext = mContext;
        this.mParseObjects = objects;
    }

    @NonNull
    @Override
    public DownloadedImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saloons_recycler_view, parent, false);

        return new DownloadedImagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedImagesViewHolder holder, int position) {
        final ParseUser user = mParseObjects.get(position);
        String name = (String) user.get(Constants.SHOP_NAME);
        if (name != null) {
            holder.shopName.setText(name);
            holder.initials.setText(name.substring(0,1));
            holder.initials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(user);
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return mParseObjects.size();
    }

    public void setClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public class DownloadedImagesViewHolder extends RecyclerView.ViewHolder {

        private TextView shopName;
        private TextView initials;

        public DownloadedImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            initials = itemView.findViewById(R.id.initials);
        }
    }

}
