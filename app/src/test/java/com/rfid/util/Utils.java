package com.rfid.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import device.common.DevInfoIndex;
import device.sdk.Information;
import device.sdk.RFIDManager;
import com.rfid.R;
import com.rfid.ui.RFIDControlActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Utils
{
    private final String TAG = "Utils";
    public static final String EXTRA_CONTENT_LIST = "ex.dev.tool.rfidsettings.EXTRA_CONTENT_LIST";
    public static final String EXTRA_TITLE = "ex.dev.tool.rfidsettings.EXTRA_TITLE";
    public static final String EXTRA_SELECTED_INT_VALUE = "ex.dev.tool.rfidsettings.EXTRA_SELECTED_INT_VALUE";
    public static final String EXTRA_SELECTED_STRING_VALUE = "ex.dev.tool.rfidsettings.EXTRA_SELECTED_STRING_VALUE";
    public static final String EXTRA_INT_RESULT = "ex.dev.tool.rfidsettings.EXTRA_INT_RESULT";
    public static final String EXTRA_STRING_RESULT = "ex.dev.tool.rfidsettings.EXTRA_INT_RESULT";
    public static final String EXTRA_DESCRITPION = "ex.dev.tool.rfidsettings.EXTRA_DESCRITPION";
    public static final String EXTRA_MAX_VALUE = "ex.dev.tool.rfidsettings.EXTRA_MAX_VALUE";
    public static final String EXTRA_MIN_VALUE = "ex.dev.tool.rfidsettings.EXTRA_MIN_VALUE";
    public static final String EXTRA_ON_RESULT = "ex.dev.tool.rfidsettings.EXTRA_ON_RESULT";
    public static final String EXTRA_OFF_RESULT = "ex.dev.tool.rfidsettings.EXTRA_OFF_RESULT";
    public static final String EXTRA_ON_SELECTED_VALUE = "ex.dev.tool.rfidsettings.EXTRA_ON_SELECTED_VALUE";
    public static final String EXTRA_OFF_SELECTED_VALUE = "ex.dev.tool.rfidsettings.EXTRA_OFF_SELECTED_VALUE";
    public static final String EXTRA_ON_MAX_VALUE = "ex.dev.tool.rfidsettings.EXTRA_ON_MAX_VALUE";
    public static final String EXTRA_OFF_MAX_VALUE = "ex.dev.tool.rfidsettings.EXTRA__OFF_MAX_VALUE";
    public static final String EXTRA_ON_MIN_VALUE = "ex.dev.tool.rfidsettings.EXTRA_ON_MIN_VALUE";
    public static final String EXTRA_OFF_MIN_VALUE = "ex.dev.tool.rfidsettings.EXTRA_OFF_MIN_VALUE";
    public static final String EXTRA_RADIOPOWER_VALUE = "ex.dev.tool.rfidsettings.EXTRA_RADIOPOWER_RESULT";
    public static final String EXTRA_TXPOWER_VALUE = "ex.dev.tool.rfidsettings.EXTRA_TXPOWER_VALUE";

    public static final String EXTRA_BT_DEVICE_NAME = "ex.dev.tool.rfidsettings.EXTRA_BT_DEVICE_NAME";


    public static final String EXTRA_SESSION = "ex.dev.tool.rfidsettings.EXTRA_SESSION";
    public static final String EXTRA_Q = "ex.dev.tool.rfidsettings.EXTRA_Q";
    public static final String EXTRA_FLAG = "ex.dev.tool.rfidsettings.EXTRA_FLAG";

    public static final String EXTRA_REPORT_TIME = "ex.dev.tool.rfidsettings.EXTRA_REPORT_TIME";
    public static final String EXTRA_REPORT_RSSI = "ex.dev.tool.rfidsettings.EXTRA_REPORT_RSSI";

    public static final String EXTRA_MODE_SINGLE = "ex.dev.tool.rfidsettings.EXTRA_MODE_SINGLE";
    public static final String EXTRA_MODE_SELECT = "ex.dev.tool.rfidsettings.EXTRA_MODE_SELECT";
    public static final String EXTRA_MODE_TIMEOUT = "ex.dev.tool.rfidsettings.EXTRA_MODE_TIMEOUT";

    public static final String EXTRA_TXCYCLE_ON = "ex.dev.tool.rfidsettings.EXTRA_TXCYCLE_ON";
    public static final String EXTRA_TXCYCLE_OFF = "ex.dev.tool.rfidsettings.EXTRA_TXCYCLE_OFF";

    public static final String EXTRA_CUSTOM_ACTION = "ex.dev.tool.rfidsettings.EXTRA_CUSTOM_ACTION";
    public static final String EXTRA_CUSTOM_CATEGORY = "ex.dev.tool.rfidsettings.EXTRA_CUSTOM_CATEGORY";
    public static final String EXTRA_CUSTOM_EXTRA_DATA = "ex.dev.tool.rfidsettings.EXTRA_CUSTOM_EXTRA_DATA";

    public static final String EXTRA_UPDATE_MODE = "ex.dev.tool.rfidsettings.EXTRA_FW_UPDATE_MODE";

    public final String HQ_UHF_READER = "HQ_UHF_READER";
    public final String OBERON_UHF_READER = "OBERON";
    public final String DOTR800_UHF_READER = "DOTR800";
    public final String DOTR2000_UHF_READER = "DOTR2000";
    public final String DOTR2100_UHF_READER = "DOTR2100";
    public final String DOTR2200_UHF_READER = "DOTR2200";
    public final String TSS2100_UHF_READER = "TSS2";
    public final String TSS2200_UHF_READER = "TSS9";
    public final String DOTR3000_UHF_READER = "DOTR3";
    public final String TSS3xxx_UHF_READER = "TSS3";
    public final String RF800_UHF_READER = "RF";
    public final String G2W_UHF_READER = "RPT";
    public final String SN_UHF_READER = "SN";

    public static final String RFID_CONTROL_FOLDER = "RFIDControl";
    public static final String RFID_JSON_FOLDER = "RFIDJsons";
    public static final String RFID_RFU_FOLDER = "RFIDRFUs";
    public static final String RFID_CSV_FOLDER = "RFIDCsvs";
    public static final String WRONG_PATH = "wrongPath";

    public final int AUTO_UPDATE_ENABLED = 1;
    public final int AUTO_UPDATE_DISABLED = 0;

    public static final String ROOT_PATH = "storage/emulated/0/";
    public static final String CONFIG_FILE = "Configuration.json";

    private Context mContext;

    public static final String RF851 = "RF851";
    public static final String RF300 = "RF300";
    public static final String RF850 = "RF850";

    public static final String PM30 = "PM30";
    public static final String PM90 = "PM90";
    public static final String PM85 = "PM85";
    public static final String PM80_PLUS = "PM80+";
    public static final String PM66 = "PM66";
    public static final String PM550 = "PM550";
    public static final String PM75 = "PM75";

    public static int ENABLE = 1;
    public static int DISABLE = 0;

    private PreferenceUtil mPrefUtils;

    public Utils(Context context)
    {
        mContext = context;
        mPrefUtils = new PreferenceUtil(mContext);
    }

    public String asciiToHex(String text)
    {
        char[] chars = text.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public String hexToAscii(String hexStr)
    {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public byte[] hexToByteArray(String hexStr)
    {
        int len = hexStr.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i +=2)
        {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i+1), 16));
        }
        return data;
    }

    public String byteArrayToHex(byte[] byteArray)
    {
      StringBuilder sb = new StringBuilder();
      for(byte b : byteArray)
      {
          sb.append(String.format("%02x", b&0xff));
      }
      return sb.toString();
    }

    public String formatElapsedTime(long startTime, long stopTime)
    {
        long elapsedSeconds = stopTime - startTime;

        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        char zeroDigit = '0';

        if(elapsedSeconds >= 86400)
        {
            days = elapsedSeconds / 86400;
            elapsedSeconds -= days * 86400;
        }
        if(elapsedSeconds >= 3600)
        {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= hours * 3600;
        }
        if(elapsedSeconds >= 60)
        {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;

        String result = hours + ":" + minutes + ":" + seconds;
        return result;
    }

    public String calElapsedTime(long startTime, long currentTime)
    {
        long time;
        if(startTime > currentTime)
            time = currentTime;
        else
            time = (currentTime - startTime) / 1000;

        long hour = time / 3600;
        long min = time / 60 % 60;
        long sec = time % 60;

        String timeFormat = hour + ":" + min + ":" + sec;

        return timeFormat;
    }

    public void disableButton(Button btn)
    {
        btn.setEnabled(false);
        btn.setTextColor(ContextCompat.getColor(mContext, R.color.col_80e0e0e0));
    }

    public void enableButton(Button btn, int colorRes)
    {
        btn.setEnabled(true);
        btn.setTextColor(ContextCompat.getColor(mContext, colorRes));
    }

    public int getDevice()
    {
        int majorNum = 0;
        try
        {
            Information information = Information.getInstance();
            majorNum = information.getMajorNumber();
        } catch(RemoteException e)
        {
            e.printStackTrace();
        }
        return majorNum;
    }

    public String getDefaultOption()
    {
        if(getDevice() == DevInfoIndex.PM30_MAJOR)
        {
            return RFIDControlActivity.OpenOption.WIRED.toString();
        }
        else if(getDevice() == DevInfoIndex.PM75_MAJOR || getDevice() == DevInfoIndex.PM90_MAJOR)
        {
            return RFIDControlActivity.OpenOption.UART.toString();
        }
        return RFIDControlActivity.OpenOption.BLUETOOTH.toString();
    }


    public int calTxCycle(int onValue, int offValue)
    {
        if((onValue + offValue) == 0)
        {
            return 0;
        }
        int txCycle = (onValue * 100) / (onValue + offValue);
        return txCycle;
    }

    public boolean isRFIDDevice(String deviceName)
    {
        deviceName = deviceName.toLowerCase();
        if(deviceName.contains(HQ_UHF_READER.toLowerCase())
                || deviceName.contains(DOTR3000_UHF_READER.toLowerCase())
                || deviceName.contains(RF800_UHF_READER.toLowerCase())
                || deviceName.contains(TSS3xxx_UHF_READER.toLowerCase())
                || deviceName.contains(G2W_UHF_READER.toLowerCase())
                || deviceName.contains(OBERON_UHF_READER.toLowerCase())
                || deviceName.contains(DOTR800_UHF_READER.toLowerCase())
                || deviceName.contains(DOTR2000_UHF_READER.toLowerCase())
                || deviceName.contains(DOTR2100_UHF_READER.toLowerCase())
                || deviceName.contains(DOTR2200_UHF_READER.toLowerCase())
                || deviceName.contains(TSS2100_UHF_READER.toLowerCase())
                || deviceName.contains(TSS2200_UHF_READER.toLowerCase())
                || deviceName.contains(SN_UHF_READER.toLowerCase()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getLastDeviceName()
    {
        String deviceName = mPrefUtils.getStringPreference(PreferenceUtil.KEY_CONNECT_BT_NAME, "");
        return deviceName;
    }

    public String getDeviceName()
    {
        int tryCount = 0;
        String result = "";
        RFIDManager mgr = RFIDManager.getInstance();
        if(mgr.IsOpened())
        {
            do
            {
                String btName = mgr.GetBtDevice();
                Log.d("Utils", "Bt Name : " + btName);
                if(btName != null && btName.length() > 0 && isRFIDDevice(btName))
                {
                    result = btName.toUpperCase();
                    return result;
                }
                else
                {
                    tryCount++;
                }

                if(tryCount > 2)
                    return "";

                sleep(100);

            }
            while(true);

        }
        return result;
    }

    public String getBTMacAddress()
    {
        int tryCount = 0;
        String result = "";
        RFIDManager mgr = RFIDManager.getInstance();
        if(mgr.IsOpened())
        {
            do
            {
                String macAddr = mgr.GetBtMacAddr();
                Log.d("Utils", "Bt MacAddress : " + macAddr);
                if(macAddr != null && macAddr.length() > 0)
                {
                    macAddr = macAddr.toUpperCase();
                    if(macAddr.length() == 12)
                    {
                        String mac0 = macAddr.substring(0, 2);
                        String mac1 = macAddr.substring(2, 4);
                        String mac2 = macAddr.substring(4, 6);
                        String mac3 = macAddr.substring(6, 8);
                        String mac4 = macAddr.substring(8, 10);
                        String mac5 = macAddr.substring(10, 12);
                        result = mac0 + ":" + mac1 + ":" + mac2 + ":" + mac3 + ":" + mac4 + ":" + mac5;
                    }
                    return result;
                }
                else
                    tryCount++;

                if(tryCount > 2)
                    return "";

                sleep(100);

            }
            while(true);

        }
        return result;
    }

    public byte[] getBytes(File file)
    {
        FileInputStream input = null;
        if(file.exists())
        {
            try
            {
                input = new FileInputStream(file);
                int len = (int) file.length();
                byte[] data = new byte[len];
                int count, total = 0;
                while((count = input.read(data, total, len - total)) > 0)
                {
                    total += count;
                }
                return data;
            } catch(Exception ex)
            {
                ex.printStackTrace();
            } finally
            {
                if(input != null)
                {
                    try
                    {
                        input.close();
                    } catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public void setMenuState(ViewGroup parentView, boolean state)
    {
        if(state)
        {
            parentView.setEnabled(true);
            parentView.setBackgroundColor(0xffffffff);
            int count = parentView.getChildCount();
            for(int i = 0; i < count; i++)
            {
                View view = parentView.getChildAt(i);
                view.setEnabled(true);
                if(view instanceof ViewGroup)
                {
                    setMenuState((ViewGroup) view, state);
                }
            }
        }
        else
        {
            parentView.setEnabled(false);
            parentView.setBackgroundColor(0xffb0b0b0);
            int count = parentView.getChildCount();
            for(int i = 0; i < count; i++)
            {
                View view = parentView.getChildAt(i);
                view.setEnabled(false);
                if(view instanceof ViewGroup)
                {
                    setMenuState((ViewGroup) view, state);
                }
            }
        }
    }

    public void startFileOnlyMediaScan(String path)
    {
        fileOnlyScanTask(path);
    }

    public void startFolderOnlyMediaScan(String path)
    {
        folderOnlyScanTask(path);
    }

    private void mediaScanFile(Context context, final File file)
    {
        Log.d(TAG, "mediaScanFile");
        if(file != null && context != null)
        {
            Log.d(TAG, "file : " + file.getPath());
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener()
                    {
                        public void onScanCompleted(String path, Uri uri)
                        {
                            Log.d(TAG, "ScanCompleted path : " + path + " uri : " + uri);
                        }
                    });
        }
    }

    private void mediaScanFolder(Context context, String path)
    {
        Log.d(TAG, "mediaScanFolder : " + path);
        final int DIR_FORMAT = 0x3001;
        Uri mediaUri = MediaStore.Files.getContentUri("external");

        //String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, path);
        values.put("format", DIR_FORMAT);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        context.getContentResolver().insert(mediaUri, values);
    }

    public String getReaderOSVersion(String version)
    {
        String date = version.substring(version.indexOf("(") + 1, version.indexOf(")"));
        String str = date.toLowerCase();
        String firstLtr = str.substring(0, 1);
        String restLtrs = str.substring(1, str.length());

        firstLtr = firstLtr.toUpperCase();
        str = firstLtr + restLtrs;

        return version.replace(date, str);
    }

    public boolean checkFileExists(String path)
    {
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(basePath + "/" + path);
        return file.exists();
    }

    public boolean createFolder(String path)
    {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return false;
        }

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(rootPath + "/" + path);

        if(!folder.exists())
        {
            if(folder.mkdirs())
                return true;
            else
                return false;
        }

        //startMediaScan(context, rootPath + "/" + path);
        startFolderOnlyMediaScan(folder.getPath());
        Log.d(TAG, "path : " + rootPath + "/" + path);

        return true;
    }

    public File createFile(String path)
    {
        boolean result = false;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            result = false;
        }

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(rootPath + "/" + path);
        try
        {
            if(!file.exists())
            {
                if(file.createNewFile())
                    result = true;
                else
                    result = false;
            }

        } catch(IOException e)
        {
            e.printStackTrace();
        }

        startFileOnlyMediaScan(file.getPath());

        if(result)
            return file;
        else
            return null;
    }

    public String importJsonFile(String filePath)
    {
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(basePath + "/" + filePath);
        StringBuilder json = new StringBuilder();

        if(file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while((line = br.readLine()) != null)
                {
                    if(line.length() > 0)
                    {
                        json.append(line);
                    }
                }
                br.close();
            } catch(IOException e)
            {
                e.printStackTrace();
            }

            Log.d("Utils", json.toString());
        }
        return json.toString();
    }

    public String getFilePathFromURI(Uri uri)
    {
        //copy file and send new file path
        String path;
        if(uri == null)
            path = null;

        path = getInternalStorageFilePath(uri);
        if(path == null)
            path = null;

        return path;
    }

    public String getInternalStorageFilePath(Uri uri)
    {
        if(DocumentsContract.isDocumentUri(mContext, uri)) ;
        {
            //ExternalStorageProvider
            if(isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.d("Utils", docId);
                final String[] split = docId.split(":");
                final String type = split[0];

                if("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private void fileOnlyScanTask(String path)
    {
        Completable.create(subscriber -> {
            File file = new File(path);
            if(file.exists())
                mediaScanFile(mContext, file);
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG,"Finished file scanning");
                }, throwable -> {
                    Log.e(TAG,"Error occurs while scanning a file");
                });
    }

    private void folderOnlyScanTask(String path)
    {
        Completable.create(subscriber -> {
            File folder = new File(path);
            if(folder.isDirectory() && folder.exists())
                mediaScanFolder(mContext, folder.getPath());
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG,"Finished folder scanning");
                }, throwable -> {
                    Log.e(TAG,"Error occurs while scanning folders");
                });
    }

    public String fromSec(int sec)
    {
        int hour;
        int minute;
        int second;

        minute = sec / 60;
        hour = minute / 60;
        second = sec % 60;
        minute = minute % 60;

        StringBuilder time = new StringBuilder();
        if(hour != 0)
            time.append(hour + "h");
        if(minute != 0)
            time.append(minute + "m");
        if(second != 0)
            time.append(second + "s");

        return time.toString();
    }

    public int toSec(String time)
    {
        String unit = time.substring(time.length() - 1);
        int number = Integer.parseInt(time.substring(0, time.length() - 1));
        int sec = 0;
        if(unit.equals("h"))
            sec = number * 60 * 60;
        else if(unit.equals("m"))
            sec = number * 60;
        else if(unit.equals("s"))
            sec = number;
        return sec;
    }

    public int stringToSec(String time)
    {
        Log.d("Utils", "time : " + time);
        String regExpAlpha = "[^a-zA-Z]";
        String regExpNumber = "[^0-9]";
        String unit = "";
        String number = "";
        int sec = 0;

        /* Get alphabet only */
        unit = time.replaceAll(regExpAlpha, "");
        Log.d("Utils", "unit : " + unit);

        /* Get number only */
        number = time.replaceAll(regExpNumber, "");
        Log.d("Utils", "number : " + number);

        if(unit.equals("hour"))
        {
            sec = Integer.parseInt(number) * 60 * 60;
        }
        else if(unit.equals("min"))
        {
            sec = Integer.parseInt(number) * 60;
        }
        else if(unit.equals("sec"))
        {
            sec = Integer.parseInt(number);
        }

        Log.d("Utils", "sec : " + sec);
        return sec;
    }

    public String secToString(int sec)
    {
        int hour;
        int minute;
        int second;

        minute = sec / 60;
        hour = minute / 60;
        second = sec % 60;
        minute = minute % 60;

        StringBuilder time = new StringBuilder();
        if(hour != 0)
            time.append(hour + "hour");
        if(minute != 0)
            time.append(minute + "min");
        if(second != 0)
            time.append(second + "sec");

        return time.toString();
    }

    public void showProgress(final ProgressDialog progress, final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(bShow)
                    {
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setMessage(act.getString(R.string.please_wait));
                        progress.setCancelable(false);
                        progress.show();
                    }
                    else
                    {
                        progress.dismiss();
                    }
                } catch(Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }

    public void showProgress(final ProgressDialog progress, final Activity act, final String title, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(bShow)
                    {
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setTitle(title);
                        progress.setMessage(act.getString(R.string.please_wait));
                        progress.setCancelable(false);
                        progress.show();
                    }
                    else
                    {
                        progress.dismiss();
                    }
                } catch(Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }

    public void showProgress(final ProgressDialog progress, final Activity act, final String title, final String msg, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(bShow)
                    {
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setTitle(title);
                        progress.setMessage(msg);
                        progress.setCancelable(false);
                        progress.show();
                    }
                    else
                    {
                        progress.dismiss();
                    }
                } catch(Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }

    public void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        } catch(Exception e)
        {
        }
    }
}
