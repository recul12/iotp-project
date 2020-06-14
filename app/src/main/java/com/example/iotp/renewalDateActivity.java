package com.example.iotp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotp.Info.memberinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class renewalDateActivity extends AppCompatActivity {
    private static final String TAG = "renewalDateActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;

    Spinner lockerSpinner;
    ArrayAdapter<String> lockerAdapter;
    memberinfo member;
    TextView renewDateText = null;

    SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_term);

        final ArrayList<String> date = new ArrayList<>();
        date.clear();
        date.add("1개월");
        date.add("3개월");
        date.add("6개월");
        date.add("1학기(4개월)");

        Intent intent = getIntent();
        memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        member = new memberinfo(memberRef);

        TextView currentDateText = (TextView)findViewById(R.id.currentDateText); //현재 사물함 사용기한
        renewDateText = (TextView)findViewById(R.id.renewDateText); //연장했을 경우 사물함 기한

        findViewById(R.id.renewalButton).setOnClickListener(onClickLitsener); //버튼

        String currentDate = memberRef.getDate();
        currentDateText.setText(currentDate);

        lockerSpinner = (Spinner)findViewById(R.id.lockerSpinner);
        lockerAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,date);

        lockerSpinner.setAdapter(lockerAdapter);
        lockerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String currentDate = member.getDate();
                setRenewalText(date.get(i), currentDate);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.renewalButton:
                    renewal();
                    startToast("사물함 대여기한이 연장되었습니다.");
                    startDActivity(MainActivity.class, member);
                    finish();
                    break;
            }
        }
    };

    private void setRenewalText(String a, String currentDate){
        Date Cdate = null;
        try {
            Cdate = td.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();

        if(a.equals("1개월")){
            cal.setTime(Cdate);
            cal.add(Calendar.MONTH,1);
            renewDateText.setText(td.format(cal.getTime()));
            startToast("연장날짜: " + td.format(cal.getTime()));
        }else if(a.equals("3개월")){
            cal.setTime(Cdate);
            cal.add(Calendar.MONTH,3);
            renewDateText.setText(td.format(cal.getTime()));
            startToast("연장날짜: " + td.format(cal.getTime()));
        }else if(a.equals("6개월")){
            cal.setTime(Cdate);
            cal.add(Calendar.MONTH,6);
            renewDateText.setText(td.format(cal.getTime()));
            startToast("연장날짜: " + td.format(cal.getTime()));
        }else if(a.equals("1학기(4개월)")){
            cal.setTime(Cdate);
            cal.add(Calendar.MONTH,4);
            renewDateText.setText(td.format(cal.getTime()));
            startToast("연장날짜: " + td.format(cal.getTime()));
        }
    }

    private void renewal(){
        renewDateText = (TextView)findViewById(R.id.renewDateText);
        String renewDate = renewDateText.getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference hopperRef = mDatabase.child("users").child(user.getUid());
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("date", renewDate);

        hopperRef.updateChildren(hopperUpdates);

        change_date(member, renewDate);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void change_date(memberinfo memberinfo, String a){
        memberinfo.setDate(a);
    }

    private void startDActivity(Class c, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }
}

