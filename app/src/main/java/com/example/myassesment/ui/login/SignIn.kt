package com.example.myassesment.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.myassesment.MainActivity
import com.example.myassesment.R
import com.example.myassesment.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var binding: ActivitySignInBinding
    lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.btnLogin.setOnClickListener {
            val email = binding.edEmailLogin.text.toString()
            val passwprd = binding.edPasswordLogin.text.toString()

            if (email.isEmpty()){
                binding.edEmailLogin.error = "Email Can't Be Empty"
                binding.edEmailLogin.requestFocus()
                return@setOnClickListener
            }

            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edEmailLogin.error = "Invalid e-mail"
                binding.edEmailLogin.requestFocus()
                return@setOnClickListener
            }

            else if (passwprd.isEmpty()){
                binding.edPasswordLogin.error = "Password Can't Be Empty"
                binding.edEmailLogin.requestFocus()
                return@setOnClickListener
            }else if (passwprd.length < 6){
                binding.edPasswordLogin.error = "Password must be at least 6 characters"
                binding.edEmailLogin.requestFocus()
                return@setOnClickListener
            }
            else{
                LoginFirebase(email,passwprd)
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
            }
        }

    }

    private fun LoginFirebase(email: String, passwprd: String) {
        auth.signInWithEmailAndPassword(email,passwprd).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this, "Welcome $email",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "${it.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }
}