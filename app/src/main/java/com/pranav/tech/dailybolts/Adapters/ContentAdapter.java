package com.pranav.tech.dailybolts.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.pranav.tech.dailybolts.Models.ContentModel;
import com.pranav.tech.dailybolts.R;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {
    private List<ContentModel> mDataset;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView like_btn, copy_btn, fav_btn, share_btn;

        MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.list_text);
            share_btn = v.findViewById(R.id.share_btn);
            like_btn = v.findViewById(R.id.like_btn);
            copy_btn = v.findViewById(R.id.copy_btn);
            fav_btn = v.findViewById(R.id.favourites_btn);
        }
    }

    public ContentAdapter(Context context, List<ContentModel> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    @Override
    public ContentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiles, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.textView.setText(mDataset.get(position).getContent());
//        holder.like_btn.setText("(12)");
//        holder.copy_btn.setText("(12)");
//        holder.fav_btn.setText("(12)");
//        holder.share_btn.setText("(12)");
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("DailyBolts", mDataset.get(position).getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = mDataset.get(position).getContent();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share using... "));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

