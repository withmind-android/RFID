package com.rfid.ui.detect.item

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import com.rfid.R
import com.rfid.adapter.DetectPictureAdapter
import com.rfid.adapter.PagerFragmentStateAdapter
import com.rfid.adapter.item.PictureItem
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.tag.PostConcrete
import com.rfid.data.remote.tag.RequestStandard
import com.rfid.data.remote.tag.Tag
import com.rfid.data.remote.tag.TagDataSourceImpl
import com.rfid.data.repository.tag.TagRepositoryImpl
import com.rfid.databinding.ItemDetect4Binding
import com.rfid.ui.base.BaseFragment
import com.rfid.util.BitmapConverter
import com.rfid.util.SharedPreferencesPackage
import java.io.File

class DetectItem4(
    private val item: Tag,
    private val pagerAdapter: PagerFragmentStateAdapter
) : BaseFragment<ItemDetect4Binding>(R.layout.item_detect_4) {
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
    private var imgList: MutableList<String> = mutableListOf()
    private var deleteList: MutableList<Int> = mutableListOf()
    private var mPostConcrete = PostConcrete(
        "", "",
        0, 0, 0,
        0, 0, 0, imgList, deleteList
    )

    override fun init() {
        setImgAdapter()
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
            getConcrete(item.tag)

            concrete.observe(viewLifecycleOwner, { mConcrete ->
                mPostConcrete.tag = item.tag

                binding.apply {
                    rvPicture.layoutManager?.isItemPrefetchEnabled = false
                    rvPicture.setItemViewCacheSize(0)
                    rvPicture.adapter = detectPictureAdapter

                    etSlumpValue.setText(mConcrete.slump.toString())
                    etAirValue.setText(mConcrete.air.toString())
                    etChlorideValue.setText(mConcrete.chloride.toString())
                    etSlumpStandard.setText(mConcrete.slump_standard.toString())
                    etAirStandard.setText(mConcrete.air_standard.toString())
                    etChlorideStandard.setText(mConcrete.chloride_standard.toString())

                    if (mConcrete.result == "양호") {
                        activateButtons(binding.btSuccess, binding.btFail)
                        mPostConcrete.result = "양호"
                    } else if (mConcrete.result == "불량") {
                        activateButtons(binding.btFail, binding.btSuccess)
                        mPostConcrete.result = "불량"
                    }
                }

                pictures.add(PictureItem(null, "", null, false))
                if (mConcrete.images != null && mConcrete.images.size != 0) {
                    for (i in mConcrete.images.indices) {
                        pictures.add(
                            0, PictureItem(
                                mConcrete.images[i].id, mConcrete.images[i].data, null, false
                            )
                        )
                    }
                }
                detectPictureAdapter.setList(pictures)
            })

            response.observe(viewLifecycleOwner, {
                if (it.result == 1) {
                    val position = pagerAdapter.getPosition(this@DetectItem4)
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
                mPostConcrete.result = "불량"
            }
            btSuccess.setOnClickListener {
                activateButtons(binding.btSuccess, binding.btFail)
                mPostConcrete.result = "양호"
            }
            btSend.setOnClickListener {
                mPostConcrete.apply {
                    slump = etSlumpValue.text.toString().toInt()
                    air = etAirValue.text.toString().toInt()
                    chloride = etChlorideValue.text.toString().toInt()
                    slump_standard = etSlumpStandard.text.toString().toInt()
                    air_standard = etAirStandard.text.toString().toInt()
                    chloride_standard = etChlorideStandard.text.toString().toInt()
                    images = imgList
                    deleted_images = deleteList
                }
                viewModel.postConcrete(mPostConcrete)
            }
        }
    }

    companion object {
        private const val TAG = "DetectItem4"
    }
}