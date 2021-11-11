package com.rfid.ui.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.rfid.ui.login.LoginActivity;
import com.rfid.R;
import com.rfid.RFIDApplication;
import com.rfid.util.SharedPreferencesPackage;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class BaseActivity<B extends ViewDataBinding> extends AppCompatActivity
{
    protected B dataBinding;
//    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    protected void onBindView(int layoutId) {
        dataBinding = DataBindingUtil.setContentView(this, layoutId);
    }
    @Override
    protected void onDestroy()
    {
//        if (!compositeDisposable.isDisposed()) {
//            compositeDisposable.dispose();
//        }
        super.onDestroy();
    }

    public RFIDApplication getRFIDApplication()
    {
        return (RFIDApplication) getApplication();
    }

    public void showToast(final Context context, final String msg, final boolean isLong)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(context, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showWarning(String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void openDialog(String text) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            SharedPreferencesPackage.INSTANCE.setAutoLogin("", "", false);

            dialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
        alert.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });

        alert.setMessage(getString(R.string.app_name) + " v" + getInfo() + "\n" + text);
        alert.show();
    }

    public String getInfo() {
        String version = getString(R.string.unknown);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            if(pi != null) {
                version = pi.versionName;
                return version;
            }
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return getString(R.string.unknown);
    }


}
