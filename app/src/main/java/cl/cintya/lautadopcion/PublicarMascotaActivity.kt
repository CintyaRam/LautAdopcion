package cl.cintya.lautadopcion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cl.cintya.lautadopcion.databinding.ActivityPublicarMascotaBinding
import cl.cintya.lautadopcion.model.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class PublicarMascotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPublicarMascotaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null
    private val tag = "PublicarMascota"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublicarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        setupImagePicker()
        setupPostButton()
    }

    private fun setupImagePicker() {
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.ivPetImage.setImageURI(uri)
            }
        }

        binding.btnAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun setupPostButton() {
        binding.btnPostPet.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val age = binding.inputAge.text.toString().trim()
            val species = binding.inputSpecies.text.toString().trim()
            val breed = binding.inputBreed.text.toString().trim()
            val gender = binding.inputGender.text.toString().trim()
            val description = binding.inputDescription.text.toString().trim()

            // Validaciones
            when {
                name.isEmpty() || age.isEmpty() || species.isEmpty() || breed.isEmpty() || gender.isEmpty() || description.isEmpty() -> {
                    Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                }
                !age.isNumeric() -> {
                    Toast.makeText(this, "La edad debe ser un número válido.", Toast.LENGTH_SHORT).show()
                }
                imageUri == null -> {
                    Toast.makeText(this, "Por favor, seleccione una imagen.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Deshabilitar botón y mostrar progreso
                    binding.btnPostPet.isEnabled = false
                    binding.progressBar.visibility = android.view.View.VISIBLE

                    uploadPetImageAndSave(name, age, species, breed, gender, description)
                }
            }
        }
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }

    private fun uploadPetImageAndSave(
        name: String,
        age: String,
        species: String,
        breed: String,
        gender: String,
        description: String
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "No se pudo obtener el usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        val imageRef = storage.reference.child("pet_images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(imageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            binding.progressBar.visibility = android.view.View.GONE
            binding.btnPostPet.isEnabled = true

            if (task.isSuccessful) {
                val imageUrl = task.result.toString()

                val mascota = Mascota(
                    name = name,
                    age = age,
                    species = species,
                    breed = breed,
                    gender = gender,
                    description = description,
                    imageUrl = imageUrl
                )

                saveMascotaToDatabase(userId, mascota)
            } else {
                Toast.makeText(this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveMascotaToDatabase(userId: String, mascota: Mascota) {
        val databaseRef = database.reference
        val mascotasRef = databaseRef.child("mascotas")
        val userMascotasRef = databaseRef.child("usuarios").child(userId).child("mascotas")

        val mascotaId = mascotasRef.push().key ?: UUID.randomUUID().toString()

        mascotasRef.child(mascotaId).setValue(mascota)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userMascotasRef.child(mascotaId).setValue(mascota)
                        .addOnCompleteListener { taskUser ->
                            if (taskUser.isSuccessful) {
                                Toast.makeText(this, "Mascota publicada correctamente.", Toast.LENGTH_SHORT).show()
                                navigateToPrincipal()
                            } else {
                                Toast.makeText(this, "Error al guardar datos del usuario.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Error al guardar en mascotas.", Toast.LENGTH_SHORT).show()
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