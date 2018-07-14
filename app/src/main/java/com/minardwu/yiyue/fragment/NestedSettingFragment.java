package com.minardwu.yiyue.fragment;

import android.preference.PreferenceFragment;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.http.mock.MockData;

import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;


/**
 * @author MinardWu
 * @date : 2018/7/15
 */

public class NestedSettingFragment extends PreferenceFragment{


    private static final String TAG_KEY = "NESTED_KEY";

    public static NestedSettingFragment newInstance(String key) {
        NestedSettingFragment fragment = new NestedSettingFragment();
        Bundle args = new Bundle();
        args.putString(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPreferenceResource();
    }

    /**
     * 根据Activity传进的参数创造相应的二级Fragment
     */
    private void checkPreferenceResource() {
        String key = getArguments().getString(TAG_KEY);
        switch (key) {
            case "settings_mock_data":
                addPreferencesFromResource(R.xml.preference_mock_data);
                SwitchPreference switchPreference = (SwitchPreference) findPreference("settings_use_mock_song_data");
                switchPreference.setOnPreferenceClickListener(preference -> {
                    if (switchPreference.isChecked()){
                        AppCache.getPlayOnlineMusicService().replaceMusicList(MockData.musicList);
                    }
                    return false;
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = view.findViewById(android.R.id.list);
        list.setDividerHeight(0);
    }

}
