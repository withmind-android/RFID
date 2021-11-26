package com.rfid.ui.detect.item

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import com.rfid.R
import com.rfid.adapter.DetectPictureAdapter
import com.rfid.adapter.PagerFragmentStateAdapter
import com.rfid.adapter.item.PictureItem
import com.rfid.adapter.item.RegisterItem
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.tag.*
import com.rfid.data.repository.tag.TagRepositoryImpl
import com.rfid.ui.base.BaseFragment
import com.rfid.databinding.ItemDetect2Binding
import com.rfid.util.BitmapConverter
import com.rfid.util.SharedPreferencesPackage
import java.io.File

class DetectItem2(
    private val item: Tag,
    private val pagerAdapter: PagerFragmentStateAdapter
) : BaseFragment<ItemDetect2Binding>(R.layout.item_detect_2) {
    private val viewModel: DetectViewModel by lazy {
        DetectViewModel(
            repository = TagRepositoryImpl(
                userDataSource = UserDataSourceImpl(
                    userDao = DatabaseClient.userDao()
                ),
                loginDataSource = LoginDataSourceImpl(
                    loginApi = RetrofitClient.loginAPI
                ),
                tagDataSource = TagDataSourceImpl(
                    tagApi = RetrofitClient.tagAPI
                )
            ),
            requestStandard = RequestStandard(
                SharedPreferencesPackage.getProject().id,
                SharedPreferencesPackage.getManufacture().id,
                item.tag
            )
        )
    }

    private lateinit var detectPictureAdapter: DetectPictureAdapter
    private val pictures = mutableListOf<PictureItem>()

    // combineLatest
    private lateinit var mCovering: Covering
    private lateinit var mPostCovering: PostCovering
    private var imgList: MutableList<String> = mutableListOf()
    private var deleteList: MutableList<Int> = mutableListOf()

    override fun init() {
        setImgAdapter()
        applyBinding()
        applyViewModel()
        setStandard(item.type)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {

            } else {
                val decode = ImageDecoder.createSource(
                    requireContext().contentResolver,
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

    private fun setImgAdapter() {
        detectPictureAdapter = DetectPictureAdapter()
        detectPictureAdapter.setItemClickListener(object : DetectPictureAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                settingPermission()
            }

            override fun onClickDelete(deleteId: Int, img: String) {
                if (deleteId != 0) {
                    deleteList.add(deleteId)
                } else {
                    imgList.remove(img)
                }
            }
        })
    }

    private fun applyViewModel() {
        viewModel.apply {
            getCovering(item.tag)

            cover.observe(viewLifecycleOwner, {
                mCovering = it
                mPostCovering = PostCovering("", "", 0, 0, imgList, deleteList)
                mPostCovering.tag = item.tag

                binding.apply {
                    rvPicture.layoutManager?.isItemPrefetchEnabled = false
                    rvPicture.setItemViewCacheSize(0)
                    rvPicture.adapter = detectPictureAdapter

                    etThick1.setText(it.thickness1.toString())
                    etThick2.setText(it.thickness2.toString())

                    if (it.result == "양호") {
                        activateButtons(binding.btSuccess, binding.btFail)
                        mPostCovering.result = "양호"
                    } else if (it.result == "불량") {
                        activateButtons(binding.btFail, binding.btSuccess)
                        mPostCovering.result = "불량"
                    }
                }

                pictures.add(PictureItem(null, "", null, false))
                if (it.images != null && it.images.size != 0) {
                    for (i in it.images.indices) {
                        pictures.add(
                            0, PictureItem(
                                it.images[i].id, it.images[i].data, null, false
                            )
                        )
                    }
                }
                detectPictureAdapter.setList(pictures)
            })

            response.observe(viewLifecycleOwner, {
                if (it.result == 1) {
                    val position = pagerAdapter.getPosition(this@DetectItem2)
                    val idList = pagerAdapter.getFragmentList()
                    val pageId = pagerAdapter.getItemId(position)
                    val delPosition = idList.indexOf(pageId)
                    pagerAdapter.removeFragment(delPosition)

                    if (idList.size == 1) {
                        Log.e(TAG, "detect 마지막 전송")
                        activity?.apply {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                } else {
                    Log.e(TAG, "error: $it")
                }
            })
        }
    }

    private fun applyBinding() {
        binding.apply {
            etId.setText(item.tag)
            etNum.setText(item.serial)
            etKind.setText(item.type)

            btFail.setOnClickListener {
                activateButtons(binding.btFail, binding.btSuccess)
                mPostCovering.result = "불량"
            }
            btSuccess.setOnClickListener {
                activateButtons(binding.btSuccess, binding.btFail)
                mPostCovering.result = "양호"
            }
            btSend.setOnClickListener {
                mPostCovering.apply {
                    thickness1 = etThick1.text.toString().toInt()
                    thickness2 = etThick2.text.toString().toInt()
                    images = imgList
                    deleted_images = deleteList
                }
                viewModel.postCovering(mPostCovering)
            }
        }
    }

    private fun setStandard(type: String) {
        when (type) {
            "벽체" -> {
                binding.llItem2.visibility = View.GONE
                binding.tvStandardThick1.text = getString(R.string.standard_thick_1)
            }
            "슬라브" -> {
                binding.llItem2.visibility = View.GONE
                binding.tvStandardThick1.text = getString(R.string.standard_thick_2)
            }
            "거더" -> {
                binding.llItem2.visibility = View.VISIBLE
                binding.tvLengthTitle.text = getString(R.string.thick_1)
                binding.tvStandardThick1.text = getString(R.string.standard_thick_3_first)
                binding.tvStandardThick2.text = getString(R.string.standard_thick_3_second)
            }
            "기둥" -> {
                binding.llItem2.visibility = View.GONE
                binding.tvStandardThick1.text = getString(R.string.standard_thick_4)
            }
        }
    }

    companion object {
        private const val TAG = "DetectItem2"
    }
}