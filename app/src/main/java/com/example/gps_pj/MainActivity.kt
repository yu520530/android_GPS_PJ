package com.example.gps_pj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //將firebaseauth引入
    private lateinit var auth:FirebaseAuth
    //tag方便偵錯
    var TAG:String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //auth 取得值
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener{
            val email = ed_email.text.toString()
            val psd = ed_psd.text.toString()

            auth.signInWithEmailAndPassword(email,psd)
                .addOnCompleteListener(this) {task ->
                    if(task.isSuccessful)
                    {
                        Log.d(TAG,"login success")
                        val intent = Intent(this,MapsActivity::class.java)
                        Toast.makeText(this,"login success",Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                    else
                    {
                        Log.e(TAG,"login unsuccess",task.exception)
                        Toast.makeText(this,"please check your email and password",Toast.LENGTH_SHORT).show()

                    }
                }

        }

    }
}