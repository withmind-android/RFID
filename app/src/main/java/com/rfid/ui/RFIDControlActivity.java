package com.rfid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.lang.ref.WeakReference;
import java.util.Set;

import device.common.DevInfoIndex;
import device.common.rfid.RFIDConst;
import device.common.rfid.RecvPacket;
import device.sdk.Information;
import device.sdk.RFIDManager;

import com.rfid.R;
import com.rfid.RFIDApplication;
import com.rfid.ui.base.BaseActivity;
import com.rfid.databinding.ActivityRfidControlBinding;
import com.rfid.util.PreferenceUtil;
import com.rfid.util.Utils;

public class RFIDControlActivity extends BaseActivity<ActivityRfidControlBinding> {
    private static int REQUEST_BLUETOOTH = 0x1001;
    private static final int REQUEST_SEARCH_RFIDREADER = 0x1002;
    private static final int REQUEST_TAP_TO_PAIR = 0x1003;
    private static final int REQUEST_SETTING = 0x1004;

    private static final int REQUEST_CONNECT = 0x1001;
    private static final int REQUEST_DISCONNECT = 0x1002;

    private DrawerLayout mDrawer;
    private ProgressDialog mProgress = null;
    private Utils mUtil;
    private PreferenceUtil mPrefUtil;

    private BluetoothAdapter mBluetoothAdapter;
    private RFIDManager mRfidMgr;

    private Switch mSwitchConnState;
    private TextView mTextRfidName;

    private String mMacAddress;
    private String mDeviceName;
    private boolean mIsDrawerOpen = false;

    private LinearLayout mLinearBtDevice;
    private RelativeLayout mRelSearchReader;

    private final String TAG = getClass().getSimpleName();

    /* In order to prevent invoked RFIDManager.close() */
    public static boolean POWER_OFF_FLAG = false;
    public static boolean USB_DETACHED_FLAG = false;

    /* In order to check sequential callback which means same RF300 attached. ( USB_Connected callback > USB_Opened callback ) */
    public static boolean USB_ATTACHED_TEMP_FLAG = false;

    private boolean isProgress = false;
    private boolean isPause = false;

    private ConnectionCheckHandler mHandler = new ConnectionCheckHandler(this);

    public enum OpenOption {
        BLUETOOTH, WIRED, UART, UNKNOWN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        mUtil = new Utils(this);
        mPrefUtil = new PreferenceUtil(getApplicationContext());

