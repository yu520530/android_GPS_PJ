package com.example.gps_pj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
        cl_signUp.visibility = View.GONE
        cl_login.visibility = View.VISIBLE


        //登入功能
        btn_login.setOnClickListener{
            val email = ed_email.text.toString()
            val psd = ed_psd.text.toString()
            //帳號驗證
            if(email ==""||psd=="")
            {
                Toast.makeText(this, "帳號或密碼空白",
                    Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(email,psd)
                    .addOnCompleteListener(this) {task ->
                        if(task.isSuccessful)
                        {
                            Log.d(TAG,"login success")
                            Toast.makeText(this,"login success",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,MapsActivity::class.java)
                            intent.putExtra("ac",email)
                            startActivity(intent)
                        }
                        else
                        {
                            Log.w(TAG,"login unsuccess",task.exception)
                            Toast.makeText(this,"please check your email and password",Toast.LENGTH_SHORT).show()

                        }
                    }
            }

        }

        //創建帳號功能
        btn_signUp.setOnClickListener {
            cl_login.visibility = View.GONE
            cl_signUp.visibility = View.VISIBLE
        }

       //confirm ac
       btn_create_confirm.setOnClickListener {
           val created_email = ed_create_email.text.toString()
           val created_psd = ed_create_psd.text.toString()
           val created_check_psd = ed_create_check_psd.text.toString()
           //確認密碼一致性
           if(created_email ==""||created_psd==""||created_check_psd=="")
           {
               Toast.makeText(this, "帳號或密碼或確認密碼空白",
                   Toast.LENGTH_SHORT).show()
           }
           else
           {
               if(created_psd == created_check_psd)
               {
                   auth.createUserWithEmailAndPassword(created_email, created_psd)
                       .addOnCompleteListener(this) { task ->
                           if (task.isSuccessful) {
                               // Sign in success, update UI with the signed-in user's information
                               Log.d(TAG, "createUserWithEmail:success")
                               Toast.makeText(this, "Authentication success.",
                                   Toast.LENGTH_SHORT).show()
                               cl_signUp.visibility = View.GONE
                               cl_login.visibility = View.VISIBLE

                           } else {
                               Log.w(TAG, "createUserWithEmail:failure", task.exception)
                               Toast.makeText(this, "Authentica tion failed.",
                                   Toast.LENGTH_SHORT).show()
                           }
                       }
               }
               else
               {
                   Log.w(TAG, "psd is not equal")
                   Toast.makeText(this, "psd not equal",
                       Toast.LENGTH_SHORT).show()
               }
           }
       }

        //取消創建帳號
        btn_create_cancel.setOnClickListener{
            cl_signUp.visibility = View.GONE
            cl_login.visibility = View.VISIBLE

        }
    }
}