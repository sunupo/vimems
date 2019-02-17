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
import android.widget.Toast;

import com.vimems.R;
import com.vimems.bean.Member;
import com.vimems.coach.MemberDetailActivity;
import com.vimems.mainactivity.SingleModeTrainingMainActivity;

import java.util.ArrayList;

/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    private ArrayList<Member> memberArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View memberView;
        ImageView memberImage;
        TextView memberName;
        Button setPara;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberView=itemView;
            memberImage= (ImageView) itemView.findViewById(R.id.member_image);
            memberName= (TextView) itemView.findViewById(R.id.member_name);
            setPara=itemView.findViewById(R.id.member_set_para);
        }
    }


    public MemberAdapter(ArrayList<Member> memberArrayList) {
        this.memberArrayList = memberArrayList;
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_member,viewGroup,false);
        final ViewHolder holder=new ViewHolder(itemView);
        // TODO: 2/16/2019 读取参数直接进入训练主页 SingleModeTrainingMainActivity
//        holder.memberName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position=holder.getAdapterPosition();
//                Member member=memberArrayList.get(position);
//                Intent intent=new Intent(v.getContext(),SingleModeTrainingMainActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putInt("MEMBER_ID",member.getMemberID());
//                intent.putExtras(bundle);
//                v.getContext().startActivity(intent);
//            }
//        });
//        holder.memberView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position=holder.getAdapterPosition();
//                Member member=memberArrayList.get(position);
//                Toast.makeText(v.getContext(),"会员名是"+member.getMemberName(),Toast.LENGTH_LONG).show();
//            }
//        });
        holder.memberView.setLongClickable(true);
        holder.memberView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.setPara.setVisibility(View.VISIBLE);
                return false;
            }
        });
        holder.setPara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Member member=memberArrayList.get(position);
                Intent intent=new Intent(v.getContext(),MemberDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("MEMBER_ID",member.getMemberID());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder viewHolder, int i) {

        Member member=memberArrayList.get(i);
        viewHolder.memberImage.setImageResource(member.getImageID());
        viewHolder.memberName.setText(member.getMemberName());
    }

    @Override
    public int getItemCount() {
        return memberArrayList.size();
    }
}