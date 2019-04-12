package com.mediatek.factorymode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mediatek.common.featureoption.FeatureOption;
import com.mediatek.factorymode.signal.SnNumber;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
/*   on: Mon, 06 Apr 2015 23:40:49 +0800
 * implement #124374, add flash test
 */
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.pm.PackageManager;
import com.mediatek.factorymode.flash.Flash;
// End of
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.mediatek.factorymode.gps.GPSTest;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
//import com.mediatek.common.telephony.ITelephonyEx;
import android.os.RemoteException;
import android.os.ServiceManager;

public class FactoryMode extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */

    private List<String> mListData;

    private SharedPreferences mSp = null;

    private GridView mGrid;

    private MyAdapter mAdapter;

    private TextView mResultView;

    private TextView mMotherBoardResult;

    public static boolean mHavePsensor = true;

    public static boolean mHaveMsensor = true;

    public static boolean mHaveFM = true;

/*   on: Mon, 06 Apr 2015 23:24:16 +0800
 * implement #124374, add flash test
 */
    public static boolean mHaveFlash = true;
// End of
    public static boolean mHaveBatteryCheck = true;
    public static boolean mHaveWifi = true;

    public static boolean mHaveGPS = true;
/*     on: Sun,26 May 2015 20:21:50 +0800
 * implement #131274,add YGPS test
 */
    public static boolean mHaveGPSTest = true;
//End of

    public static boolean mHaveGyroscope = true;

/*     on: Fri, 01 Nov 2013 19:15:47 +0800
 * porting hall test from 89
 */
    public static boolean mhall = true;
// End of

    public static boolean mHaveNFC = true;

    public static boolean mHaveFuelGauge = true;

    public static boolean mHavaDualMic = false;

    public static boolean mIsSharedSDcard = false;

    private SharedPreferences mPassVerificationSp = null;

    private SharedPreferences mResultSp = null;

    public static Context mContext;

    static ArrayList<Integer> itemString = new ArrayList<Integer>();

    static final int itemList[] = {
/*   on: Sat, 28 Mar 2015 16:23:06 +0800
 * implement #124374, add flash test
 */
            R.string.flash_name,
// End of
            R.string.touchscreen1_name,
            R.string.touchscreen2_name,
/*   on: Wed, 08 Apr 2015 16:02:18 +0800
 * add battery check
 */
            R.string.battery_check,
// End of
            R.string.battery_name,
            R.string.lcd_name,
            R.string.backlight_name,
            R.string.KeyCode_name,
            R.string.vibrator_name,
            R.string.speaker_name,
            R.string.earphone_name,
            R.string.headset_name,
            R.string.microphone_name,
            R.string.telephone_name,
            R.string.fmradio_name,
            R.string.wifi_name,
            R.string.bluetooth_name,
            R.string.gps_name,
            R.string.gps_test_name,
            R.string.gyroscope_name,
            R.string.lsensor_name,
            R.string.gsensor_name,
            R.string.msensor_name,
/*    on: Tue, 28 May 2013 18:59:31 +0800
 * implement #38369
            R.string.sdcardformat_name,
 */
// End of
            R.string.psensor_name,
            R.string.camera_name,
            R.string.subcamera_name,
            R.string.sim_name,
            R.string.snnumber_name,
/*    on: Tue, 28 May 2013 19:00:00 +0800
 * implement #38369
 */
            R.string.sdcard_name,
            R.string.sdcardformat_name,
            R.string.board_info,
            R.string.build_version,
// End of
/*     on: Fri, 01 Nov 2013 19:16:30 +0800
 * porting hall test from 89
 */
            R.string.hall,
// End of
            R.string.nfc_name,
            R.string.fuelgauge_name
    };

    private Button mBtAll;

    private Button mBtAuto;

    private Button mReset;
/*     on: Wed, 16 Apr 2014 10:10:18 +0800
 * implement #73121 for kk
 */
