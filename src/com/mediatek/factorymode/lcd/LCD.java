
package com.mediatek.factorymode.lcd;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

public class LCD extends Activity implements OnClickListener {
    private TextView mText1 = null;

    private int mNum = 0;

    private Timer timer;

    SharedPreferences mSp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        setContentView(R.layout.lcd);
        //timer = new Timer();
        initView();

    }

    protected void onDestroy() {
        super.onDestroy();
        //timer.cancel();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                    myHandler.removeMessages(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(LCD.this);
                    builder.setTitle(R.string.FMRadio_notice);
                    builder.setPositiveButton(R.string.Success,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Utils.SetPreferences(getApplicationContext(), mSp,
                                            R.string.lcd_name, AppDefine.FT_SUCCESS);
                                    finish();
                                }
                            });
                    builder.setNegativeButton(getResources().getString(R.string.Failed),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Utils.SetPreferences(getApplicationContext(), mSp,
                                            R.string.lcd_name, AppDefine.FT_FAILED);
                                    finish();
                                }
                            });
                    builder.create().show();
            }
        }
    };

    private void initView() {
        mText1 = (TextView) findViewById(R.id.test_color_text1);
        mText1.setOnClickListener(this);
    }

    private void changeColor(int num) {
        switch (num) {
            case 0:
                mText1.setBackgroundColor(Color.RED);
                break;
            case 1:
                mText1.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                mText1.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                mText1.setBackgroundColor(Color.BLACK);
                break;
            case 4:
                mText1.setBackgroundColor(Color.WHITE);
                break;
            case 5:
                Message msg = new Message();
                msg.what = 0;
                myHandler.sendMessage(msg);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        changeColor(++mNum);
    }
}
