package com.rfid.ui

import android.content.Intent
import android.view.MenuItem
import com.rfid.ui.base.BaseActivityK
import com.rfid.databinding.ActivitySelectProjectBinding
import com.rfid.ui.login.LoginActivity

class SelectProjectActivity : BaseActivityK<ActivitySelectProjectBinding>(
    { ActivitySelectProjectBinding.inflate(it) }) {

    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btSelectFinish.setOnClickListener {
            val intent = Intent()
            intent.setClass(applicationContext, RFIDControlActivity::class.java)
            startActivityForResult(intent, REQUEST_START_MAIN)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> {
                openDialog("로그아웃 하시겠습니까?")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_START_MAIN) {
            if (resultCode == RESULT_OK) {
                // 로그아웃
                val intent = Intent()
                intent.setClass(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // onBackPressed
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        openDialog("로그아웃 하시겠습니까?")
    }

    companion object {
        private const val TAG = "SelectProjectActivity"
        private const val REQUEST_START_MAIN = 0x101
    }
}