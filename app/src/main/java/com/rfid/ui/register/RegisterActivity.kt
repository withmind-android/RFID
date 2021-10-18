package com.rfid.ui.register

import android.Manifest
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.rx3.TedPermission
import com.rfid.R
import com.rfid.adapter.DetectPictureAdapter
import com.rfid.adapter.RegisterAdapter
import com.rfid.adapter.item.PictureItem
import com.rfid.adapter.item.RegisterItem
import com.rfid.ui.base.BaseActivity
import com.rfid.databinding.ActivityRegisterBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    private lateinit var registerAdapter: RegisterAdapter
    private var registerList = mutableListOf<RegisterItem>()
    private lateinit var detectPictureAdapter: DetectPictureAdapter
    private val pictures = mutableListOf<PictureItem>()

    protected lateinit var currentPhotoPath: String
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBindView(R.layout.activity_register)

        registerList = intent.getSerializableExtra("list") as MutableList<RegisterItem>

        init()
    }

    fun init() {
        registerAdapter = RegisterAdapter()
        dataBinding.appBarRegister.register.rvRegister.layoutManager?.isItemPrefetchEnabled = false
        dataBinding.appBarRegister.register.rvRegister.setItemViewCacheSize(0)
        dataBinding.appBarRegister.register.rvRegister.adapter = registerAdapter
        registerAdapter.setList(registerList)

        detectPictureAdapter = DetectPictureAdapter()
        dataBinding.appBarRegister.register.rvPicture.layoutManager?.isItemPrefetchEnabled = false
        dataBinding.appBarRegister.register.rvPicture.setItemViewCacheSize(0)
        dataBinding.appBarRegister.register.rvPicture.adapter = detectPictureAdapter
        if (pictures.size == 0) {
            // 등록된 사진없음
            pictures.apply {
                add(PictureItem("", null, false))
            }
        } else {

        }
        detectPictureAdapter.setList(pictures)
        detectPictureAdapter.setItemClickListener(object : DetectPictureAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                settingPermission()
            }
        })

        dataBinding.appBarRegister.register.btSend.setOnClickListener {
            Log.e(TAG, "register 전송")
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media
                    .getBitmap(contentResolver, Uri.fromFile(file))
                pictures.add(0, PictureItem("", file, false))
                detectPictureAdapter.resetDeleteBtn()
            } else {
                val decode = ImageDecoder.createSource(
                    contentResolver,
                    Uri.fromFile(file)
                )
                val bitmap = ImageDecoder.decodeBitmap(decode)
                pictures.add(0, PictureItem("", file, false))
                detectPictureAdapter.resetDeleteBtn()
            }
        }
    }

    fun settingPermission() {
        TedPermission.create()
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .request()
            .subscribe({ result ->
                if (result.isGranted) {
                    startCapture()
                } else {
                    Toast.makeText(
                        this,
                        "카메라 권한 요청이 거부되었습니다.", Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun startCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.rfid.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}