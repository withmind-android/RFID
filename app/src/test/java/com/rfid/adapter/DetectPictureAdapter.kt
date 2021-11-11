package com.rfid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rfid.adapter.item.PictureItem
import com.rfid.databinding.ItemPictureBinding
import com.rfid.util.BitmapConverter

//ViewHolder에 맞춰서 Adapter가 구현된다
class DetectPictureAdapter : RecyclerView.Adapter<DetectPictureAdapter.ViewHolder>() {
    private var pictures = mutableListOf<PictureItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPictureBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = pictures.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pictures[position])
    }

    fun setList(pictures: MutableList<PictureItem>) {
        this.pictures = pictures
        notifyDataSetChanged()
    }

    fun resetDeleteBtn() {
        for (i in pictures.indices) {
            pictures[i].isVisibleDeleteBtn = false
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureItem) {
            if (!item.isVisibleDeleteBtn) {
                binding.btDelete.visibility = View.GONE
            }
            if (item.img == "") {
                Glide.with(itemView)
                    .load(item.file)
                    .into(binding.ivPicture)
            } else {
                val bitmap = BitmapConverter.stringToBitmap(item.img)
                Glide.with(itemView)
                    .asBitmap()
                    .load(bitmap)
                    .into(binding.ivPicture)
            }

            binding.ivPicture.setOnClickListener {
                if (pictures.size == adapterPosition + 1) {
                    itemClickListener.onClick(it, adapterPosition)
                } else {
                    for (i in pictures.indices) {
                        pictures[i].isVisibleDeleteBtn = false
                    }
                    if (binding.btDelete.isVisible) {
                        if (pictures[adapterPosition].id != null) {
                            itemClickListener.onClickDelete(pictures[adapterPosition].id!!, "0")
                        } else {
                            itemClickListener.onClickDelete(0, pictures[adapterPosition].img)
                        }
                        pictures.removeAt(adapterPosition)
                    } else {
                        item.isVisibleDeleteBtn = true
                        binding.btDelete.visibility = View.VISIBLE
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
        fun onClickDelete(deleteId: Int, img: String)
    }

    //를릭 리스너
    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}