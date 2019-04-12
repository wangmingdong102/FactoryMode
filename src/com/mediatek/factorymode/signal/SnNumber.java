package com.mediatek.factorymode.signal;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
//import com.mediatek.common.telephony.ITelephonyEx;
import android.os.RemoteException;
import android.os.ServiceManager;

public class SnNumber extends Activity {

    SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
/*     on: Wed, 16 Apr 2014 10:17:57 +0800
 * implement #73121 for kk
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
 */
// End of
        AlertDialog.Builder builder = new AlertDialog.Builder(SnNumber.this);
        builder.setTitle(R.string.sn_number_title);
/*     on: Wed, 16 Apr 2014 10:22:14 +0800
 * implement #73121 for kk
        builder.setMessage(tm.getSN());
 */
        String sn = getSN();
        builder.setMessage(sn);
// End of
        builder.setCancelable(false);
        if (!"".equals(sn)) {
            builder.setPositiveButton(R.string.Success,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.SetPreferences(SnNumber.this, mSp, R.string.snnumber_name,
                                AppDefine.FT_SUCCESS);
                        finish();
                    }
                });
        }
        builder.setNegativeButton(R.string.Failed, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utils.SetPreferences(SnNumber.this, mSp, R.string.snnumber_name,
                        AppDefine.FT_FAILED);
                finish();
            }
        });
        builder.create().show();
    }
/*     on: Wed, 16 Apr 2014 11:07:39 +0800
 * implement #73121 for kk
 */
    private String getSN() {
        String serialNumber = "unknown";
//        ITelephonyEx mITelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));
//        if (mITelephonyEx != null) {
//            try {
//                serialNumber = mITelephonyEx.getSerialNumber();
//            } catch (RemoteException e) {
//                android.util.Log.i("libing", "RemoteException happens......");
//            }
//        }
        return serialNumber;
    }
// End of
}
