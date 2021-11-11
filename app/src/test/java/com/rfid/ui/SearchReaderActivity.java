package com.rfid.ui;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

import device.common.rfid.RFIDCallback;
import device.common.rfid.RFIDConst;
import device.sdk.RFIDManager;
import com.rfid.R;
import com.rfid.util.Utils;


public class SearchReaderActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public final int MY_PERMISSION_REQUEST_BLUETOOTH = 0x1001;

    private ListView mListRfid;
    private com.rfid.adapter.RFIDListAdapter mListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mItems;

    private RFIDManager mRfidMgr;

    private MenuItem mSearchMenu;

    private ProgressDialog mProgressSearchDlg;
    private ProgressDialog mProgressDlg;
    private Utils mUtil;
    private com.rfid.util.PreferenceUtil mPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_reader);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.search_reader);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRfidMgr = RFIDManager.getInstance();
        mRfidMgr.RegisterRFIDCallback(mRfidStateCallback);

        mUtil = new Utils(this);
        mPrefUtil = new com.rfid.util.PreferenceUtil(getApplicationContext());
        mProgressDlg = new ProgressDialog(SearchReaderActivity.this);

        init();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkPermission();
    }

    private void init() {
        mListRfid = (ListView) findViewById(R.id.list_rfid_search);
        mListAdapter = new com.rfid.adapter.RFIDListAdapter(getApplicationContext());
        mItems = new ArrayList<>();
        mListAdapter.setItems(mItems);
        mListRfid.setAdapter(mListAdapter);
        mListRfid.setOnItemClickListener(mOnItemClickListener);

        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //BluetoothAdapter.ACTION_BOND_STATE_CHANGED
        registerReceiver(mBluetoothSearchReceiver, searchFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothSearchReceiver);
        mRfidMgr.UnregisterRFIDCallback(mRfidStateCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_bt_menu, menu);
        mSearchMenu = menu.findItem(R.id.action_bt_search);
        updatePlayStatus();

        return true;
    }

    public void updatePlayStatus() {
        if (mBluetoothAdapter.isDiscovering())
            mSearchMenu.setIcon(R.drawable.ic_start);
        else
            mSearchMenu.setIcon(R.drawable.ic_start);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            Log.d("finish()", "finishing!!!!");
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_bt_search) {
            mBluetoothAdapter.cancelDiscovery();
            mSearchMenu.setIcon(R.drawable.ic_start);
            mItems.clear();
            mListAdapter.setItems(mItems);
            mListAdapter.notifyDataSetChanged();

            startSearchDevice();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissionArr = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            requestPermissions(permissionArr, MY_PERMISSION_REQUEST_BLUETOOTH);
        } else {
            startSearchDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSearchDevice();
            } else {
                Log.d("finish()", "finishing!!!!");
                finish();
            }
        }
    }

    private void startSearchDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //mBluetoothAdapter.startDiscovery() : 블루투스 검색 시작
        mBluetoothAdapter.startDiscovery();

        mProgressSearchDlg = new ProgressDialog(SearchReaderActivity.this);
        mProgressSearchDlg.setMessage(getString(R.string.search_bt_devices));
        mProgressSearchDlg.setCancelable(false);

        mProgressSearchDlg.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Stop",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mProgressSearchDlg != null && mProgressSearchDlg.isShowing()) {
                            mBluetoothAdapter.cancelDiscovery();
                            mProgressSearchDlg.dismiss();
                            dialog.cancel();
                        }
                    }
                });
        mProgressSearchDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mBluetoothAdapter.cancelDiscovery();
            }
        });
        mProgressSearchDlg.show();
    }

    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {

            } else if (action.equalsIgnoreCase(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if (deviceName == null)
                    return;

                if (mUtil.isRFIDDevice(deviceName)) {
                    boolean isExist = false;
                    for (BluetoothDevice dev : mItems) {
                        if (dev.getName().equalsIgnoreCase(deviceName)) {
                            isExist = true;
                            break;
                        }
                    }
                    if (isExist)
                        return;

                    mItems.add(device);
                    mListAdapter.notifyDataSetChanged();
                }
            } else if (action.equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mProgressSearchDlg.dismiss();
            } else if (action.equalsIgnoreCase(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                if (state == BluetoothDevice.BOND_BONDING) {
                    Log.d("AAAA", "-- state :  BluetoothDevice.BOND_BONDING");
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    Log.d("AAAA", "-- state :  BluetoothDevice.BOND_BONDED");
                } else if (state == BluetoothDevice.BOND_NONE) {
                    Log.d("AAAA", "-- state :  BluetoothDevice.BOND_NONE");
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mRfidMgr.IsOpened()) {
                requestRfidDisConnection(position);
            } else {
                requestRfidConnection(position);
            }
        }
    };

    private Handler mHandlerBtConnect = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (mRfidMgr != null && !mRfidMgr.IsOpened()) {
                mRfidMgr.DisconnectBTDevice();
                Log.d(TAG, "disconn5");
                mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, false);
            }
            super.dispatchMessage(msg);
        }
    };

    public void requestRfidConnection(int index) {
        final BluetoothDevice btDevice = mItems.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchReaderActivity.this);
        builder.setTitle(R.string.connect_dlg_title);
        builder.setMessage(R.string.connect_dlg_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRfidMgr.ConnectBTDevice(btDevice.getAddress(), btDevice.getName());
                Log.d(TAG, "ConnectBTDevice");
                String connMsg = getString(R.string.connect_request_msg) + btDevice.getAddress();
                mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, connMsg, true);
                mHandlerBtConnect.sendEmptyMessageDelayed(0x1001, 10000);

            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void requestRfidDisConnection(final int index) {
        final BluetoothDevice btDevice = mItems.get(index);
        final String macAddr = mUtil.getBTMacAddress();
        String msg = String.format(getString(R.string.disconnect_msg), macAddr);
        final String dlgMsg = getString(R.string.disconnect_request_msg) + macAddr;
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchReaderActivity.this);
        if (btDevice.getAddress().equalsIgnoreCase(macAddr)) {
            builder.setTitle(R.string.disconnect_title);
            builder.setMessage(R.string.request_disconnect);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String currentMac = mPrefUtil.getStringPreference(com.rfid.util.PreferenceUtil.KEY_CONNECT_BT_MACADDR);
                    String connMsg = getString(R.string.disconnect_request_msg) + currentMac;
                    mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, connMsg, true);
                    mRfidMgr.Close();
                    Log.d("SearchReader", "close16");
                    mRfidMgr.DisconnectBTDevice();
                    Log.d(TAG, "disconnect 13");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, false);
                        }
                    }, 5000);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            String currentMac = mPrefUtil.getStringPreference(com.rfid.util.PreferenceUtil.KEY_CONNECT_BT_MACADDR);
            String connMsg = getString(R.string.disconnect_request_msg) + currentMac;
            mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, connMsg, true);
            mRfidMgr.Close();
            Log.d("SearchReader", "close17");
            mRfidMgr.DisconnectBTDevice();
            Log.d(TAG, "disconnect 14");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, false);
                    requestRfidConnection(index);
                }
            }, 5000);
        }
    }

    private final Handler mCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });

    private final RFIDCallback mRfidStateCallback = new RFIDCallback(mCallbackHandler) {
        @Override
        public void onNotifyChangedState(int state) {
            if (state == RFIDConst.DeviceState.BT_CONNECTED) {
                AsyncRfidOpen async = new AsyncRfidOpen();
                async.execute();
            }
            super.onNotifyChangedState(state);
        }
    };

    public class AsyncRfidOpen extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = true;
            int tryCount = 0;
            mRfidMgr.Open(RFIDConst.DeviceType.DEVICE_BT);
            do {
                if (mRfidMgr.IsOpened()) {
                    result = true;
                    break;
                } else {
                    tryCount++;
                }
                if (tryCount > 10) {
                    result = false;
                    break;
                }
            }
            while (true);

            if (result) {
                String macAddr = mUtil.getBTMacAddress();
                String deviceName = mUtil.getDeviceName();

                if (macAddr != null && !macAddr.isEmpty())
                    mPrefUtil.putStringPrefrence(com.rfid.util.PreferenceUtil.KEY_CONNECT_BT_MACADDR, macAddr);

                if (deviceName != null && !deviceName.isEmpty())
                    mPrefUtil.putStringPrefrence(com.rfid.util.PreferenceUtil.KEY_CONNECT_BT_NAME, deviceName);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mUtil.showProgress(mProgressDlg, SearchReaderActivity.this, false);
            if (result) {
                String btName = mPrefUtil.getStringPreference(com.rfid.util.PreferenceUtil.KEY_CONNECT_BT_NAME, "");
                Intent resIntent = new Intent();
                resIntent.putExtra(Utils.EXTRA_BT_DEVICE_NAME, btName);

                setResult(Activity.RESULT_OK, resIntent);
                Log.d("finish()", "finishing!!!!");
                finish();
            }
        }
    }

    public void startBluetoothDeviceConnection(int index) {
        final BluetoothDevice btDevice = mItems.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchReaderActivity.this);
        builder.setTitle(R.string.connect_dlg_title);
        builder.setMessage(R.string.connect_dlg_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connectBluetoothDevice(btDevice);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void connectBluetoothDevice(final BluetoothDevice device) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        int pairedDeviceCount = pairedDevices.size();

        //pairing되어 있는 기기의 목록을 가져와서 연결하고자 하는 기기가 이전 기기 목록에 있는지 확인
        boolean already_bonded_flag = false;
        if (pairedDeviceCount > 0) {
            for (BluetoothDevice bonded_device : pairedDevices) {
                if (device.getName().equals(bonded_device.getName())) {
                    already_bonded_flag = true;
                }
            }
        }
        //pairing process
        //만약 pairing기록이 있으면 바로 연결을 수행하며, 없으면 createBond()함수를 통해서 pairing을 수행한다.
        if (!already_bonded_flag) {
            try {
                //pairing수행
                //device.createBond();
                RFIDManager rfidMgr = RFIDManager.getInstance();
                rfidMgr.ConnectBTDevice(device.getAddress(), device.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
