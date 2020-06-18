package com.example.iotp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iotp.Adapter.AlarmAdapter;
import com.example.iotp.Info.MemoInfo;
import com.example.iotp.Info.memberinfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MemoInfo> arrayList;
    private ArrayList<String> arrayKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        FloatingActionButton fabCreateMemo = (FloatingActionButton) findViewById(R.id.fab2);
        recyclerView = findViewById(R.id.memorecyclerView);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager((this));
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        arrayKeyList=new ArrayList<>();
        arrayList.clear();

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");


        FirebaseDatabase.getInstance().getReference("memos/"+user.getUid()).
                addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    arrayKeyList.add(dataSnapshot.getKey());
                    Log.d(TAG, dataSnapshot.getKey());

                    MemoInfo memoInfo = dataSnapshot.getValue(MemoInfo.class);

                    String cd=memoInfo.getCreateDate();

                    long tmp;
                    try {
                        tmp=timecheck(cd);
                        if(tmp<0){

                            FirebaseDatabase.getInstance().getReference("memos/"+user.getUid()).child(dataSnapshot.getKey()).setValue(null);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    arrayList.add(memoInfo);

                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });



        adapter = new AlarmAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new selectdocumentActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new selectdocumentActivity.ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "SUCCESS");
                MemoInfo memo = arrayList.get(position);
                String key= arrayKeyList.get(position);
                startDActivity(AlarmActivity.class, memberRef ,memo, key);
                finish();
            }
        }));
        fabCreateMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext() ,AlarmActivity.class);
                intent.putExtra("memberRef", memberRef);
                startActivity(intent);
            }
        });;
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

    private void startDActivity(Class c, memberinfo memberinfo,MemoInfo a, String key){
        Intent intent=new Intent(this,c);
        intent.putExtra("memoRef", a);
        intent.putExtra("memberRef", memberinfo);
        intent.putExtra("keyString",key);
        startActivity(intent);
    }
}
