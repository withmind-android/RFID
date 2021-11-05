//package com.rfid.ui;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.app.ActivityCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import device.common.rfid.AccessTag;
//import device.common.rfid.ModeOfInvent;
//import device.common.rfid.RFIDConst;
//import device.common.rfid.RecvPacket;
//import device.common.rfid.ReportFormatOfInvent_ext;
//import device.common.rfid.SelConfig;
//import device.sdk.RFIDManager;
//
//import com.rfid.R;
//import com.rfid.RFIDApplication;
//import com.rfid.adapter.RfidRvAdapter;
//import com.rfid.adapter.item.RegisterItem;
//import com.rfid.adapter.item.RfidListItem;
//import com.rfid.ui.base.BaseActivity;
//import com.rfid.util.custom.DialogWriteTag;
//import com.rfid.databinding.ActivityRfidDemoBinding;
//import com.rfid.ui.detect.DetectActivity;
//import com.rfid.ui.register.RegisterActivity;
//import com.rfid.util.PreferenceUtil;
//import com.rfid.util.Utils;
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Completable;
//import io.reactivex.rxjava3.core.Single;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//public class RFIDDemoActivity extends BaseActivity<ActivityRfidDemoBinding> {
//    public static final String D = "Debug";
//    private final String TAG = getClass().getSimpleName();
//    private static int REQUEST_BLUETOOTH = 0x1004;
//    private static int REQUEST_WRITE_TAG = 0x1005;
//    private static int REQUEST_DETECT_ACTIVITY = 0x1006;
//    private static int REQUEST_REGISTER_ACTIVITY = 0x1007;
//    private final int HANDLER_MSG_SAVE_LOG = 0x1001;
//
//    private final int RFID_MODE = 0;
//    private final int SCAN_MODE = 1;
//
//    private final String RFID_CONTROL_FOLDER = "RFIDControl";
//    private final String SAVED_LOG_FOLDER = "RFIDLogs";
//    private final String SAVED_LOG_FILE = "RFIDLogFile";
//    private final String SAVED_CSV_FILE = "RFID";
//    private final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HHmmss_SSS", Locale.getDefault());
//
//    private RFIDManager mRfidMgr;
//    private BluetoothAdapter mBluetoothAdapter;
//    private ModeOfInvent mOperationMode = new ModeOfInvent();
//
//    private RecyclerView mRecycleViewRfid;
//    private RfidRvAdapter mRvAdapter;
//
//    private ArrayList<RfidListItem> mItems;
//    private ArrayList<RegisterItem> registerItems;
//    private HashMap<String, Integer> mHashItems;
//
//    private int mReadCount = 0;
//    private TextView mTextReadCount;
//    private int mTotalCount = 0;
//    private TextView mTextTotalCount;
//
//    private boolean mAutoScanStart = false;
//    private boolean mIsReadStart = false;
//    private int mAutoScanInterval = 0;
//
//    private boolean mIsSaveLog = false;
//    private File mFile;
//    private long mLogStartTime = -1;
//
//    private SimpleDateFormat mSimpleDataFormat;
//    private boolean mIsTemp = false;
//    private String mTempValue = "0";
//    private int mVolumeValue;
//    private int mIdx = 0;
//
//    private ProgressDialog mProgress = null;
//
//    private TextView mTextTemp;
//    private LinearLayout mLinearTemp;
//    private LinearLayout mLinearCheckbox;
//
//    private Utils mUtil;
//    private PreferenceUtil mPrefUtil;
//
//    private CheckBox mCheckboxContinuous;
//    private CheckBox mCheckboxAutoScan;
//    private CheckBox mCheckboxSaveLog;
//    private CheckBox mCheckboxBeepSound;
//    private Button mBtnStartInventory;
//    private Button mBtnWriteTag;
//    private Button mBtnReadTag;
//
//    private boolean isPause = false;
//    private Toast mToast;
//
//    private Menu mOptionMenu;
//    private boolean mIsAsc = true;
//
//    private LogSaveHandler mHandler = new LogSaveHandler(this);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate()");
//        super.onCreate(savedInstanceState);
//
//        mUtil = new Utils(this);
//        mPrefUtil = new PreferenceUtil(getApplicationContext());
//        mProgress = new ProgressDialog(RFIDDemoActivity.this);
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        onBindView(R.layout.activity_rfid_demo);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        mRfidMgr = RFIDManager.getInstance();
//
//        mCheckboxContinuous = findViewById(R.id.checkbox_continuous);
//        mCheckboxAutoScan = findViewById(R.id.checkbox_auto_scan);
//        mCheckboxSaveLog = findViewById(R.id.checkbox_save_log);
//        mCheckboxBeepSound = findViewById(R.id.checkbox_beep_sound);
//        mCheckboxContinuous.setOnClickListener(mOnCheckboxListener);
//        mCheckboxAutoScan.setOnClickListener(mOnCheckboxListener);
//        mCheckboxSaveLog.setOnClickListener(mOnCheckboxListener);
//        mCheckboxBeepSound.setOnClickListener(mOnCheckboxListener);
//        mBtnStartInventory = findViewById(R.id.btn_start_inventory);
//        mBtnWriteTag = findViewById(R.id.btn_write_tag);
//        mBtnReadTag = findViewById(R.id.btn_read_tag);
//        mBtnStartInventory.setOnClickListener(mOnClickListener);
//        mBtnWriteTag.setOnClickListener(mOnClickListener);
//        mBtnReadTag.setOnClickListener(mOnClickListener);
//        mLinearCheckbox = findViewById(R.id.linear_checkbox);
//
//        mItems = new ArrayList<>();
//        registerItems = new ArrayList<>();
//
//        mRecycleViewRfid = (RecyclerView) findViewById(R.id.recy_tag_item);
//        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
//        mRecycleViewRfid.setLayoutManager(mLinearLayoutManager);
//        mRvAdapter = new RfidRvAdapter(mItems);
//        mRecycleViewRfid.setAdapter(mRvAdapter);
//
//        mHashItems = new HashMap<>();
//
//        mReadCount = 0;
//        mTextReadCount = (TextView) findViewById(R.id.text_total_read_count);
//        mTotalCount = 0;
//        mTextTotalCount = (TextView) findViewById(R.id.text_total_count);
//
//        mSimpleDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//
//        mTextTemp = (TextView) findViewById(R.id.text_temperature);
//        mLinearTemp = (LinearLayout) findViewById(R.id.linear_temp);
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        //Check EXTERNAL_STORAGE_PERMISSION
//        if(!checkPermission())
//            requestPermission();
//
//        bluetoothOn();
//
//        Intent getIntent = getIntent();
//        mIdx = getIntent.getIntExtra("idx", 0);
//    }
//
//    private void sendDetectView() {
//        Intent detect = new Intent(this, DetectActivity.class);
//        detect.putExtra("id", mIdx);
//        switch (mIdx) {
//            case 1:
//                detect.putExtra("title", "몰드 검사");
//                break;
//            case 2:
//                detect.putExtra("title", "피복두께 검사");
//                break;
//            case 3:
//                detect.putExtra("title", "매입철물 검사");
//                break;
//            case 4:
//                detect.putExtra("title", "콘크리트 검사");
//                break;
//            case 5:
//                detect.putExtra("title", "탈형강도 검사");
//                break;
//            case 6:
//                detect.putExtra("title", "외관 검사");
//                break;
//        }
//        if (registerItems.size() == 0) {
//            Toast.makeText(this, "다시한번 태그를 확인해주세요.", Toast.LENGTH_SHORT).show();
//        } else {
//            detect.putExtra("list", registerItems);
//            startActivityForResult(detect, REQUEST_DETECT_ACTIVITY);
//        }
//    }
//
//    private void sendRegisterView() {
//        Intent detect = new Intent(this, RegisterActivity.class);
//        if (registerItems.size() == 0) {
//            Toast.makeText(this, "다시한번 태그를 확인해주세요.", Toast.LENGTH_SHORT).show();
//        } else {
//            detect.putExtra("list", registerItems);
//            startActivityForResult(detect, REQUEST_REGISTER_ACTIVITY);
//        }
//    }
//
//    private void checkTempOption() {
//        mIsTemp = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_UPDATE_TEMP, false);
//        ReportFormatOfInvent_ext reportFormatOfInvent_ext = new ReportFormatOfInvent_ext();
//        if(mIsTemp) {
//            Log.d(TAG, "Temp option on");
//            reportFormatOfInvent_ext.temp = 1;
//            mLinearTemp.setVisibility(View.VISIBLE);
//        }
//        else {
//            Log.d(TAG, "Temp option off");
//            reportFormatOfInvent_ext.temp = 0;
//            mLinearTemp.setVisibility(View.GONE);
//        }
//        int result = mRfidMgr.SetInventoryReportFormat_ext(reportFormatOfInvent_ext);
//        if(result == RFIDConst.CommandErr.COMM_ERR) {
//            mTextTemp.setText(getString(R.string.temp_fail));
//        }
//    }
//
//    private void bluetoothOn() {
//        if(!mRfidMgr.IsOpened())
//            rfidConnectDialog();
//
//        if(mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION, mUtil.getDefaultOption())
//                .equalsIgnoreCase(RFIDControlActivity.OpenOption.BLUETOOTH.toString())) {
//            if(!mRfidMgr.IsOpened()) {
//                mRfidMgr.DisconnectBTDevice();
//                Log.d(TAG, "disconnect 11");
//            }
//
//            if(!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
//            }
//        }
//    }
//
//    private void initState() {
//        //Operation Mode Value
//        mRfidMgr.GetOperationMode(mOperationMode);
//        if(mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS) {
//            mCheckboxContinuous.setChecked(true);
//            mCheckboxAutoScan.setChecked(false);
//            mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false);
//        }
//        else {
//            mCheckboxContinuous.setChecked(false);
//        }
//        Log.e("checkchange_init", mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE ?
//                "single" : "continuous");
//
//        //Auto Scan Value
//        boolean isAutoScan = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE);
//        mCheckboxAutoScan.setChecked(isAutoScan);
//        if(isAutoScan) {
//            mCheckboxContinuous.setChecked(false);
//            mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE;
//            mRfidMgr.SetOperationMode(mOperationMode);
//        }
//
//        //Log Save Value
//        boolean isSaveLOG = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG);
//
//        if(isSaveLOG) {
//            mCheckboxSaveLog.setChecked(true);
//        }
//        else {
//            mCheckboxSaveLog.setChecked(false);
//        }
//
//        //Volume Value
//        // Log.e("initState", "mVolume === " + mVolumeValue);
//        if(mVolumeValue == 1 || mVolumeValue == 2) {
//            mPrefUtil.putIntPreference(PreferenceUtil.KEY_BEEP_SOUND, mVolumeValue);
//            mCheckboxBeepSound.setChecked(true);
//        }
//        else if(mVolumeValue == 0) {
//            mCheckboxBeepSound.setChecked(false);
//        }
//    }
//
//    private boolean checkPermission() {
//        int permissionResult = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if(permissionResult == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        return false;
//    }
//
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        try {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
////                ActivityCompat.finishAffinity(this);
//                onBackPressed();
//            }
//        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        mOptionMenu = menu;
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.demo_main, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(mAutoScanStart || mIsReadStart) {
//            mAutoScanStart = false;
//            mIsReadStart = false;
//
//            mRfidMgr.Stop();
//            //stopScan();
//            stopScanCompletable();
//        }
//
//        if(item.getItemId() == android.R.id.home) {
//            setResult(Activity.RESULT_CANCELED);
//            Log.d("finish()", "finishing!!!!");
//            finish();
//            return true;
//        }
////        else if(item.getItemId() == R.id.action_sort) {
////            if(mIsAsc)
////                mIsAsc = false;
////            else
////                mIsAsc = true;
////
////            mItems.clear();
////            Iterator it = sortByValue(mHashItems, mIsAsc).iterator();
////            while(it.hasNext()) {
////                String key = (String) it.next();
////                int value = mHashItems.get(key);
////                RfidListItem listItem = new RfidListItem(key, value);
////                mItems.add(listItem);
////            }
////
////            mRvAdapter.notifyDataSetChanged();
////            //readTagDataTest();
////        } else if(item.getItemId() == R.id.action_delete) {
////            mHashItems.clear();
////            mItems.clear();
////            mReadCount = 0;
////            mTotalCount = 0;
////            mTextReadCount.setText("0");
////            mTextTotalCount.setText("0");
////
////            mRvAdapter.notifyDataSetChanged();
////        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        isPause = false;
//        getRFIDApplication().setNotifyDataCallback(mDataCallbacks);
//        Log.d(TAG, "onResume()");
//        //checkIsOpened(this, mRfidMgr);
//
//        if(mRfidMgr.IsOpened()) {
//            //Change ResultType to Callback
//            mRfidMgr.SetResultType(RFIDConst.ResultType.RFID_RESULT_CALLBACK);
//            mUtil.showProgress(mProgress, RFIDDemoActivity.this, true);
//            AsyncGetVolume asyncGetVolume = new AsyncGetVolume();
//            asyncGetVolume.execute();
//        }
//        checkTempOption();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause()");
//        isPause = true;
//
//        /* To roll-back previous Result Type */
//        if(mRfidMgr.IsOpened()) {
//            int currentType = mPrefUtil.getIntPreference(PreferenceUtil.KEY_RESULT_TYPE,
//                    RFIDConst.ResultType.RFID_RESULT_CALLBACK);
//            if(currentType != RFIDConst.ResultType.RFID_RESULT_CALLBACK) {
//                mRfidMgr.SetResultType(currentType);
//            }
//        }
//
//        if(mIsReadStart || mAutoScanStart) {
//            mIsReadStart = false;
//            mAutoScanStart = false;
//            stopScanCompletable();
//            mRfidMgr.Stop();
//            Log.d(TAG, "stop2");
//        }
//
//        if(mToast != null) {
//            Log.d(TAG, "mToast cancel");
//            mToast.cancel();
//        }
//    }
//
//    private void startWriteTag(String writeData) {
//        Single.create(subscriber -> {
//            Log.d(TAG, "startWriteTag()+++");
//            boolean result;
//            result = changeToSingleMode();
//            mUtil.sleep(1000);
//
////            result = setFilter(getSelect("aaaa"));
////            mUtil.sleep(3000);
//
//            result = writeTag(writeData);
//            mUtil.sleep(1000);
//
//            result = rollbackMode();
//            mUtil.sleep(1000);
//
////            result = setFilter(getDefaultSelect());
//
//            subscriber.onSuccess(true);
//
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(it -> {
//                    Log.d(TAG, "startWriteTag()---");
//                    mUtil.showProgress(mProgress,RFIDDemoActivity.this,  false);
//                }, throwable -> {
//                    Log.d(TAG, "Error startWriteTag()---");
//                    mUtil.showProgress(mProgress,RFIDDemoActivity.this,  false);
//                });
//    }
//
//    private void startReadTag() {
//        Single.create(subscriber -> {
//            Log.d(TAG, "startWriteTag()+++");
//            boolean result;
//            result = changeToSingleMode();
//            mUtil.sleep(1000);
//
//            result = readTag();
//            mUtil.sleep(1000);
//
//            result = rollbackMode();
//            subscriber.onSuccess(true);
//
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(it -> {
//                    Log.d(TAG, "startReadTag()---");
//                }, throwable -> {
//                    Log.d(TAG, "Error startReadTag()---");
//                });
//    }
//
//    /* 한번만 tag write 하기 위해 모드를 싱글로 변경 및 원하는 tag만 write하기 위하여 select inclusion 설정
//         RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE : mode를 single로 변경
//         RFIDConst.RFIDConfig.INVENTORY_SELECT_INCLUSION : Select로 설정된 tag pattern을 가진 tag만 read/write 하도록 설정
//     */
//    private boolean changeToSingleMode() {
//        Log.d(TAG, "changeToSingleMode()+++");
//        ModeOfInvent modeOfInvent = new ModeOfInvent();
//        modeOfInvent.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE;
//        //modeOfInvent.select = RFIDConst.RFIDConfig.INVENTORY_SELECT_INCLUSION;
//        int result = mRfidMgr.SetOperationMode(modeOfInvent);
//        Log.d(TAG, "changeToSingleMode()--- result : " + result);
//        return result == RFIDConst.CommandErr.SUCCESS;
//    }
//
//    /* 기존에 설정되어있던 모드로 되돌림 */
//    private boolean rollbackMode() {
//        Log.d(TAG,"rollbackMode()+++");
//        boolean isContinuous = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_CONTINUOUS_ENABLE, true);
//        Log.d(TAG,isContinuous ? "isContinuous true" : "isContinuous false");
//        ModeOfInvent modeOfInvent = new ModeOfInvent();
//        if(!isContinuous)
//            modeOfInvent.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE;
//        else
//            modeOfInvent.single = RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS;
//        modeOfInvent.select = mPrefUtil.getIntPreference(PreferenceUtil.KEY_SELECT, RFIDConst.RFIDConfig.INVENTORY_SELECT_NONE);
//        int result = mRfidMgr.SetOperationMode(modeOfInvent);
//        Log.d(TAG,"rollbackMode()--- result : " + result);
//        return result == RFIDConst.CommandErr.SUCCESS;
//    }
//
//    /* write를 원하는 tag의 filter 설정 */
//    private boolean setFilter(SelConfig selConfig) {
//        Log.d(TAG, "setFilter+++");
//        int result = mRfidMgr.SetSelMask(selConfig);
//        Log.d(TAG, "setFilter--- result : " + result);
//        return result == RFIDConst.CommandErr.SUCCESS;
//    }
//
//    /*변경하고자하는 tag의 현재 데이터만 write/read 되도록 설정 */
//    private SelConfig getSelect(String tagPattern) {
//        SelConfig selConfig = new SelConfig();
//        selConfig.index = 3;
//        selConfig.memBank = 3;
//        selConfig.action = 1;
//        selConfig.target = 4;
//        selConfig.selectData = tagPattern;
//        selConfig.length = selConfig.selectData.length() * 4;
//        selConfig.offset = 8 * 4;
//
//        return selConfig;
//    }
//
//    /* 설정했던 tag filter를 제거함. 제거해주지 않으면 tag read시 filter에 해당하는 tag만 읽어짐 */
//    private SelConfig getDefaultSelect() {
//        SelConfig selConfig = new SelConfig();
//        selConfig.index = 3;
//        selConfig.memBank = 3;
//        selConfig.action = 1;
//        selConfig.target = 4;
//        selConfig.selectData = "";
//        selConfig.length = selConfig.selectData.length();
//        selConfig.offset = 0;
//
//        return selConfig;
//    }
//
//    /* Tag 데이터를 write */
//    private boolean writeTag(String tagIdToWrite) {
//        Log.d(TAG, "writeTag()+++");
//        AccessTag accessTag = new AccessTag();
//        accessTag.wTagData = tagIdToWrite;
//        Log.d(TAG, "tagData : " + accessTag.wTagData);
//        accessTag.length = accessTag.wTagData.length() / 4; //unit : word
//        if((accessTag.wTagData.length() % 4) != 0)
//            accessTag.length += 1;
//        Log.d(TAG, "length : " + accessTag.length);
//        accessTag.memBank = 3;
//        Log.d(TAG, "membank : " + accessTag.memBank);
//        accessTag.offset = 0;
//        Log.d(TAG, "offset : " + accessTag.offset);
//        accessTag.acsPwd = "0";
//        int result = mRfidMgr.WriteTag(accessTag);
//        Log.d(TAG, "write tag error : " + accessTag.errOp);
//        Log.d(TAG, "writeTag()--- : " + result);
//        return result == RFIDConst.CommandErr.SUCCESS;
//    }
//
//    /* Tag 데이터를 read */
//    private boolean readTag() {
//        Log.d(TAG, "readTag()+++");
//        AccessTag accessTag = new AccessTag();
//        accessTag.length = 1;
//        accessTag.memBank = 3;
//        accessTag.offset = 0;
//        accessTag.acsPwd = "0";
//        int result = mRfidMgr.ReadTag(accessTag);
//        Log.d(TAG, "read tag data hex : " + accessTag.tagId);
//        Log.d(TAG, "readTag()--- : " + result);
//        return result == RFIDConst.CommandErr.SUCCESS;
//    }
//
//    private void startScanCompletable() {
//        Log.d(D, "startScanCompletable");
//        changeStartToStop();
//        Completable.create(subscriber -> {
//            startScan();
//            subscriber.onComplete();
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(() -> {
//                    Log.d(TAG, "Completed start scan");
//                }, throwable -> {
//                    Log.d(TAG, "Error in startScanCompletable");
//                });
//    }
//
//    private void stopScanCompletable() {
//        Log.d(D, "stopScanCompletable");
//        changeStopToStart();
//        Completable.create(subscriber -> {
//            stopScan();
//            subscriber.onComplete();
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(() -> {
//                    Log.d(TAG, "Completed stop scan");
//                }, throwable -> {
//                    Log.d(TAG, "Error in stopScanCompletable");
//                });
//    }
//
//    private void triggerStartScanCompletable() {
//        Log.d(D, "triggerStartScanCompletable");
//        changeStartToStop();
//        Completable.create(subscriber -> {
//            triggerScanStart();
//            subscriber.onComplete();
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(() -> {
//                    Log.d(TAG, "Completed stop scan");
//                }, throwable -> {
//                    Log.d(TAG, "Error in triggerStartScanCompletable");
//                });
//    }
//
//    private void triggerStopScanCompletable() {
//        Log.d(D, "triggerStopScanCompletable");
//        changeStopToStart();
//        Completable.create(subscriber -> {
//            triggerScanStop();
//            subscriber.onComplete();
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(() -> {
//                    Log.d(TAG, "Completed stop scan");
////                    mHashItems.clear();
//                    for (int i = 0; i < mItems.size(); i++) {
//                        Log.e(TAG, mItems.get(i).getName());
//                    }
//                    if (mIdx == 0) {
//                        sendRegisterView();
//                    } else {
//                        sendDetectView();
//                    }
//                }, throwable -> {
//                    Log.d(TAG, "Error in triggerStopScanCompletable");
//                });
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        Log.d(TAG, "onDestroy()");
//
//        if(mIsReadStart || mAutoScanStart) {
//            mIsReadStart = false;
//            mAutoScanStart = false;
//            stopScanCompletable();
//            mRfidMgr.Stop();
//            Log.d(TAG, "stop4");
//        }
//
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == REQUEST_BLUETOOTH) {
//            if(resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_disable), Toast.LENGTH_SHORT).show();
//                Log.d("finish()", "finishing!!!!");
//                finish();
//            }
//        }
//        else if(requestCode == REQUEST_WRITE_TAG && resultCode == Activity.RESULT_OK) {
//            String currentTagId = data.getStringExtra(DialogWriteTag.CURRENT_TAG_ID);
//            String tagIdToWrite = data.getStringExtra(DialogWriteTag.TAG_ID_TO_WRITE);
//            if(tagIdToWrite != null) {
//                Log.d(TAG,"currentTagId : " + currentTagId + " tagIdToWrite : " + tagIdToWrite);
//                mUtil.showProgress(mProgress,RFIDDemoActivity.this,  true);
//                startWriteTag(tagIdToWrite);
//            }
//        }
//        else if (requestCode == REQUEST_DETECT_ACTIVITY && resultCode == Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult: detect 전송완료");
//            finish();
//        }
//        else if (requestCode == REQUEST_REGISTER_ACTIVITY && resultCode == Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult: register 전송완료");
//            finish();
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public static Handler mAutoScanHandler = new Handler() {
//        @Override
//        public void dispatchMessage(@NonNull Message msg) {
//
//            super.dispatchMessage(msg);
//        }
//    };
//
//    private void startAutoScan() {
//        try {
//            mAutoScanStart = true;
//            int interval = mPrefUtil.getIntPreference(PreferenceUtil.KEY_SCAN_AUTO_INTERVAL);
//            mAutoScanInterval = (interval + 1) * 1000;
//            Log.d(TAG, "autoScanInterval" + mAutoScanInterval);
//            mAutoScanHandler.post(runnable);
//        } catch(Exception e) {
//        }
//    }
//
//    /* To check if data packet received after invoked startInventory on auto read mode */
//    private CountDownTimer mCallbackCheckTimer = new CountDownTimer(5000, 1000) {
//        @Override
//        public void onTick(long l) {
//        }
//
//        @Override
//        public void onFinish() {
//            boolean isAutoScan = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE);
//
//            if(isAutoScan) {
//                Log.d(TAG, "=== Didn't receive RFIDCallback, Restart StartAutoScan");
//                mAutoScanHandler.removeCallbacksAndMessages(null);
//                mAutoScanHandler.post(runnable);
//            }
//        }
//    };
//
//    /* Start reading RFID tag */
//    private void startScan() {
//        mIsSaveLog = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//        if(mIsSaveLog) {
//            String currentDate = mSimpleDataFormat.format(new Date());
//            String strStart = "Start Scan : " + currentDate;
//            logToFile(strStart);
//            mHandler.sendEmptyMessage(HANDLER_MSG_SAVE_LOG);
//        }
//
//        boolean isAutoScan = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE);
//        if(isAutoScan) {
//            startAutoScan();
//        }
//        else {
//            AsyncStart asyncStart = new AsyncStart();
//            asyncStart.execute();
//        }
//    }
//
//    private void changeStopToStart() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mLinearCheckbox.setVisibility(View.VISIBLE);
//                mBtnStartInventory.setText(R.string.read_rfid_tag);
//            }
//        });
//    }
//
//    private void changeStartToStop() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mBtnStartInventory.setText(R.string.stop_rfid_tag);
//                mLinearCheckbox.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    /* Stop reading RFID tag */
//    private void stopScan() {
//        mAutoScanStart = false;
//        mAutoScanHandler.removeCallbacksAndMessages(null);
//        callbackTimerCancel();
//    }
//
//    private void rfidConnectDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(RFIDDemoActivity.this);
//        builder.setTitle(R.string.connect_dlg_title);
//        builder.setMessage(R.string.not_connected);
//        builder.setCancelable(false);
//        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if(keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.dismiss();
//                    Log.d("finish()", "finishing!!!!");
//                    finish();
//                }
//                return false;
//            }
//        });
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//                Intent intent = new Intent(getApplicationContext(), RFIDControlActivity.class);
//                startActivity(intent);
//                Log.d("finish()", "finishing!!!!");
//                finish();
//
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private String getBatteryInfo(boolean isReading) {
//        int batteryVolt = 0;
//        int batteryLevel = 0;
//        int chargingState = 0;
//        int count = 0;
//
//        do {
//            batteryLevel = mRfidMgr.GetBattLevel();
//            Log.d(TAG, "GetBattLevel :" + batteryLevel);
//            if(batteryLevel > 0) {
//                break;
//            }
//            else
//                count++;
//
//            if(count > 3)
//                break;
//        }
//        while(true);
//
//        count = 0;
//
//        do {
//            chargingState = mRfidMgr.GetChargingState();
//            Log.d(TAG, "GetChargingState : " + chargingState);
//            if(chargingState >= RFIDConst.CommandErr.SUCCESS) {
//                if(chargingState == 1 || chargingState == 0)
//                    break;
//            }
//            else {
//                count++;
//            }
//
//            if(count > 3)
//                break;
//
//            mUtil.sleep(100);
//        }
//        while(true);
//
//        do {
//            batteryVolt = mRfidMgr.GetBattVolt();
//            Log.d(TAG, "GetBattVolt :" + batteryVolt);
//            if(batteryVolt >= 0)
//                break;
//            else
//                count++;
//
//            if(count > 3)
//                break;
//
//            mUtil.sleep(100);
//        }
//        while(true);
//
//        String batteryInfo = batteryVolt + "mV,  " + batteryLevel + "%,  "
//                + ((chargingState == 1) ? getString(R.string.charging) : getString(R.string.not_charging));
//
//        return batteryInfo;
//    }
//
//    private boolean makeFolderAndFile() {
//        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            return false;
//        }
//
//        if(mFile != null) {
//            return true;
//        }
//
//        String fileName = SAVED_LOG_FILE + "_" + LOG_DATE_FORMAT.format(new Date()) + ".log";
//        mUtil.createFolder(RFID_CONTROL_FOLDER);
//        mUtil.createFolder(RFID_CONTROL_FOLDER + "/" + SAVED_LOG_FOLDER);
//        mFile = mUtil.createFile(RFID_CONTROL_FOLDER + "/" + SAVED_LOG_FOLDER + "/" + fileName);
//        if(mFile.exists()) {
//            String title = "CurrentTime, ReadCount[Number of Tag, Total Read], Batt(mV), Batt(%), Charging flag, RFID Module Temp";
//            logToFile(title);
//        }
//
//        return true;
//    }
//
//    public void saveLog(String batteryInfo) {
//        if(mLogStartTime < 0)
//            mLogStartTime = System.currentTimeMillis();
//
//        String currentDate = mSimpleDataFormat.format(new Date());
//        //String eslapseTime = formatElapsedTime(System.currentTimeMillis() - mLogStartTime);
//
//        //String logStr = currentDate+",  [" + mTotalCount + "  " + mReadCount + "],  " + eslapseTime + ",  " + mBatterySummary;
//        String logStr = currentDate + ",  [" + mTotalCount + "  " + mReadCount + "],  " + batteryInfo + ",  " + mTempValue;
//
//        logToFile(logStr);
//
//    }
//
//    private synchronized boolean logToFile(String s) {
//        if(mFile == null || !mFile.exists()) {
//            makeFolderAndFile();
//        }
//
//        boolean result = true;
//        FileWriter fw = null;
//        BufferedWriter writer = null;
//        StringBuilder buffer = new StringBuilder();
//        try {
//            fw = new FileWriter(mFile, true);
//            writer = new BufferedWriter(fw);
//            buffer.append(s + "\r\n");
//            writer.write(buffer.toString());
//            writer.flush();
//        } catch(IOException e) {
//            e.printStackTrace();
//            result = false;
//        } finally {
//            buffer.setLength(0);
//            buffer = null;
//            try {
//                if(writer != null) {
//                    writer.close();
//                    writer = null;
//                }
//                if(fw != null) {
//                    fw.close();
//                    fw = null;
//                }
//            } catch(IOException ie) {
//                ie.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    private void addScanData(final String ogrData) {
//        runOnUiThread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        String data = ogrData;
//                        Log.d(TAG, ogrData);
//                        if(mIsTemp) {
//                            String[] arrStr = data.split(",h="); //h= : RFID Module temperature
//                            if(arrStr.length > 1) {
//                                data = arrStr[0];
//                                mTempValue = arrStr[1];
//                                mTextTemp.setText(mTempValue);
//                            }
//                        }
//
//                        Integer count = mHashItems.get(data);
//                        if(count == null) {
//                            mTotalCount += 1;
//                            mHashItems.put(data, 1);
//                            mItems.add(new RfidListItem(data, 1));
//                            registerItems.add(new RegisterItem(data, data, data));
//                            mRvAdapter.notifyItemChanged(mItems.size() - 1);
//                        }
//                        else {
//                            mHashItems.put(data, count + 1);
//                            for(int i = 0; i < mItems.size(); i++) {
//                                RfidListItem rfItem = mItems.get(i);
//                                if(rfItem.getName().equalsIgnoreCase(data)) {
//                                    rfItem.setCount(count + 1);
//                                    mRvAdapter.notifyItemChanged(i);
//                                    break;
//                                }
//                            }
//                        }
//                        mTextTotalCount.setText("" + mTotalCount);
//                        mReadCount += 1;
//                        mTextReadCount.setText("" + mReadCount);
//
//                    }
//                });
//    }
//
//    public List sortByValue(final Map map, boolean isAsc) {
//
//        List<String> list = new ArrayList();
//        list.addAll(map.keySet());
//
//        Collections.sort(list, new Comparator() {
//            public int compare(Object o1, Object o2) {
//                return ((Comparable) o2).compareTo(o1);
//            }
//        });
//
//        if(isAsc) {
//            mOptionMenu.getItem(0).setIcon(R.drawable.sort_down);
//        }
//        else {
//            mOptionMenu.getItem(0).setIcon(R.drawable.sort_up);
//            Collections.reverse(list); // 주석시 오름차순
//        }
//
//        return list;
//    }
//
//    private RFIDApplication.NotifyDataCallbacks mDataCallbacks = new RFIDApplication.NotifyDataCallbacks() {
//        @Override
//        public void notifyDataPacket(RecvPacket recvPacket) {
//            Log.d(TAG, "notifyDataPacket : " + recvPacket.RecvString);
//
//            callbackTimerCancel();
//
////            String temp = mUtil.hexToAscii( "7777772e6261656c64756e672e636f6d");
////            addScanData(temp);
////            String text =  mUtil.hexToAscii(recvPacket.RecvString);
//            addScanData(recvPacket.RecvString);
//
//            boolean isAutoScan = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE);
//            mRfidMgr.GetOperationMode(mOperationMode);
//            boolean isContinuous = mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS;
//            if(!isContinuous && !isAutoScan) {
//                mIsReadStart = false;
//                stopScanCompletable();
//                mRfidMgr.Stop();
//            }
//
//            mAutoScanHandler.removeCallbacksAndMessages(null);
//
//            if(mIsReadStart && mAutoScanStart)
//                mAutoScanHandler.postDelayed(runnable, mAutoScanInterval);
//        }
//
//        @Override
//        public void notifyChangedState(int state) {
//            Log.d(TAG, "notifyChangedState : " + state);
//
//            if(state == RFIDConst.DeviceState.TRIGGER_RFID_KEYDOWN) {
//                Log.d(D, "Trigger key down");
//                Log.d(TAG, "TRIGGER_RFID_KEYDOWN");
//                mAutoScanHandler.removeCallbacksAndMessages(null);
//                if(!mIsReadStart && !mAutoScanStart) {
//                    mIsReadStart = true;
//                    triggerStartScanCompletable();
//                }
//                else {
//                    mIsReadStart = false;
//                    stopScanCompletable();
//                }
//            }
//            else if(state == RFIDConst.DeviceState.TRIGGER_RFID_KEYUP) {
//                Log.d(D, "Trigger key up");
//                Log.d(TAG, "TRIGGER_RFID_KEYUP");
//                if(!mAutoScanStart) {
//                    mIsReadStart = false;
//                    triggerStopScanCompletable();
//                }
//            }
//            else if(state == RFIDConst.DeviceState.TRIGGER_SCAN_KEYDOWN) {
//                Log.d(TAG, "TRIGGER_SCAN_KEYDOWN");
//                if(!isPause)
//                    Toast.makeText(getApplicationContext(), getString(R.string.scanner_mode), Toast.LENGTH_LONG).show();
//            }
//            else if(state == RFIDConst.DeviceState.LOW_BATT) {
//                Log.d(TAG, "LOW_BATT");
//                mIsReadStart = false;
//                stopScanCompletable();
//                mRfidMgr.Stop();
//                Log.d(TAG, "stop7");
//            }
//            else if(state == RFIDConst.DeviceState.BT_DISCONNECTED) {
//                Log.d(TAG, "BT_DISCONNECTED");
//                mRfidMgr.Close();
//                Log.d(TAG, "close12");
//                mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//                mIsReadStart = false;
//                RFIDControlActivity.POWER_OFF_FLAG = true;
//                Intent intent = new Intent(RFIDDemoActivity.this, RFIDControlActivity.class);
//                startActivity(intent);
//            }
//            else if(state == RFIDConst.DeviceState.USB_DISCONNECTED
//                    && mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION)
//                    .equalsIgnoreCase(RFIDControlActivity.OpenOption.WIRED.toString())) {
//                Log.d(TAG, "USB_DISCONNECTED");
//                Log.d(TAG, mIsReadStart ? "mIsReadStart : true" : "mIsReadStart : false");
//                RFIDControlActivity.USB_DETACHED_FLAG = true;
//                mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//                mIsReadStart = false;
//                Intent connectionIntent = new Intent(RFIDDemoActivity.this, RFIDControlActivity.class);
//                startActivity(connectionIntent);
//            }
//            else if(state == RFIDConst.DeviceState.POWER_OFF
//                    && mPrefUtil.getStringPreference(PreferenceUtil.KEY_OPEN_OPTION)
//                    .equalsIgnoreCase(RFIDControlActivity.OpenOption.WIRED.toString())) {
//                mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//                mIsReadStart = false;
//                RFIDControlActivity.POWER_OFF_FLAG = true;
//                Intent intent = new Intent(RFIDDemoActivity.this, RFIDControlActivity.class);
//                startActivity(intent);
//            }
//        }
//    };
//
//    private void callbackTimerCancel() {
//        try {
//            mCallbackCheckTimer.cancel();
//            Log.d(TAG, "CallbackTimer Canceled");
//
//        } catch(Exception e) {
//        }
//    }
//
//    private final Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if(mAutoScanStart) {
//                Log.d(TAG, "CallbackTimer Started");
//                mCallbackCheckTimer.start();
//                Log.d(TAG, "--- handler start ");
//                mRfidMgr.StartInventory();
//            }
//        }
//    };
//
//    private void triggerScanStart() {
//        mIsSaveLog = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//        if(mIsSaveLog) {
//            String currentDate = mSimpleDataFormat.format(new Date());
//            String strStart = "Start Scan : " + currentDate;
//            logToFile(strStart);
//            mHandler.sendEmptyMessage(HANDLER_MSG_SAVE_LOG);
//        }
//
//        boolean isAutoScan = mPrefUtil.getBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE);
//        if(isAutoScan) {
//            mAutoScanStart = true;
//            int interval = mPrefUtil.getIntPreference(PreferenceUtil.KEY_SCAN_AUTO_INTERVAL);
//            mAutoScanInterval = (interval + 1) * 1000;
//            Log.d(TAG, "autoScanInterval" + mAutoScanInterval);
//        }
//    }
//
//    private void triggerScanStop() {
//        if(mIsSaveLog) {
//            mUtil.startFileOnlyMediaScan(mFile.getParent());
//        }
//    }
//
//    private View.OnClickListener mOnCheckboxListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(v.getId() == R.id.checkbox_auto_scan) {
//                if(mCheckboxAutoScan.isChecked()) {
//                    mCheckboxContinuous.setChecked(false);
//                    mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE;
//                    mRfidMgr.SetOperationMode(mOperationMode);
//
//                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, true);
//                }
//                else {
//                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false);
//                }
//                Log.e("checkchange_autoscan", mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE ?
//                        "single" : "continuous");
//            }
//            else if(v.getId() == R.id.checkbox_continuous) {
//                if(mCheckboxContinuous.isChecked()) {
//                    mCheckboxAutoScan.setChecked(false);
//                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SCAN_AUTO_ENABLE, false);
//                    mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_CONTINUOUS;
//                    mAutoScanStart = false;
//                }
//                else {
//                    mOperationMode.single = RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE;
//                }
//                mRfidMgr.SetOperationMode(mOperationMode);
//                Log.e("checkchange_continuous", mOperationMode.single == RFIDConst.RFIDConfig.INVENTORY_MODE_SINGLE ?
//                        "single" : "continuous");
//            }
//            else if(v.getId() == R.id.checkbox_save_log) {
//                if(mCheckboxSaveLog.isChecked()) {
//                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, true);
//                }
//                else {
//                    mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_SAVE_LOG, false);
//                }
//            }
//            else if(v.getId() == R.id.checkbox_beep_sound) {
//                mUtil.showProgress(mProgress, RFIDDemoActivity.this, true);
//
//                if(mCheckboxBeepSound.isChecked()) {
//                    AsyncSetVolume asyncSetVolume = new AsyncSetVolume();
//                    asyncSetVolume.execute(mPrefUtil.getIntPreference(PreferenceUtil.KEY_BEEP_SOUND, RFIDConst.DeviceConfig.BUZZER_HIGH));
//                }
//                else {
//                    AsyncSetVolume asyncSetVolume = new AsyncSetVolume();
//                    asyncSetVolume.execute(RFIDConst.DeviceConfig.BUZZER_MUTE);
//                }
//            }
//        }
//    };
//
//    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(v.getId() == R.id.btn_start_inventory) {
//                /* Stop */
//                if(mIsReadStart || mAutoScanStart) {
//                    //stopScan();
//                    stopScanCompletable();
//                    mIsReadStart = false;
//                    AsyncStop asyncStop = new AsyncStop();
//                    asyncStop.execute();
//                    //mPrefUtil.putBooleanPreference(PreferenceUtil.KEY_IS_READ_TEMP, false);
//                }
//                /* Read */
//                else {
//                    int mode = mRfidMgr.GetTriggerMode();
//                    Log.d("mode", "trigger mode : " + mode);
//                    switch(mode) {
//                        case SCAN_MODE:
//                            if(!isPause) {
//                                Log.d(TAG, "RFID Demo is currently paused");
//                                Toast.makeText(getApplicationContext(), getString(R.string.scanner_mode), Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//                        case RFID_MODE:
//                        default:
//                            mIsReadStart = true;
//                            startScanCompletable();
//                            break;
//                    }
//                }
//            }
//            else if(v.getId() == R.id.btn_write_tag) {
//                Intent intent = new Intent(RFIDDemoActivity.this, DialogWriteTag.class);
//                startActivityForResult(intent, REQUEST_WRITE_TAG);
//                //startWriteTag();
//            }
//            else if(v.getId() == R.id.btn_read_tag) {
//                startReadTag();
//            }
//        }
//    };
//
//    private static class LogSaveHandler extends Handler {
//        private final WeakReference<RFIDDemoActivity> weakReference;
//
//        public LogSaveHandler(RFIDDemoActivity activity) {
//            this.weakReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            RFIDDemoActivity rfidDemoActivity = weakReference.get();
//            rfidDemoActivity.handleMessage(msg);
//        }
//    }
//
//    private void handleMessage(Message msg) {
//        switch(msg.what) {
//            case HANDLER_MSG_SAVE_LOG:
//                if(mIsReadStart && mIsSaveLog) {
//                    Log.d(TAG, "MSG_SAVE_LOG");
//                    /* Save log every 10 seconds to check battery info */
//                    saveLog(getBatteryInfo(true));
//                    mHandler.sendEmptyMessageDelayed(HANDLER_MSG_SAVE_LOG, 10 * 1000);
//                }
//        }
//    }
//
//    private void removeHandlerMessage() {
//        if(mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//        else
//            Log.d(TAG, "Handler is null");
//    }
//
//    class AsyncGetVolume extends AsyncTask<Void, Void, Boolean> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mUtil.showProgress(mProgress, RFIDDemoActivity.this, true);
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            if(mRfidMgr.IsOpened()) {
//                int count = 0;
//                do {
//                    mVolumeValue = mRfidMgr.GetBuzzerVol();
//                    Log.e("AsyncGetVolume", "mVolume === " + mVolumeValue);
//                    if(mVolumeValue > 0) {
//                        if(mVolumeValue == 0 || mVolumeValue == 1 || mVolumeValue == 2) {
//                            break;
//                        }
//                    }
//                    else {
//                        count++;
//                    }
//
//                    if(count > 2)
//                        break;
//
//                    try {
//                        Thread.sleep(100);
//                    } catch(Exception e) {
//                    }
//                }
//                while(true);
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSuccess) {
//            if(isSuccess) {
//                initState();
//            }
//            else {
//                Log.d("FailMessage", "GetBuzzerVol Failed");
//            }
//            mUtil.showProgress(mProgress, RFIDDemoActivity.this, false);
//            super.onPostExecute(isSuccess);
//        }
//    }
//
//    class AsyncSetVolume extends AsyncTask<Integer, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Integer... integers) {
//            if(mRfidMgr.IsOpened()) {
//                do {
//                    int result = mRfidMgr.SetBuzzerVol(integers[0]);
//                    Log.e("buzzerVolumeResult=====", "" + result);
//                    if(result != -1 && result != -100) {
//                        break;
//                    }
//                }
//                while(true);
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSuccess) {
//            mUtil.showProgress(mProgress, RFIDDemoActivity.this, false);
//            super.onPostExecute(isSuccess);
//        }
//    }
//
//    class AsyncStop extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Log.d(TAG, "stop9");
//            mRfidMgr.Stop();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            if(mIsSaveLog) {
//                removeHandlerMessage();
//                mUtil.startFileOnlyMediaScan(mFile.getParent());
//                saveLog(getBatteryInfo(false));
//            }
//        }
//    }
//
//    class AsyncStart extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mRfidMgr.StartInventory();
//            return null;
//        }
//    }
//
//    /* To detect status bar touched */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int Y = (int) event.getY();
//
//        if(Y < 400) {
//            onWindowFocusChanged(true);
//        }
//        return true;
//    }
//
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        Log.d("Focus debug", "Focus changed !");
//
//        if(!hasFocus) {
//            Log.d("Focus debug", "Lost focus !");
//
//            if(mIsReadStart || mAutoScanStart) {
//                Log.e(TAG, "Stop RFID Reading");
//                mIsReadStart = false;
//                mAutoScanStart = false;
//                stopScanCompletable();
//                mRfidMgr.Stop();
//                Log.d("stop", "stop10");
//            }
//        }
//    }
//}
