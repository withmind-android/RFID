package com.rfid.ui.base

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.gun0912.tedpermission.rx3.TedPermission
import com.rfid.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {
    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected val compositeDisposable = CompositeDisposable()

    protected lateinit var currentPhotoPath: String
    protected val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        init()
    }

    override fun onDestroyView() {
        _binding = null
        compositeDisposable.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    abstract fun init()

    protected fun getColor(color: Int): Int {
        return ContextCompat.getColor(requireContext(), color)
    }

    protected fun getDrawable(drawable: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), drawable)
    }

    protected fun showToast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected fun activateButtons(btnFirst: Button, btnSecond: Button) {
        if (btnFirst.background == getDrawable(R.drawable.grey_radius_5_container)) {
            btnFirst.background = getDrawable(R.drawable.grey_radius_5_container)
            btnFirst.setTextColor(getColor(R.color.text_hint))
            btnSecond.background = getDrawable(R.drawable.btn_main_light)
            btnSecond.setTextColor(getColor(R.color.white))
        } else {
            btnFirst.background = getDrawable(R.drawable.btn_main_light)
            btnFirst.setTextColor(getColor(R.color.white))
            btnSecond.background = getDrawable(R.drawable.grey_radius_5_container)
            btnSecond.setTextColor(getColor(R.color.text_hint))
        }
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
                        requireContext(),
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
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
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
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}