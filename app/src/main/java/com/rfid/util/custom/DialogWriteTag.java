package com.rfid.util.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

//import device.sdk.RFIDManager;
import com.rfid.R;

public class DialogWriteTag extends Activity {
    private final String TAG = getClass().getSimpleName();
//    private RFIDManager mRfidMgr;

    private EditText etCurrentTagId;
    private EditText etTagIdToWrite;
    private Button btnCancel;
    private Button btnOk;

    public static String CURRENT_TAG_ID = "current_tag_id";
    public static String TAG_ID_TO_WRITE = "tag_id_to_write";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edittext);
        setFinishOnTouchOutside(false);
        init();
    }

    private void init() {
//        mRfidMgr = RFIDManager.getInstance();

        etCurrentTagId = findViewById(R.id.et_current_tag_id);
        etTagIdToWrite = findViewById(R.id.et_tag_id_to_write);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk = findViewById(R.id.btn_apply);
        btnCancel.setOnClickListener(onClickListener);
        btnOk.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_cancel) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
            else if(v.getId() == R.id.btn_apply) {
                Intent intent = new Intent();
                intent.putExtra(CURRENT_TAG_ID, etCurrentTagId.getText().toString());
                intent.putExtra(TAG_ID_TO_WRITE, etTagIdToWrite.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

}
