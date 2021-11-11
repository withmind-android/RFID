package com.rfid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil
{
    private SharedPreferences mPref;
    private Context mContext;

    public static final String KEY_CONNECT_BT_MACADDR = "connect_bt_macaddr";
    public static final String KEY_CONNECT_BT_NAME = "connect_bt_name";

    public static final String KEY_SCAN_AUTO_ENABLE = "KeyAcanAutoEnable";
    public static final String KEY_SCAN_CONTINUOUS_ENABLE = "KeyScanContinuousEnable";
    public static final String KEY_SCAN_AUTO_INTERVAL = "KeyScanAutoInterval";
    public static final String KEY_SAVE_LOG = "keySaveLog";
    public static final String KEY_SAVE_TO_CSV = "keySaveToCsv";
    public static final String KEY_UPDATE_TEMP = "key_Temperature";
    public static final String KEY_FIRST_START = "key_first_start";
    public static final String KEY_FIRST_CONNECT = "key_first_connect";
    public static final String KEY_RUNNING_ACTIVITY_CHECK = "key_running_activity_check";
    public static final String KEY_BEEP_SOUND = "key_beep_sound";
    public static final String KEY_RESULT_TYPE = "key_result_type";
    public static final String KEY_SELECT = "key_select";

    public static final String KEY_JSON_FILE_NAME = "key_json_file_name";

    //Auto Update
    public static final String KEY_RFU_AUTO_UPDATE = "key_rfu_auto_update";
    public static final String KEY_RFU_FILE_PATH = "key_rfu_file_path";
    public static final String KEY_JSON_AUTO_UPDATE = "key_json_auto_update";

    public static final String KEY_OPEN_OPTION = "key_open_option";
    public static final String KEY_OPEN_ERROR = "key_open_error";

    public static final String KEY_CREATED_HOME_APP = "key_created_home_app";

    public PreferenceUtil(Context context)
    {
        mContext = context;
        if(mPref == null)
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putStringPrefrence(String key, String value)
    {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringPreference(String key)
    {
        return mPref.getString(key, null);
    }

    public String getStringPreference(String key, String defaultValue)
    {
        return mPref.getString(key, defaultValue);
    }


    public void putBooleanPreference(String key, boolean value)
    {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    public boolean getBooleanPreference(String key)
    {
        return mPref.getBoolean(key, false);
    }

    public boolean getBooleanPreference(String key, boolean defaultValue)
    {
        return mPref.getBoolean(key, defaultValue);
    }

    public int getIntPreference(String key)
    {
        return getIntPreference(key, 0);
    }

    public int getIntPreference(String key, int defaultValue)
    {
        return mPref.getInt(key, defaultValue);
    }

    public void putIntPreference(String key, int value)
    {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLongPreference(String key, long value)
    {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void removePreference(String key)
    {
        mPref.edit().remove(key).commit();
    }
}
