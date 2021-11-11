package com.rfid.ui.shipment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.rfid.R
import com.rfid.adapter.DetectPictureAdapter
import com.rfid.adapter.RegisterAdapter
import com.rfid.adapter.item.PictureItem
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.shipment.ShipmentDataSourceImpl
import com.rfid.data.remote.tag.Tag
import com.rfid.data.repository.shipment.ShipmentRepositoryImpl
import com.rfid.databinding.ActivityShipmentBinding
import com.rfid.ui.base.BaseActivityK
import com.rfid.util.BitmapConverter
import java.io.File
import java.util.*

class ShipmentActivity : BaseActivityK<ActivityShipmentBinding>(R.layout.activity_shipment) {
    private val viewModel: ShipmentViewModel by lazy {
        ShipmentViewModel(
            repository = ShipmentRepositoryImpl(
                userDataSource = UserDataSourceImpl(
                    userDao = DatabaseClient.userDao()
                ),
                loginDataSource = LoginDataSourceImpl(
                    loginApi = RetrofitClient.loginAPI
                ),
                shipmentDataSource = ShipmentDataSourceImpl(
                    shipmentApi = RetrofitClient.shipmentAPI
                )
            )
        )
    }

    private lateinit var registerAdapter: RegisterAdapter
    private lateinit var detectPictureAdapter: DetectPictureAdapter
    private var tags = mutableListOf<Tag>()
    private val pictures = mutableListOf<PictureItem>()

    private var tagList: MutableList<String> = mutableListOf()
    private var imgList: MutableList<String> = mutableListOf()

    override fun init() {
        setSupportActionBar(binding.appBarRegister.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appBarRegister.tbTitle.text = intent.getStringExtra("title")

        setAdapter()
        applyBinding()
        applyViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {
//                val bitmap = MediaStore.Images.Media
//                    .getBitmap(requireContext().contentResolver, Uri.fromFile(file))
//                pictures.add(0, PictureItem(null, "", file, false))
//                detectPictureAdapter.resetDeleteBtn()
            } else {
                val decode = ImageDecoder.createSource(
                    contentResolver,
                    Uri.fromFile(file)
                )
                var bitmap = ImageDecoder.decodeBitmap(decode)
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                val bytes = BitmapConverter.bitmapToByteArray(bitmap)
                val sImage = Base64.encodeToString(bytes, Base64.DEFAULT)

                pictures.add(0, PictureItem(null, sImage, file, false))
                imgList.add(sImage.toString())
                detectPictureAdapter.resetDeleteBtn()
            }
        }
    }

    private fun setAdapter() {
        tags = intent.getSerializableExtra("list") as MutableList<Tag>
        for (i in tags.indices) {
            tagList.add(tags[i].tag)
        }

        registerAdapter = RegisterAdapter()
        detectPictureAdapter = DetectPictureAdapter()

        binding.appBarRegister.register.apply {
            rvRegister.layoutManager?.isItemPrefetchEnabled = false
            rvRegister.setItemViewCacheSize(0)
            rvRegister.adapter = registerAdapter

            rvPicture.layoutManager?.isItemPrefetchEnabled = false
            rvPicture.setItemViewCacheSize(0)
            rvPicture.adapter = detectPictureAdapter
        }

        registerAdapter.setList(tags)
        pictures.add(PictureItem(null, "", null, false))
        detectPictureAdapter.setList(pictures)

        detectPictureAdapter.setItemClickListener(object : DetectPictureAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                settingPermission()
            }

            override fun onClickDelete(deleteId: Int, img: String) {
                imgList.remove(img)
            }
        })
    }

    private fun applyViewModel() {
        viewModel.apply {
            response.observe(this@ShipmentActivity, {
                if (it.result == 1) {
                    Log.e(TAG, "shipment 전송 success")
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Log.e(TAG, "error: $it")
                }
            })
        }
    }

    private fun applyBinding() {
        binding.appBarRegister.register.apply {
            btSend.setOnClickListener {
                viewModel.postShipment(Shipment(tagList, imgList))
                Log.e(TAG, "register 전송")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        private const val TAG = "RegisterActivity"
    }
}