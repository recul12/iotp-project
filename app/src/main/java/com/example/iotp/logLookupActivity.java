package com.example.iotp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotp.Adapter.logAdapter;
import com.example.iotp.Info.GoodsNameInfo;
import com.example.iotp.Info.RawLogInfo;
import com.example.iotp.Info.documentInfo;
import com.example.iotp.Info.logInfo;
import com.example.iotp.Info.memberinfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class logLookupActivity extends AppCompatActivity {
    private static final String TAG = "logLookupActivity";
    private DatabaseReference mDatabase;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<logInfo> logList;
    private ArrayList<logInfo> logList_origin;

    Spinner goodsSpinner;
    Spinner inOutSpinner;
    ArrayAdapter<String> goodsAdapter;
    ArrayAdapter<String> inOutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        Log.d(TAG, " => " + get_member_lockerID(memberRef));
        mDatabase = FirebaseDatabase.getInstance().getReference();


        recyclerView = findViewById(R.id.logRecyclerView);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager((this));
        recyclerView.setLayoutManager(layoutManager);
        logList = new ArrayList<>();
        logList_origin = new ArrayList<>();

        logList.clear();
        logList_origin.clear();

        mDatabase.child("inOutList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            RawLogInfo RawLog = snapshot.getValue(RawLogInfo.class);
                            if (RawLog != null) {
                                if(get_log_lockerID(RawLog).equals(get_member_lockerID(memberRef))) {
                                    //String time = rename_time(snapshot.getKey());
                                    logInfo log = new logInfo(snapshot.getKey(), get_log_ID(RawLog), get_log_inOut(RawLog));
                                    Log.d(TAG, " => " + get_log_logTime(log));
                                    logList.add(log);
                                    logList_origin.add(log);
                                }
                            }else{
                                startToast("아직 아무 기록도 없어요.");
                                finish();
                            }
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });


        adapter = new logAdapter(logList, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "SUCCESS");
                startDActivity(logDetailActivity.class, logList.get(position), memberRef);
            }
        }));

        Switch ascSwitch = (Switch) findViewById(R.id.ascSwitch);

        ascSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Comparator<logInfo> noDesc = new Comparator<logInfo>() {
                        @Override
                        public int compare(logInfo o1, logInfo o2) {
                            SimpleDateFormat td = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                            String date1 = get_log_logTime(o1);
                            String date2 = get_log_logTime(o2);
                            int ret = 0;

                            Date day1 = null;
                            Date day2 = null;
                            try {
                                day1 = td.parse(date1);
                                day2 = td.parse(date2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            int compare = day1.compareTo(day2);

                            if(compare < 0)
                                ret = 1;
                            else if (compare > 0)
                                ret = -1;
                            else
                                ret = 0;

                            return ret;
                        }
                    };
                    Collections.sort(logList, noDesc);
                    adapter.notifyDataSetChanged();
                }else{
                    Comparator<logInfo> noAsc = new Comparator<logInfo>() {
                        @Override
                        public int compare(logInfo o1, logInfo o2) {
                            SimpleDateFormat td = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                            String date1 = get_log_logTime(o1);
                            String date2 = get_log_logTime(o2);
                            int ret = 0;

                            Date day1 = null;
                            Date day2 = null;
                            try {
                                day1 = td.parse(date1);
                                day2 = td.parse(date2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            int compare = day1.compareTo(day2);

                            if(compare < 0)
                                ret = -1;
                            else if (compare > 0)
                                ret = 1;
                            else
                                ret = 0;

                            return ret;
                        }
                    };
                    Collections.sort(logList, noAsc);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        final ArrayList<String> goodsName = new ArrayList<>();
        final ArrayList<String> inOut = new ArrayList<>();
        final ArrayList<GoodsNameInfo> goods = new ArrayList<>();
        goodsName.clear();
        inOut.clear();
        goods.clear();

        goodsName.add("");
        inOut.add("");
        inOut.add("수납/열림");
        inOut.add("출납/닫힘");

        mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            documentInfo documentInfo = snapshot.getValue(documentInfo.class);
                            GoodsNameInfo goodsNameInfo = new GoodsNameInfo(snapshot.getKey(), documentInfo.getGoodsName());
                            goods.add(goodsNameInfo);
                            goodsName.add(documentInfo.getGoodsName());
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });

        goodsSpinner = (Spinner)findViewById(R.id.goodsSpinner);
        inOutSpinner = (Spinner)findViewById(R.id.inOutSpinner);

        goodsAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,goodsName);
        inOutAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,inOut);

        goodsSpinner.setAdapter(goodsAdapter);
        inOutSpinner.setAdapter(inOutAdapter);

        goodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(goodsName.get(i).length() != 0){
                    logList.clear();
                    String gRFID = change_goodsName_RFID(goods.get(i-1));
                    for(logInfo log : logList_origin){
                        if(inOutSpinner.getSelectedItem().toString().equals("출납/닫힘")){
                            if(log.getInOUT() && log.getContentsRFID().equals(gRFID)){
                                logList.add(log);
                            }
                        }else if(inOutSpinner.getSelectedItem().toString().equals("수납/열림")){
                            if(!log.getInOUT() && log.getContentsRFID().equals(gRFID)){
                                logList.add(log);
                            }
                        }else{
                            if(log.getContentsRFID().equals(gRFID)){
                                logList.add(log);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    if(inOutSpinner.getSelectedItem().toString().equals("출납/닫힘")){
                        for(logInfo log : logList_origin) {
                            if (log.getInOUT()) {
                                logList.add(log);
                            }
                        }
                    }else if(inOutSpinner.getSelectedItem().toString().equals("수납/열림")){
                        for(logInfo log : logList_origin) {
                            if (!log.getInOUT()) {
                                logList.add(log);
                            }
                        }
                    }else{
                        logList.addAll(logList_origin);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        inOutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String gName = null;
                if(goodsSpinner.getSelectedItem().toString().length()!=0){
                    for(GoodsNameInfo goods : goods){
                        if(goods.getGoodsName().equals(goodsSpinner.getSelectedItem().toString())){
                            gName = change_goodsName_RFID(goods);
                        }
                    }
                }

                if(gName != null) {
                    if (inOut.get(i).length() != 0) {
                        if (inOutSpinner.getSelectedItem().toString().equals("출납/닫힘")) {
                            logList.clear();
                            for (logInfo log : logList_origin) {
                                if (log.getInOUT() && log.getContentsRFID().equals(gName)) {
                                    logList.add(log);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else if (inOutSpinner.getSelectedItem().toString().equals("수납/열림")) {
                            logList.clear();
                            for (logInfo log : logList_origin) {
                                if (!log.getInOUT() && log.getContentsRFID().equals(gName)) {
                                    logList.add(log);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        logList.clear();
                        for (logInfo log : logList_origin) {
                            if (log.getContentsRFID().equals(gName)) {
                                logList.add(log);
                            }
                        }
                        //logList.addAll(logList_origin);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    if (inOut.get(i).length() != 0) {
                        if (inOutSpinner.getSelectedItem().toString().equals("출납/닫힘")) {
                            logList.clear();
                            for (logInfo log : logList_origin) {
                                if (log.getInOUT()) {
                                    logList.add(log);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else if (inOutSpinner.getSelectedItem().toString().equals("수납/열림")) {
                            logList.clear();
                            for (logInfo log : logList_origin) {
                                if (!log.getInOUT()) {
                                    logList.add(log);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        logList.clear();
                        logList.addAll(logList_origin);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }




    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }

    private String get_log_ID(RawLogInfo rawLogInfo){
        return rawLogInfo.getID();
    }

    private String get_log_lockerID(RawLogInfo rawLogInfo){
        return rawLogInfo.getLockerID();
    }

    private boolean get_log_inOut(RawLogInfo rawLogInfo){
        return rawLogInfo.isInOut();
    }

    private String get_log_logTime(logInfo loginfo){
        return loginfo.getLogTime();
    }

    private String change_goodsName_RFID(GoodsNameInfo goodsNameInfo){
        return goodsNameInfo.getRFID();
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startDActivity(Class c, logInfo log, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.putExtra("log", log);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }
}

