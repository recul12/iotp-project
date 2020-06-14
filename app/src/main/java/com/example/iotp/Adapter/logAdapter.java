package com.example.iotp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotp.Info.logInfo;
import com.example.iotp.R;

import java.util.ArrayList;


public class logAdapter extends RecyclerView.Adapter<logAdapter.CustomViewHolder> {

    private ArrayList<logInfo> arrayList;
    private Context context;

    public logAdapter(ArrayList<logInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logdata, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.logTime.setText(arrayList.get(position).getLogTime());
        holder.contentsRFID.setText(arrayList.get(position).getContentsRFID());
        if(arrayList.get(position).getInOUT()) {
            if(arrayList.get(position).getContentsRFID().equals("none")){
                holder.inOUT.setText("열림");
                holder.inOUT.setTextColor(Color.parseColor("#FF4444"));
            }else {
                holder.inOUT.setText("출납");
                holder.inOUT.setTextColor(Color.parseColor("#FF4444"));
            }
        }
        else {
            if(arrayList.get(position).getContentsRFID().equals("none")){
                holder.inOUT.setText("닫힘");
                holder.inOUT.setTextColor(Color.parseColor("#009900"));
            }else {
                holder.inOUT.setText("수납");
                holder.inOUT.setTextColor(Color.parseColor("#009900"));
            }
        }
    }

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView logTime;
        TextView contentsRFID;
        TextView inOUT;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.logTime = itemView.findViewById(R.id.logTime);
            this.contentsRFID = itemView.findViewById((R.id.contentsRFID));
            this.inOUT = itemView.findViewById((R.id.inOUT));
        }
    }
}
