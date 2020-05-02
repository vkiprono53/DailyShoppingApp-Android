package com.vkiprono.shoppingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vkiprono.shoppingapp.models.Data
import kotlinx.android.synthetic.main.input_data_dialog.*
import kotlinx.android.synthetic.main.input_dialog_text2.*
import java.util.*
import kotlin.math.log

class HomeActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null

    private var databaseRef:DatabaseReference?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        databaseRef = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    fun addShopping(view: View) {

        Log.d("ADD SHOPPING", "BEGINNING=====")

        val alertDialogView = LayoutInflater.from(this).inflate(R.layout.input_dialog_text2, null)

        val builder = AlertDialog.Builder(this)

        builder.setView(alertDialogView)

        val alertDialog = builder.create()


        alertDialog.show()

        val button = alertDialogView.findViewById<Button>(R.id.btnSave)

        button.setOnClickListener {
            Log.d("BUTTON", "USING LAMBDA-----BEGINNING OF CLICK BUTTON")


            val type = alertDialogView.findViewById<EditText>(R.id.etInputType)

            val amount = alertDialogView.findViewById<EditText>(R.id.etInputAmount)

            val note = alertDialogView.findViewById<EditText>(R.id.etInputNote)

            val inputType: String = type!!.text.toString()

            val inputAmount: Int = amount!!.text.toString().toInt()

            val inputNote: String = note!!.text.toString()

            Log.d("Type", "Type is======>$inputType")
            Log.d("Note", "Note is======>$inputNote")
            Log.d("Amount", "Amount is======>$inputAmount")

            if (inputType.isEmpty()) {
                type.error = "Required field"
                return@setOnClickListener

            }

            if (inputAmount == null) {
                amount.error = "Required field"
                return@setOnClickListener

            }

            if (inputNote.isEmpty()) {
                note.error = "Required field"
                return@setOnClickListener

            }

            //Saving to the database
            Log.d("SAVING TO DB", "BEGINNING TO SAVE TO THE DATABASE")

            val uuid = UUID.randomUUID().toString()
            val userId = mAuth!!.uid

            val data = Data(inputType, inputAmount, inputNote, Date().toString(), userId!!)

            val firebaseRef = firebaseDatabase!!.getReference("shopping/$uuid")


            firebaseRef.setValue(data).addOnSuccessListener {
                Log.d("SAVING TO DB", "=======Successfully saved to the database")
                Toast.makeText(
                    applicationContext,
                    "Successfully saved the shopping",
                    Toast.LENGTH_SHORT
                ).show()
                alertDialog.dismiss()
            }
                .addOnFailureListener { exception ->
                    Log.d("SAVING TO DB", "======Failed to save to the database")

                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnFailureListener
                }
            Log.d("SAVING TO DB", "ENDING TO SAVE TO THE DATABASE")

        }

    }

}
