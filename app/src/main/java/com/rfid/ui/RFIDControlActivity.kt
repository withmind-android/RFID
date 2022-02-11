package com.rfid.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import com.jakewharton.rxbinding4.view.clicks
import com.rfid.R
//import com.rfid.RFIDApplication.NotifyDataCallbacks
import com.rfid.databinding.ActivityRfidControlBinding
import com.rfid.ui.base.BaseActivityK
import com.rfid.ui.rfiddemo.RFIDDemoActivity
import com.rfid.util.Constants
import com.rfid.util.PreferenceUtil
import com.rfid.util.Utils
//import device.common.DevInfoIndex
//import device.common.rfid.RFIDConst
//import device.common.rfid.RecvPacket
//import device.sdk.Information
//import device.sdk.RFIDManager
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class RFIDControlActivity : BaseActivityK<ActivityRfidControlBinding>(R.layout.activity_rfid_control) {
    private val mDrawer: DrawerLayout? = null
    private var mProgress: ProgressDialog? = null
    private var mUtil: Utils? = null
    private var mPrefUtil: PreferenceUtil? = null
//    private var mBluetoothAdapter: BluetoothAdapter? = null
//    private var mRfidMgr: RFIDManager? = null
    private var mMacAddress: String? = null
    private var mDeviceName: String? = null
    private val mIsDrawerOpen = false
    private var isProgress = false
    private var isPause = false
    private val mHandler = ConnectionCheckHandler(this)


    enum class OpenOption {
        BLUETOOTH, WIRED, UART, UNKNOWN
    }

    override fun init() {
        mUtil = Utils(this)
        mPrefUtil = PreferenceUtil(applicationContext)
        setFinishOnTouchOutside(false)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mProgress = ProgressDialog(this@RFIDControlActivity)
//        mRfidMgr = RFIDManager.getInstance()
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //init value
        if (mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_FIRST_START, true)) {
            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false)
//            mRfidMgr!!.SetResultType(RFIDConst.ResultType.RFID_RESULT_COPYPASTE)
            mPrefUtil!!.putIntPreference(
                PreferenceUtil.KEY_RESULT_TYPE,
//                RFIDConst.ResultType.RFID_RESULT_COPYPASTE
                2
            )
            Log.d(TAG, "First Start App")
        }
        mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_FIRST_START, false)
        createFolder()
//        initOpenOption(checkDeviceInfo())
        POWER_OFF_FLAG = false
        USB_DETACHED_FLAG = false
        // Bluetooth 기능 삭제
//        registerBTStateReceiver()
//        registerParingReceiver()
//        bluetoothOn()
        onClicks()
    }

//    private fun registerBTStateReceiver() {
//        try {
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
//            registerReceiver(mBluetoothOffReceiver, intentFilter)
//        } catch (e: Exception) {
//        }
//    }

//    private fun unregisterBTStateReceiver() {
//        try {
//            unregisterReceiver(mBluetoothOffReceiver)
//        } catch (e: Exception) {
//        }
//    }

//    private fun registerParingReceiver() {
//        try {
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
//            registerReceiver(mParingReceiver, intentFilter)
//        } catch (e: Exception) {
//        }
//    }
//
//    private fun unregisterParingReceiver() {
//        try {
//            unregisterReceiver(mParingReceiver)
//        } catch (e: Exception) {
//        }
//    }
//
//    private fun setSwitchChanged(isEnable: Boolean) {
//        //SWITCH_CHANGED_FLAG = true;
//        binding.appBarMain.main.apply {
//            switchReadConnect.setOnCheckedChangeListener(null)
//            switchReadConnect.isChecked = isEnable
//            switchReadConnect.setOnCheckedChangeListener(mOnSwitchChangedListener)
//        }
//    }

    override fun onStart() {
        Log.d("RFIDControlActivity", "OnStart!!!")
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "RFIDControlActivity onResume")
        isPause = false
//        getRFIDApplication().setNotifyDataCallback(mDataCallbacks)
//        initState()
    }

