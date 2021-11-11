package com.rfid.ui.detect.item

import android.app.Activity.RESULT_OK
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
import com.rfid.adapter.DetectSubjectAdapter
import com.rfid.adapter.PagerFragmentStateAdapter
import com.rfid.adapter.item.PictureItem
import com.rfid.adapter.item.SubjectItem
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.tag.*
import com.rfid.data.repository.tag.TagRepositoryImpl
import com.rfid.databinding.ItemDetect3Binding
import com.rfid.ui.base.BaseFragment
import com.rfid.util.BitmapConverter
import com.rfid.util.SharedPreferencesPackage
import java.io.File

class DetectItem3(
    private val item: Tag,
    private val pagerAdapter: PagerFragmentStateAdapter
) : BaseFragment<ItemDetect3Binding>(R.layout.item_detect_3) {
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
    private lateinit var detectSubjectAdapter: DetectSubjectAdapter
    private val pictures = mutableListOf<PictureItem>()
    private val subjects = mutableListOf<SubjectItem>()

    // combineLatest
    private var imgList: MutableList<String> = mutableListOf()
    private var deleteList: MutableList<Int> = mutableListOf()
    private var mPostIronware = PostIronware(
        "", "",
        "", "", "", "", "",
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        "", "", "", "", "",
        imgList, deleteList
    )

    override fun init() {
        setImgAdapter()
        applyBinding()
        applyViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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

    private fun setUnitAdapter(subjects: MutableList<SubjectItem>) {
        detectSubjectAdapter = DetectSubjectAdapter()
        binding.rvSubject.layoutManager?.isItemPrefetchEnabled = false
        binding.rvSubject.setItemViewCacheSize(0)
        binding.rvSubject.adapter = detectSubjectAdapter

        detectSubjectAdapter.setList(subjects)
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
            getIronware(item.tag)

            ironware.observe(viewLifecycleOwner, { mIronware ->
                mPostIronware.apply {
                    tag = item.tag
                    mIronware.result?.let { result = mIronware.result } ?: run { result = "" }
                    mIronware.field1?.let { field1 = mIronware.field1 } ?: run { field1 = "" }
                    mIronware.field2?.let { field2 = mIronware.field2 } ?: run { field2 = "" }
                    mIronware.field3?.let { field3 = mIronware.field3 } ?: run { field3 = "" }
                    mIronware.field4?.let { field4 = mIronware.field4 } ?: run { field4 = "" }
                    mIronware.field5?.let { field5 = mIronware.field5 } ?: run { field5 = "" }
                    mIronware.value1.let { value1 = mIronware.value1 }
                    mIronware.value2.let { value2 = mIronware.value2 }
                    mIronware.value3.let { value3 = mIronware.value3 }
                    mIronware.value4.let { value4 = mIronware.value4 }
                    mIronware.value5.let { value5 = mIronware.value5 }
                    mIronware.standard1.let { standard1 = mIronware.standard1 }
                    mIronware.standard2.let { standard2 = mIronware.standard2 }
                    mIronware.standard3.let { standard3 = mIronware.standard3 }
                    mIronware.standard4.let { standard4 = mIronware.standard4 }
                    mIronware.standard5.let { standard5 = mIronware.standard5 }
                    mIronware.unit1?.let { unit1 = mIronware.unit1 } ?: run { unit1 = "" }
                    mIronware.unit2?.let { unit2 = mIronware.unit2 } ?: run { unit2 = "" }
                    mIronware.unit3?.let { unit3 = mIronware.unit3 } ?: run { unit3 = "" }
                    mIronware.unit4?.let { unit4 = mIronware.unit4 } ?: run { unit4 = "" }
                    mIronware.unit5?.let { unit5 = mIronware.unit5 } ?: run { unit5 = "" }
                    images = imgList
                    deleted_images = deleteList
                }
                subjects.add(SubjectItem(mPostIronware.field1, mPostIronware.value1, mPostIronware.standard1, mPostIronware.unit1))
                subjects.add(SubjectItem(mPostIronware.field2, mPostIronware.value2, mPostIronware.standard2, mPostIronware.unit2))
                subjects.add(SubjectItem(mPostIronware.field3, mPostIronware.value3, mPostIronware.standard3, mPostIronware.unit3))
                subjects.add(SubjectItem(mPostIronware.field4, mPostIronware.value4, mPostIronware.standard4, mPostIronware.unit4))
                subjects.add(SubjectItem(mPostIronware.field5, mPostIronware.value5, mPostIronware.standard5, mPostIronware.unit5))

                binding.apply {
                    rvPicture.layoutManager?.isItemPrefetchEnabled = false
                    rvPicture.setItemViewCacheSize(0)
                    rvPicture.adapter = detectPictureAdapter

                    if (mIronware.result == "양호") {
                        activateButtons(binding.btSuccess, binding.btFail)
                        mPostIronware.result = "양호"
                    } else if (mIronware.result == "불량") {
                        activateButtons(binding.btFail, binding.btSuccess)
                        mPostIronware.result = "불량"
                    }
                }

                pictures.add(PictureItem(null, "", null, false))
                if (mIronware.images != null && mIronware.images.size != 0) {
                    for (i in mIronware.images.indices) {
                        pictures.add(
                            0, PictureItem(
                                mIronware.images[i].id, mIronware.images[i].data, null, false
                            )
                        )
                    }
                }
                detectPictureAdapter.setList(pictures)
                setUnitAdapter(subjects)
            })

            response.observe(viewLifecycleOwner, {
                if (it.result == 1) {
                    val position = pagerAdapter.getPosition(this@DetectItem3)
                    val idList = pagerAdapter.getFragmentList()
                    val pageId = pagerAdapter.getItemId(position)
                    val delPosition = idList.indexOf(pageId)
                    pagerAdapter.removeFragment(delPosition)

                    if (idList.size == 1) {
                        Log.e(TAG, "detect 마지막 전송")
                        activity?.apply {
                            setResult(RESULT_OK)
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
                mPostIronware.result = "불량"
            }
            btSuccess.setOnClickListener {
                activateButtons(binding.btSuccess, binding.btFail)
                mPostIronware.result = "양호"
            }
            btSend.setOnClickListener {
                mPostIronware.apply {
                    val list = detectSubjectAdapter.getList()
                    Log.e(TAG, "list: $list")
                    field1 = list[0].field
                    field2 = list[1].field
                    field3 = list[2].field
                    field4 = list[3].field
                    field5 = list[4].field
                    value1 = list[0].value
                    value2 = list[1].value
                    value3 = list[2].value
                    value4 = list[3].value
                    value5 = list[4].value
                    standard1 = list[0].standard
                    standard2 = list[1].standard
                    standard3 = list[2].standard
                    standard4 = list[3].standard
                    standard5 = list[4].standard
                    unit1 = list[0].unit
                    unit2 = list[1].unit
                    unit3 = list[2].unit
                    unit4 = list[3].unit
                    unit5 = list[4].unit
                    images = imgList
                    deleted_images = deleteList
                }
                viewModel.postIronware(mPostIronware)
            }
        }
    }

    companion object {
        private const val TAG = "DetectItem3"
    }
}