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
import com.rfid.data.remote.tag.*
import com.rfid.data.repository.tag.TagRepositoryImpl
import com.rfid.databinding.ItemDetect5Binding
import com.rfid.ui.base.BaseFragment
import com.rfid.ui.detect.TabLayoutListener
import com.rfid.util.BitmapConverter
import com.rfid.util.SharedPreferencesPackage
import java.io.File

class DetectItem5(
    private val item: Tag,
    private val pagerAdapter: PagerFragmentStateAdapter
) : BaseFragment<ItemDetect5Binding>(R.layout.item_detect_5) {
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
    private var mPostUnMolding = PostUnMolding("","",0, imgList, deleteList)

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
            getUnMolding(item.tag)

            standard.observe(viewLifecycleOwner, { mStandard ->
                binding.tvStrengthStandard.text = mStandard.strength.toString()
            })

            unMolding.observe(viewLifecycleOwner, { mUnMolding ->
                mPostUnMolding.tag = item.tag

                binding.apply {
                    rvPicture.layoutManager?.isItemPrefetchEnabled = false
                    rvPicture.setItemViewCacheSize(0)
                    rvPicture.adapter = detectPictureAdapter

                    etStrengthValue.setText(mUnMolding.strength.toString())

                    if (mUnMolding.result == "양호") {
                        activateButtons(binding.btSuccess, binding.btFail)
                        mPostUnMolding.result = "양호"
                    } else if (mUnMolding.result == "불량") {
                        activateButtons(binding.btFail, binding.btSuccess)
                        mPostUnMolding.result = "불량"
                    }
                }

                pictures.add(PictureItem(null, "", null, false))
                if (mUnMolding.images != null && mUnMolding.images.size != 0) {
                    for (i in mUnMolding.images.indices) {
                        pictures.add(
                            0, PictureItem(
                                mUnMolding.images[i].id, mUnMolding.images[i].data, null, false
                            )
                        )
                    }
                }
                detectPictureAdapter.setList(pictures)
            })

            response.observe(viewLifecycleOwner, {
                if (it.result == 1) {
                    val position = pagerAdapter.getPosition(this@DetectItem5)
                    val idList = pagerAdapter.getFragmentList()
                    val pageId = pagerAdapter.getItemId(position)
                    val delPosition = idList.indexOf(pageId)
                    pagerAdapter.removeFragment(delPosition)
                    tabLayoutListener.removeFragment(delPosition)

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
                mPostUnMolding.result = "불량"
            }
            btSuccess.setOnClickListener {
                activateButtons(binding.btSuccess, binding.btFail)
                mPostUnMolding.result = "양호"
            }
            btSend.setOnClickListener {
                mPostUnMolding.apply {
                    strength = etStrengthValue.text.toString().toInt()
                    images = imgList
                    deleted_images = deleteList
                }
                viewModel.postUnMolding(mPostUnMolding)
            }
        }
    }

    // tabLayout listener
    private lateinit var tabLayoutListener: TabLayoutListener
    fun setItemClickListener(tabLayoutListener: TabLayoutListener) {
        this.tabLayoutListener = tabLayoutListener
    }

    companion object {
        private const val TAG = "DetectItem5"
    }
}