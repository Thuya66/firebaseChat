package thuya.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import thuya.firebasechat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        // Signin Button Click
        binding.btnSignin.setOnClickListener(){
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext, "Email is required!", Toast.LENGTH_SHORT).show()
            }else if(TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext, "Password is required!", Toast.LENGTH_SHORT).show()
            }else{
                loginUser(email, password)
            }
        }

        // Sign Up Button Click
        binding.btnSignUp.setOnClickListener(){
            val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext, "Email or Password invalid!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}