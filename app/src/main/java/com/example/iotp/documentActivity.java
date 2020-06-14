package com.example.iotp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotp.Info.documentInfo;
import com.example.iotp.Info.memberinfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class documentActivity extends AppCompatActivity {
    private static final String TAG = "documentActivity";
    private ImageView imageView;
    private final int GET_GALLERY_IMAGE = 200;
    private DatabaseReference mDatabase;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        imageView = (ImageView)findViewById(R.id.docuImage); //이미지뷰 연결

        findViewById(R.id.checkButton).setOnClickListener(onClickLitsener);
        findViewById(R.id.docuImage).setOnClickListener(onClickLitsener);
    }

    View.OnClickListener onClickLitsener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    Log.e("클릭", "클릭");
                    infoUpdate();
                    finish();
                    break;
                case R.id.docuImage:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        }
    };

    private void infoUpdate(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat td = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        StorageReference storageRef = storage.getReference();
        final StorageReference ImagesRef = storageRef.child("images/"+user.getUid()+"_"+td.format(cal.getTime())+"_profile.jpg");  //스토리지에 저장될 사진의 이름 설정

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); // 밑의 네 줄까지 이미지뷰에 저장되있는 사진을 스토리지에 보낼 준비
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ImagesRef.putBytes(data); // 스토리지에 저장하는 함수

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) { //저장 성공시 실행되는 리스너 함수
                if (task.isSuccessful()) {
                    String dname = ((EditText)findViewById(R.id.dnameEditText)).getText().toString();
                    String detail = ((EditText)findViewById(R.id.detailEditText)).getText().toString();
                    Uri downloadUri = task.getResult(); //사진의 저장경로를 뽑아오는 함수
                    Log.d(TAG, "SUCCESS"+ downloadUri);

                    if(dname.length() > 0){
                        if(detail.length() == 0){
                            detail = "없음";
                        }

                        Intent intent = getIntent();
                        //documentInfo goodsRef = (documentInfo) intent.getSerializableExtra("goodsRef");
                        String goodsRfid = intent.getExtras().getString("goodsRfid");
                        memberinfo memberRef = (memberinfo) intent.getSerializableExtra("memberRef");

                        documentInfo documentInfo = new documentInfo(dname, detail, false, downloadUri.toString());

                        /*db.collection("goods").document(goodsRfid).set(documentInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startToast("물품 정보 갱신에 성공하였습니다");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        startToast("물품 등록 실패");
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });*/
                        mDatabase.child("locker").child(get_member_lockerID(memberRef)).child("goods").child(goodsRfid).setValue(documentInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("물품 정보 갱신에 성공하였습니다");
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        startToast("물품 등록 실패");
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }else{
                        startToast("물건의 이름은 정해주어야 합니다.");
                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            /*try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
             */
            imageView.setImageURI(selectedImageUri); //갤러리에서 불러온 사진을 이미지뷰에 세트
        }

    }



    public String get_user_name(memberinfo user){
        return user.getName();
    }

    private String get_member_lockerID(memberinfo memberinfo){
        return memberinfo.getLockerID();
    }


    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

