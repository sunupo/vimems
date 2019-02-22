package com.vimems.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vimems.R;
import com.vimems.bean.Member;
import com.vimems.coach.MemberDetailActivity;

import java.util.ArrayList;

/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class MultiplayerMemberAdapter extends RecyclerView.Adapter<MultiplayerMemberAdapter.ViewHolder>{

    private ArrayList<Member> memberArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View memberView;
        ImageView memberImage;
        TextView memberName;
        Button memberBindDevice;
        TextView memberBindDeviceFlag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberView=itemView;
            memberImage= (ImageView) itemView.findViewById(R.id.member_image);
            memberName= (TextView) itemView.findViewById(R.id.member_name);
            memberBindDevice=itemView.findViewById(R.id.member_bind_device);
        }
    }

    public MultiplayerMemberAdapter(ArrayList<Member> memberArrayList) {
        this.memberArrayList = memberArrayList;
    }

    @NonNull
    @Override
    public MultiplayerMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_member_multiplayer,viewGroup,false);
        final ViewHolder holder=new ViewHolder(itemView);

        holder.memberBindDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2/19/2019 绑定那个设备 
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MultiplayerMemberAdapter.ViewHolder viewHolder, int i) {

        Member member=memberArrayList.get(i);
        viewHolder.memberImage.setImageResource(member.getImageID());
        viewHolder.memberName.setText(member.getMemberName());
    }

    @Override
    public int getItemCount() {
        return memberArrayList.size();
    }
}