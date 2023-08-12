package thuya.firebasechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import thuya.firebasechat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        // check if user login then navigate to user screen
        if(firebaseUser != null){
            val intent = Intent(this@LoginActivity, UsersActivity::class.java)
            startActivity(intent)
        }

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
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(this@LoginActivity, UsersActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Email or Password invalid!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}