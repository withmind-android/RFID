package com.rfid.ui

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.jakewharton.rxbinding4.view.clicks
import com.rfid.R
import com.rfid.databinding.ActivitySettingBinding
import com.rfid.ui.base.BaseActivityK
import com.rfid.util.Constants
import java.util.concurrent.TimeUnit

class SettingActivity : BaseActivityK<ActivitySettingBinding>(R.layout.activity_setting) {
    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.version.text = " v${getInfo()}"

        compositeDisposable.add(
            binding.logout
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    openDialog("로그아웃 하시겠습니까?")
                }, { it.printStackTrace() })
        )

        compositeDisposable.add(
            binding.openSourceLicense
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    openLicense()
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLicense() {
        val intent = Intent()
        intent.setClass(applicationContext, LicenseActivity::class.java)
        startActivity(intent)
    }
}