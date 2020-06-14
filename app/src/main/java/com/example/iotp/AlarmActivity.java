package com.example.iotp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";
    private FirebaseAuth mFirebaseAuth; //인증 정보 저장하는 멤버 객체

    private FirebaseUser mFirebaseUser; //사용자 정보 저장하는 멤버 객체

    private EditText memo; //view 중 content(글 내용)를 받아오는 멤버 객체

    private DatabaseReference mFirebaseDatabase;
    private String date="";
    private String time="";
    private String key;
    private TextView textView_Date;
    private TextView textView_time;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private TimePickerDialog.OnTimeSetListener timecallback;
    private MemoInfo memoRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        memo = (EditText) findViewById(R.id.memo);
        mFirebaseDatabase=FirebaseDatabase.getInstance().getReference();
        FloatingActionButton fabSaveMemo = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent=getIntent();
        memoRef = (MemoInfo) intent.getSerializableExtra("memoRef");
        key= intent.getStringExtra("keyString");
        if ( mFirebaseUser == null ) { // 인증 객체에서 User 정보를 가져오지 못하는 경우
            startActivity(new Intent(AlarmActivity.this, MainActivity.class));
            finish();
            return;
        }

        if(memoRef==null){
        initMemo();
        this.InitializeView();
        this.InitializeListener();
        fabSaveMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveMemo();
                }
            });
        }
        else{
            this.InitializeView();
            this.InitializeListener();
            fabSaveMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMemo();

                }
            });
        }




    }
    private void initMemo() {
        memo.setText("");
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
                    moy="0"+String.valueOf(monthOfYear);
                else
                    moy=String.valueOf(monthOfYear);
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
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2020, 0, 1);

        dialog.show();
    }

    public void TimeOnClickHandler(View view)
    {
        TimePickerDialog dialog = new TimePickerDialog(this, timecallback, 0, 0, false);

        dialog.show();
    }
    private void saveMemo() {

        String text = memo.getText().toString();
        if ( text.isEmpty() ) {
            return;
        }

        final MemoInfo memo = new MemoInfo();
        memo.setTxt(text);
        memo.setCreateDate(date+time);
        mFirebaseDatabase
                .child("memos")
                .child(mFirebaseUser.getUid())
                .push()
                .setValue(memo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "메모가 저장되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        Alarm();
        finish();
    }
    private void updateMemo() {
        String text = memo.getText().toString();
        if ( text.isEmpty() ) {
            return;
        }

        MemoInfo memo = memoRef;
        memo.setTxt(text);
        memo.setCreateDate(date+time);
        Log.e(TAG,key);
        FirebaseDatabase.getInstance().
                getReference("memos/" + mFirebaseUser.getUid() + "/" + key)
                .setValue(memo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "메모가 저장되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        Alarm();
        finish();
    }

    public void Alarm() {

        Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기
        int year=Integer.parseInt(memoRef.getCreateDate().substring(0,4));
        int month=Integer.parseInt(memoRef.getCreateDate().substring(4,6));
        int day=Integer.parseInt(memoRef.getCreateDate().substring(6,8));
        int hour=Integer.parseInt(memoRef.getCreateDate().substring(8,10));
        int minute=Integer.parseInt(memoRef.getCreateDate().substring(10,12));

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Broadcast.class);

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);


        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        Log.d(TAG,String.valueOf(year));
        Log.d(TAG,String.valueOf(month));
        Log.d(TAG,String.valueOf(day));
        Log.d(TAG,String.valueOf(hour));
        Log.d(TAG,String.valueOf(minute));
        Log.d(TAG, String.valueOf(calendar.getTimeInMillis()));
            //알람 예약
        am.set(AlarmManager.RTC,calendar.getTimeInMillis(),sender);
    }


}
