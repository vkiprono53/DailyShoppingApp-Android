package com.vkiprono.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var firebaseStorage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
    }

    fun signup(view: View) {

        val intent = Intent(applicationContext, SignupActivity::class.java)
        startActivity(intent)
    }

    fun signin(view: View) {
        Log.d("SIGNIN", "BEGINNING OF SIGN IN")

        val email = tvSigninEmail.text.toString()
        val password = etSigninPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        }

        firebaseAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("SIGNIN", "SUCCESSFULLY LOGGED IN")
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }

        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
    }
}
