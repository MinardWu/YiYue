package com.minardwu.yiyue.fragment;

import android.preference.PreferenceFragment;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.http.mock.MockData;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.widget.dialog.ChooseOptionDialog;

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
                        //fixme 替换失败
                        AppCache.getPlayOnlineMusicService().replaceMusicList(MockData.musicList);
                    }
                    return false;
                });
                break;
            case "settings_scan_filter":
                addPreferencesFromResource(R.xml.preference_file_filter);
                findPreference("settings_scan_filter_time").setOnPreferenceClickListener((preference) -> {
                    final int second[]= getActivity().getResources().getIntArray(R.array.filter_time_num);
                    ChooseOptionDialog timeFilterDialog = new ChooseOptionDialog(getActivity(),R.style.StopTimeDialog);
                    timeFilterDialog.setTitle("按时长过滤");
                    timeFilterDialog.setItem(R.array.filter_time_title);
                    timeFilterDialog.setShowImagePosition(Preferences.getFilterTimePosition());
                    timeFilterDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Preferences.saveFilterTimePosition(position);
                            Preferences.saveFilterTime(second[position]);
                            AppCache.updateLocalMusicList();
                        }
                    });
                    timeFilterDialog.show();
                    return false;
                });
                findPreference("settings_scan_filter_size").setOnPreferenceClickListener((preference) -> {
                    final int size[]= getActivity().getResources().getIntArray(R.array.filter_size_num);
                    ChooseOptionDialog sizeFilterDialog = new ChooseOptionDialog(getActivity(),R.style.StopTimeDialog);
                    sizeFilterDialog.setTitle("按大小过滤");
                    sizeFilterDialog.setItem(R.array.filter_size_title);
                    sizeFilterDialog.setShowImagePosition(Preferences.getFilterSizePosition());
                    sizeFilterDialog.setOnDialogItemClickListener(new ChooseOptionDialog.onDialogItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Preferences.saveFilterSizePosition(position);
                            Preferences.saveFilterSize(size[position]);
                            AppCache.updateLocalMusicList();
                        }
                    });
                    sizeFilterDialog.show();
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