//    private fun checkDeviceInfo(): Array<String> {
//        val information = Information.getInstance()
//        try {
//            val majorNum = information.majorNumber
//            return if (majorNum == DevInfoIndex.PM85_MAJOR) {
//                resources.getStringArray(R.array.pm85_option)
//            } else if (majorNum == DevInfoIndex.PM90_MAJOR) {
//                resources.getStringArray(R.array.pm90_option)
//            } else if (majorNum == DevInfoIndex.PM30_MAJOR) {
//                resources.getStringArray(R.array.pm30_option)
//            } else if (majorNum == DevInfoIndex.PM500_MAJOR) {
//                resources.getStringArray(R.array.pm550_option)
//            } else if (majorNum == DevInfoIndex.PM75_MAJOR) {
//                resources.getStringArray(R.array.pm75_option)
//            } else {
//                resources.getStringArray(R.array.not_support)
//            }
//        } catch (e: RemoteException) {
//            e.printStackTrace()
//        }
//        return resources.getStringArray(R.array.not_support)
//    }

//    private fun initOpenOption(selectedItem: Array<String>) {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, selectedItem)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        /* Default mode of PM85 is Bluetooth mode */if (mUtil!!.device != DevInfoIndex.PM30_MAJOR && mUtil!!.device != DevInfoIndex.PM90_MAJOR && mUtil!!.device != DevInfoIndex.PM75_MAJOR && !mRfidMgr!!.IsOpened()) {
//            setSavedOption(OpenOption.BLUETOOTH)
//        }
//
//        /* Default mode of PM30 is USB mode */if (mUtil!!.device == DevInfoIndex.PM30_MAJOR
//            && !mRfidMgr!!.IsOpened()
//        ) {
//            setSavedOption(OpenOption.WIRED)
//        }
//
//        /*  Default mode of PM75 is UART mode*/if ((mUtil!!.device == DevInfoIndex.PM75_MAJOR
//                    || mUtil!!.device == DevInfoIndex.PM90_MAJOR)
//            && !mRfidMgr!!.IsOpened()
//        ) {
//            setSavedOption(OpenOption.UART)
//        }
//    }

    private val savedOption: String
        private get() = mPrefUtil!!.getStringPreference(
            PreferenceUtil.KEY_OPEN_OPTION,
            mUtil!!.defaultOption
        )

    private fun setSavedOption(openOption: OpenOption) {
        mPrefUtil!!.putStringPrefrence(PreferenceUtil.KEY_OPEN_OPTION, openOption.toString())
    }

    private fun createFolder(): Boolean {
        Log.d(TAG, "created folder")
        if (!mUtil!!.createFolder(Utils.RFID_CONTROL_FOLDER)) return false
        if (!mUtil!!.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_JSON_FOLDER)) return false
        if (!mUtil!!.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_RFU_FOLDER)) return false
        return if (!mUtil!!.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_CSV_FOLDER)) false else true
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if (mIsDrawerOpen) {
            mDrawer!!.closeDrawers()
        } else {
            setResult(RESULT_CANCELED)
            finish()
        }
        //        ActivityCompat.finishAffinity(this);
        super.onBackPressed()
    }

