
package com.mediatek.factorymode.headset;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.factorymode.VUMeter;
import com.mediatek.factorymode.Recorder;

public class HeadSet extends Activity implements OnClickListener {

    private Button mRecord;

    private Button mBtOk;

    private Button mBtFailed;

    boolean mMicClick = false;

    boolean mSpkClick = false;

    VUMeter mVUMeter;

    private Recorder mRecorder;

    SharedPreferences mSp;

    private final static int STATE_HEADSET_PLUG = 0;

    private final static int STATE_HEADSET_UNPLUG = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headset);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mRecord = (Button) findViewById(R.id.mic_bt_start);
        mRecord.setOnClickListener(this);
        mRecord.setEnabled(false);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtOk.setOnClickListener(this);
        mBtOk.setEnabled(false);
        mBtFailed = (Button) findViewById(R.id.bt_failed);
        mBtFailed.setOnClickListener(this);
        mVUMeter = (VUMeter) findViewById(R.id.uvMeter);

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);
        mRecorder = new Recorder();
        mRecorder.setVUMeter(mVUMeter);
    }

    protected void onDestroy() {
        super.onDestroy();
        mRecorder.stopPlayback();
        mRecorder.stopRecording();
        unregisterReceiver(mReceiver);
        h.removeCallbacks(ra);
        mRecorder.delete();
    }

    Handler h = new Handler();

    Runnable ra = new Runnable() {
        @Override
        public void run() {
            mVUMeter.invalidate();
            h.postDelayed(this, 100);
        }
    };

    private void start() {
        h.post(ra);
        mRecorder.stopPlayback();
        try {
            mRecorder.startRecording(MediaRecorder.OutputFormat.THREE_GPP,".3gpp", this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
        }
        mRecord.setTag("ing");
        mRecord.setText(R.string.Mic_stop);
    }

    private void stopAndSave() {
        h.removeCallbacks(ra);
        mRecord.setText(R.string.Mic_start);
        mRecord.setTag("");
        mVUMeter.mCurrentAngle = 0;
        mRecorder.stopRecording();
        mRecorder.startPlayback();
        mBtOk.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mRecord.getId()) {
            if (mRecord.getTag() == null || !mRecord.getTag().equals("ing")) {
                start();
            } else {
                stopAndSave();
            }
        }
        if (v.getId() == mBtOk.getId()) {
            Utils.SetPreferences(this, mSp, R.string.headset_name, AppDefine.FT_SUCCESS);
            finish();
        } else if (v.getId() == mBtFailed.getId()) {
            Utils.SetPreferences(this, mSp, R.string.headset_name, AppDefine.FT_FAILED);
            finish();
        }
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATE_HEADSET_PLUG:
                    mRecord.setText(R.string.Mic_start);
                    mRecord.setEnabled(true);
                    break;
                case STATE_HEADSET_UNPLUG:
                    mRecorder.stopRecording();
                    mRecord.setTag("");
                    mVUMeter.mCurrentAngle = 0;
                    mRecorder.stopPlayback();
                    mRecord.setText(R.string.HeadSet_tips);
                    mRecord.setEnabled(false);
                    break;
            }
        }
    };

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.getIntExtra("state", 0) == 1) {
                    myHandler.sendEmptyMessage(STATE_HEADSET_PLUG);
                } else {
                    myHandler.sendEmptyMessage(STATE_HEADSET_UNPLUG);
                }
            }
        }
    };
}
