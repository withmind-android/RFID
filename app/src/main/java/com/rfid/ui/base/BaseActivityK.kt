package com.rfid.ui.base

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gun0912.tedpermission.rx3.TedPermission
import com.rfid.R
import com.rfid.RFIDApplication
import com.rfid.ui.login.LoginActivity
import com.rfid.util.Constants
import com.rfid.util.SharedPreferencesPackage
import com.rfid.util.SharedPreferencesPackage.setAutoLogin
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivityK<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {
    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected val compositeDisposable = CompositeDisposable()

    protected lateinit var currentPhotoPath: String
    protected val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutId)
        setContentView(binding.root)
        init()
    }

    abstract fun init()

    open fun openDialog(text: String) {
        val alert = AlertDialog.Builder(this)
        alert.setPositiveButton(R.string.ok) { dialog: DialogInterface, _: Int ->
            setAutoLogin("", "", false)
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra(Constants.LOGOUT, true)
            intent.setClass(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        alert.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alert.setMessage("RFID v ${getInfo()}\n$text")
        alert.show()
    }

    open fun getInfo(): String? {
        try {
            val pi = packageManager.getPackageInfo(packageName, 0)
            if (pi != null) {
                return pi.versionName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return getString(R.string.unknown)
    }

    override fun onDestroy() {
        _binding = null
        compositeDisposable.dispose()
        super.onDestroy()
    }

    open fun getRFIDApplication(): RFIDApplication {
        return application as RFIDApplication
    }

    protected fun settingPermission() {
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

    protected fun showLoading(isShow: Boolean, loadingView: View) {
        if (isShow) {
            loadingView.visibility = View.VISIBLE
        } else {
            loadingView.visibility = View.GONE
        }
    }

    protected fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}