//    private fun initState() {
//        runOnUiThread {
//            binding.appBarMain.main.linearConnect.visibility = View.VISIBLE
//            binding.appBarMain.main.relSearchReader.visibility = View.VISIBLE
//            val macAddress = mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR)
//            val deviceName = mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_NAME)
//            Log.d(TAG, if (USB_DETACHED_FLAG) "usb disconnected" else "usb connected")
//            if (mRfidMgr!!.IsOpened()) {
//                Log.d(TAG, "RFID OPENED")
//
////                    /* When host device enters suspend mode, it cannot get callback
////                       When user changed openOption after detached,
////                       change openOption to Wired automatically.
////                    */
////                    if(USB_DETACHED_FLAG)
////                    {
////                        if(!getSavedOption().equals(OpenOption.WIRED.toString()))
////                        {
////                            mSpOpenOption.setSelection(INDEX_WIRED);
////                            setOpenOptionView(OpenOption.WIRED.toString());
////                        }
////                    }
//                setSwitchChanged(true)
//
//                /* Even if RF300 is power off RFIDManager.IsOpened maintain true to keep performing after power on.
//                           so,check if RF300 is currently power off.
//                         */if (POWER_OFF_FLAG) binding.appBarMain.main.switchReadConnect.isChecked = false
//
////                    Log.d(TAG, USB_DETACHED_FLAG ? "USB_DISCONNECTED_FLAG : true" : "USB_DISCONNECTED_FLAG : false");
//                Log.d(
//                    TAG,
//                    if (POWER_OFF_FLAG) "POWER_OFF_FLAG : true" else "POWER_OFF_FLAG : false"
//                )
//                if (mPrefUtil!!.getStringPreference(
//                        PreferenceUtil.KEY_OPEN_OPTION,
//                        mUtil!!.defaultOption
//                    ).equals(
//                        OpenOption.BLUETOOTH.toString(), ignoreCase = true
//                    )
//                ) {
//                    //startConnService();
//                    if (macAddress == null || macAddress.isEmpty() || deviceName == null || deviceName.isEmpty()) {
//                        val asyncDeviceInfo = AsyncDeviceInfo()
//                        asyncDeviceInfo.execute()
//                    } else {
//                        binding.appBarMain.main.textRfidName.text = deviceName
//                    }
//                }
//            } else {
//                Log.d(TAG, "RFID NOT OPENED")
//                setSwitchChanged(false)
//                binding.appBarMain.main.textRfidName.text = ""
//            }
//
//            /* [#18295] When host device enters suspend mode, it cannot get callback
//                     * so if RFIDManager.IsOpened is true, it's assumed currently not detached.*/USB_DETACHED_FLAG =
//            false
//        }
//    }

//    private fun bluetoothOn() {
//        Log.d(TAG, "bluetoothOn")
//        if (!mBluetoothAdapter!!.isEnabled
//            && mPrefUtil!!.getStringPreference(
//                PreferenceUtil.KEY_OPEN_OPTION,
//                mUtil!!.defaultOption
//            ).equals(
//                OpenOption.BLUETOOTH.toString(), ignoreCase = true
//            )
//        ) {
//            setSwitchChanged(false)
//            binding.appBarMain.main.textRfidName.text = ""
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH)
//        }
//    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        //Save open option
//        if (!mRfidMgr!!.IsOpened()) {
//            if (mUtil!!.device == DevInfoIndex.PM30_MAJOR) setSavedOption(OpenOption.WIRED) else if (mUtil!!.device == DevInfoIndex.PM75_MAJOR
//                || mUtil!!.device == DevInfoIndex.PM75_MAJOR
//            ) setSavedOption(OpenOption.UART)
//        }
        // Bluetooth 기능 삭제
