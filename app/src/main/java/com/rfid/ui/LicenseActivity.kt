package com.rfid.ui

import android.util.Log
import android.view.MenuItem
import com.rfid.R
import com.rfid.databinding.ActivityLicenseBinding
import com.rfid.ui.base.BaseActivityK

class LicenseActivity : BaseActivityK<ActivityLicenseBinding>(R.layout.activity_license) {
    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}