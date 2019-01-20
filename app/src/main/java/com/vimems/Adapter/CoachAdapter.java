package com.vimems.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.AdapterItem.CoachItem;
import com.vimems.R;
import com.vimems.admin.CoachDetailActivity;

import java.util.ArrayList;

import util.InitBean;

/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.ViewHolder>{

    private ArrayList<CoachItem> coachItemArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View coachView;
        ImageView coachImage;
        TextView coachName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coachView=itemView;
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
        final ViewHolder holder=new ViewHolder(itemView);
        holder.coachName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position=holder.getAdapterPosition();
                CoachItem coachItem=coachItemArrayList.get(position);
                Toast.makeText(v.getContext(), "教练"+coachItem.getName()+"有"+InitBean.getCoachMemberNum(coachItem.getCoachID(),InitBean.memberArrayList)+"个会员"
                        ,Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(v.getContext(), CoachDetailActivity.class);
                intent.putExtra("coachUsername",coachItem.getName());
                intent.putExtra("coachID",coachItem.getCoachID());
                v.getContext().startActivity(intent);

            }
        });
        holder.coachView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                CoachItem coachItem=coachItemArrayList.get(position);
                //Toast.makeText(v.getContext(),"教练是"+coachItem.getName(),Toast.LENGTH_LONG).show();
            }
        });
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