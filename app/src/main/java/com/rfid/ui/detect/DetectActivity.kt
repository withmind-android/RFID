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
    private lateinit var detectItem1: DetectItem1
    private lateinit var detectItem2: DetectItem2
    private lateinit var detectItem3: DetectItem3
    private lateinit var detectItem4: DetectItem4
    private lateinit var detectItem5: DetectItem5
    private lateinit var detectItem6: DetectItem6

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
        //  TODO 앞뒤로 뭐 4자리 짜른다는거
        //  var tag = tagList[i].tag.substring(4)
        //  tag = tag.substring(0, tag.length - 4)
        //  tagList[i].tag = tag
            when (id) {
                1 -> {
                    detectItem1 = DetectItem1(tagList[i], pagerAdapter)
                    detectItem1.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem1)
                }
                2 -> {
                    detectItem2 = DetectItem2(tagList[i], pagerAdapter)
                    detectItem2.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem2)
                }
                3 -> {
                    detectItem3 = DetectItem3(tagList[i], pagerAdapter)
                    detectItem3.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem3)
                }
                4 -> {
                    detectItem4 = DetectItem4(tagList[i], pagerAdapter)
                    detectItem4.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem4)
                }
                5 -> {
                    detectItem5 = DetectItem5(tagList[i], pagerAdapter)
                    detectItem5.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem5)
                }
                6 -> {
                    detectItem6 = DetectItem6(tagList[i], pagerAdapter)
                    detectItem6.setItemClickListener(object : TabLayoutListener {
                        override fun removeFragment(position: Int) {
                            tagList.removeAt(position)
                            setMediator()
                        }
                    })
                    pagerAdapter.addFragment(detectItem6)
                }
            }
        }
        // Adapter
        binding.appBarDetect.detect.vpDetect.adapter = pagerAdapter

        setMediator()

        binding.appBarDetect.detect.vpDetect.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                clipToPadding = false
            }
            adapter = pagerAdapter
        }
    }

    private fun setMediator() {
        TabLayoutMediator(
            binding.appBarDetect.detect.tlDetect,
            binding.appBarDetect.detect.vpDetect,
            false
        ) { tab, position ->
//            tab.text = "   ${position + 1}   "
            tab.text = "${position + 1}. ${tagList[position].type}"
        }.attach()
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