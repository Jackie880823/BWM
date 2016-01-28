package com.bondwithme.BondCorp.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.bondwithme.BondCorp.R;


/**
 * Created by christepherzhang on 15/6/29.
 */
public class CountryCodeUtil {

    //自动获取国家区号方法
    public static String GetCountryZipCode(Context context) {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
