package thuya.firebasechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import thuya.firebasechat.adapter.UserAdapter
import thuya.firebasechat.databinding.ActivityUsersBinding
import thuya.firebasechat.model.User

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser : FirebaseUser
    val userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseReference = Firebase.database.reference

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this,
            RecyclerView.VERTICAL,false)

        binding.imgBack.setOnClickListener(){
            onBackPressed()
        }

        binding.imgProfile.setOnClickListener(){
            onProfilePressed()
        }

        getUsersList()
    }

    fun getUsersList(){
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val user_ref = databaseReference.child("users")

        user_ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                userList.clear()

                for (dataSnapShot: DataSnapshot in dataSnapShot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (user!!.userId == firebaseUser.uid) {
                        if(user!!.userImage != ""){
                            Glide.with(this@UsersActivity).load(user.userImage).into(binding.imgProfile)
                        }
                    }else{
                        userList.add(user)
                    }
                }
                val userAdapter = UserAdapter(this@UsersActivity, userList)
                binding.userRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"${error.message}",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun onProfilePressed(){
        val intent = Intent(this@UsersActivity,
            ProfileActivity::class.java)
        startActivity(intent)
    }
}