package com.rfid.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerFragmentStateAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private var fragments: ArrayList<Fragment> = ArrayList()
    private val pageIds = fragments.map { it.hashCode().toLong() }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun getItemId(position: Int): Long {
        return fragments[position].hashCode().toLong() // make sure notifyDataSetChanged() works
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getPosition(fragment: Fragment): Int {
        return fragments.indexOf(fragment)
    }

    fun getFragmentList(): List<Long> {
        return fragments.map { it.hashCode().toLong() }
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)

        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment(position: Int) {
        fragments.removeAt(position)
        notifyItemRangeChanged(position, fragments.size)
        notifyDataSetChanged()
    }

}