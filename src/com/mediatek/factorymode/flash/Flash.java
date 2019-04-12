
package com.mediatek.factorymode.flash;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.factorymode.FactoryMode;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import java.util.List;

public class Flash extends Activity implements OnClickListener {

    public static final String TAG = "FlashTest";

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

    private Camera mCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash);
        if (!FactoryMode.mHaveFlash) {
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.flash_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.flash_bt_failed);
        mBtFailed.setOnClickListener(this);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static void turnLightOn(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
               // mCamera.startPreview();
                Log.d(TAG, " Turn on the flash ");
            } else {
                Log.e(TAG, "FLASH_MODE_TORCH not supported");
            }
        }
    }

    public static void turnLightOff(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                Log.d(TAG, " Turn off the flash ");
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }
    public void onResume() {
        super.onResume();
        mCamera = getCameraInstance();
        Log.d(TAG, " mCamera = " + mCamera);
        turnLightOn(mCamera);
    }

    public void onPause() {
        super.onPause();
        turnLightOff(mCamera);
        //mCamera.setPreviewCallback(null) ;
        //mCamera.stopPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.flash_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
