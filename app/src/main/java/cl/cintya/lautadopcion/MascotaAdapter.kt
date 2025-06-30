package cl.cintya.lautadopcion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cl.cintya.lautadopcion.databinding.ItemPetBinding
import cl.cintya.lautadopcion.model.Mascota
import com.bumptech.glide.Glide

class MascotaAdapter : ListAdapter<Mascota, MascotaAdapter.MascotaViewHolder>(MascotaComparator()) {

    // Listener opcional para clics en items
    var onItemClickListener: ((Mascota) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val binding = ItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MascotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = getItem(position)
        holder.bind(mascota)

        // Clic en item (opcional)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(mascota)
        }
    }

    inner class MascotaViewHolder(private val binding: ItemPetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mascota: Mascota) {
            with(binding) {
                // Cargar imagen con Glide
                Glide.with(petImage.context)
                    .load(mascota.imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .into(petImage)

                // Asignar nombre y edad
                petName.text = mascota.name
                petAge.text = mascota.age
            }
        }
    }

    // Difusión para actualizar solo lo necesario
    class MascotaComparator : androidx.recyclerview.widget.DiffUtil.ItemCallback<Mascota>() {
        override fun areItemsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem == newItem
        }
    }
}