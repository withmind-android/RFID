package com.rfid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.rfid.R;
import com.rfid.ui.base.BaseActivity;
import com.rfid.databinding.ActivitySettingBinding;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBindView(R.layout.activity_setting);

        this.setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataBinding.logout.setOnClickListener(v -> {
            openDialog("로그아웃 하시겠습니까?");
        });
        dataBinding.version.setText(" v" + getInfo());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            Log.d("finish()", "finishing!!!!");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}