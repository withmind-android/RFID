package com.rfid.ui.detect

import android.app.Activity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.rfid.R
import com.rfid.adapter.PagerFragmentStateAdapter
import com.rfid.data.remote.tag.Tag
import com.rfid.databinding.ActivityDetectBinding
import com.rfid.ui.base.BaseActivityK
import com.rfid.ui.detect.item.*
import java.util.*

class DetectActivity : BaseActivityK<ActivityDetectBinding>(R.layout.activity_detect) {
    private var tagList: MutableList<Tag> = mutableListOf()
    private var id: Int? = null
    private var title: String? = null

    private lateinit var mOptionMenu: Menu

    private val pagerAdapter = PagerFragmentStateAdapter(this)

    override fun init() {
        id = intent.getIntExtra("id", 1)
        title = intent.getStringExtra("title")
        tagList = intent.getParcelableArrayListExtra("list")!!

        setSupportActionBar(binding.appBarDetect.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appBarDetect.tbTitle.text = title

        for (i in tagList.indices) {
            //TODO 앞뒤로 뭐 4자리 짜른다는거
//            var tag = tagList[i].tag.substring(4)
//            tag = tag.substring(0, tag.length - 4)
//            tagList[i].tag = tag
            when (id) {
                1 -> {
                    pagerAdapter.addFragment(DetectItem1(tagList[i], pagerAdapter))
                }
                2 -> {
                    pagerAdapter.addFragment(DetectItem2(tagList[i], pagerAdapter))
                }
                3 -> {
                    pagerAdapter.addFragment(DetectItem3(tagList[i], pagerAdapter))
                }
                4 -> {
                    pagerAdapter.addFragment(DetectItem4(tagList[i], pagerAdapter))
                }
                5 -> {
                    pagerAdapter.addFragment(DetectItem5(tagList[i], pagerAdapter))
                }
                6 -> {
                    pagerAdapter.addFragment(DetectItem6(tagList[i], pagerAdapter))
                }
            }
        }
        // Adapter
        binding.appBarDetect.detect.vpDetect.adapter = pagerAdapter

        TabLayoutMediator(
            binding.appBarDetect.detect.tlDetect,
            binding.appBarDetect.detect.vpDetect
        ) { tab, position ->
            tab.text = "   ${position + 1}   "
        }.attach()

        binding.appBarDetect.detect.vpDetect.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                clipToPadding = false
            }
            adapter = pagerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mOptionMenu = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.detect_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }
//        else if (item.itemId == R.id.action_plus_rfid) {
//            Log.e(TAG, "onOptionsItemSelected: action_plus_rfid")
//        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "DetectActivity"
    }
}