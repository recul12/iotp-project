package com.example.iotp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotp.Info.memberinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class memberInsertActivity extends AppCompatActivity {
    private static final String TAG = "memberActivity";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_confirm);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");

        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView phoneText = (TextView)findViewById(R.id.phoneText);
        TextView lockerText = (TextView)findViewById(R.id.lockerText);
        TextView dateText = (TextView)findViewById(R.id.dateText);

        nameText.setText(memberRef.getName());
        phoneText.setText(memberRef.getPhoneNumber());
        lockerText.setText(memberRef.getLocal());
        dateText.setText(memberRef.getDate());

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);
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
                    save();
                    finish();
                    break;
            }
        }
    };

    private void save(){
        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.child("users").child(user.getUid()).setValue(memberRef)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startToast("회원정보 등록 실패");
                Log.w(TAG, "Error adding document", e);
            }
        });

        DatabaseReference hopperRef = mDatabase.child("locker").child(get_member_lockerID(memberRef));
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("userID", user.getUid());

        hopperRef.updateChildren(hopperUpdates);

        startDActivity(ResultActivity.class, memberRef);
    }


    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }

    private void startDActivity(Class c, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }
}

