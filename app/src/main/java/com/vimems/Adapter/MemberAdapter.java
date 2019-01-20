package com.vimems.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.AdapterItem.MemberItem;
import com.vimems.R;

import java.util.ArrayList;

/**
 * author sunupo
 * date 2019/1/19 14:00
 * description 
 * 
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    private ArrayList<MemberItem> memberItemArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View memberView;
        ImageView memberImage;
        TextView memberName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberView=itemView;
            memberImage= itemView.findViewById(R.id.member_image);
            memberName= itemView.findViewById(R.id.member_name);
        }
    }


    public MemberAdapter(ArrayList<MemberItem> memberItemArrayList) {
        this.memberItemArrayList = memberItemArrayList;
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_member,viewGroup,false);
        final ViewHolder holder=new ViewHolder(itemView);
        holder.memberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                MemberItem memberItem=memberItemArrayList.get(position);
                Toast.makeText(v.getContext(),"you have clicked view"+memberItem.getName(),Toast.LENGTH_LONG).show();
            }
        });
        holder.memberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                MemberItem memberItem=memberItemArrayList.get(position);
                Toast.makeText(v.getContext(),"教练是"+memberItem.getName(),Toast.LENGTH_LONG).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder viewHolder, int i) {

        MemberItem memberItem=memberItemArrayList.get(i);
        viewHolder.memberImage.setImageResource(memberItem.getImageId());
        viewHolder.memberName.setText(memberItem.getName());
    }

    @Override
    public int getItemCount() {
        return memberItemArrayList.size();
    }
}