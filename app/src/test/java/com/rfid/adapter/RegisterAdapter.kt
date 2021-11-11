package com.rfid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rfid.adapter.item.RegisterItem
import com.rfid.data.remote.tag.Tag
import com.rfid.databinding.ItemShipmentBinding

//ViewHolder에 맞춰서 Adapter가 구현된다
class RegisterAdapter : RecyclerView.Adapter<RegisterAdapter.ViewHolder>() {
    private var tags = mutableListOf<Tag>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShipmentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    fun setList(tags: MutableList<Tag>) {
        this.tags = tags
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemShipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: Tag) {
            binding.apply {
//                var id = tag.tag.substring(4)
//                id = id.substring(0, id.length - 4)

                etId.setText(tag.tag)
                etNum.setText(tag.serial)
                etKind.setText(tag.type)
            }
        }
    }
}