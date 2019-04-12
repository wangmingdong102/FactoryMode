
package com.mediatek.factorymode.gps;

import android.os.SystemProperties;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.FactoryMode;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

public class GPSTest extends Activity implements OnClickListener {

    private SharedPreferences mSp;
    private static final String GPS_CLOCK_PROP = "gps.clock.type";
    private Button mBtOk;
    private Button mBtFailed;
    private TextView mc0View;
    private TextView mc1View;
    private TextView minitUView;
    private TextView mlastUView;
    private TextView resultView;

    private static String c0Value = null;
    private static String c1Value = null;
    private static String initUValue = null;
    private static String lastUValue = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_test);
        if (FactoryMode.mHaveGPSTest == false) {
            finish();
            return;
        }

        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.gps_test_bt_ok);
        mBtFailed = (Button) findViewById(R.id.gps_test_bt_failed);
        mc0View = (TextView) findViewById(R.id.gps_test_c0);
        mc1View = (TextView) findViewById(R.id.gps_test_c1);
        minitUView = (TextView) findViewById(R.id.gps_test_initU);
        mlastUView = (TextView) findViewById(R.id.gps_test_lastU);
        resultView = (TextView) findViewById(R.id.gps_test_result);
        mBtOk.setOnClickListener(this);
        mBtOk.setEnabled(false);
        mBtFailed.setOnClickListener(this);
        initValue();
        mc0View.setText(c0Value);
        mc1View.setText(c1Value);
        minitUView.setText(initUValue);
        mlastUView.setText(lastUValue);
        mc0View.setText(getString(R.string.gps_test_c0_title) + ":  " + c0Value);
        mc1View.setText(getString(R.string.gps_test_c1_title) + ":  " + c1Value);
        minitUView.setText(getString(R.string.gps_test_initU_title) + ":  " + initUValue);
        mlastUView.setText(getString(R.string.gps_test_lastU_title) + ":  " + lastUValue);

        android.util.Log.d("cuixiaojun","c0Value lenth = "+c0Value.length());
        android.util.Log.d("xiaojun", " c0Value = " + c0Value + "c1Value = " + c1Value + " initUValue = " + initUValue + "lastUValue = " + lastUValue);

        if(!c0Value.equals("0")&& !c1Value.equals("0") && !initUValue.equals("0") && !lastUValue.equals("0")){
            mBtOk.setEnabled(true);
            resultView.setText(getString(R.string.gps_test_result_title) + "：" + getString(R.string.gps_test_bt_ok_title) +"！");
        } else {
            resultView.setText(getString(R.string.gps_test_result_title) + "：" + getString(R.string.gps_test_bt_failed_title) +"！");
        }
    };

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.gps_test_name,
                    (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }

    public static String getClockProp(String defaultValue){
        String clockType = SystemProperties.get(GPS_CLOCK_PROP);
        if (null == clockType || clockType.isEmpty()) {
            clockType = defaultValue;
        }
        return clockType;
    }

    public static boolean isCoClock(){
        String ss = getClockProp("unknown");
        initValue();
        char clockType = ss.charAt(1);
        if ( c0Value.equals("") || c1Value.equals("") || initUValue.equals("") || lastUValue.equals("")){
            android.util.Log.d("cuixiaojun","value = "+c0Value+" "+c1Value + " "+initUValue+" "+lastUValue);
            return false;
        } else if (clockType == '1'){
            return true;
        } else {
            return false;
        }
    }
    public static String getGpsPropValue(String defaultValue){
        return SystemProperties.get(defaultValue,"");
    }
    public static void initValue(){
        c0Value = getGpsPropValue("gps.clock.c0");
        c1Value = getGpsPropValue("gps.clock.c1");
        initUValue = getGpsPropValue("gps.clock.initU");
        lastUValue = getGpsPropValue("gps.clock.lastU");
    }
}
