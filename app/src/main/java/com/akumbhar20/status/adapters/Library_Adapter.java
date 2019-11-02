package com.akumbhar20.status.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akumbhar20.status.R;
import com.akumbhar20.status.models.Video;
import com.bumptech.glide.Glide;

import java.util.List;

public class Library_Adapter extends RecyclerView.Adapter<Library_Adapter.library_view_holder> {
    List<Video> videoList;
    Context mctx;


    public Library_Adapter(FragmentActivity activity, List<Video> video_list) {
        this.mctx = activity;
        this.videoList = video_list;
    }

    @NonNull
    @Override
    public Library_Adapter.library_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mctx).inflate(R.layout.uploads_video, parent, false);
        Library_Adapter.library_view_holder vh = new Library_Adapter.library_view_holder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Library_Adapter.library_view_holder holder, int position) {
        holder.title.setText(videoList.get(position).getTitle().toUpperCase());
        holder.status.setText(videoList.get(position).getStatus());
        String time = videoList.get(position).getTimestamp().toDate().toString().substring(0, 19);

        holder.time.setText(time);
        Glide.with(mctx).load(videoList.get(position).getThumbnail())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class library_view_holder extends RecyclerView.ViewHolder {
        TextView title, time, status;
        ImageView thumbnail;

        public library_view_holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.uploads_video_title);
            time = itemView.findViewById(R.id.upload_time);
            status = itemView.findViewById(R.id.upload_status);
            thumbnail = itemView.findViewById(R.id.video_image);
        }
    }
}