//        unregisterBTStateReceiver()
//        unregisterParingReceiver()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu, this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.control_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_CANCELED)
            Log.d("finish()", "finishing!!!!")
            finish()
            return true
        } else if (item.itemId == R.id.action_setting) {
            openSetting()
            //openInfo();
            //writeTagData();
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSetting() {
        val intent = Intent()
        intent.setClass(applicationContext, SettingActivity::class.java)
        startActivityForResult(intent, REQUEST_SETTING)
    }

    private fun openInfo() {
        val alert = AlertDialog.Builder(this)
        alert.setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
        alert.setMessage(getString(R.string.app_name) + " v" + getInfo())
        alert.show()
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
        } else if (requestCode == REQUEST_SETTING) {
            if (resultCode == RESULT_OK) {
                // 로그아웃
                setResult(RESULT_OK)
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sleep(mills: Int) {
        try {
            Thread.sleep(mills.toLong())
        } catch (e: InterruptedException) {
        }
    }

//    fun requestRfidConnection(address: String, name: String?) {
//        val builder = AlertDialog.Builder(this@RFIDControlActivity)
//        builder.setTitle(R.string.connect_dlg_title)
//        builder.setMessage(R.string.connect_dlg_msg)
//        builder.setCancelable(false)
//        builder.setOnKeyListener { dialog, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                dialog.dismiss()
//                finish()
//            }
//            false
//        }
//        builder.setPositiveButton(R.string.connect) { dialog, which ->
//            mRfidMgr!!.ConnectBTDevice(address, name)
//            Log.d(TAG, "ConnectBTDevice")
//            val connMsg = getString(R.string.connect_request_msg) + address
//            mUtil!!.showProgress(mProgress, this@RFIDControlActivity, connMsg, true)
//            mHandler.sendEmptyMessageDelayed(REQUEST_CONNECT, 10000)
//        }
//        builder.setNegativeButton(R.string.cancel) { dialog, which -> setSwitchChanged(false) }
//        val alert = builder.create()
//        alert.setCancelable(false)
//        alert.show()
//    }

//    private fun performSwitchClicked() {
//        /* Switch On */
//        if (binding.appBarMain.main.switchReadConnect.isChecked) {
//            /* Open option : Bluetooth */
//            if (savedOption.equals(OpenOption.BLUETOOTH.toString(), ignoreCase = true)
////                && !mRfidMgr!!.IsOpened()
//            ) {
//                Log.d(TAG, "Switch on, Open option : Bluetooth")
//                val macAddr = mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR)
//                if (macAddr == null) {
//                    Log.d(TAG, "null")
//                }
//                if (macAddr == null || !BluetoothAdapter.checkBluetoothAddress(macAddr)) {
//                    Log.d(TAG, "mac address is null")
//                    setSwitchChanged(false)
//                    return
//                }
//                val rfDevice = mBluetoothAdapter!!.getRemoteDevice(macAddr)
//                var already_bonded_flag = false
//                val pairedDevices = mBluetoothAdapter!!.bondedDevices
//                val pairedDeviceCount = pairedDevices.size
//                if (pairedDeviceCount > 0) {
//                    for (bonded_device in pairedDevices) {
//                        if (macAddr == bonded_device.address) {
//                            already_bonded_flag = true
//                        }
//                    }
//                }
//                if (!already_bonded_flag) {
//                    Log.d(TAG, "222")
//                    mPrefUtil!!.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_MACADDR, null)
//                    setSwitchChanged(false)
//                    return
//                }
////                if (rfDevice != null) {
////                    requestRfidConnection(macAddr, rfDevice.name)
////                }
//            } else if (savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
////                && !mRfidMgr!!.IsOpened()
//            ) {
//                Log.d(TAG, "Switch On, Open option : Wired")
//                USB_ATTACHED_TEMP_FLAG = false
//                //unRegisterConnReceiver();
//                val async = AsyncInit()
//                async.execute()
//            } else if (savedOption.equals(OpenOption.UART.toString(), ignoreCase = true)
////                && !mRfidMgr!!.IsOpened()
//            ) {
//                Log.d(TAG, "Switch On, Open option : Uart")
//                val async = AsyncInit()
//                async.execute()
//            }
//        } else {
//            /* Open option : Bluetooth */
//            if (savedOption.equals(OpenOption.BLUETOOTH.toString(), ignoreCase = true)
////                && mRfidMgr!!.IsOpened()
//            ) {
//                Log.d(TAG, "Switch off, Open option : Bluetooth")
//                /* Stop service When BT is disconnected by connection switch otherwise RFID closes twice. */
//                //stopConnService();
//                val asyncClose = AsyncClose()
//                asyncClose.execute(OpenOption.BLUETOOTH)
//                mHandler.sendEmptyMessageDelayed(REQUEST_DISCONNECT, 7000)
//            } else if (savedOption.equals(
//                    OpenOption.WIRED.toString(),
//                    ignoreCase = true
//                ) // && mRfidMgr!!.IsOpened()
//                && !POWER_OFF_FLAG
//                && !USB_DETACHED_FLAG
//            ) {
//                Log.d(TAG, "Switch Off, Open option : Wired")
//                val asyncClose = AsyncClose()
//                asyncClose.execute(OpenOption.WIRED)
//            } else if (savedOption.equals(OpenOption.UART.toString(), ignoreCase = true)
////                && mRfidMgr!!.IsOpened()
//            ) {
//                val asyncClose = AsyncClose()
//                asyncClose.execute(OpenOption.UART)
//            }
//        }
//    }

//    var mDataCallbacks: NotifyDataCallbacks = object : NotifyDataCallbacks {
//        override fun notifyDataPacket(recvPacket: RecvPacket) {
//            Log.d(TAG, "notifyDataPacket : " + recvPacket.RecvString)
//        }
//
//        override fun notifyChangedState(state: Int) {
//            Log.d(TAG, "notifyChangedState : $state")
//
//            /* Only for BLUETOOTH */if (state == RFIDConst.DeviceState.BT_CONNECTED && savedOption.equals(
//                    OpenOption.BLUETOOTH.toString(), ignoreCase = true
//                )
//                && !mRfidMgr!!.IsOpened()
//            ) {
//                POWER_OFF_FLAG = false
//                Log.d(TAG, "BT_CONNECTED")
//                val async = AsyncInit()
//                async.execute()
//            } else if (state == RFIDConst.DeviceState.BT_DISCONNECTED
//                && savedOption.equals(OpenOption.BLUETOOTH.toString(), ignoreCase = true)
//            ) {
//                Log.d(TAG, "BT_DISCONNECTED")
//                binding.appBarMain.main.textRfidName.text = ""
//                setSwitchChanged(false)
//                mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
//                mRfidMgr!!.Close()
//                Log.d(TAG, "close11")
//                bluetoothOn()
//                Log.d(TAG, "bluetoothOn 2")
//            } else if (state == RFIDConst.DeviceState.USB_CONNECTED) {
//                Log.d(TAG, "USB_CONNECTED")
//                USB_DETACHED_FLAG = false
//                Log.d(TAG, if (USB_DETACHED_FLAG) "usb disconnected" else "usb connected")
//                USB_ATTACHED_TEMP_FLAG = true
//                Log.d(TAG, if (USB_ATTACHED_TEMP_FLAG) "usb attached" else "usb not attached")
//            } else if (state == RFIDConst.DeviceState.USB_DISCONNECTED) {
//                Log.d(TAG, "USB_DISCONNECTED")
//                if (binding.appBarMain.main.switchReadConnect.isChecked && getRFIDApplication().mIsSleep) Toast.makeText(
//                    applicationContext, getString(R.string.reader_detached), Toast.LENGTH_SHORT
//                ).show()
//                USB_DETACHED_FLAG = true
//                setSwitchChanged(false)
//            } else if (state == RFIDConst.DeviceState.USB_OPENED) {
//                Log.d(TAG, "USB_OPENED")
//                Log.d(TAG, if (USB_ATTACHED_TEMP_FLAG) "usb attached" else "usb not attached")
//
////                /* When user changed openOption after detached,
////                   change openOption to Wired automatically.
////                 */
////                if(!getSavedOption().equals(OpenOption.WIRED.toString()))
////                {
////                    mSpOpenOption.setSelection(INDEX_WIRED);
////                    setOpenOptionView(OpenOption.WIRED.toString());
////                }
//                if (USB_ATTACHED_TEMP_FLAG && savedOption == OpenOption.WIRED.toString()) {
//                    setSwitchChanged(true)
//                    USB_ATTACHED_TEMP_FLAG = false
//                }
//                isProgress = true
//                val asyncClose = AsyncClose()
//                asyncClose.execute(OpenOption.WIRED)
//            } else if (state == RFIDConst.DeviceState.USB_CLOSED) {
//                Log.d(TAG, "USB_CLOSED")
//                if (savedOption.equals(
//                        OpenOption.WIRED.toString(),
//                        ignoreCase = true
//                    )
//                ) setSwitchChanged(false)
//            } else if (state == RFIDConst.DeviceState.POWER_OFF
//                && savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
//            ) {
//                Log.d(TAG, "POWER_OFF")
//                POWER_OFF_FLAG = true
//                setSwitchChanged(false)
//            } else if (state == RFIDConst.DeviceState.TRIGGER_RFID_KEYDOWN) {
//                val currentType = mPrefUtil!!.getIntPreference(
//                    PreferenceUtil.KEY_RESULT_TYPE,
//                    RFIDConst.ResultType.RFID_RESULT_COPYPASTE
//                )
//                Log.d(TAG, "result type : $currentType")
//                if (currentType == RFIDConst.ResultType.RFID_RESULT_CALLBACK
//                    && mRfidMgr!!.IsOpened()
//                ) {
//                    mRfidMgr!!.Stop()
//                    val demoIntent = Intent(applicationContext, RFIDDemoActivity::class.java)
//                    startActivity(demoIntent)
//                }
//            }
//        }
//    }

    private fun onClicks() {
        binding.appBarMain.main.apply {
//            relSearchReader
//                .clicks()
//                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
//                .subscribe({
//                    binding.appBarMain.main.linearConnect
//                    intent.setClass(applicationContext, SearchReaderActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    startActivityForResult(intent, REQUEST_SEARCH_RFIDREADER)
//                }, { it.printStackTrace() })
            detectItem1
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 1)
                    startActivity(intent)
                }, { it.printStackTrace() })
            detectItem2
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 2)
                    startActivity(intent)
                }, { it.printStackTrace() })
            detectItem3
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 3)
                    startActivity(intent)
                }, { it.printStackTrace() })
            detectItem4
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 4)
                    startActivity(intent)
                }, { it.printStackTrace() })
            detectItem5
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 5)
                    startActivity(intent)
                }, { it.printStackTrace() })
            detectItem6
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 6)
                    startActivity(intent)
                }, { it.printStackTrace() })
            btShipment
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    intent.setClass(applicationContext, RFIDDemoActivity::class.java)
                    intent.putExtra("idx", 0)
                    startActivity(intent)
                }, { it.printStackTrace() })
        }
    }

