package com.example.instaclone

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instaclone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null){
            gotoPostsActivity()
        }


        binding.loginBtn.setOnClickListener {
            binding.loginBtn.isEnabled = false
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if(email.isEmpty() || password.isEmpty() ){
                Toast.makeText(this, "e-mail or password can't be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // firebase authentication check

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                binding.loginBtn.isEnabled = true
                if (task.isSuccessful){
                    Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
                    gotoPostsActivity()
                }
                else{
                    Log.i(TAG,"signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed, try again", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun gotoPostsActivity() {
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
        //Toast.makeText(this, "going to post activity", Toast.LENGTH_LONG).show()

    }
}