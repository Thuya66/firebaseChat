package thuya.firebasechat.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import thuya.firebasechat.databinding.ActivityProfileBinding
import thuya.firebasechat.model.User
import java.io.IOException
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUserReference: DatabaseReference
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var storageReference:StorageReference

    private var filePath:Uri? = null
    private  final val PICK_IMAGE_REQUEST:Int = 2020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseReference = Firebase.database.reference
        storageReference = FirebaseStorage.getInstance().reference
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUserReference = databaseReference.child("users").child(firebaseUser.uid)

        getUserInfo()

        binding.imgBack.setOnClickListener(){onBackPressed()}
        binding.btnLogout.setOnClickListener(){userLogout()}
        binding.userImage.setOnClickListener(){chooseImage()}
        binding.btnSave.setOnClickListener(){
            uploadImage()
            binding.progressBar.visibility = View.VISIBLE
        }

    }

    private fun getUserInfo(){
        currentUserReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.userName.setText(user!!.userName.toString())
                if(user!!.userImage != ""){
                    Glide.with(this@ProfileActivity).load(user.userImage).into(binding.userImage)
                }
            }
        })
    }

    private fun userLogout(){
        // TODO : sign out user
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun chooseImage(){
        val intent:Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode != null){
            filePath = data!!.data
            try {
                var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                binding.userImage.setImageBitmap(bitmap)
                binding.btnSave.visibility = View.VISIBLE
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            var ref:StorageReference = storageReference.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    binding.btnSave.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("userName",binding.userName.text.toString())
                    hashMap.put("userImage",filePath.toString())

                    currentUserReference.updateChildren(hashMap as Map<String, Any>)

                    Toast.makeText(applicationContext, "Upload Success!",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener(
                    OnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Upload Failed!",Toast.LENGTH_SHORT).show()
                    }
                )

        }
    }
}