package com.example.wearable

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wearable.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding : ActivityLoginBinding
    private val binding get() = _binding

    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        checkLogin()

        binding.btLogin.setOnClickListener {
            if(binding.etUserName.text!!.isNotEmpty() && binding.etNumber.text!!.isNotEmpty()){
                if(binding.etNumber.text!!.length < 10 || binding.etNumber.text!!.length > 10){
                    binding.etNumber.error = "Enter valid number"
                }
                else{
                editor.putString("name",binding.etUserName.text.toString())
                editor.putString("number",binding.etNumber.text.toString())
                editor.commit()

                Intent(this,MainActivity::class.java).let {
                    startActivity(it)
                    finish()
                    }
                }
            } else
            {
                Toast.makeText(this,"Enter all the details",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin(){
        if(sharedPreferences == null){
            sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        }

        var userName: String? = sharedPreferences.getString("name","")

        if(userName != null && userName != ""){
            val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkLogin()
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }
}