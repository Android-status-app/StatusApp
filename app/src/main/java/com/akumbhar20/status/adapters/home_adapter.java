package com.akumbhar20.status.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akumbhar20.status.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class home_adapter extends RecyclerView.Adapter<home_adapter.sampleViewHolder> {
    Context mctx;

    public home_adapter(FragmentActivity activity) {
        mctx=activity;
    }

    @NonNull
    @Override
    public home_adapter.sampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mctx).inflate(R.layout.home_item,parent,false);
        sampleViewHolder svh=new sampleViewHolder(view);
        return svh;
    }



    @Override
    public void onBindViewHolder(@NonNull home_adapter.sampleViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class sampleViewHolder extends RecyclerView.ViewHolder{

        public sampleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