//    private ITelephonyEx mITelephonyEx;
// End of

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
/*     on: Wed, 16 Apr 2014 10:10:50 +0800
 * implement #73121 for kk
 */
//        mITelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));
// End of
        if (!FeatureOption.MTK_FM_SUPPORT) {
            mHaveFM = false;
        }
        if (!FeatureOption.MTK_WLAN_SUPPORT) {
            mHaveWifi = false;
        }
        File file = new File("/sys/bus/platform/drivers/als_ps/ps");
        if (!file.exists()) {
            mHavePsensor = false;
        }

        File file1 = new File("/dev/msensor");
        if (!file1.exists()) {
            mHaveMsensor = false;

        }

        if (!FeatureOption.MTK_GPS_SUPPORT) {
            mHaveGPS = false;
        }
        if (!GPSTest.isCoClock()){
            mHaveGPSTest = false;
        }

/*   on: Wed, 08 Apr 2015 16:06:36 +0800
 * add battery check
 */
         File batteryFile1 = new File("sys/devices/platform/fan5405-user/fan5405_safety_limit_reg");
         File batteryFile2 = new File("/sys/bus/platform/drivers/ncp1854-user/ncp1854-user/ncp1854_vchr");
         if (!batteryFile1.exists() && !batteryFile2.exists()) {
             mHaveBatteryCheck = false;
         }
// End of
/*     on: Tue, 17 Dec 2013 18:35:24 +0800
 * TODO: replace this line with your comment
 */
        File file2 = new File("/sys/bus/i2c/drivers/MPU3000/2-0068/name");
        File file3 = new File("/sys/bus/platform/drivers/gyroscope/chipinfo");
// End of
        if (!file2.exists() && !file3.exists()) {
            mHaveGyroscope = false;
        }
/*     on: Fri, 01 Nov 2013 19:17:56 +0800
 * porting hall test from 89
 */
        File fileHall = new File ("/sys/class/switch/hall/state");
        if (!fileHall.exists()) {
            mhall = false;
        }
// End of
        if (!FeatureOption.MTK_NFC_SUPPORT) {
            mHaveNFC = false;
        }
        File fileFuelGauge = new File("/sys/bus/platform/drivers/bq27520/bq27520_capacity");
        if (!fileFuelGauge.exists()) {
            mHaveFuelGauge = false;
        }

        File fileDualMic = new File ("/sys/bus/platform/drivers/fm36_driver/fm36_state");
        if (FeatureOption.MTK_DUAL_MIC_SUPPORT) {
            mHavaDualMic = true;
        }
        if (FeatureOption.MTK_SHARED_SDCARD) {
            mIsSharedSDcard = true;
        }