//    private val mOnSwitchChangedListener =
//        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
//            Log.d(TAG, "onCheckChanged")
//            performSwitchClicked()
//        }
//    private val mBluetoothOffReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
//                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
//                if (state == BluetoothAdapter.STATE_OFF) {
////                    if (!mRfidMgr!!.IsOpened()) {
////                        bluetoothOn()
////                        Log.d(TAG, "bluetoothOn 3")
////                    }
//                }
//            }
//        }
//    }

    private class ConnectionCheckHandler(activity: RFIDControlActivity) : Handler() {
        private val weakReference: WeakReference<RFIDControlActivity>
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val rfidControlActivity = weakReference.get()
//            rfidControlActivity!!.checkConnection(msg)
        }

        init {
            weakReference = WeakReference(activity)
        }
    }

//    private fun checkConnection(msg: Message) {
//        when (msg.what) {
//            REQUEST_CONNECT -> if (mRfidMgr != null && !mRfidMgr!!.IsOpened() and !isPause) {
//                mRfidMgr!!.DisconnectBTDevice()
//                mRfidMgr!!.Close()
//                Log.d(TAG, "close8")
//                setSwitchChanged(false)
//                mUtil!!.showProgress(mProgress, this@RFIDControlActivity, false)
//            }
//            REQUEST_DISCONNECT -> {
//                Log.d(TAG, "REQUEST_DISCONNECT")
//                mUtil!!.showProgress(mProgress, this@RFIDControlActivity, false)
//            }
//        }
//    }

