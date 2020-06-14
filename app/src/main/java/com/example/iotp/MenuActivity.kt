package com.example.iotp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.iotp.Info.memberinfo
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val intent = intent
        val memberRef = intent.getSerializableExtra("memberRef") as memberinfo

        lockerCard.setOnClickListener{
            val intent=Intent(applicationContext, selectdocumentActivity::class.java)
            intent.putExtra("memberRef", memberRef)
            startActivity(intent)
        }

        logCard.setOnClickListener{
            val intent=Intent(applicationContext, logLookupActivity::class.java)
            intent.putExtra("memberRef", memberRef)
            startActivity(intent)
        }

        scheduleCard.setOnClickListener{
            val intent=Intent(applicationContext, ListActivity::class.java)
            intent.putExtra("memberRef", memberRef)
            startActivity(intent)
        }
    }

}
