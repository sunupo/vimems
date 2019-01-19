package com.vimems.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vimems.AdapterItem.CoachItem;
import com.vimems.R;

import java.util.ArrayList;

/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.ViewHolder>{

    private ArrayList<CoachItem> coachItemArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView coachImage;
        TextView coachName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coachImage= itemView.findViewById(R.id.coach_image);
            coachName= itemView.findViewById(R.id.coach_name);
        }
    }


    public CoachAdapter(ArrayList<CoachItem> coachItemArrayList) {
        this.coachItemArrayList = coachItemArrayList;
    }

    @NonNull
    @Override
    public CoachAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_coach,viewGroup,false);
        ViewHolder holder=new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoachAdapter.ViewHolder viewHolder, int i) {

        CoachItem coachItem=coachItemArrayList.get(i);
        viewHolder.coachImage.setImageResource(coachItem.getImageId());
        viewHolder.coachName.setText(coachItem.getName());
    }

    @Override
    public int getItemCount() {
        return coachItemArrayList.size();
    }
}