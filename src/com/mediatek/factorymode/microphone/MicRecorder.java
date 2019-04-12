
package com.mediatek.factorymode.microphone;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.media.AudioSystem;
import com.mediatek.factorymode.FactoryMode;
import android.widget.LinearLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.factorymode.VUMeter;
import com.mediatek.factorymode.Recorder;

public class MicRecorder extends Activity implements OnClickListener {

    private Button mRecord;

    private Button mBtMicOk;

    private Button mBtMicFailed;

    private Button mBtSpkOk;

    private Button mBtSpkFailed;

    private LinearLayout mRgLayout;

    private RadioButton mRbMic1;

    private RadioButton mRbMic2;

    boolean mMicClick = false;

    boolean mSpkClick = false;

    boolean mic1Done = false;
    boolean mic2Done = false;

    private File mDualMicFile;

    VUMeter mVUMeter;

    private Recorder mRecorder;

    SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.micrecorder);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mRecord = (Button) findViewById(R.id.mic_bt_start);
        mRecord.setOnClickListener(this);
        mBtMicOk = (Button) findViewById(R.id.mic_bt_ok);
        mBtMicOk.setOnClickListener(this);
        mBtMicOk.setEnabled(false);
        mBtMicFailed = (Button) findViewById(R.id.mic_bt_failed);
        mBtMicFailed.setOnClickListener(this);
        mBtSpkOk = (Button) findViewById(R.id.speaker_bt_ok);
        mBtSpkOk.setOnClickListener(this);
        mBtSpkOk.setEnabled(false);
        mBtSpkFailed = (Button) findViewById(R.id.speaker_bt_failed);
        mBtSpkFailed.setOnClickListener(this);
        mVUMeter = (VUMeter) findViewById(R.id.uvMeter);
        mRgLayout = (LinearLayout) findViewById(R.id.rg_layout);
        mDualMicFile = new File ("/sys/bus/platform/drivers/fm36_driver/fm36_state");
        mRbMic1 = (RadioButton) findViewById(R.id.mic1);
        mRbMic2 = (RadioButton) findViewById(R.id.mic2);
        mRbMic1.setChecked(true);
        mRbMic1.setEnabled(true);
        mRbMic2.setEnabled(true);
        if (FactoryMode.mHavaDualMic) {
            mRgLayout.setVisibility(View.VISIBLE);
        } else {
            mRgLayout.setVisibility(View.GONE);
        }
        mRecorder = new Recorder();
        mRecorder.setVUMeter(mVUMeter);
    }

    protected void onDestroy() {
        super.onDestroy();
        mRecorder.stopPlayback();
        mRecorder.stopRecording();
        mRecorder.delete();
        if (FactoryMode.mHavaDualMic) {
            AudioSystem.setParameters("ForceUseSpecificMic=0");
        }
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
        mRecorder.stopRecording();

        mRecord.setText(R.string.Mic_start);
        mRecord.setTag("");
        mVUMeter.mCurrentAngle = 0;
        mRecorder.startPlayback();
        if (FactoryMode.mHavaDualMic) {
            if (mRbMic1.isChecked()) {
                mic1Done = true;
            } else if (mRbMic2.isChecked()) {
                mic2Done = true;
            }
            if (mic1Done && mic2Done) {
                mBtMicOk.setEnabled(true);
                mBtSpkOk.setEnabled(true);
            }
        } else {
            mBtMicOk.setEnabled(true);
            mBtSpkOk.setEnabled(true);
        }
    }

    public void isFinish(){
        if(mMicClick == true && mSpkClick == true){
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mRecord.getId()) {
            if (mRecord.getTag() == null || !mRecord.getTag().equals("ing")) {
                if (FactoryMode.mHavaDualMic) {
                    if (mRbMic2.isChecked()) {
                        AudioSystem.setParameters("ForceUseSpecificMic=2");
                    } else {
                        AudioSystem.setParameters("ForceUseSpecificMic=1");
                    }
                }
                mRbMic1.setEnabled(false);
                mRbMic2.setEnabled(false);
                start();
            } else {
                mRbMic1.setEnabled(true);
                mRbMic2.setEnabled(true);
                stopAndSave();
            }
        }
        if (v.getId() == mBtMicOk.getId()) {
            mMicClick = true;
            mBtMicFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtMicOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mBtMicFailed.getId()) {
            mMicClick = true;
            mBtMicOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtMicFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_FAILED);
        }
        if (v.getId() == mBtSpkOk.getId()) {
            mSpkClick = true;
            mBtSpkFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtSpkOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSp, R.string.speaker_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mBtSpkFailed.getId()) {
            mSpkClick = true;
            mBtSpkOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtSpkFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSp, R.string.speaker_name, AppDefine.FT_FAILED);
        }
        isFinish();
    }

    public static boolean dualMicWriter(File file, String writeValue) {
        boolean writeFlag = true;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(writeValue);
            bw.flush();
        } catch (Exception e) {
            writeFlag = false;
            e.printStackTrace();
        } finally {
            if (null != bw) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bw = null;
            }
        }
        return writeFlag;
    }
}