/*   on: Mon, 06 Apr 2015 23:25:09 +0800
 * implement #124374, add flash test
 */
        Camera camera = null;
        Parameters parameters = null;
        List<String> supportedFlash = null;
        camera = Flash.getCameraInstance();
        if (camera != null) {
            parameters = camera.getParameters();
        }
        if (camera != null && parameters != null) {
            supportedFlash = parameters.getSupportedFlashModes();
        }
        Log.d("FlashTest", " supportedFlash = " + supportedFlash);
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
                && (supportedFlash == null || supportedFlash.size() == 0)) {
            mHaveFlash = false;
            Log.d("FlashTest", "this device does't support flash function");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
// End of
        for (int i = 0; i < itemList.length; i++) {
/*   on: Wed, 08 Apr 2015 19:14:19 +0800
 * implement #124374, add flash test
 */
            if (itemList[i] == R.string.flash_name && !mHaveFlash)
                continue;
// End of
            if (itemList[i] == R.string.fmradio_name && !mHaveFM)
                continue;
            if (itemList[i] == R.string.gps_name && !mHaveGPS)
                continue;
            if (itemList[i] == R.string.battery_check && !mHaveBatteryCheck)
                continue;
            if(itemList[i] == R.string.gps_test_name && !mHaveGPSTest)
                continue;
            if (itemList[i] == R.string.gyroscope_name && !mHaveGyroscope)
                continue;
            if (itemList[i] == R.string.msensor_name && !mHaveMsensor)
                continue;
            if (itemList[i] == R.string.lsensor_name && !mHavePsensor)
                continue;
            if (itemList[i] == R.string.psensor_name && !mHavePsensor)
                continue;
            if (itemList[i] == R.string.sdcardformat_name && mIsSharedSDcard)
                continue;
            if (itemList[i] == R.string.hall && !mhall)
                continue;
            if (itemList[i] == R.string.nfc_name && !mHaveNFC)
                continue;
            if (itemList[i] == R.string.fuelgauge_name && !mHaveFuelGauge)
                continue;

            itemString.add(itemList[i]);
        }

        mBtAuto = (Button) findViewById(R.id.main_bt_autotest);
        mBtAuto.setOnClickListener(cl);
        mBtAll = (Button) findViewById(R.id.main_bt_alltest);
        mBtAll.setOnClickListener(cl);
        mReset = (Button) findViewById(R.id.verfication_reset);
        mReset.setOnClickListener(cl);
        mGrid = (GridView) findViewById(R.id.main_grid);
        mResultView = (TextView) findViewById(R.id.verification_result);
        mMotherBoardResult = (TextView) findViewById(R.id.motherboard_verification_result);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mListData = getData();
        mPassVerificationSp = getSharedPreferences("PassVerification", Context.MODE_PRIVATE);
        mPassVerificationSp.edit().putInt("TotalItems", mListData.size()).commit();
        mAdapter = new MyAdapter(this);
/*     on: Tue, 01 Jul 2014 17:56:55 +0800
 * #84226 when back ont change location
 */
        mGrid.setAdapter(mAdapter);
// End of
    }

    @Override
    protected void onResume() {
        super.onResume();
/*     on: Tue, 01 Jul 2014 18:02:24 +0800
 * #84226 when back ont change location
        mGrid.setAdapter(mAdapter);
 */
// End of
        mGrid.setOnItemClickListener(this);
        int result = NvRAMAdapter.readNvRAM();
        if (result == 1) {
            mResultView.setText(R.string.pass_verification);
            mResultView.setTextColor(Color.GREEN);
            mReset.setVisibility(View.VISIBLE);
        } else {
            mResultView.setText(R.string.fail_verification);
            mResultView.setTextColor(Color.RED);
            mReset.setVisibility(View.GONE);
        }
        if ((null == getSN()) || ("".equals(getSN()))) {
            mMotherBoardResult.setText(R.string.fail_get_motherboard_status);
            mMotherBoardResult.setTextColor(Color.RED);
        } else if (getSN().endsWith("10")) {
            mMotherBoardResult.setText(R.string.pass_motherboard_verification);
            mMotherBoardResult.setTextColor(Color.GREEN);
        } else {
            mMotherBoardResult.setText(R.string.fail_motherboard_verification);
            mMotherBoardResult.setTextColor(Color.RED);
        }
/*     on: Tue, 01 Jul 2014 18:00:56 +0800
 * #84226 when back ont change location
 */
        mAdapter.notifyDataSetChanged();
// End of
    }

    private String getSN() {
/*     on: Wed, 16 Apr 2014 10:13:25 +0800
 * implement #73121 for kk
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSN();
 */
        String serialNumber = "unknown";
//        if (mITelephonyEx != null) {
//            try {
//                serialNumber = mITelephonyEx.getSerialNumber();
//            } catch (RemoteException e) {
//                android.util.Log.i("libing", "RemoteException happens......");
//            }
//        }
        return serialNumber;
// End of
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public View.OnClickListener cl = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            int reqId = -1;
            if (v.getId() == mBtAuto.getId()) {
                intent.setClassName("com.mediatek.factorymode", "com.mediatek.factorymode.AutoTest");
                reqId = AppDefine.FT_AUTOTESTID;
                startActivityForResult(intent, reqId);
            }
            if (v.getId() == mBtAll.getId()) {
                intent.setClassName("com.mediatek.factorymode", "com.mediatek.factorymode.AllTest");
                reqId = AppDefine.FT_ALLTESTID;
                startActivityForResult(intent, reqId);
            }
            if (v.getId() == mReset.getId()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FactoryMode.this);
                builder.setTitle(R.string.pass_reset);
                builder.setMessage(R.string.config_reset);
                builder.setPositiveButton(R.string.pass_reset,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mReset.setVisibility(View.GONE);
                                mResultView.setText(R.string.fail_verification);
                                mResultView.setTextColor(Color.RED);
                                mReset.setVisibility(View.GONE);
                                NvRAMAdapter.writeNvRam(0);
                                init();
                                onResume();
                            }
                        });
                builder.setNegativeButton(R.string.cancel_reset,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        });
                builder.create().show();
            }
        }
    };

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public MyAdapter(FactoryMode factoryMode, int factoryButton) {
        }

        public int getCount() {
            if (mListData == null) {
                return 0;
            }
            return mListData.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.main_grid, null);
            TextView textview = (TextView) convertView.findViewById(R.id.factor_button);
            textview.setText(mListData.get(position));
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int height = wm.getDefaultDisplay().getHeight();
            textview.setHeight(height / 9);
            SetColor(textview);
            return convertView;
        }
    }

    private void init() {
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        Editor editor = mSp.edit();
        for (int i = 0; i < itemString.size(); i++) {
            editor.putString(getString(itemString.get(i)), AppDefine.FT_DEFAULT);
        }
        editor.putString(getString(R.string.headsethook_name), AppDefine.FT_DEFAULT);
        editor.commit();
    }

    private void SetColor(TextView s) {
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        for (int i = 0; i < itemString.size(); i++) {
            if (getResources().getString(itemString.get(i)).equals(s.getText().toString())) {
                String name = mSp.getString(Integer.toString(itemString.get(i)), null);
                if ((AppDefine.FT_SUCCESS).equals(name)) {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
                } else if ((AppDefine.FT_FAILED).equals(name)) {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.Red));
                } else {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                }
            }
        }
    }

    private List<String> getData() {
        List<String> items = new ArrayList<String>();
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < itemString.size(); i++) {
            if (true == pre.getBoolean(getString(itemString.get(i)), true)) {
                items.add(getString(itemString.get(i)));
            }
        }
        return items;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            String name = mListData.get(position);
            String classname = null;
            if (getString(R.string.speaker_name) == name) {
                classname = "com.mediatek.factorymode.audio.AudioTest";
/*   on: Sat, 28 Mar 2015 16:24:30 +0800
 * implement #124374, add flash test
 */
            } else if (getString(R.string.flash_name) == name) {
                classname = "com.mediatek.factorymode.flash.Flash";
// End of
            } else if (getString(R.string.battery_name) == name) {
                classname = "com.mediatek.factorymode.BatteryLog";
            } else if (getString(R.string.touchscreen1_name) == name) {
                classname = "com.mediatek.factorymode.touchscreen.LineTest1";
            } else if (getString(R.string.touchscreen2_name) == name) {
                classname = "com.mediatek.factorymode.touchscreen.LineTest2";
            } else if (getString(R.string.battery_check) == name) {
                classname = "com.mediatek.factorymode.battery.BatteryCheck";
            } else if (getString(R.string.camera_name) == name) {
                classname = "com.mediatek.factorymode.camera.CameraTest";
            } else if (getString(R.string.wifi_name) == name) {
                classname = "com.mediatek.factorymode.wifi.WiFiTest";
            } else if (getString(R.string.bluetooth_name) == name) {
                classname = "com.mediatek.factorymode.bluetooth.Bluetooth";
            } else if (getString(R.string.headset_name) == name) {
                classname = "com.mediatek.factorymode.headset.HeadSet";
            } else if (getString(R.string.earphone_name) == name) {
                classname = "com.mediatek.factorymode.earphone.Earphone";
            } else if (getString(R.string.vibrator_name) == name) {
                classname = "com.mediatek.factorymode.vibrator.Vibrator";
            } else if (getString(R.string.telephone_name) == name) {
                classname = "com.mediatek.factorymode.signal.Signal";
            } else if (getString(R.string.gps_name) == name) {
                classname = "com.mediatek.factorymode.gps.GPS";
            } else if (getString(R.string.gps_test_name) == name) {
                classname = "com.mediatek.factorymode.gps.GPSTest";
            } else if (getString(R.string.backlight_name) == name) {
                classname = "com.mediatek.factorymode.backlight.BackLight";
            } else if (getString(R.string.memory_name) == name) {
                classname = "com.mediatek.factorymode.memory.Memory";
            } else if (getString(R.string.microphone_name) == name) {
                classname = "com.mediatek.factorymode.microphone.MicRecorder";
            } else if (getString(R.string.gsensor_name) == name) {
                classname = "com.mediatek.factorymode.sensor.GSensor";
            } else if (getString(R.string.msensor_name) == name) {
                classname = "com.mediatek.factorymode.sensor.MSensor";
            } else if (getString(R.string.lsensor_name) == name) {
                classname = "com.mediatek.factorymode.sensor.LSensor";
            } else if (getString(R.string.psensor_name) == name) {
                classname = "com.mediatek.factorymode.sensor.PSensor";
            } else if (getString(R.string.sdcard_name) == name) {
                classname = "com.mediatek.factorymode.sdcard.SDCard";
            } else if (getString(R.string.sdcardformat_name) == name) {
                classname = "com.mediatek.factorymode.sdcard.SDCardFormat";
            } else if (getString(R.string.fmradio_name) == name) {
                classname = "com.mediatek.factorymode.fmradio.FMRadio";
            } else if (getString(R.string.KeyCode_name) == name) {
                classname = "com.mediatek.factorymode.KeyCode";
            } else if (getString(R.string.lcd_name) == name) {
                classname = "com.mediatek.factorymode.lcd.LCD";
            } else if (getString(R.string.sim_name) == name) {
                classname = "com.mediatek.factorymode.simcard.SimCardActivity";
            } else if (getString(R.string.subcamera_name) == name) {
                classname = "com.mediatek.factorymode.camera.SubCamera";
            } else if (getString(R.string.snnumber_name) == name) {
                classname = "com.mediatek.factorymode.signal.SnNumber";
            } else if (getString(R.string.gyroscope_name) == name) {
                classname = "com.mediatek.factorymode.sensor.Gyroscope";
            } else if (getString(R.string.board_info) == name) {
                classname = "com.mediatek.factorymode.boardinfo.Boardinfo";
            } else if (getString(R.string.build_version) == name) {
                classname = "com.mediatek.factorymode.buildversion.Buildversion";
            }
/*     on: Fri, 01 Nov 2013 19:20:17 +0800
 * porting hall test from 89
 */
            else if (getString(R.string.hall) == name) {
                classname = "com.mediatek.factorymode.hall.Hall";
            }
// End of
            else if (getString(R.string.nfc_name) == name) {
                classname = "com.mediatek.factorymode.nfc.NFC";
            } else if (getString(R.string.fuelgauge_name) == name) {
                classname = "com.mediatek.factorymode.fuelgauge.FuelGauge";
            }
            intent.setClassName(this, classname);
            this.startActivity(intent);

        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.PackageIerror);
            builder.setMessage(R.string.Packageerror);
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        Intent intent = new Intent(FactoryMode.this, Report.class);
        startActivity(intent);
    }

}
