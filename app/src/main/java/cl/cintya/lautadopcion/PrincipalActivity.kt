package cl.cintya.lautadopcion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cl.cintya.lautadopcion.databinding.ActivityPrincipalBinding


class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout con ViewBinding
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            btnPublishPet.setOnClickListener {
                val intent = Intent(this@PrincipalActivity, PublicarMascotaActivity::class.java)
                startActivity(intent)
            }

            btnAdoptPet.setOnClickListener {
                val intent = Intent(this@PrincipalActivity, AdoptarMascotaActivity::class.java)
                startActivity(intent)
            }

            btnViewPosts.setOnClickListener {
                val intent = Intent(this@PrincipalActivity, MisPublicacionesActivity::class.java)
                startActivity(intent)
            }
        }
    }
}