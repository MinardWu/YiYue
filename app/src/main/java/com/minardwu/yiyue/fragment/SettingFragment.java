package com.minardwu.yiyue.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.minardwu.yiyue.R;

/**
 * @author MinardWu
 * @date : 2018/5/22
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference);
//        SwitchPreference pref = (SwitchPreference) findPreference("auto_reversal");
//        pref.setWidgetLayoutResource(R.layout.preference_switch_layout);
    }

}
