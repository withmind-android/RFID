package com.rfid.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rfid.adapter.item.PictureItem
import com.rfid.databinding.ItemPictureBinding
import com.rfid.util.BitmapConverter

//ViewHolder에 맞춰서 Adapter가 구현된다
class DetectPictureAdapter : RecyclerView.Adapter<DetectPictureAdapter.ViewHolder>() {
    private var pictures = mutableListOf<PictureItem>()
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
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

    fun setDialog(deleteId: Int, img: String, adapterPosition: Int, item: PictureItem, deleteImg: ImageView) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("삭제 하시겠습니까?")
        builder.setMessage("선택한 사진을 삭제합니다.")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("확인") { _, _ ->
            itemClickListener.onClickDelete(deleteId, img)
            pictures.removeAt(adapterPosition)
            notifyDataSetChanged()
            Toast.makeText(mContext, "선택한 사진을 삭제했습니다.", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("취소") { _, _ ->
            item.isVisibleDeleteBtn = true
            deleteImg.visibility = View.VISIBLE
            notifyDataSetChanged()
            Toast.makeText(mContext, "취소했습니다.", Toast.LENGTH_SHORT).show()
        }
        builder.show()
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
                            setDialog(pictures[adapterPosition].id!!,"0", adapterPosition, item, binding.btDelete)
                        } else {
                            setDialog(0, pictures[adapterPosition].img, adapterPosition, item, binding.btDelete)
                        }
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