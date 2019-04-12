
package com.mediatek.factorymode.simcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
//import com.mediatek.common.telephony.ITelephonyEx;
//import com.android.internal.telephony.gemini.GeminiPhone;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.common.featureoption.FeatureOption;

public class SimCard extends Activity {

//    private GeminiPhone mGeminiPhone;

    private boolean Sim1State = false;

    private boolean Sim2State = false;

    private String mSimStatus = "";
/*     on: Mon, 14 Apr 2014 17:32:17 +0800
 * bugfix #73732 add for kk
 */
//    private ITelephonyEx mITelephonyEx;
// End of

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*     on: Mon, 14 Apr 2014 17:33:30 +0800
 * bugfix #73732 add for kk
 */
//        mITelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));
// End of
        if (FeatureOption.MTK_GEMINI_SUPPORT) {
//            mGeminiPhone = (GeminiPhone) PhoneFactory.getDefaultPhone();
/*     on: Mon, 14 Apr 2014 15:52:05 +0800
 * bugfix #73732 add for kk
            Sim1State = mGeminiPhone.isSimInsert(PhoneConstants.GEMINI_SIM_1)
                    & mGeminiPhone.isRadioOnGemini(PhoneConstants.GEMINI_SIM_1);
            Sim2State = mGeminiPhone.isSimInsert(PhoneConstants.GEMINI_SIM_2)
                    & mGeminiPhone.isRadioOnGemini(PhoneConstants.GEMINI_SIM_2);
 */
//            Sim1State = isSimInserted(PhoneConstants.GEMINI_SIM_1)
//                    & mGeminiPhone.isRadioOnGemini(PhoneConstants.GEMINI_SIM_1);
//            Sim2State = isSimInserted(PhoneConstants.GEMINI_SIM_2)
//                    & mGeminiPhone.isRadioOnGemini(PhoneConstants.GEMINI_SIM_2);
// End of
            if (Sim1State == true) {
                mSimStatus += getString(R.string.sim1_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim1_info_failed) + "\n";
            }
            if (Sim2State == true) {
                mSimStatus += getString(R.string.sim2_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim2_info_failed) + "\n";
            }
        } else {
            Sim1State = TelephonyManager.getDefault().hasIccCard();
            if (Sim1State == true) {
                mSimStatus += getString(R.string.sim_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim_info_failed) + "\n";
            }
        }
    }

    public void onResume() {
        super.onResume();
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(SimCard.this);
        builder.setTitle(R.string.FMRadio_notice);
        builder.setMessage(mSimStatus);
        builder.setCancelable(false);
        if ((FeatureOption.MTK_GEMINI_SUPPORT && Sim1State && Sim2State)
                || (!FeatureOption.MTK_GEMINI_SUPPORT && Sim1State)) {
            builder.setPositiveButton(R.string.Success, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    intent.putExtra("result", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
        builder.setNegativeButton(getResources().getString(R.string.Failed),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        intent.putExtra("result", false);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        builder.create().show();
    }

/*     on: Mon, 14 Apr 2014 17:34:02 +0800
 * bugfix #73732 add for kk
 */
    private boolean isSimInserted(int slotId) {
        boolean isSimInserted = false;
        isSimInserted = TelephonyManager.getDefault().hasIccCard();
//        if (mITelephonyEx != null) {
//            try {
//                isSimInserted = mITelephonyEx.hasIccCard(slotId);
//            } catch (RemoteException e) {
//                android.util.Log.i("libing", "RemoteException happens......");
//            }
//        }
        return isSimInserted;
    }
// End of
}
