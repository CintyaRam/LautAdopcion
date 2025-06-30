package cl.cintya.lautadopcion

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.cintya.lautadopcion.model.Mascota
import cl.cintya.lautadopcion.adapter.MascotaAdapter
import cl.cintya.lautadopcion.databinding.ActivityAdoptarMascotaBinding
import com.google.firebase.database.*

class AdoptarMascotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdoptarMascotaBinding
    private lateinit var adapter: MascotaAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar layout con ViewBinding
        binding = ActivityAdoptarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar RecyclerView
        binding.recyclerViewAdoptPet.layoutManager = LinearLayoutManager(this)
        adapter = MascotaAdapter()
        binding.recyclerViewAdoptPet.adapter = adapter

        setupEmptyState()
        loadAllMascotas()
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

    private fun loadAllMascotas() {
        database = FirebaseDatabase.getInstance().reference.child("mascotas")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mascotasList = snapshot.children.mapNotNull { child ->
                    child.getValue(Mascota::class.java)
                }
                adapter.submitList(mascotasList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdoptarMascotaActivity, "Error al cargar mascotas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}