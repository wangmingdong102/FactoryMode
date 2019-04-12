
package com.mediatek.factorymode.battery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.FactoryMode;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.common.featureoption.FeatureOption;

/*   on: Tue, 02 Dec 2014 10:57:16 +0800
 * modify mPath0 path when shared open & swap close
 */
import com.mediatek.storage.StorageManagerEx;
// End of

public class BatteryCheck extends Activity implements OnClickListener {

    public static final String TAG = "BatteryCheck";

    private TextView mInfo;

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

    String filePath = "";

    String filePath1 = "/sys/devices/platform/fan5405-user/fan5405_safety_limit_reg";
    String filePath2 = "/sys/bus/platform/drivers/ncp1854-user/ncp1854-user/ncp1854_vchr";
    File batteryFile1 = new File("/sys/devices/platform/fan5405-user/fan5405_safety_limit_reg");
    File batteryFile2 = new File("/sys/bus/platform/drivers/ncp1854-user/ncp1854-user/ncp1854_vchr");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_check);
        if (!FactoryMode.mHaveBatteryCheck) {
            finish();
            return;
        }
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mInfo = (TextView) findViewById(R.id.battery_check_info);
        mBtOk = (Button) findViewById(R.id.battery_check_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.battery_check_failed);
        mBtFailed.setOnClickListener(this);

        if (batteryFile1.exists()) {
            filePath = filePath1;
        } else if (batteryFile2.exists()) {
            filePath = filePath2;
        } else {
            return;
        }

        mInfo.setText(getResultInfo(filePath));
    }

    private String getResultInfo(String filePath) {
        String result = getBatteryValue(filePath);
        int compareResult = result.compareTo("0x29");
        if (filePath.equals("")) {
            result = getResources().getString(R.string.unknow_battery);
        }
        if (filePath.equals(filePath1)) {
            if (result.equals("0x70")) {
                result = result + ":" + getResources().getString(R.string.normal_battery);
            } else if (result.equals("0x78") || result.equals("0x77")) {
                result = result + ":" +  getResources().getString(R.string.high_battery);
            } else {
                result = result + ":" + getResources().getString(R.string.unknow_battery);
            }
        } else if (filePath.equals(filePath2)) {
            if (compareResult < 0) {
                result = result + ":" + getResources().getString(R.string.normal_battery);
            } else if (compareResult >= 0) {
                result = result + ":" +  getResources().getString(R.string.high_battery);
            } else {
                result = result + ":" + getResources().getString(R.string.unknow_battery);
            }
        }

        return result;
    }

    private String getBatteryValue(String filePath) {
        String state = "";
        char[] buffer = new char[8];

        try {
            FileReader file = new FileReader(filePath);
            int len = file.read(buffer, 0, 8);
            file.close();
            state = (new String(buffer, 0, len)).trim();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "This kernel does not have this support path : sys/devices/platform/fan5405-user/fan5405_safety_limit_reg");
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        Log.v(TAG, "Battery check state ===== " + state);
        return state;
    }

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.battery_check,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
