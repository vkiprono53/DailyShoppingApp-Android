package com.vkiprono.shoppingapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vkiprono.shoppingapp.models.User
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseStorage: FirebaseStorage? = null

    var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

    }

    fun signin(view: View) {
        val intent = Intent(applicationContext, SigninActivity::class.java)
        startActivity(intent)
    }

    fun signup(view: View) {

        val username = etSignupName.text.toString()
        val email = tvSignupEmail.text.toString()
        val password = etSignupPassword.text.toString()

        if (username.isEmpty()) {
            etSignupName.error = "Name required"
        }

        if (email.isEmpty()) {
            tvSignupEmail.error = "Email required"
            return
        }
        if (password.isEmpty()) {
            etSignupPassword.error = "Password required"
            return
        }


        firebaseAuth!!.createUserWithEmailAndPassword(email, password)

            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    uploadImage()

                    Log.d("CREATING", "SUCCESSFULLY CREATED USER")

                    Toast.makeText(
                        applicationContext,
                        "Account Successfully created..Log in ",
                        Toast.LENGTH_SHORT
                    ).show()


                    val intent = Intent(applicationContext, SigninActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()

            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun selectImage(view: View) {
        Log.d("SIGNUP", "BEGINNING TO SELECT IMAGE")

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            Log.d("SIGNUP", "SELECTING IMAGE")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 2)
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                imgUri = data.data
                Log.d("SIGNUP", "IMAGE URI IS:::$imgUri")

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imgUri)
                img_signup.setImageBitmap(bitmap)

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImage() {
        Log.d("SIGNUP", "BEGINNING TO UPLOAD IMAGE")
        val fileName = UUID.randomUUID().toString()
        val ref = firebaseStorage!!.getReference("/images/$fileName")

        ref.putFile(imgUri!!).addOnCompleteListener { task ->
            Log.d("SIGNUP", "SUCCESSFULLY UPLOADED THE IMAGE")

            ref.downloadUrl.addOnSuccessListener {
                uploadToFirebaseDatabase(it.toString())
                Log.d("SIGNUP", "DOWNLOAD URI IS:::$it")
            }
            Toast.makeText(
                applicationContext,
                "Image uploaded successfully",
                Toast.LENGTH_SHORT
            ).show()

        }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadToFirebaseDatabase(imgUrl: String) {
        Log.d("SIGNUP", "BEGINING TO UPLOADING TO DATABASE")

        val uuid = UUID.randomUUID().toString()
        val userName = etSignupName.text.toString()
        val userEmail = tvSignupEmail.text.toString()

        val user = User(userName, userEmail, imgUrl)

        val databaseRef = firebaseDatabase!!.getReference("users/$uuid")

        databaseRef.setValue(user).addOnSuccessListener {
            Log.d("SIGNUP", "SUCCESSFULLY UPLOADED THE RECORDS TO THE DB")
        }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }


    }
}
