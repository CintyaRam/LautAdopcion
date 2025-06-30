package cl.cintya.lautadopcion

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.cintya.lautadopcion.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            registerButton.setOnClickListener {
                val name = editTextName.text.toString().trim()
                val rut = editTextRut.text.toString().trim()
                val city = editTextCity.text.toString().trim()
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString().trim()
                val confirmPassword = editTextConfirmPassword.text.toString().trim()

                // Validaciones
                when {
                    name.isEmpty() || rut.isEmpty() || city.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                        Toast.makeText(this@RegisterActivity, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                    }
                    !isValidEmail(email) -> {
                        Toast.makeText(this@RegisterActivity, "Correo electrónico no válido.", Toast.LENGTH_SHORT).show()
                    }
                    password != confirmPassword -> {
                        Toast.makeText(this@RegisterActivity, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        registerButton.isEnabled = false
                        progressBar.visibility = android.view.View.VISIBLE

                        registerUser(email, password, name, rut, city)
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(email: String, password: String, name: String, rut: String, city: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.registerButton.isEnabled = true

                if (task.isSuccessful) {
                    // Si el registro es exitoso, obtenemos el userId
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        saveUserToDatabase(it, email, name, rut, city)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToDatabase(userId: String, email: String, name: String, rut: String, city: String) {
        val database = FirebaseDatabase.getInstance().reference

        val user = mapOf(
            "email" to email,
            "nombre" to name,
            "rut" to rut,
            "ciudad" to city
        )

        database.child("usuarios").child(userId).setValue(user)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                } else {
                    Toast.makeText(this, "Error al guardar los datos en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}