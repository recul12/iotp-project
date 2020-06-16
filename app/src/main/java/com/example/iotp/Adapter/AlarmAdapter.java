package com.example.iotp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotp.Info.MemoInfo;
import com.example.iotp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.CustomViewHolder>{
    private ArrayList<MemoInfo> arrayList;
    private Context context;
    private SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMddHHmm");
    private SimpleDateFormat dateFormat =new SimpleDateFormat("MM/dd HH:mm");
    public AlarmAdapter(ArrayList<MemoInfo> arrayList, Context context) { //생성자
        this.arrayList = arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public AlarmAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        AlarmAdapter.CustomViewHolder holder = new AlarmAdapter.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.CustomViewHolder holder, int position) {  //실질적으로 텍스트뷰에 텍스트를 설정하는 부분
        try {
            Date date1=dateFormat1.parse(arrayList.get(position).getCreateDate());
            holder.summaryView.setText(arrayList.get(position).getTxt());
            holder.dateView.setText(dateFormat.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0); // 널값이 아니면 리스트 사이즈를, 널값이면 0 반환
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView summaryView;
        TextView dateView;
        public CustomViewHolder(@NonNull View itemView) {  // 레이아웃 아이템들 연결
            super(itemView);
            this.summaryView = itemView.findViewById((R.id.summaryView));
            this.dateView = itemView.findViewById((R.id.dateView));
        }
    }
}