//    private val mParingReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            Log.d(TAG, "Paring receiver action : $action")
//            if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST, ignoreCase = true)) {
//                Log.d(TAG, "ACTION_PARING_REQUEST")
//                val macAddress =
//                    mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR, "")
//                val pickedDevice: BluetoothDevice
//                if (!macAddress.isEmpty()) {
//                    pickedDevice = mBluetoothAdapter!!.getRemoteDevice(macAddress)
//                    //                    if(pickedDevice != null)
////                        pickedDevice.setPairingConfirmation(true);
//                }
//            }
//        }
//    }

//    internal inner class AsyncDeviceInfo : AsyncTask<Void?, Void?, Void?>() {
//        override fun doInBackground(vararg voids: Void?): Void? {
//            /* Get MacAddress */
//            mMacAddress = mUtil!!.btMacAddress
//            Log.d(TAG, "macAddress : $mMacAddress")
//            if (mMacAddress!!.isNotEmpty() && mMacAddress != null) mPrefUtil!!.putStringPrefrence(
//                PreferenceUtil.KEY_CONNECT_BT_MACADDR,
//                mMacAddress
//            )
//
//            /* Get Device Name */
//            mDeviceName = mUtil!!.deviceName
//            Log.d(TAG, "Device Name : $mDeviceName")
//            if (mDeviceName!!.isNotEmpty() && mDeviceName != null) mPrefUtil!!.putStringPrefrence(
//                PreferenceUtil.KEY_CONNECT_BT_NAME,
//                mDeviceName
//            )
//            return null
//        }
//
//        override fun onPostExecute(aVoid: Void?) {
//            super.onPostExecute(aVoid)
//            binding.appBarMain.main.textRfidName.text = mDeviceName
//        }
//    }

    internal inner class AsyncInit : AsyncTask<Void?, Void?, Boolean>() {
        override fun onPreExecute() {
            if (savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
                || savedOption.equals(OpenOption.UART.toString(), ignoreCase = true)
            ) {
                mUtil!!.showProgress(
                    mProgress,
                    this@RFIDControlActivity,
                    getString(R.string.rfid_opening),
                    true
                )
            }
            super.onPreExecute()
        }

        override fun doInBackground(vararg voids: Void?): Boolean {
            POWER_OFF_FLAG = false
            Log.d(TAG, if (POWER_OFF_FLAG) "power off" else "power not off ")
            val result: Boolean = false
            var tryCount = 0
            do {
                /* Open for bluetooth */
                if (savedOption.equals(OpenOption.BLUETOOTH.toString(), ignoreCase = true)) {
//                    val isOpen = mRfidMgr!!.Open(RFIDConst.DeviceType.DEVICE_BT)
//                    if (isOpen == RFIDConst.CommandErr.SUCCESS) setSavedOption(OpenOption.BLUETOOTH)
                    setSavedOption(OpenOption.BLUETOOTH)
//                    Log.d(TAG, "Open via Bluetooth : $isOpen")
                    Log.d(TAG, "Open via Bluetooth")
                } else if (savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
//                    && mUtil!!.device == DevInfoIndex.PM30_MAJOR
                ) {
//                    val isOpen = mRfidMgr!!.Open(RFIDConst.DeviceType.DEVICE_USB)
//                    if (isOpen == RFIDConst.CommandErr.SUCCESS) setSavedOption(OpenOption.WIRED)
                    setSavedOption(OpenOption.WIRED)
//                    Log.d(TAG, "Open via USB : $isOpen")
                    Log.d(TAG, "Open via USB")
                } else if (savedOption.equals(OpenOption.UART.toString(), ignoreCase = true)
//                    && (mUtil!!.device == DevInfoIndex.PM75_MAJOR
//                            || mUtil!!.device == DevInfoIndex.PM90_MAJOR)
                ) {
//                    val isOpen = mRfidMgr!!.Open(RFIDConst.DeviceType.DEVICE_UART)
//                    Log.d(TAG, "Open via UART : $isOpen")
//                    if (isOpen == RFIDConst.CommandErr.SUCCESS) setSavedOption(OpenOption.UART)
                    Log.d(TAG, "Open via UART")
                    setSavedOption(OpenOption.UART)
                }
//                if (mRfidMgr!!.IsOpened()) {
//                    Log.d(TAG, "OPENED")
//                    //mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_OPEN_OPTION, getOpenOption().toString());
//                    result = true
//                    break
//                } else {
//                    tryCount++
//                }
//                if (tryCount > 3) {
//                    result = false
//                    break
//                }
//            } while (true)
            } while (false)

            /* Get device info */if (result) {
                var macAddr: String? = ""
                macAddr = mUtil!!.btMacAddress
                Log.d(TAG, "MacAddress : $macAddr")
                if ((macAddr == null || macAddr.isEmpty()) && !savedOption.equals(
                        OpenOption.UART.toString(),
                        ignoreCase = true
                    )
                ) {
                    mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true)
                    return false
                } else {
                    mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false)
                }
                sleep(50)
                mDeviceName = mUtil!!.deviceName
                Log.d(TAG, "Device Name : $mDeviceName")
                if ((mDeviceName == null || mDeviceName!!.isEmpty()) && !savedOption.equals(
                        OpenOption.UART.toString(), ignoreCase = true
                    )
                ) {
                    mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true)
                    return false
                } else {
                    mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false)
                }
                if (macAddr != null && mDeviceName != null) {
                    mPrefUtil!!.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_MACADDR, macAddr)
                    mPrefUtil!!.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_NAME, mDeviceName)
                }

                /* [#18024]KCTM-2000 module's default tag focus value is enable,
                   but, it's reported as disable from RFID service.
                   because RFID service is developed based on DOTR-3000.
                   That's why if the module is KCTM-2000, set enable tag focus value as soon as open RFID service.
                 */if (result && (mDeviceName!!.startsWith(Utils.RF851) || mDeviceName!!.startsWith(
                        Utils.RF300
                    ))
                ) {
                    Log.d(TAG, "This is not RF850. Set tag focus as enable.")
                    tryCount = 0
//                    do {
//                        val tagFocusResult = mRfidMgr!!.SetTagFocus(Utils.ENABLE)
//                        Log.d(TAG, "tagFocusResult : $tagFocusResult")
//                        if (tagFocusResult == RFIDConst.CommandErr.SUCCESS) {
//                            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false)
//                            break
//                        } else tryCount++
//                        if (tryCount > 3) {
//                            Log.d(TAG, "close15")
//                            mRfidMgr!!.Close()
//                            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true)
//                            return false
//                        }
//                        sleep(100)
//                    } while (true)
                }
            }
            return result
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (mPrefUtil!!.getBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false)
                && savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
            ) Toast.makeText(
                applicationContext, getString(R.string.check_mode), Toast.LENGTH_SHORT
            ).show()
