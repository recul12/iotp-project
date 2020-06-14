package com.example.iotp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.iotp.Info.LockerInfo;
import com.example.iotp.Info.memberinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MemberInitActivity extends AppCompatActivity {
    private static final String TAG = "memberActivity";
    private DatabaseReference mDatabase;
    memberinfo sendmember;

    Spinner boxspinner;
    Spinner datespinner;
    ArrayAdapter<String> boxAdapter;
    ArrayAdapter<String> dateAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<String> lockerID = new ArrayList<>();
    String lockerIDD = null;

    SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView nameText = (TextView)findViewById(R.id.nameText);
        nameText.setText("유저 네임 : "+user.getDisplayName());
        final ArrayList<String> date = new ArrayList<>();
        date.clear();
        date.add("1개월");
        date.add("3개월");
        date.add("6개월");
        date.add("1학기(4개월)");

        final ArrayList<String> box = new ArrayList<>();
        box.clear();

        boxspinner = (Spinner)findViewById(R.id.boxSpinner);
        datespinner = (Spinner)findViewById(R.id.dateSpinner);

        mDatabase.child("locker").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            LockerInfo lockerinfo = snapshot.getValue(LockerInfo.class);
                            if(lockerinfo.getUserID().equals("none")) {
                                lockerID.add(snapshot.getKey());
                                box.add(lockerinfo.getLocal());

                                boxAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,box);

                                boxspinner.setAdapter(boxAdapter);

                                boxspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        String boxnum = boxspinner.getSelectedItem().toString();
                                        if(boxnum != "사물함을 선택해주세요."){
                                            startToast(box.get(i)+" 선택되었습니다.");
                                            lockerIDD = lockerID.get(i-1);
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        cal.setTime(new Date());
        startToast(td.format(cal.getTime()));

        box.add("사물함을 선택해주세요.");

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);

        dateAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,date);

        datespinner.setAdapter(dateAdapter);

        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startToast(date.get(i)+" 선택되었습니다.");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }


    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    Log.e("클릭", "클릭");
                    String boxnum = boxspinner.getSelectedItem().toString();
                    if(boxnum != "사물함을 선택해주세요."){
                        profileUpdate();
                        finish();
                    }else{
                        startToast("사물함을 선택해주세요.");
                    }
                    break;
            }
        }
    };

    private void profileUpdate(){
        String name=user.getDisplayName();
        String phoneNumber = ((EditText)findViewById(R.id.PhoneEditText)).getText().toString();
        String Local = boxspinner.getSelectedItem().toString();
        String Pdate = null;

        if(datespinner.getSelectedItem().toString().equals("1개월")){
            cal.add(Calendar.MONTH,1);
            Pdate = td.format(cal.getTime());
        }else if(datespinner.getSelectedItem().toString().equals("3개월")){
            cal.add(Calendar.MONTH,3);
            Pdate = td.format(cal.getTime());
        }else if(datespinner.getSelectedItem().toString().equals("6개월")){
            cal.add(Calendar.MONTH,6);
            Pdate = td.format(cal.getTime());
        }else if(datespinner.getSelectedItem().toString().equals("1학기(4개월)")){
            cal.add(Calendar.MONTH,4);
            Pdate = td.format(cal.getTime());
        }

        if(name.length()>0 && Local.length() > 0 && Pdate.length() > 0 && phoneNumber.length() > 0){
            memberinfo memberref = new memberinfo(name, Local, lockerIDD, Pdate, phoneNumber,false);
            Log.d(TAG, memberref.getName());
            startDActivity(memberInsertActivity.class, memberref);
        }else{
            startToast("회원정보를 입력해주세요.");
        }
    }


    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startDActivity(Class c, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }
}

