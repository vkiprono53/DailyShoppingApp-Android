package com.vkiprono.shoppingapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vkiprono.shoppingapp.models.Data
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.shoppingitems.view.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.view.menu.MenuView
import java.time.LocalDate


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

        dataFromDatabase()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.etHomeLogout){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext,SigninActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.O)
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


            val date = LocalDate.now()
            Log.d("SAVING TO DB", "DATE IS============>$date")

            val formatter = DateTimeFormatter.ofPattern("dd, MM yyyy")
            val text = date.format(formatter)
            val parsedDate = LocalDate.parse(text, formatter)



            Log.d("SAVING TO DB", "parsedDate DATE IS============>$parsedDate")

            val data = Data(inputType, inputAmount, inputNote, parsedDate.toString(), userId!!)

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

    private fun dataFromDatabase(){
        val ref = FirebaseDatabase.getInstance().getReference("/shopping")
        val adapter = GroupAdapter<GroupieViewHolder>()

      //  ref.addListenerForSingleValueEvent(object :ValueEventListener{
        ref.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                var totalAmount = 0
                p0.children.forEach {
                    Log.d("Shopping", "===>${it.toString()}")
                    val data = it.getValue(Data::class.java)
                    adapter.add(ShoppingItems(data!!))
                    totalAmount += data.amount

                    homeTotalAmount.text = totalAmount.toString()
                }
//                homeRecyclerView.hasFixedSize()
                homeRecyclerView.adapter = adapter



            }


            override fun onCancelled(p0: DatabaseError) {
                Log.w("DataFromDB:", "Database error:::${p0.toException()}")
            }

        })
    }


    class ShoppingItems(val data: Data):Item<GroupieViewHolder>(){
        override fun getLayout(): Int {

            return R.layout.shoppingitems
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.tvdbType.text = data.type
            viewHolder.itemView.tvdbDate.text = data.date.format("")
            viewHolder.itemView.tvdbNote.text = data.note
            viewHolder.itemView.tvdbAmount.text= data.amount.toString()
        }
    }

    }


