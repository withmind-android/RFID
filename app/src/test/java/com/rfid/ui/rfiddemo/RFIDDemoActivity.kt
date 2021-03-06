package com.rfid.ui.rfiddemo

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rfid.R
import com.rfid.RFIDApplication.NotifyDataCallbacks
import com.rfid.adapter.RfidRvAdapter
import com.rfid.adapter.item.RfidListItem
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.tag.Tag
import com.rfid.data.remote.tag.TagDataSourceImpl
import com.rfid.data.repository.tag.TagRepositoryImpl
import com.rfid.databinding.ActivityRfidDemoBinding
import com.rfid.ui.RFIDControlActivity
import com.rfid.ui.base.BaseActivityK
import com.rfid.ui.detect.DetectActivity
import com.rfid.ui.shipment.ShipmentActivity
import com.rfid.util.PreferenceUtil
import com.rfid.util.Utils
import com.rfid.util.custom.DialogWriteTag
import device.common.rfid.*
import device.sdk.RFIDManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RFIDDemoActivity : BaseActivityK<ActivityRfidDemoBinding>(R.layout.activity_rfid_demo) {
    //    private val viewModel by viewModels<RFIDDemoViewModel>()
    private val viewModel: RFIDDemoViewModel by lazy {
        RFIDDemoViewModel(
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
            )
        )
    }

    private val LOG_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd_HHmmss_SSS", Locale.getDefault())
    private var mRfidMgr: RFIDManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mOperationMode = ModeOfInvent()
    private var mRvAdapter: RfidRvAdapter? = null
    private var mItems: ArrayList<RfidListItem>? = null
    private var registerItems: MutableList<Tag> = mutableListOf()
    private var idList: MutableList<String> = mutableListOf()
    private var mIdx = 0
    private var mHashItems: HashMap<String, Int>? = null
    private var mReadCount = 0
    private var mTotalCount = 0
    private var mAutoScanStart = false
    private var mIsReadStart = false
    private var mAutoScanInterval = 0
    private var mIsSaveLog = false
    private var mFile: File? = null
    private var mLogStartTime: Long = -1
    private var mSimpleDataFormat: SimpleDateFormat? = null
    private var mIsTemp = false
    private var mTempValue = "0"
    private var mVolumeValue = 0
    private var mProgress: ProgressDialog? = null
    private var mUtil: Utils? = null
    private var mPrefUtil: PreferenceUtil? = null
    private var isPause = false
    private val mToast: Toast? = null
    private var mOptionMenu: Menu? = null
    private var mIsAsc = true
    private val mHandler: LogSaveHandler? = LogSaveHandler(this)

    override fun init() {
        binding.lifecycleOwner = this
        binding.appBarDemo.contentRfid.pbLoading.visibility = View.GONE
        mUtil = Utils(this)
        mPrefUtil = PreferenceUtil(applicationContext)
        mProgress = ProgressDialog(this@RFIDDemoActivity)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setSupportActionBar(binding.appBarDemo.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mRfidMgr = RFIDManager.getInstance()
        mItems = ArrayList()
        val mLinearLayoutManager = LinearLayoutManager(this)
        binding.appBarDemo.contentDemo.recyTagItem.layoutManager = mLinearLayoutManager
        mRvAdapter = RfidRvAdapter(mItems)
        binding.appBarDemo.contentDemo.recyTagItem.adapter = mRvAdapter
        mHashItems = HashMap()
        mReadCount = 0
        mTotalCount = 0
        mSimpleDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //Check EXTERNAL_STORAGE_PERMISSION
        if (!checkPermission()) requestPermission()
        bluetoothOn()

        mIdx = intent.getIntExtra("idx", 0)

        viewModel.tags.observe(this, {
            registerItems = it.data
            if (mIdx == 0) {
                sendRegisterView()
            } else {
                sendDetectView()
            }
        })
    }

    private fun sendDetectView() {
        val detect = Intent(this, DetectActivity::class.java)
        detect.putExtra("id", mIdx)
        when (mIdx) {
            1 -> detect.putExtra("title", "?????? ??????")
            2 -> detect.putExtra("title", "???????????? ??????")
            3 -> detect.putExtra("title", "???????????? ??????")
            4 -> detect.putExtra("title", "???????????? ??????")
            5 -> detect.putExtra("title", "???????????? ??????")
            6 -> detect.putExtra("title", "?????? ??????")
        }
        if (registerItems.size == 0) {
            Toast.makeText(this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
        } else {
            detect.putExtra("list", registerItems as ArrayList<*>)
            startActivityForResult(detect, REQUEST_DETECT_ACTIVITY)
        }
    }

    private fun sendRegisterView() {
        val detect = Intent(this, ShipmentActivity::class.java)
        detect.putExtra("id", mIdx)
        detect.putExtra("title", "?????? ??????")
        if (registerItems.size == 0) {
            Toast.makeText(this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
        } else {
            detect.putExtra("list", registerItems as ArrayList<*>)
            startActivityForResult(detect, REQUEST_REGISTER_ACTIVITY)
        }
    }

    private fun checkTempOption() {
        mIsTemp = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_UPDATE_TEMP, false)
        val reportFormatOfInvent_ext = ReportFormatOfInvent_ext()
        if (mIsTemp) {
            Log.d(TAG, "Temp option on")
            reportFormatOfInvent_ext.temp = 1
            binding.appBarDemo.contentDemo.linearTemp.visibility = View.VISIBLE
        } else {
            Log.d(TAG, "Temp option off")
            reportFormatOfInvent_ext.temp = 0
            binding.appBarDemo.contentDemo.linearTemp.visibility = View.GONE
        }
        val result = mRfidMgr!!.SetInventoryReportFormat_ext(reportFormatOfInvent_ext)
        if (result == RFIDConst.CommandErr.COMM_ERR) {
            binding.appBarDemo.contentDemo.textTemperature.text = getString(R.string.temp_fail)
        }
    }

    private fun bluetoothOn() {
        if (!mRfidMgr!!.IsOpened()) rfidConnectDialog()
        if (mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION, mUtil!!.defaultOption)
                .equals(RFIDControlActivity.OpenOption.BLUETOOTH.toString(), ignoreCase = true)
        ) {
            if (!mRfidMgr!!.IsOpened()) {
                mRfidMgr!!.DisconnectBTDevice()
                Log.d(TAG, "disconnect 11")
            }
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH)
            }
        }
    }

    private fun initState() {
        //Operation Mode Value
        mRfidMgr!!.GetOperationMode(mOperationMode)
        if (mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS) {
            binding.appBarDemo.contentDemo.checkboxContinuous.isChecked = true
            binding.appBarDemo.contentDemo.checkboxAutoScan.isChecked = false
            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false)
        } else {
            binding.appBarDemo.contentDemo.checkboxContinuous.isChecked = false
        }
        Log.e(
            "checkchange_init",
            if (mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE) "single" else "continuous"
        )

        //Auto Scan Value
        val isAutoScan = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE)
        binding.appBarDemo.contentDemo.checkboxAutoScan.isChecked = isAutoScan
        if (isAutoScan) {
            binding.appBarDemo.contentDemo.checkboxContinuous.isChecked = false
            mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE
            mRfidMgr!!.SetOperationMode(mOperationMode)
        }

        //Log Save Value
        val isSaveLOG = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG)
        binding.appBarDemo.contentDemo.checkboxSaveLog.isChecked = isSaveLOG

        //Volume Value
        // Log.e("initState", "mVolume === " + mVolumeValue);
        if (mVolumeValue == 1 || mVolumeValue == 2) {
            mPrefUtil!!.putIntPreference(PreferenceUtil.KEY_BEEP_SOUND, mVolumeValue)
            binding.appBarDemo.contentDemo.checkboxBeepSound.isChecked = true
        } else if (mVolumeValue == 0) {
            binding.appBarDemo.contentDemo.checkboxBeepSound.isChecked = false
        }
    }

    private fun checkPermission(): Boolean {
        val permissionResult =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            0
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.finishAffinity(this);
                onBackPressed()
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mOptionMenu = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.demo_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mAutoScanStart || mIsReadStart) {
            mAutoScanStart = false
            mIsReadStart = false
            mRfidMgr!!.Stop()
            //stopScan();
            stopScanCompletable()
        }

        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            Log.d("finish()", "finishing!!!!")
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        idList.clear()
        getRFIDApplication().setNotifyDataCallback(mDataCallbacks)
        Log.d(TAG, "onResume()")
        //checkIsOpened(this, mRfidMgr);
        if (mRfidMgr!!.IsOpened()) {
            //Change ResultType to Callback
            mRfidMgr!!.SetResultType(RFIDConst.ResultType.RFID_RESULT_CALLBACK)
            mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, true)
            val asyncGetVolume = AsyncGetVolume()
            asyncGetVolume.execute()
        }
        checkTempOption()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
        isPause = true

        /* To roll-back previous Result Type */if (mRfidMgr!!.IsOpened()) {
            val currentType = mPrefUtil!!.getIntPreference(
                PreferenceUtil.KEY_RESULT_TYPE,
                RFIDConst.ResultType.RFID_RESULT_CALLBACK
            )
            if (currentType != RFIDConst.ResultType.RFID_RESULT_CALLBACK) {
                mRfidMgr!!.SetResultType(currentType)
            }
        }
        if (mIsReadStart || mAutoScanStart) {
            mIsReadStart = false
            mAutoScanStart = false
            stopScanCompletable()
            mRfidMgr!!.Stop()
            Log.d(TAG, "stop2")
        }
        if (mToast != null) {
            Log.d(TAG, "mToast cancel")
            mToast.cancel()
        }
    }

    private fun startWriteTag(writeData: String) {
        Single.create { subscriber: SingleEmitter<Any?> ->
            Log.d(TAG, "startWriteTag()+++")
            var result: Boolean
            result = changeToSingleMode()
            mUtil!!.sleep(1000)

//            result = setFilter(getSelect("aaaa"));
//            mUtil.sleep(3000);
            result = writeTag(writeData)
            mUtil!!.sleep(1000)
            result = rollbackMode()
            mUtil!!.sleep(1000)

//            result = setFilter(getDefaultSelect());
            subscriber.onSuccess(true)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it: Any? ->
                Log.d(TAG, "startWriteTag()---")
                mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, false)
            }) { throwable: Throwable? ->
                Log.d(TAG, "Error startWriteTag()---")
                mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, false)
            }
    }

    private fun startReadTag() {
        Single.create { subscriber: SingleEmitter<Any?> ->
            Log.d(TAG, "startWriteTag()+++")
            var result: Boolean
            result = changeToSingleMode()
            mUtil!!.sleep(1000)
            result = readTag()
            mUtil!!.sleep(1000)
            result = rollbackMode()
            subscriber.onSuccess(true)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it: Any? ->
                Log.d(
                    TAG,
                    "startReadTag()---"
                )
            }) { throwable: Throwable? -> Log.d(TAG, "Error startReadTag()---") }
    }

    /* ????????? tag write ?????? ?????? ????????? ????????? ?????? ??? ????????? tag??? write?????? ????????? select inclusion ??????
         RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE : mode??? single??? ??????
         RFIDConst.RFIDConfig.INVENTORY_SELECT_INCLUSION : Select??? ????????? tag pattern??? ?????? tag??? read/write ????????? ??????
     */
    private fun changeToSingleMode(): Boolean {
        Log.d(TAG, "changeToSingleMode()+++")
        val modeOfInvent = ModeOfInvent()
        modeOfInvent.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE
        //modeOfInvent.select = RFIDConst.RFIDConfig.INVENTORY_SELECT_INCLUSION;
        val result = mRfidMgr!!.SetOperationMode(modeOfInvent)
        Log.d(TAG, "changeToSingleMode()--- result : $result")
        return result == RFIDConst.CommandErr.SUCCESS
    }

    /* ????????? ?????????????????? ????????? ????????? */
    private fun rollbackMode(): Boolean {
        Log.d(TAG, "rollbackMode()+++")
        val isContinuous =
            mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_CONTINUOUS_ENABLE, true)
        Log.d(TAG, if (isContinuous) "isContinuous true" else "isContinuous false")
        val modeOfInvent = ModeOfInvent()
        if (!isContinuous) modeOfInvent.single =
            RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE else modeOfInvent.single =
            RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS
        modeOfInvent.select = mPrefUtil!!.getIntPreference(
            PreferenceUtil.KEY_SELECT,
            RFIDConst.RFIDConfig.INVENTORY_SELECT_NONE
        )
        val result = mRfidMgr!!.SetOperationMode(modeOfInvent)
        Log.d(TAG, "rollbackMode()--- result : $result")
        return result == RFIDConst.CommandErr.SUCCESS
    }

    /* write??? ????????? tag??? filter ?????? */
    private fun setFilter(selConfig: SelConfig): Boolean {
        Log.d(TAG, "setFilter+++")
        val result = mRfidMgr!!.SetSelMask(selConfig)
        Log.d(TAG, "setFilter--- result : $result")
        return result == RFIDConst.CommandErr.SUCCESS
    }

    /*????????????????????? tag??? ?????? ???????????? write/read ????????? ?????? */
    private fun getSelect(tagPattern: String): SelConfig {
        val selConfig = SelConfig()
        selConfig.index = 3
        selConfig.memBank = 3
        selConfig.action = 1
        selConfig.target = 4
        selConfig.selectData = tagPattern
        selConfig.length = selConfig.selectData.length * 4
        selConfig.offset = 8 * 4
        return selConfig
    }

    /* ???????????? tag filter??? ?????????. ??????????????? ????????? tag read??? filter??? ???????????? tag??? ????????? */
    private val defaultSelect: SelConfig
        private get() {
            val selConfig = SelConfig()
            selConfig.index = 3
            selConfig.memBank = 3
            selConfig.action = 1
            selConfig.target = 4
            selConfig.selectData = ""
            selConfig.length = selConfig.selectData.length
            selConfig.offset = 0
            return selConfig
        }

    /* Tag ???????????? write */
    private fun writeTag(tagIdToWrite: String): Boolean {
        Log.d(TAG, "writeTag()+++")
        val accessTag = AccessTag()
        accessTag.wTagData = tagIdToWrite
        Log.d(TAG, "tagData : " + accessTag.wTagData)
        accessTag.length = accessTag.wTagData.length / 4 //unit : word
        if (accessTag.wTagData.length % 4 != 0) accessTag.length += 1
        Log.d(TAG, "length : " + accessTag.length)
        accessTag.memBank = 3
        Log.d(TAG, "membank : " + accessTag.memBank)
        accessTag.offset = 0
        Log.d(TAG, "offset : " + accessTag.offset)
        accessTag.acsPwd = "0"
        val result = mRfidMgr!!.WriteTag(accessTag)
        Log.d(TAG, "write tag error : " + accessTag.errOp)
        Log.d(TAG, "writeTag()--- : $result")
        return result == RFIDConst.CommandErr.SUCCESS
    }

    /* Tag ???????????? read */
    private fun readTag(): Boolean {
        Log.d(TAG, "readTag()+++")
        val accessTag = AccessTag()
        accessTag.length = 1
        accessTag.memBank = 3
        accessTag.offset = 0
        accessTag.acsPwd = "0"
        val result = mRfidMgr!!.ReadTag(accessTag)
        Log.d(TAG, "read tag data hex : " + accessTag.tagId)
        Log.d(TAG, "readTag()--- : $result")
        return result == RFIDConst.CommandErr.SUCCESS
    }

    private fun startScanCompletable() {
        Log.d(D, "startScanCompletable")
        changeStartToStop()
        Completable.create { subscriber: CompletableEmitter ->
            startScan()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "Completed start scan") }) { throwable: Throwable? ->
                Log.d(
                    TAG,
                    "Error in startScanCompletable"
                )
            }
    }

    private fun stopScanCompletable() {
        Log.d(D, "stopScanCompletable")
        changeStopToStart()
        Completable.create { subscriber: CompletableEmitter ->
            stopScan()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "Completed stop scan") }) { throwable: Throwable? ->
                Log.d(
                    TAG,
                    "Error in stopScanCompletable"
                )
            }
    }

    private fun triggerStartScanCompletable() {
        Log.d(D, "triggerStartScanCompletable")
        changeStartToStop()
        Completable.create { subscriber: CompletableEmitter ->
            triggerScanStart()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.d(TAG, "Completed stop scan") }) { throwable: Throwable? ->
                Log.d(
                    TAG,
                    "Error in triggerStartScanCompletable"
                )
            }
    }

    private fun triggerStopScanCompletable() {
        Log.d(D, "triggerStopScanCompletable")
        changeStopToStart()
        Completable.create { subscriber: CompletableEmitter ->
            triggerScanStop()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "Completed stop scan")
                for (i in mItems!!.indices) {
                    Log.e(TAG, mItems!![i].name)
                }
                // TODO idList ?????? mItems[i].name ??? ?????????
//                    idList.add("10001")
//                    idList.add("10006")
//                    idList.add("10011")
//                    idList.add("10016")
                idList.add("10003")
                idList.add("10007")
                idList.add("10013")
                idList.add("10015")
                viewModel.scanTag(idList)
            }) {
                Log.d(TAG, "Error in triggerStopScanCompletable")
            }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        if (mIsReadStart || mAutoScanStart) {
            mIsReadStart = false
            mAutoScanStart = false
            stopScanCompletable()
            mRfidMgr!!.Stop()
            Log.d(TAG, "stop4")
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_BLUETOOTH) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.bluetooth_disable),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("finish()", "finishing!!!!")
                finish()
            }
        } else if (requestCode == REQUEST_WRITE_TAG && resultCode == RESULT_OK) {
            val currentTagId = data!!.getStringExtra(DialogWriteTag.CURRENT_TAG_ID)
            val tagIdToWrite = data.getStringExtra(DialogWriteTag.TAG_ID_TO_WRITE)
            if (tagIdToWrite != null) {
                Log.d(TAG, "currentTagId : $currentTagId tagIdToWrite : $tagIdToWrite")
                mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, true)
                startWriteTag(tagIdToWrite)
            }
        } else if (requestCode == REQUEST_DETECT_ACTIVITY && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult: detect ????????????")
            finish()
        } else if (requestCode == REQUEST_REGISTER_ACTIVITY && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult: register ????????????")
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startAutoScan() {
        try {
            mAutoScanStart = true
            val interval = mPrefUtil!!.getIntPreference(PreferenceUtil.KEY_SCAN_AUTO_INTERVAL)
            mAutoScanInterval = (interval + 1) * 1000
            Log.d(TAG, "autoScanInterval$mAutoScanInterval")
            mAutoScanHandler.post(runnable)
        } catch (e: Exception) {
        }
    }

    /* To check if data packet received after invoked startInventory on auto read mode */
    private val mCallbackCheckTimer: CountDownTimer = object : CountDownTimer(5000, 1000) {
        override fun onTick(l: Long) {}
        override fun onFinish() {
            val isAutoScan = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE)
            if (isAutoScan) {
                Log.d(TAG, "=== Didn't receive RFIDCallback, Restart StartAutoScan")
                mAutoScanHandler.removeCallbacksAndMessages(null)
                mAutoScanHandler.post(runnable)
            }
        }
    }

    /* Start reading RFID tag */
    private fun startScan() {
        mIsSaveLog = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
        if (mIsSaveLog) {
            val currentDate = mSimpleDataFormat!!.format(Date())
            val strStart = "Start Scan : $currentDate"
            logToFile(strStart)
            mHandler!!.sendEmptyMessage(HANDLER_MSG_SAVE_LOG)
        }
        val isAutoScan = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE)
        if (isAutoScan) {
            startAutoScan()
        } else {
            val asyncStart = AsyncStart()
            asyncStart.execute()
        }
    }

    private fun changeStopToStart() {
        runOnUiThread {
            binding.appBarDemo.contentDemo.linearCheckbox.visibility = View.VISIBLE
            binding.appBarDemo.contentDemo.btnStartInventory.setText(R.string.read_rfid_tag)
        }
    }

    private fun changeStartToStop() {
        runOnUiThread {
            binding.appBarDemo.contentDemo.btnStartInventory.setText(R.string.stop_rfid_tag)
            binding.appBarDemo.contentDemo.linearCheckbox.visibility = View.GONE
        }
    }

    /* Stop reading RFID tag */
    private fun stopScan() {
        mAutoScanStart = false
        mAutoScanHandler.removeCallbacksAndMessages(null)
        callbackTimerCancel()
    }

    private fun rfidConnectDialog() {
        val builder = AlertDialog.Builder(this@RFIDDemoActivity)
        builder.setTitle(R.string.connect_dlg_title)
        builder.setMessage(R.string.not_connected)
        builder.setCancelable(false)
        builder.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
                Log.d("finish()", "finishing!!!!")
                finish()
            }
            false
        }
        builder.setPositiveButton(R.string.ok) { dialog, which ->
            dialog.dismiss()
            val intent = Intent(applicationContext, RFIDControlActivity::class.java)
            startActivity(intent)
            Log.d("finish()", "finishing!!!!")
            finish()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun getBatteryInfo(isReading: Boolean): String {
        var batteryVolt = 0
        var batteryLevel = 0
        var chargingState = 0
        var count = 0
        do {
            batteryLevel = mRfidMgr!!.GetBattLevel()
            Log.d(TAG, "GetBattLevel :$batteryLevel")
            if (batteryLevel > 0) {
                break
            } else count++
            if (count > 3) break
        } while (true)
        count = 0
        do {
            chargingState = mRfidMgr!!.GetChargingState()
            Log.d(TAG, "GetChargingState : $chargingState")
            if (chargingState >= RFIDConst.CommandErr.SUCCESS) {
                if (chargingState == 1 || chargingState == 0) break
            } else {
                count++
            }
            if (count > 3) break
            mUtil!!.sleep(100)
        } while (true)
        do {
            batteryVolt = mRfidMgr!!.GetBattVolt()
            Log.d(TAG, "GetBattVolt :$batteryVolt")
            if (batteryVolt >= 0) break else count++
            if (count > 3) break
            mUtil!!.sleep(100)
        } while (true)
        return (batteryVolt.toString() + "mV,  " + batteryLevel + "%,  "
                + if (chargingState == 1) getString(R.string.charging) else getString(R.string.not_charging))
    }

    private fun makeFolderAndFile(): Boolean {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return false
        }
        if (mFile != null) {
            return true
        }
        val fileName = SAVED_LOG_FILE + "_" + LOG_DATE_FORMAT.format(Date()) + ".log"
        mUtil!!.createFolder(RFID_CONTROL_FOLDER)
        mUtil!!.createFolder("$RFID_CONTROL_FOLDER/$SAVED_LOG_FOLDER")
        mFile = mUtil!!.createFile("$RFID_CONTROL_FOLDER/$SAVED_LOG_FOLDER/$fileName")
//        if (mFile.exists()) {
//            val title =
//                "CurrentTime, ReadCount[Number of Tag, Total Read], Batt(mV), Batt(%), Charging flag, RFID Module Temp"
//            logToFile(title)
//        }
        return true
    }

    fun saveLog(batteryInfo: String) {
        if (mLogStartTime < 0) mLogStartTime = System.currentTimeMillis()
        val currentDate = mSimpleDataFormat!!.format(Date())
        //String eslapseTime = formatElapsedTime(System.currentTimeMillis() - mLogStartTime);

        //String logStr = currentDate+",  [" + mTotalCount + "  " + mReadCount + "],  " + eslapseTime + ",  " + mBatterySummary;
        val logStr = "$currentDate,  [$mTotalCount  $mReadCount],  $batteryInfo,  $mTempValue"
        logToFile(logStr)
    }

    @Synchronized
    private fun logToFile(s: String): Boolean {
        if (mFile == null || !mFile!!.exists()) {
            makeFolderAndFile()
        }
        var result = true
        var fw: FileWriter? = null
        var writer: BufferedWriter? = null
        var buffer: StringBuilder? = StringBuilder()
        try {
            fw = FileWriter(mFile, true)
            writer = BufferedWriter(fw)
            buffer!!.append(
                """
    $s
    
    """.trimIndent()
            )
            writer.write(buffer.toString())
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        } finally {
            buffer!!.setLength(0)
            buffer = null
            try {
                if (writer != null) {
                    writer.close()
                    writer = null
                }
                if (fw != null) {
                    fw.close()
                    fw = null
                }
            } catch (ie: IOException) {
                ie.printStackTrace()
            }
        }
        return result
    }

    private fun addScanData(ogrData: String) {
        runOnUiThread {
            var data = ogrData
            Log.d(TAG, ogrData)
            if (mIsTemp) {
                val arrStr = data.split(",h=").toTypedArray() //h= : RFID Module temperature
                if (arrStr.size > 1) {
                    data = arrStr[0]
                    mTempValue = arrStr[1]
                    binding.appBarDemo.contentDemo.textTemperature.text = mTempValue
                }
            }
            val count = mHashItems!![data]
            if (count == null) {
                mTotalCount += 1
                mHashItems!![data] = 1
                mItems!!.add(RfidListItem(data, 1))
//                idList.add(data)
                mRvAdapter!!.notifyItemChanged(mItems!!.size - 1)
            } else {
                mHashItems!![data] = count + 1
                for (i in mItems!!.indices) {
                    val rfItem = mItems!![i]
                    if (rfItem.name.equals(data, ignoreCase = true)) {
                        rfItem.count = count + 1
                        mRvAdapter!!.notifyItemChanged(i)
                        break
                    }
                }
            }
            binding.appBarDemo.contentDemo.textTotalCount.text = "" + mTotalCount
            mReadCount += 1
            binding.appBarDemo.contentDemo.textTotalReadCount.text = "" + mReadCount
        }
    }

    fun sortByValue(map: Map<*, *>?, isAsc: Boolean): List<*> {
        val list: MutableList<String?> = ArrayList<String?>()
//        list.addAll(map!!.keys)
//        Collections.sort(list) { o1, o2 -> (o2 as Comparable<*>).compareTo(o1) }
//        if (isAsc) {
//            mOptionMenu!!.getItem(0).setIcon(R.drawable.sort_down)
//        } else {
//            mOptionMenu!!.getItem(0).setIcon(R.drawable.sort_up)
//            Collections.reverse(list) // ????????? ????????????
//        }
        return list
    }

    private val mDataCallbacks: NotifyDataCallbacks = object :
        NotifyDataCallbacks {
        override fun notifyDataPacket(recvPacket: RecvPacket) {
            Log.d(TAG, "notifyDataPacket : " + recvPacket.RecvString)
            callbackTimerCancel()

//            String temp = mUtil.hexToAscii( "7777772e6261656c64756e672e636f6d");
//            addScanData(temp);
//            String text =  mUtil.hexToAscii(recvPacket.RecvString);
            addScanData(recvPacket.RecvString)
            val isAutoScan = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE)
            mRfidMgr!!.GetOperationMode(mOperationMode)
            val isContinuous =
                mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS
            if (!isContinuous && !isAutoScan) {
                mIsReadStart = false
                stopScanCompletable()
                mRfidMgr!!.Stop()
            }
            mAutoScanHandler.removeCallbacksAndMessages(null)
            if (mIsReadStart && mAutoScanStart) mAutoScanHandler.postDelayed(
                runnable,
                mAutoScanInterval.toLong()
            )
        }

        override fun notifyChangedState(state: Int) {
            Log.d(TAG, "notifyChangedState : $state")
            if (state == RFIDConst.DeviceState.TRIGGER_RFID_KEYDOWN) {
                Log.d(D, "Trigger key down")
                Log.d(TAG, "TRIGGER_RFID_KEYDOWN")
                binding.appBarDemo.contentRfid.pbLoading.visibility = View.VISIBLE
                mAutoScanHandler.removeCallbacksAndMessages(null)
                if (!mIsReadStart && !mAutoScanStart) {
                    mIsReadStart = true
                    triggerStartScanCompletable()
                } else {
                    mIsReadStart = false
                    stopScanCompletable()
                }
            } else if (state == RFIDConst.DeviceState.TRIGGER_RFID_KEYUP) {
                Log.d(D, "Trigger key up")
                Log.d(TAG, "TRIGGER_RFID_KEYUP")
                binding.appBarDemo.contentRfid.pbLoading.visibility = View.GONE
                if (!mAutoScanStart) {
                    mIsReadStart = false
                    triggerStopScanCompletable()
                }
            } else if (state == RFIDConst.DeviceState.TRIGGER_SCAN_KEYDOWN) {
                Log.d(TAG, "TRIGGER_SCAN_KEYDOWN")
                if (!isPause) Toast.makeText(
                    applicationContext,
                    getString(R.string.scanner_mode),
                    Toast.LENGTH_LONG
                ).show()
            } else if (state == RFIDConst.DeviceState.LOW_BATT) {
                Log.d(TAG, "LOW_BATT")
                mIsReadStart = false
                stopScanCompletable()
                mRfidMgr!!.Stop()
                Log.d(TAG, "stop7")
            } else if (state == RFIDConst.DeviceState.BT_DISCONNECTED) {
                Log.d(TAG, "BT_DISCONNECTED")
                mRfidMgr!!.Close()
                Log.d(TAG, "close12")
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
                mIsReadStart = false
                RFIDControlActivity.POWER_OFF_FLAG = true
                val intent = Intent(this@RFIDDemoActivity, RFIDControlActivity::class.java)
                startActivity(intent)
            } else if (state == RFIDConst.DeviceState.USB_DISCONNECTED
                && mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION)
                    .equals(RFIDControlActivity.OpenOption.WIRED.toString(), ignoreCase = true)
            ) {
                Log.d(TAG, "USB_DISCONNECTED")
                Log.d(TAG, if (mIsReadStart) "mIsReadStart : true" else "mIsReadStart : false")
                RFIDControlActivity.USB_DETACHED_FLAG = true
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
                mIsReadStart = false
                val connectionIntent =
                    Intent(this@RFIDDemoActivity, RFIDControlActivity::class.java)
                startActivity(connectionIntent)
            } else if (state == RFIDConst.DeviceState.POWER_OFF
                && mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION)
                    .equals(RFIDControlActivity.OpenOption.WIRED.toString(), ignoreCase = true)
            ) {
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
                mIsReadStart = false
                RFIDControlActivity.POWER_OFF_FLAG = true
                val intent = Intent(this@RFIDDemoActivity, RFIDControlActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun callbackTimerCancel() {
        try {
            mCallbackCheckTimer.cancel()
            Log.d(TAG, "CallbackTimer Canceled")
        } catch (e: Exception) {
        }
    }

    private val runnable = Runnable {
        if (mAutoScanStart) {
            Log.d(TAG, "CallbackTimer Started")
            mCallbackCheckTimer.start()
            Log.d(TAG, "--- handler start ")
            mRfidMgr!!.StartInventory()
        }
    }

    private fun triggerScanStart() {
        mIsSaveLog = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
        if (mIsSaveLog) {
            val currentDate = mSimpleDataFormat!!.format(Date())
            val strStart = "Start Scan : $currentDate"
            logToFile(strStart)
            mHandler!!.sendEmptyMessage(HANDLER_MSG_SAVE_LOG)
        }
        val isAutoScan = mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE)
        if (isAutoScan) {
            mAutoScanStart = true
            val interval = mPrefUtil!!.getIntPreference(PreferenceUtil.KEY_SCAN_AUTO_INTERVAL)
            mAutoScanInterval = (interval + 1) * 1000
            Log.d(TAG, "autoScanInterval$mAutoScanInterval")
        }
    }

    private fun triggerScanStop() {
        if (mIsSaveLog) {
            mUtil!!.startFileOnlyMediaScan(mFile!!.parent)
        }
    }

    private val mOnCheckboxListener = View.OnClickListener { v ->
        if (v.id == R.id.checkbox_auto_scan) {
            if (binding.appBarDemo.contentDemo.checkboxAutoScan.isChecked) {
                binding.appBarDemo.contentDemo.checkboxContinuous.isChecked = false
                mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE
                mRfidMgr!!.SetOperationMode(mOperationMode)
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, true)
            } else {
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false)
            }
            Log.e(
                "checkchange_autoscan",
                if (mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE) "single" else "continuous"
            )
        } else if (v.id == R.id.checkbox_continuous) {
            if (binding.appBarDemo.contentDemo.checkboxContinuous.isChecked) {
                binding.appBarDemo.contentDemo.checkboxAutoScan.isChecked = false
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false)
                mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS
                mAutoScanStart = false
            } else {
                mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE
            }
            mRfidMgr!!.SetOperationMode(mOperationMode)
            Log.e(
                "checkchange_continuous",
                if (mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE) "single" else "continuous"
            )
        } else if (v.id == R.id.checkbox_save_log) {
            if (binding.appBarDemo.contentDemo.checkboxSaveLog.isChecked) {
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, true)
            } else {
                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
            }
        } else if (v.id == R.id.checkbox_beep_sound) {
            mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, true)
            if (binding.appBarDemo.contentDemo.checkboxBeepSound.isChecked) {
                val asyncSetVolume = AsyncSetVolume()
                asyncSetVolume.execute(
                    mPrefUtil!!.getIntPreference(
                        PreferenceUtil.KEY_BEEP_SOUND,
                        RFIDConst.DeviceConfig.BUZZER_HIGH
                    )
                )
            } else {
                val asyncSetVolume = AsyncSetVolume()
                asyncSetVolume.execute(RFIDConst.DeviceConfig.BUZZER_MUTE)
            }
        }
    }
    private val mOnClickListener = View.OnClickListener { v ->
        if (v.id == R.id.btn_start_inventory) {
            /* Stop */
            if (mIsReadStart || mAutoScanStart) {
                //stopScan();
                stopScanCompletable()
                mIsReadStart = false
                val asyncStop = AsyncStop()
                asyncStop.execute()
                //mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_IS_READ_TEMP, false);
            } else {
                val mode = mRfidMgr!!.GetTriggerMode()
                Log.d("mode", "trigger mode : $mode")
                when (mode) {
                    SCAN_MODE -> if (!isPause) {
                        Log.d(TAG, "RFID Demo is currently paused")
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.scanner_mode),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    RFID_MODE -> {
                        mIsReadStart = true
                        startScanCompletable()
                    }
                    else -> {
                        mIsReadStart = true
                        startScanCompletable()
                    }
                }
            }
        } else if (v.id == R.id.btn_write_tag) {
            val intent = Intent(this@RFIDDemoActivity, DialogWriteTag::class.java)
            startActivityForResult(intent, REQUEST_WRITE_TAG)
            //startWriteTag();
        } else if (v.id == R.id.btn_read_tag) {
            startReadTag()
        }
    }

    private class LogSaveHandler(activity: RFIDDemoActivity) : Handler() {
        private val weakReference: WeakReference<RFIDDemoActivity>
        override fun handleMessage(msg: Message) {
            val rfidDemoActivity = weakReference.get()
            rfidDemoActivity!!.handleMessage(msg)
        }

        init {
            weakReference = WeakReference(activity)
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            HANDLER_MSG_SAVE_LOG -> if (mIsReadStart && mIsSaveLog) {
                Log.d(TAG, "MSG_SAVE_LOG")
                /* Save log every 10 seconds to check battery info */saveLog(getBatteryInfo(true))
                mHandler!!.sendEmptyMessageDelayed(HANDLER_MSG_SAVE_LOG, (10 * 1000).toLong())
            }
        }
    }

    private fun removeHandlerMessage() {
        mHandler?.removeCallbacksAndMessages(null) ?: Log.d(TAG, "Handler is null")
    }

    internal inner class AsyncGetVolume : AsyncTask<Void?, Void?, Boolean>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, true)
        }

        override fun doInBackground(vararg voids: Void?): Boolean {
            if (mRfidMgr!!.IsOpened()) {
                var count = 0
                do {
                    mVolumeValue = mRfidMgr!!.GetBuzzerVol()
                    Log.e("AsyncGetVolume", "mVolume === $mVolumeValue")
                    if (mVolumeValue > 0) {
                        if (mVolumeValue == 0 || mVolumeValue == 1 || mVolumeValue == 2) {
                            break
                        }
                    } else {
                        count++
                    }
                    if (count > 2) break
                    try {
                        Thread.sleep(100)
                    } catch (e: Exception) {
                    }
                } while (true)
            }
            return true
        }

        override fun onPostExecute(isSuccess: Boolean) {
            if (isSuccess) {
                initState()
            } else {
                Log.d("FailMessage", "GetBuzzerVol Failed")
            }
            mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, false)
            super.onPostExecute(isSuccess)
        }
    }

    internal inner class AsyncSetVolume : AsyncTask<Int?, Void?, Boolean>() {
        override fun doInBackground(vararg integers: Int?): Boolean {
            if (mRfidMgr!!.IsOpened()) {
                do {
                    val result = mRfidMgr!!.SetBuzzerVol(integers[0]!!)
                    Log.e("buzzerVolumeResult=====", "" + result)
                    if (result != -1 && result != -100) {
                        break
                    }
                } while (true)
            }
            return true
        }

        override fun onPostExecute(isSuccess: Boolean) {
            mUtil!!.showProgress(mProgress, this@RFIDDemoActivity, false)
            super.onPostExecute(isSuccess)
        }
    }

    internal inner class AsyncStop : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg voids: Void?): Void? {
            Log.d(TAG, "stop9")
            mRfidMgr!!.Stop()
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (mIsSaveLog) {
                removeHandlerMessage()
                mUtil!!.startFileOnlyMediaScan(mFile!!.parent)
                saveLog(getBatteryInfo(false))
            }
        }
    }

    internal inner class AsyncStart : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg voids: Void?): Void? {
            mRfidMgr!!.StartInventory()
            return null
        }
    }

    /* To detect status bar touched */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val Y = event.y.toInt()
        if (Y < 400) {
            onWindowFocusChanged(true)
        }
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("Focus debug", "Focus changed !")
        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !")
            if (mIsReadStart || mAutoScanStart) {
                Log.e(TAG, "Stop RFID Reading")
                mIsReadStart = false
                mAutoScanStart = false
                stopScanCompletable()
                mRfidMgr!!.Stop()
                Log.d("stop", "stop10")
            }
        }
    }

    companion object {
        const val D = "Debug"
        private const val REQUEST_BLUETOOTH = 0x1004
        private const val REQUEST_WRITE_TAG = 0x1005
        private const val REQUEST_DETECT_ACTIVITY = 0x1006
        private const val REQUEST_REGISTER_ACTIVITY = 0x1007
        private const val HANDLER_MSG_SAVE_LOG = 0x1001
        private const val TAG = "RFIDDemoActivity"
        private const val RFID_MODE = 0
        private const val SCAN_MODE = 1
        private const val RFID_CONTROL_FOLDER = "RFIDControl"
        private const val SAVED_LOG_FOLDER = "RFIDLogs"
        private const val SAVED_LOG_FILE = "RFIDLogFile"
        private const val SAVED_CSV_FILE = "RFID"
        var mAutoScanHandler: Handler = object : Handler() {
        }
    }
}