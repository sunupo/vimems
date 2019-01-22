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

import com.vimems.R;
import com.vimems.admin.CoachDetailActivity;
import com.vimems.bean.Coach;

import java.util.ArrayList;

import util.InitBean;


/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.ViewHolder>{

    private ArrayList<Coach> coachArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View coachView;
        ImageView coachImage;
        TextView coachName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coachView=itemView;
            coachImage= (ImageView) itemView.findViewById(R.id.item_coach_image);
            coachName= (TextView) itemView.findViewById(R.id.item_coach_name);
        }
    }

    public CoachAdapter(ArrayList<Coach> coachArrayList) {
        this.coachArrayList = coachArrayList;
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
                Coach coach=coachArrayList.get(position);
                Toast.makeText(v.getContext(), "教练"+coach.getCoachName()+"有"+InitBean.getCoachMemberNum(coach.getCoachID(),InitBean.memberArrayList)+"个会员"
                        ,Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(v.getContext(), CoachDetailActivity.class);
                intent.putExtra("coachName",coach.getCoachName());
                intent.putExtra("coachID",coach.getCoachID());
                v.getContext().startActivity(intent);

            }
        });
        holder.coachView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Coach coach=coachArrayList.get(position);
                Toast.makeText(v.getContext(),"教练是"+coach.getCoachName(),Toast.LENGTH_LONG).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoachAdapter.ViewHolder viewHolder, int i) {

        Coach coach=coachArrayList.get(i);
        viewHolder.coachImage.setImageResource(coach.getImageID());
        viewHolder.coachName.setText(coach.getCoachName());
    }

    @Override
    public int getItemCount() {
        return coachArrayList.size();
    }
}