//            if (result) {
//                val btName = mPrefUtil!!.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_NAME, "")
//                if (!savedOption.equals(
//                        OpenOption.WIRED.toString(),
//                        ignoreCase = true
//                    )
//                )
//                    binding.appBarMain.main.textRfidName.text = btName
//                setSwitchChanged(true)
//            } else {
////                mRfidMgr!!.DisconnectBTDevice()
//                Log.d(TAG, "disconn4")
////                setSwitchChanged(false)
//            }
            mUtil!!.showProgress(mProgress, this@RFIDControlActivity, false)
        }
    }

    internal inner class AsyncClose : AsyncTask<OpenOption?, Void?, Void?>() {
        override fun onPreExecute() {
            if (!isProgress) mUtil!!.showProgress(
                mProgress,
                this@RFIDControlActivity,
                getString(R.string.disconnecting),
                true
            )
            super.onPreExecute()
        }

        override fun doInBackground(vararg openOptions: OpenOption?): Void? {
            mPrefUtil!!.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false)
//            mRfidMgr!!.Close()
            Log.d(TAG, "close14")
            if (openOptions[0] == OpenOption.BLUETOOTH) {
                Log.d(TAG, "disconnect20")
//                mRfidMgr!!.DisconnectBTDevice()
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
//            binding.appBarMain.main.textRfidName.text = ""
//            setSwitchChanged(false)
            if (savedOption.equals(OpenOption.WIRED.toString(), ignoreCase = true)
                || savedOption.equals(OpenOption.UART.toString(), ignoreCase = true)
            ) mUtil!!.showProgress(mProgress, this@RFIDControlActivity, false)
            isProgress = false
        }
    }

    companion object {
        private const val TAG = "RFIDControlActivity"
        private const val REQUEST_BLUETOOTH = 0x1001
        private const val REQUEST_SEARCH_RFIDREADER = 0x1002
        private const val REQUEST_TAP_TO_PAIR = 0x1003
        private const val REQUEST_SETTING = 0x1004
        private const val REQUEST_CONNECT = 0x1001
        private const val REQUEST_DISCONNECT = 0x1002

        /* In order to prevent invoked RFIDManager.close() */
        var POWER_OFF_FLAG = false
        var USB_DETACHED_FLAG = false

        /* In order to check sequential callback which means same RF300 attached. ( USB_Connected callback > USB_Opened callback ) */
        var USB_ATTACHED_TEMP_FLAG = false
    }
}