        setFinishOnTouchOutside(false);
        onBindView(R.layout.activity_rfid_control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(RFIDControlActivity.this);

        mRfidMgr = RFIDManager.getInstance();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mLinearBtDevice = (LinearLayout) findViewById(R.id.linear_connect);
        mSwitchConnState = (Switch) findViewById(R.id.switch_read_connect);
        mSwitchConnState.setOnCheckedChangeListener(mOnSwitchChangedListener);
        mRelSearchReader = (RelativeLayout) findViewById(R.id.rel_search_reader);
        mRelSearchReader.setOnClickListener(mOnClickListener);

        mTextRfidName = (TextView) findViewById(R.id.text_rfid_name);

        //init value
        if(mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_FIRST_START, true)) {
            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false);
            mRfidMgr.SetResultType(RFIDConst.ResultType.RFID_RESULT_COPYPASTE);
            mPrefUtil.putIntPreference(PreferenceUtil.KEY_RESULT_TYPE, RFIDConst.ResultType.RFID_RESULT_COPYPASTE);
            Log.d(TAG, "First Start App");
        }
        mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_FIRST_START, false);

        createFolder();
        initOpenOption(checkDeviceInfo());

        POWER_OFF_FLAG = false;
        USB_DETACHED_FLAG = false;

        registerBTStateReceiver();
        registerParingReceiver();

        bluetoothOn();
        dataBinding.appBarMain.main.detectItem1.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.detectItem2.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.detectItem3.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.detectItem4.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.detectItem5.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.detectItem6.setOnClickListener(mOnClickListener);
        dataBinding.appBarMain.main.btRegister.setOnClickListener(mOnClickListener);
    }

    private void registerBTStateReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBluetoothOffReceiver, intentFilter);

        } catch(Exception e) {

        }
    }

    private void unregisterBTStateReceiver() {
        try {
            unregisterReceiver(mBluetoothOffReceiver);
        } catch(Exception e) {

        }
    }

    private void registerParingReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            registerReceiver(mParingReceiver, intentFilter);

        } catch(Exception e) {

        }
    }

    private void unregisterParingReceiver() {
        try {
            unregisterReceiver(mParingReceiver);
        } catch(Exception e) {

        }
    }

    private void setSwitchChanged(boolean isEnable) {
        //SWITCH_CHANGED_FLAG = true;
        mSwitchConnState.setOnCheckedChangeListener(null);
        mSwitchConnState.setChecked(isEnable);
        mSwitchConnState.setOnCheckedChangeListener(mOnSwitchChangedListener);
    }

    @Override
    protected void onStart() {
        Log.d("RFIDControlActivity", "OnStart!!!");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RFIDControlActivity onResume");
        isPause = false;
        getRFIDApplication().setNotifyDataCallback(mDataCallbacks);
        initState();
    }

    private String[] checkDeviceInfo() {
        Information information = Information.getInstance();
        try {
            int majorNum = information.getMajorNumber();
            if(majorNum == DevInfoIndex.PM85_MAJOR) {
                return getResources().getStringArray(R.array.pm85_option);
            }
            else if(majorNum == DevInfoIndex.PM90_MAJOR) {
                return getResources().getStringArray(R.array.pm90_option);
            }
            else if(majorNum == DevInfoIndex.PM30_MAJOR) {
                return getResources().getStringArray(R.array.pm30_option);
            }
            else if(majorNum == DevInfoIndex.PM500_MAJOR) {
                return getResources().getStringArray(R.array.pm550_option);
            }
            else if(majorNum == DevInfoIndex.PM75_MAJOR) {
                return getResources().getStringArray(R.array.pm75_option);
            }
            else {
                return getResources().getStringArray(R.array.not_support);
            }
        } catch(RemoteException e) {
            e.printStackTrace();
        }

        return getResources().getStringArray(R.array.not_support);
    }

    private void initOpenOption(String[] selectedItem) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, selectedItem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* Default mode of PM85 is Bluetooth mode */
        if(mUtil.getDevice() != DevInfoIndex.PM30_MAJOR
                && mUtil.getDevice() != DevInfoIndex.PM90_MAJOR
                && mUtil.getDevice() != DevInfoIndex.PM75_MAJOR
                && !mRfidMgr.IsOpened()) {
            setSavedOption(OpenOption.BLUETOOTH);
        }

        /* Default mode of PM30 is USB mode */
        if(mUtil.getDevice() == DevInfoIndex.PM30_MAJOR
                && !mRfidMgr.IsOpened()) {
            setSavedOption(OpenOption.WIRED);
        }

        /*  Default mode of PM75 is UART mode*/
        if((mUtil.getDevice() == DevInfoIndex.PM75_MAJOR
                || mUtil.getDevice() == DevInfoIndex.PM90_MAJOR)
                && !mRfidMgr.IsOpened()) {
            setSavedOption(OpenOption.UART);
        }
    }

    private String getSavedOption() {
        return mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION, mUtil.getDefaultOption());
    }

    private void setSavedOption(OpenOption openOption) {
        mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_OPEN_OPTION, openOption.toString());
    }

    private boolean createFolder() {
        Log.d(TAG, "created folder");

        if(!mUtil.createFolder(Utils.RFID_CONTROL_FOLDER))
            return false;

        if(!mUtil.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_JSON_FOLDER))
            return false;

        if(!mUtil.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_RFU_FOLDER))
            return false;

        if(!mUtil.createFolder(Utils.RFID_CONTROL_FOLDER + "/" + Utils.RFID_CSV_FOLDER))
            return false;

        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (mIsDrawerOpen) {
            mDrawer.closeDrawers();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
//        ActivityCompat.finishAffinity(this);
        super.onBackPressed();
    }

    private void initState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLinearBtDevice.setVisibility(View.VISIBLE);
                mRelSearchReader.setVisibility(View.VISIBLE);

                String macAddress = mPrefUtil.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR);
                String deviceName = mPrefUtil.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_NAME);

                Log.d(TAG, USB_DETACHED_FLAG ? "usb disconnected" : "usb connected");

                if(mRfidMgr.IsOpened()) {
                    Log.d(TAG, "RFID OPENED");

//                    /* When host device enters suspend mode, it cannot get callback
//                       When user changed openOption after detached,
//                       change openOption to Wired automatically.
//                    */
//                    if(USB_DETACHED_FLAG)
//                    {
//                        if(!getSavedOption().equals(OpenOption.WIRED.toString()))
//                        {
//                            mSpOpenOption.setSelection(INDEX_WIRED);
//                            setOpenOptionView(OpenOption.WIRED.toString());
//                        }
//                    }

                    setSwitchChanged(true);

                    /* Even if RF300 is power off RFIDManager.IsOpened maintain true to keep performing after power on.
                       so,check if RF300 is currently power off.
                     */
                    if(POWER_OFF_FLAG)
                        mSwitchConnState.setChecked(false);

//                    Log.d(TAG, USB_DETACHED_FLAG ? "USB_DISCONNECTED_FLAG : true" : "USB_DISCONNECTED_FLAG : false");
                    Log.d(TAG, POWER_OFF_FLAG ? "POWER_OFF_FLAG : true" : "POWER_OFF_FLAG : false");

                    if(mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION, mUtil.getDefaultOption()).equalsIgnoreCase(OpenOption.BLUETOOTH.toString())) {
                        //startConnService();
                        if(macAddress == null || macAddress.isEmpty() || deviceName == null || deviceName.isEmpty()) {
                            AsyncDeviceInfo asyncDeviceInfo = new AsyncDeviceInfo();
                            asyncDeviceInfo.execute();
                        }
                        else {
                            mTextRfidName.setText(deviceName);
                        }
                    }
                }
                else {
                    Log.d(TAG, "RFID NOT OPENED");
                    setSwitchChanged(false);
                    mTextRfidName.setText("");
                }

                /* [#18295] When host device enters suspend mode, it cannot get callback
                 * so if RFIDManager.IsOpened is true, it's assumed currently not detached.*/
                USB_DETACHED_FLAG = false;
            }
        });
    }

    private void bluetoothOn() {
        Log.d(TAG, "bluetoothOn");
        if(!mBluetoothAdapter.isEnabled()
                && mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION, mUtil.getDefaultOption()).equalsIgnoreCase(OpenOption.BLUETOOTH.toString())) {
            setSwitchChanged(false);
            mTextRfidName.setText("");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        isPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        //Save open option
        if(!mRfidMgr.IsOpened()) {
            if(mUtil.getDevice() == DevInfoIndex.PM30_MAJOR)
                setSavedOption(OpenOption.WIRED);
            else if(mUtil.getDevice() == DevInfoIndex.PM75_MAJOR
                    || mUtil.getDevice() == DevInfoIndex.PM75_MAJOR)
                setSavedOption(OpenOption.UART);
        }

        unregisterBTStateReceiver();
        unregisterParingReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.control_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            Log.d("finish()", "finishing!!!!");
            finish();
            return true;
        } else if(item.getItemId() == R.id.action_setting) {
            openSetting();
            //openInfo();
            //writeTagData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSetting() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SettingActivity.class);
        startActivityForResult(intent, REQUEST_SETTING);
    }
    private void openInfo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setMessage(getString(R.string.app_name) + " v" + getInfo());
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_BLUETOOTH) {
            if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_disable), Toast.LENGTH_SHORT).show();
                Log.d("finish()", "finishing!!!!");
                finish();
            }
        } else if (requestCode == REQUEST_SETTING) {
            if (resultCode == RESULT_OK) {
                // 로그아웃
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch(InterruptedException e) {
        }
    }

    public void requestRfidConnection(final String address, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RFIDControlActivity.this);
        builder.setTitle(R.string.connect_dlg_title);
        builder.setMessage(R.string.connect_dlg_msg);
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRfidMgr.ConnectBTDevice(address, name);
                Log.d(TAG, "ConnectBTDevice");
                String connMsg = getString(R.string.connect_request_msg) + address;
                mUtil.showProgress(mProgress, RFIDControlActivity.this, connMsg, true);
                mHandler.sendEmptyMessageDelayed(REQUEST_CONNECT, 10000);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSwitchChanged(false);
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void performSwitchClicked() {
        /* Switch On */
        if(mSwitchConnState.isChecked()) {
            /* Open option : Bluetooth */
            if(getSavedOption().equalsIgnoreCase(OpenOption.BLUETOOTH.toString())
                    && !mRfidMgr.IsOpened()) {
                Log.d(TAG, "Switch on, Open option : Bluetooth");
                String macAddr = mPrefUtil.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR);

                if(macAddr == null) {
                    Log.d(TAG, "null");
                }
                if(macAddr == null || !BluetoothAdapter.checkBluetoothAddress(macAddr)) {
                    Log.d(TAG, "mac address is null");
                    setSwitchChanged(false);
                    return;
                }

                BluetoothDevice rfDevice = mBluetoothAdapter.getRemoteDevice(macAddr);

                boolean already_bonded_flag = false;
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                int pairedDeviceCount = pairedDevices.size();
                if(pairedDeviceCount > 0) {
                    for(BluetoothDevice bonded_device : pairedDevices) {
                        if(macAddr.equals(bonded_device.getAddress())) {
                            already_bonded_flag = true;
                        }
                    }
                }
                if(!already_bonded_flag) {
                    Log.d(TAG, "222");
                    mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_MACADDR, null);
                    setSwitchChanged(false);
                    return;
                }

                if(rfDevice != null) {
                    requestRfidConnection(macAddr, rfDevice.getName());
                }
            }
            /* Open option : Wired */
            else if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString())
                    && !mRfidMgr.IsOpened()) {
                Log.d(TAG, "Switch On, Open option : Wired");
                USB_ATTACHED_TEMP_FLAG = false;
                //unRegisterConnReceiver();
                AsyncInit async = new AsyncInit();
                async.execute();
            }
            /* Open option : UART */
            else if(getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())
                    && !mRfidMgr.IsOpened()) {
                Log.d(TAG, "Switch On, Open option : Uart");
                AsyncInit async = new AsyncInit();
                async.execute();
            }
        }
        /* Switch Off */
        else {
            /* Open option : Bluetooth */
            if(getSavedOption().equalsIgnoreCase(OpenOption.BLUETOOTH.toString())
                    && mRfidMgr.IsOpened()) {
                Log.d(TAG, "Switch off, Open option : Bluetooth");
                /* Stop service When BT is disconnected by connection switch otherwise RFID closes twice. */
                //stopConnService();
                AsyncClose asyncClose = new AsyncClose();
                asyncClose.execute(OpenOption.BLUETOOTH);
                mHandler.sendEmptyMessageDelayed(REQUEST_DISCONNECT, 7000);
            }
            /* Open option : Wired */
            else if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString()) && mRfidMgr.IsOpened()
                    && !POWER_OFF_FLAG
                    && !USB_DETACHED_FLAG) {
                Log.d(TAG, "Switch Off, Open option : Wired");
                AsyncClose asyncClose = new AsyncClose();
                asyncClose.execute(OpenOption.WIRED);
            }
            /* Open option : Uart */
            else if(getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())
                    && mRfidMgr.IsOpened()) {
                AsyncClose asyncClose = new AsyncClose();
                asyncClose.execute(OpenOption.UART);
            }
        }
    }

    RFIDApplication.NotifyDataCallbacks mDataCallbacks = new RFIDApplication.NotifyDataCallbacks() {
        @Override
        public void notifyDataPacket(RecvPacket recvPacket) {
            Log.d(TAG, "notifyDataPacket : " + recvPacket.RecvString);
        }

        @Override
        public void notifyChangedState(int state) {
            Log.d(TAG, "notifyChangedState : " + state);

            /* Only for BLUETOOTH */
            if(state == RFIDConst.DeviceState.BT_CONNECTED
                    && getSavedOption().equalsIgnoreCase(OpenOption.BLUETOOTH.toString())
                    && !mRfidMgr.IsOpened()) {
                POWER_OFF_FLAG = false;
                Log.d(TAG, "BT_CONNECTED");
                AsyncInit async = new AsyncInit();
                async.execute();
            }
            /* Only for BLUETOOTH */
            else if(state == RFIDConst.DeviceState.BT_DISCONNECTED
                    && getSavedOption().equalsIgnoreCase(OpenOption.BLUETOOTH.toString())) {
                Log.d(TAG, "BT_DISCONNECTED");
                mTextRfidName.setText("");
                setSwitchChanged(false);
                mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);

                mRfidMgr.Close();
                Log.d(TAG, "close11");

                bluetoothOn();
                Log.d(TAG, "bluetoothOn 2");
            }
            else if(state == RFIDConst.DeviceState.USB_CONNECTED) {
                Log.d(TAG, "USB_CONNECTED");
                USB_DETACHED_FLAG = false;
                Log.d(TAG, USB_DETACHED_FLAG ? "usb disconnected" : "usb connected");
                USB_ATTACHED_TEMP_FLAG = true;
                Log.d(TAG, USB_ATTACHED_TEMP_FLAG ? "usb attached" : "usb not attached");

            }
            else if(state == RFIDConst.DeviceState.USB_DISCONNECTED) {
                Log.d(TAG, "USB_DISCONNECTED");
                if(mSwitchConnState.isChecked() && !getRFIDApplication().mIsSleep)
                    Toast.makeText(getApplicationContext(), getString(R.string.reader_detached), Toast.LENGTH_SHORT).show();
                USB_DETACHED_FLAG = true;
                setSwitchChanged(false);
            }
            else if(state == RFIDConst.DeviceState.USB_OPENED) {
                Log.d(TAG, "USB_OPENED");
                Log.d(TAG, USB_ATTACHED_TEMP_FLAG ? "usb attached" : "usb not attached");

//                /* When user changed openOption after detached,
//                   change openOption to Wired automatically.
//                 */
//                if(!getSavedOption().equals(OpenOption.WIRED.toString()))
//                {
//                    mSpOpenOption.setSelection(INDEX_WIRED);
//                    setOpenOptionView(OpenOption.WIRED.toString());
//                }

                if(USB_ATTACHED_TEMP_FLAG && getSavedOption().equals(OpenOption.WIRED.toString())) {
                    setSwitchChanged(true);
                    USB_ATTACHED_TEMP_FLAG = false;
                }
                    isProgress = true;
                    AsyncClose asyncClose = new AsyncClose();
                    asyncClose.execute(OpenOption.WIRED);
            }
            else if(state == RFIDConst.DeviceState.USB_CLOSED) {
                Log.d(TAG, "USB_CLOSED");
                if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString()))
                    setSwitchChanged(false);
            }
            else if(state == RFIDConst.DeviceState.POWER_OFF
                    && getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString())) {
                Log.d(TAG, "POWER_OFF");
                POWER_OFF_FLAG = true;
                setSwitchChanged(false);
            }
            else if(state == RFIDConst.DeviceState.TRIGGER_RFID_KEYDOWN) {
                int currentType = mPrefUtil.getIntPreference(PreferenceUtil.KEY_RESULT_TYPE, RFIDConst.ResultType.RFID_RESULT_COPYPASTE);
                Log.d(TAG, "result type : " + currentType);
                if(currentType == RFIDConst.ResultType.RFID_RESULT_CALLBACK
                        && mRfidMgr.IsOpened()) {
                    mRfidMgr.Stop();
                    Intent demoIntent = new Intent(getApplicationContext(), RFIDDemoActivity.class);
                    startActivity(demoIntent);
                }
            }
        }
    };

    private View.OnClickListener mOnClickListener = v -> {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rel_search_reader:
                intent.setClass(getApplicationContext(), SearchReaderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQUEST_SEARCH_RFIDREADER);
                break;
            case R.id.detect_item_1:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 1);
                startActivity(intent);
                break;
            case R.id.detect_item_2:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 2);
                startActivity(intent);
                break;
            case R.id.detect_item_3:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 3);
                startActivity(intent);
                break;
            case R.id.detect_item_4:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 4);
                startActivity(intent);
                break;
            case R.id.detect_item_5:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 5);
                startActivity(intent);
                break;
            case R.id.detect_item_6:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 6);
                startActivity(intent);
                break;
            case R.id.bt_register:
                intent.setClass(getApplicationContext(), RFIDDemoActivity.class);
                intent.putExtra("idx", 0);
                startActivity(intent);
                break;
        }
    };

    private final CompoundButton.OnCheckedChangeListener mOnSwitchChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "onCheckChanged");
            performSwitchClicked();
        }
    };

    private BroadcastReceiver mBluetoothOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_OFF) {
                    if(!mRfidMgr.IsOpened()) {
                        bluetoothOn();
                        Log.d(TAG, "bluetoothOn 3");
                    }
                }
            }
        }
    };

    private static class ConnectionCheckHandler extends Handler {
        private final WeakReference<RFIDControlActivity> weakReference;

        public ConnectionCheckHandler(RFIDControlActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RFIDControlActivity rfidControlActivity = weakReference.get();
            rfidControlActivity.checkConnection(msg);
        }
    }

    private void checkConnection(Message msg) {
        switch(msg.what) {
            case REQUEST_CONNECT:
                if(mRfidMgr != null && !mRfidMgr.IsOpened() & !isPause) {
                    mRfidMgr.DisconnectBTDevice();
                    mRfidMgr.Close();
                    Log.d(TAG, "close8");
                    setSwitchChanged(false);
                    mUtil.showProgress(mProgress, RFIDControlActivity.this, false);
                }
                break;
            case REQUEST_DISCONNECT:
                Log.d(TAG, "REQUEST_DISCONNECT");
                mUtil.showProgress(mProgress,RFIDControlActivity.this, false);
                break;
        }
    }

    private BroadcastReceiver mParingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Paring receiver action : " + action);
            if(action.equalsIgnoreCase(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                Log.d(TAG, "ACTION_PARING_REQUEST");

                String macAddress = mPrefUtil.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_MACADDR, "");
                BluetoothDevice pickedDevice;
                if(!macAddress.isEmpty()) {
                    pickedDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
//                    if(pickedDevice != null)
//                        pickedDevice.setPairingConfirmation(true);
                }
            }
        }
    };

    class AsyncDeviceInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            /* Get MacAddress */
            mMacAddress = mUtil.getBTMacAddress();
            Log.d(TAG, "macAddress : " + mMacAddress);
            if(!mMacAddress.isEmpty() && mMacAddress != null)
                mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_MACADDR, mMacAddress);

            /* Get Device Name */
            mDeviceName = mUtil.getDeviceName();
            Log.d(TAG, "Device Name : " + mDeviceName);
            if(!mDeviceName.isEmpty() && mDeviceName != null)
                mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_NAME, mDeviceName);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTextRfidName.setText(mDeviceName);
        }
    }

    class AsyncInit extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString())
                    || getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())) {
                mUtil.showProgress(mProgress, RFIDControlActivity.this, getString(R.string.rfid_opening), true);
            }
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            POWER_OFF_FLAG = false;
            Log.d(TAG, POWER_OFF_FLAG ? "power off" : "power not off ");

            boolean result;
            int tryCount = 0;

            do {
                /* Open for bluetooth */
                if(getSavedOption().equalsIgnoreCase(OpenOption.BLUETOOTH.toString())) {
                    int isOpen = mRfidMgr.Open(RFIDConst.DeviceType.DEVICE_BT);
                    if(isOpen == RFIDConst.CommandErr.SUCCESS)
                        setSavedOption(OpenOption.BLUETOOTH);
                    Log.d(TAG, "Open via Bluetooth : " + isOpen);
                }
                /* Open for wired on PM30*/
                else if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString())
                        && (mUtil.getDevice() == DevInfoIndex.PM30_MAJOR)) {
                    int isOpen = mRfidMgr.Open(RFIDConst.DeviceType.DEVICE_USB);
                    if(isOpen == RFIDConst.CommandErr.SUCCESS)
                        setSavedOption(OpenOption.WIRED);
                    Log.d(TAG, "Open via USB : " + isOpen);
                }
                /* Open for uart on PM75 */
                else if(getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())
                        && (mUtil.getDevice() == DevInfoIndex.PM75_MAJOR
                        || mUtil.getDevice() == DevInfoIndex.PM90_MAJOR)) {
                    int isOpen = mRfidMgr.Open(RFIDConst.DeviceType.DEVICE_UART);
                    Log.d(TAG, "Open via UART : " + isOpen);
                    if(isOpen == RFIDConst.CommandErr.SUCCESS)
                        setSavedOption(OpenOption.UART);
                }

                if(mRfidMgr.IsOpened()) {
                    Log.d(TAG, "OPENED");
                    //mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_OPEN_OPTION, getOpenOption().toString());
                    result = true;
                    break;
                }
                else {
                    tryCount++;
                }

                if(tryCount > 3) {
                    result = false;
                    break;
                }
            }
            while(true);

            /* Get device info */
            if(result) {
                String macAddr = "";

                macAddr = mUtil.getBTMacAddress();
                Log.d(TAG, "MacAddress : " + macAddr);
                if((macAddr == null || macAddr.isEmpty()) && !getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())) {
                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true);
                    return false;
                }
                else {
                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false);
                }

                sleep(50);

                mDeviceName = mUtil.getDeviceName();
                Log.d(TAG, "Device Name : " + mDeviceName);
                if((mDeviceName == null || mDeviceName.isEmpty()) && !getSavedOption().equalsIgnoreCase(OpenOption.UART.toString())) {
                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true);
                    return false;
                }
                else {
                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false);
                }

                if(macAddr != null && mDeviceName != null) {
                    mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_MACADDR, macAddr);
                    mPrefUtil.putStringPrefrence(PreferenceUtil.KEY_CONNECT_BT_NAME, mDeviceName);
                }

                /* [#18024]KCTM-2000 module's default tag focus value is enable,
                   but, it's reported as disable from RFID service.
                   because RFID service is developed based on DOTR-3000.
                   That's why if the module is KCTM-2000, set enable tag focus value as soon as open RFID service.
                 */
                if(result && (mDeviceName.startsWith(Utils.RF851) || mDeviceName.startsWith(Utils.RF300))) {
                    Log.d(TAG, "This is not RF850. Set tag focus as enable.");
                    tryCount = 0;
                    do {
                        int tagFocusResult = mRfidMgr.SetTagFocus(Utils.ENABLE);
                        Log.d(TAG, "tagFocusResult : " + tagFocusResult);
                        if(tagFocusResult == RFIDConst.CommandErr.SUCCESS) {
                            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false);
                            break;
                        }
                        else
                            tryCount++;

                        if(tryCount > 3) {
                            Log.d(TAG, "close15");
                            mRfidMgr.Close();
                            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, true);
                            return false;
                        }

                        sleep(100);
                    }
                    while(true);
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_OPEN_ERROR, false)
                    && getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString()))
                Toast.makeText(getApplicationContext(), getString(R.string.check_mode), Toast.LENGTH_SHORT).show();

            if(result) {
                String btName = mPrefUtil.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_NAME, "");
                if(!getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString()))
                    mTextRfidName.setText(btName);
                setSwitchChanged(true);
            }
            else {
                    mRfidMgr.DisconnectBTDevice();
                    Log.d(TAG, "disconn4");
                setSwitchChanged(false);
            }
            mUtil.showProgress(mProgress, RFIDControlActivity.this, false);
        }
    }

    class AsyncClose extends AsyncTask<OpenOption, Void, Void> {
        @Override
        protected void onPreExecute() {
            if(!isProgress)
                mUtil.showProgress(mProgress, RFIDControlActivity.this, getString(R.string.disconnecting), true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(RFIDControlActivity.OpenOption... openOptions) {
            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
            mRfidMgr.Close();
            Log.d(TAG, "close14");
            if(openOptions[0] == RFIDControlActivity.OpenOption.BLUETOOTH) {
                Log.d(TAG, "disconnect20");
                mRfidMgr.DisconnectBTDevice();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTextRfidName.setText("");
            setSwitchChanged(false);

            if(getSavedOption().equalsIgnoreCase(OpenOption.WIRED.toString())
                    || getSavedOption().equalsIgnoreCase(OpenOption.UART.toString()))
                mUtil.showProgress(mProgress, RFIDControlActivity.this, false);

            isProgress = false;
        }
    }


}
