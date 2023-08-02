package thuya.firebasechat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import thuya.firebasechat.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        // Sign Up Button Click
        binding.btnSignUp.setOnClickListener(){
            val userName = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (TextUtils.isEmpty(userName)){
                Toast.makeText(applicationContext, "Username is required!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext, "Email is required!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext, "Password is required!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(confirmPassword)){
                Toast.makeText(applicationContext, "Confirm Password is required!", Toast.LENGTH_SHORT).show()
            }else if (password != confirmPassword){
                Toast.makeText(applicationContext, "Password doesn't Match!", Toast.LENGTH_SHORT).show()
            }else{
                // Register User
                registerUser(userName,email,password)
            }
        }

        binding.btnLogin.setOnClickListener(){
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(userName:String,email:String,password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(this){
                val user:FirebaseUser? = auth.currentUser
                val userId:String = user!!.uid

                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String,String> = HashMap()
                hashMap["userId"] = userId
                hashMap["userName"] = userName
                hashMap["profileImage"] = ""
                databaseReference.setValue(hashMap)

                // Show Registration Success
                Toast.makeText(applicationContext, "Register Success!", Toast.LENGTH_SHORT).show()

                // Move to Main Activity
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{
                Toast.makeText(applicationContext, "${it.message.toString()}", Toast.LENGTH_SHORT).show()
            }
    }
}