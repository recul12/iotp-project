package com.example.iotp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.iotp.Info.GoodsNameInfo;
import com.example.iotp.Info.MemoInfo;
import com.example.iotp.Info.documentInfo;
import com.example.iotp.Info.memberinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth; //인증 정보 저장하는 멤버 객체

    private FirebaseUser mFirebaseUser; //사용자 정보 저장하는 멤버 객체

    private EditText memo; //view 중 content(글 내용)를 받아오는 멤버 객체

    private String date="";
    private String time="";
    private String key;
    private TextView textView_Date;
    private TextView textView_time;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private TimePickerDialog.OnTimeSetListener timecallback;
    private MemoInfo memoRef;

    Spinner goodsSpinner;
    ArrayAdapter<String> goodsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mFirebaseAuth = FirebaseAuth.getInstance();

        memo = (EditText) findViewById(R.id.memo);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FloatingActionButton fabSaveMemo = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent=getIntent();
        memoRef = (MemoInfo) intent.getSerializableExtra("memoRef");
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        key= intent.getStringExtra("keyString");


        this.InitializeView();
        this.InitializeListener();

        final ArrayList<String> goodsName = new ArrayList<>();
        final ArrayList<GoodsNameInfo> goods = new ArrayList<>();
        goodsName.clear();
        goods.clear();

        goodsName.add("");

        mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            documentInfo documentInfo = snapshot.getValue(documentInfo.class);
                            Log.d(TAG,snapshot.getKey());
                            GoodsNameInfo goodsNameInfo = new GoodsNameInfo(snapshot.getKey(), documentInfo.getGoodsName());
                            goods.add(goodsNameInfo);
                            goodsName.add(documentInfo.getGoodsName());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });

        goodsSpinner = (Spinner)findViewById(R.id.goodsSpinner);
        goodsAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,goodsName);
        goodsSpinner.setAdapter(goodsAdapter);

        goodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        fabSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goodsSpinner.getSelectedItem().toString().equals("")){
                    startToast("알람을 등록할 물건을 선택해주세요.");
                }else{
                    if(memoRef!=null){
                        try {
                            updateMemo(memoRef, goodsSpinner.getSelectedItem().toString(), key);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            saveMemo(goodsSpinner.getSelectedItem().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    public void InitializeView()
    {
        textView_Date = (TextView)findViewById(R.id.textView_date);
        textView_time = (TextView)findViewById(R.id.textView_time);
    }

    public void InitializeListener()
    {
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                String moy="", dom="";
                int moy1=monthOfYear+1;
                textView_Date.setText(year + "년" + moy1 + "월" + dayOfMonth + "일");
                if(monthOfYear<10)
                    moy="0"+String.valueOf(moy1);
                else
                    moy=String.valueOf(moy1);
                if(dayOfMonth<10)
                    dom="0"+String.valueOf(dayOfMonth);
                else
                    dom=String.valueOf(dayOfMonth);
                date=String.valueOf(year)+moy+dom;
            }
        };

        timecallback = new TimePickerDialog.OnTimeSetListener() {

            @Override

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hod="", min="";
                textView_time.setText(hourOfDay+"시"+minute+"분");


                if(hourOfDay<10)
                    hod="0"+String.valueOf(hourOfDay);
                else
                    hod=String.valueOf(hourOfDay);
                if(minute<10)
                    min="0"+String.valueOf(minute);
                else
                    min=String.valueOf(minute);
                time=hod+min;
            }

        };

    }
    public void OnClickHandler(View view)
    {
        SimpleDateFormat td = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String current = td.format(date);

        int year=Integer.parseInt(current.substring(0,4));
        int month=Integer.parseInt(current.substring(4,6));
        int day=Integer.parseInt(current.substring(6,8));

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, year, month-1, day);

        dialog.show();
    }

    public void TimeOnClickHandler(View view)
    {
        SimpleDateFormat td = new SimpleDateFormat("HHmm");
        Date date = new Date();
        String current = td.format(date);

        int hour=Integer.parseInt(current.substring(0,2));
        int minute=Integer.parseInt(current.substring(2,4));

        TimePickerDialog dialog = new TimePickerDialog(this, timecallback, hour, minute, true);

        dialog.show();
    }
    private void saveMemo(String goodsName) throws ParseException {
        String text = memo.getText().toString();
        String timedate=date+time;
        if ( text.isEmpty() ) {
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final MemoInfo memo = new MemoInfo();
        memo.setTxt(text);
        memo.setCreateDate(timedate);
        memo.setGoodsName(goodsName);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String current = td.format(date);

        long ln=timecheck(timedate);
        if(ln<0){
            startToast("잘못된 시간을 입력하셨습니다.");
        }
        else{
            mDatabase.child("memos").child(user.getUid()).child(current).setValue(memo).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                    startToast("알람 등록 성공");
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
            memoRef=memo;
            Alarm();
            finish();
        }
    }
    private void updateMemo(MemoInfo memoInfo, String goodsName, String key) throws ParseException {
        String text = memo.getText().toString();
        String timedate=date+time;
        if ( text.isEmpty() ) {
            return;
        }

        MemoInfo memo = memoInfo;
        memo.setTxt(text);
        memo.setCreateDate(timedate);
        memo.setGoodsName(goodsName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        long ln=timecheck(timedate);
        if(ln<0){
            startToast("잘못된 시간을 입력하셨습니다.");
        }
        else {
            mDatabase.child("memos").child(user.getUid()).child(key).setValue(memo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "메모가 저장되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            Alarm();
            finish();
        }
    }

    public long timecheck(String data) throws ParseException {

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date=new Date();
        Date to = transFormat.parse(data);

        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(to);
        calendar1.setTime(date);
        long gti=calendar.getTimeInMillis();
        Log.e(TAG, String.valueOf(gti));

        long gti1=calendar1.getTimeInMillis();

        Log.e(TAG, String.valueOf(gti1));
        return gti-gti1;
    }

    public void Alarm() {

        Calendar calendar = Calendar.getInstance();


        Log.d(TAG,memoRef.getCreateDate());
        int year=Integer.parseInt(memoRef.getCreateDate().substring(0,4));
        int month=Integer.parseInt(memoRef.getCreateDate().substring(4,6));
        int day=Integer.parseInt(memoRef.getCreateDate().substring(6,8));
        int hour=Integer.parseInt(memoRef.getCreateDate().substring(8,10));
        int minute=Integer.parseInt(memoRef.getCreateDate().substring(10,12));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Broadcast.class);
        intent.putExtra("goodsName", memoRef.getGoodsName());
        intent.putExtra("memo", memoRef.getTxt());
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);


        Log.d(TAG,String.valueOf(year));
        Log.d(TAG,String.valueOf(month));
        Log.d(TAG,String.valueOf(day));
        Log.d(TAG,String.valueOf(hour));
        Log.d(TAG,String.valueOf(minute));
        Log.d(TAG, String.valueOf(calendar.getTimeInMillis()));
            //알람 예약
        am.set(AlarmManager.RTC,calendar.getTimeInMillis(),sender);
    }

    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
