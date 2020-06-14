package com.example.iotp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotp.Info.memberinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class userInfoActivity extends AppCompatActivity {
    private static final String TAG = "userInfoActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;
    memberinfo memberRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent intent = getIntent();
        memberRef = (memberinfo)intent.getSerializableExtra("memberRef");

        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView phoneText = (TextView)findViewById(R.id.phoneText);
        TextView lockerText = (TextView)findViewById(R.id.lockerText);
        TextView dateText = (TextView)findViewById(R.id.dateText);

        nameText.setText("사용자 이름: "+memberRef.getName());
        phoneText.setText("전화번호: " + memberRef.getPhoneNumber());
        lockerText.setText("사물함 위치: "+ memberRef.getLocal());
        dateText.setText("대여기한: "+ memberRef.getDate());

        findViewById(R.id.lockerTermButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent = getIntent();
            memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
            switch ((v.getId())){
                case R.id.lockerTermButton:
                    startDActivity(renewalDateActivity.class, memberRef);
                    finish();
                    break;
            }
        }
    };

    private void startDActivity(Class c, memberinfo memberinfo){
        Intent intent=new Intent(this,c);
        intent.putExtra("memberRef", memberinfo);
        startActivity(intent);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

