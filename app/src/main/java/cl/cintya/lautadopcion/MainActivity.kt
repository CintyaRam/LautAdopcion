package cl.cintya.lautadopcion

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.cintya.lautadopcion.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Si ya hay usuario logueado, redirigir
        if (auth.currentUser != null) {
            navigateToPrincipal()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            loginButton.setOnClickListener {
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString().trim()

                when {
                    email.isEmpty() || password.isEmpty() -> {
                        Toast.makeText(this@MainActivity, "Por favor, complete ambos campos.", Toast.LENGTH_SHORT).show()
                    }
                    !isValidEmail(email) -> {
                        Toast.makeText(this@MainActivity, "Correo electrónico no válido.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        loginButton.isEnabled = false
                        progressBar.visibility = android.view.View.VISIBLE

                        loginUser(email, password)
                    }
                }
            }

            registerButton.setOnClickListener {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.loginButton.isEnabled = true

                if (task.isSuccessful) {
                    navigateToPrincipal()
                } else {
                    Toast.makeText(
                        this,
                        "Error al iniciar sesión: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun navigateToPrincipal() {
        val intent = Intent(this, PrincipalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}