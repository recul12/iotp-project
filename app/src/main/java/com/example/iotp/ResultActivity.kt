package com.example.iotp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.iotp.Info.memberinfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*


class ResultActivity : AppCompatActivity() {
    private var mDatabase: DatabaseReference? = null
    var db = FirebaseDatabase.getInstance()
    var providerId:String?=null
    var uid:String?=null
    var name:String?=null
    var email:String?=null
    var photoUrl: Uri?=null
    var user = FirebaseAuth.getInstance().currentUser
    //val docRef = db.collection("users").document(user!!.email!!)
    var sendmember: memberinfo? = null
    var member : memberinfo? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        myToolbar.setTitle(R.string.Toolbar)
        setSupportActionBar(myToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_menu_black_18dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val nickName = intent.getStringExtra("nickName")

        tv_result.setText(nickName+"님 환영합니다!")

        getProviderData()

        dbInit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            android.R.id.home->{ // 메뉴 버튼
                val intent = Intent(applicationContext, MenuActivity::class.java)
                intent.putExtra("memberRef", sendmember)
                startActivity(intent)
            }
            R.id.userInfo->{ // 검색 버튼
                val intent = Intent(applicationContext, MemberActivity::class.java)
                intent.putExtra("memberRef", sendmember)
                intent.putExtra("email",email)
                intent.putExtra("photoUrl",photoUrl)
                startActivity(intent)
            }
            R.id.menu_account->{ // 계정 버튼
                val intent=Intent(this,userInfoActivity::class.java)
                intent.putExtra("memberRef", sendmember)
                startActivity(intent)
            }
            R.id.menu_shutdown->{ // 로그아웃 버튼
                ActivityCompat.finishAffinity(this)
                System.exit(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendpack(memberinfo: memberinfo) {
        sendmember = memberinfo(memberinfo)
        Log.d("tag", memberinfo.name)
    }

    private fun getProviderData() {
        // [START get_provider_data]
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                providerId = profile.providerId

                // UID specific to the provider
                uid = profile.uid

                // Name, email address, and profile photo Url
                name = profile.displayName
                email = profile.email
                photoUrl = profile.photoUrl
            }
        }
        // [END get_provider_data]
    }

    private fun dbInit(){
        mDatabase = FirebaseDatabase.getInstance().reference
        user?.getUid()?.let {
            mDatabase!!.child("users").child(it).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        member =
                            dataSnapshot.getValue(memberinfo::class.java)
                        if (member == null) {
                            startActivity(Intent(applicationContext,MemberInitActivity::class.java))
                        } else {
                            sendpack(member!!)
                        }
                        locker_button.setOnClickListener{
                            val intent = Intent(applicationContext, MenuActivity::class.java)
                            intent.putExtra("memberRef", sendmember)
                            startActivity(intent)
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        }
    }


    private fun startToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}