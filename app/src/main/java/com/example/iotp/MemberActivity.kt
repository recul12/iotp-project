package com.example.iotp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.iotp.Info.memberinfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_member.*


class MemberActivity : AppCompatActivity() {
    private var TAG = "MemberActivity"
    var sendmember: memberinfo? = null
    var user = FirebaseAuth.getInstance().currentUser
    private var mDatabase: DatabaseReference? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        mDatabase = FirebaseDatabase.getInstance().reference

        val memberRef = intent.getSerializableExtra("memberRef") as memberinfo
        val email = intent.getStringExtra("email")
        val photoUrl = intent.getParcelableExtra<Uri>("photoUrl")

        name_text.setText(memberRef.name)
        email_text.setText(email)
        Glide.with(this).load(photoUrl).into(ig_photoUrl)
        locker_name.setText(memberRef.lockerID)
        phone_num.setText(memberRef.phoneNumber)




        phone_button.setOnClickListener {
            memberRef.phoneNumber =
                (findViewById<View>(R.id.phone_num) as EditText).text
                    .toString()
            mDatabase!!.child("users").child(user!!.uid).setValue(memberRef)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext,"회원 정보 변경에 성공하였습니다",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("memberRef", memberRef)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext,"정보 등록 실패",Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

    }
    private fun sendpack(memberinfo: memberinfo) {
        sendmember = memberinfo(memberinfo)
    }


}
