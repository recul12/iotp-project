package com.example.iotp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.iotp.Adapter.docuAdapter;
import com.example.iotp.Info.documentInfo;
import com.example.iotp.Info.memberinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class selectdocumentActivity extends AppCompatActivity {
    private static final String TAG = "selectdocumentActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<documentInfo> arrayList;
    private ArrayList<String> RFIDname;

    //documentInfo DI; //물건객체
    //ArrayList<String> documentRFID = new ArrayList<String>();    //물건 RFID를 넣을 문자열



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        final memberinfo memberRef = (memberinfo)intent.getSerializableExtra("memberRef");
        Log.d(TAG, " => " + get_member_lockerID(memberRef));


        recyclerView = findViewById(R.id.docurecyclerView);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager((this));
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        RFIDname = new ArrayList<>();

        arrayList.clear();

        startToast("물건을 선택하시면 편집하실 수 있습니다.");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            documentInfo documentInfo = snapshot.getValue(documentInfo.class);
                            if(documentInfo == null){
                                Log.d(TAG, "아직 물건등록이 되어있지 않아요.");
                                finish();
                            }
                            RFIDname.add(snapshot.getKey());
                            arrayList.add(documentInfo);
                        }
                        adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "get failed ");
                    }
                });

        adapter = new docuAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener(){
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "SUCCESS");
                documentInfo docu = arrayList.get(position);
                String rfid = RFIDname.get(position);
                Log.d(TAG, "=>" + rfid);
                startDActivity(documentActivity.class, docu, memberRef, rfid);
                finish();
            }
        }));

        /*ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, documentRFID) ;

        ListView listview = (ListView) findViewById(R.id.docuListView) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String RFID = (String) parent.getItemAtPosition(position) ;
                startDActivity(documentActivity.class, RFID);
            }
        }) ;*/
        /*goodsSpinner = (Spinner)findViewById(R.id.goodsSpinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, docuID);
        goodsSpinner.setAdapter(arrayAdapter);  // 스피너 연결

        boxnumTextView = (TextView)findViewById(R.id.boxnumTextView);
        userIDTextView = (TextView)findViewById(R.id.userIDTextView);
        goodsNameTextView = (TextView)findViewById(R.id.goodsNameTextView);
        detailTextView = (TextView)findViewById(R.id.detailTextView); // 각 텍스트뷰를 연결*/

        /*goodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // 스피너가 선택되었을 때 이벤트 설정
                documentRFID = docuID.get(position);
                startToast(documentRFID + "가 선택되었습니다");
                Log.i(TAG, "Spinner selected item = "+documentRFID);
                db_item(documentRFID);

                boxnumTextView.setText(get_docu_boxnum());
                userIDTextView.setText(get_docu_userID());
                goodsNameTextView.setText(get_docu_goodsName());
                detailTextView.setText(get_docu_detail());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
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

    /*private void readData(final Callback callback){

    }

    private interface Callback{
        void onCallback(List<documentInfo> list);
    }*/


    /*private void makeArray(){
        db.collection("goods")
                .whereEqualTo("boxnum", user_get_boxnum())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                documentRFID.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }*/

    /*public void db_item(){
        DocumentReference docRef = db.collection("users").document(user.getEmail());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                memberinfo = documentSnapshot.toObject(memberinfo.class);
            }
        });
    }*/

    private void startDActivity(Class c, documentInfo a,memberinfo b, String d){
        Intent intent=new Intent(this,c);
        intent.putExtra("goodsRef", a);
        intent.putExtra("memberRef", b);
        intent.putExtra("goodsRfid", d);
        startActivity(intent);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

