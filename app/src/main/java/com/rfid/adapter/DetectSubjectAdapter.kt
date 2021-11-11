package com.rfid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.itemSelections
import com.jakewharton.rxbinding4.widget.textChanges
import com.rfid.R
import com.rfid.adapter.item.SubjectItem
import com.rfid.databinding.ItemSubjectBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable

//ViewHolder에 맞춰서 Adapter가 구현된다
class DetectSubjectAdapter : RecyclerView.Adapter<DetectSubjectAdapter.ViewHolder>() {
    private val compositeDisposable = CompositeDisposable()
    private var subjects = mutableListOf<SubjectItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSubjectBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = subjects.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    fun setList(subjects: MutableList<SubjectItem>) {
        this.subjects = subjects
        notifyDataSetChanged()
    }

    fun getList(): MutableList<SubjectItem> {
        notifyDataSetChanged()
        return subjects
    }

    inner class ViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubjectItem) {
            val subjectList = binding.root.resources.getStringArray(R.array.spinner_detect)
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_dropdown_item,
                subjectList
            )

            binding.apply {
                etSubjectTitle.setText(item.field)
                etSubjectValue.setText(item.value.toString())
                etSubjectStandard.setText(item.standard.toString())
                spSubject.adapter = adapter
                if (item.unit == "EA") {
                    spSubject.setSelection(1)
                } else {
                    spSubject.setSelection(0)
                }

                compositeDisposable.add(
                    etSubjectTitle
                        .textChanges()
                        .subscribe({
                            item.field = it.toString()
                        }, { it.printStackTrace() })
                )
                compositeDisposable.add(
                    etSubjectValue
                        .textChanges()
                        .subscribe({
                            if (it.isNullOrBlank()) {
                                item.value = 0
                            } else {
                                item.value = it.toString().toInt()
                            }
                        }, { it.printStackTrace() })
                )
                compositeDisposable.add(
                    etSubjectStandard
                        .textChanges()
                        .subscribe({
                            if (it.isNullOrBlank()) {
                                item.standard = 0
                            } else {
                                item.standard = it.toString().toInt()
                            }
                        }, { it.printStackTrace() })
                )
                compositeDisposable.add(
                    spSubject
                        .itemSelections()
                        .subscribe({
                            if (it == 0) {
                                item.unit = "mm"
                            } else {
                                item.unit = "EA"
                            }
                        }, { it.printStackTrace() })
                )
            }
        }
    }
}