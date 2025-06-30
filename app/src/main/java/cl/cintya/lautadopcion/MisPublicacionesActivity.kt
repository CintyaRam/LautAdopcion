package cl.cintya.lautadopcion

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.cintya.lautadopcion.adapter.MascotaAdapter
import cl.cintya.lautadopcion.databinding.ActivityMisPublicacionesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import cl.cintya.lautadopcion.model.Mascota
import android.widget.Toast

class MisPublicacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisPublicacionesBinding
    private lateinit var adapter: MascotaAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisPublicacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().reference
        val userId = currentUser.uid

        // Configurar RecyclerView
        binding.recyclerViewMisPublicaciones.layoutManager = LinearLayoutManager(this)
        adapter = MascotaAdapter()
        binding.recyclerViewMisPublicaciones.adapter = adapter

        setupEmptyState()

        // Cargar mascotas del usuario
        loadUserMascotas(userId)
    }

    private fun setupEmptyState() {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                toggleEmptyView(adapter.itemCount == 0)
            }
        })
    }

    private fun toggleEmptyView(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun loadUserMascotas(userId: String) {
        val mascotasRef = database.child("usuarios").child(userId).child("mascotas")

        mascotasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mascotasList = mutableListOf<Mascota>()
                for (dataSnapshot in snapshot.children) {
                    dataSnapshot.getValue(Mascota::class.java)?.let {
                        mascotasList.add(it)
                    }
                }
                adapter.submitList(mascotasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MisPublicacionesActivity, "Error al cargar publicaciones: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}