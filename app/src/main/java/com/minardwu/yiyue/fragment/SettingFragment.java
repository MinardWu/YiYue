package com.minardwu.yiyue.fragment;


import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

/**
 * @author MinardWu
 * @date : 2018/5/22
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    /**
     * 不同于经常在调用处使用set***Listener
     * 这里使用Attach函数直接将context（即Activity，实现了NestedScreenClickListener）赋值给listener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof NestedScreenClickListener){
            this.listener = (NestedScreenClickListener) context;
        }else {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference);

        findPreference("settings_clear_lrc_cache").setOnPreferenceClickListener(this);
        findPreference("settings_mock_data").setOnPreferenceClickListener(this);
        findPreference("settings_about_author").setOnPreferenceClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = view.findViewById(android.R.id.list);
        list.setDividerHeight(0);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "settings_clear_lrc_cache":
                YesOrNoDialog yesOrNoDialog = new YesOrNoDialog.Builder()
                .context(getActivity())
                .subtitle(R.string.is_clear_cache)
                .yes(R.string.clear, (dialog,view)->{
                    dialog.dismiss();
                    ToastUtils.showShortToast(R.string.clear_cache_success);
                })
                .no(R.string.cancel, (dialog,view)-> dialog.dismiss())
                .build();
                yesOrNoDialog.show();
                break;
            case "settings_mock_data":
                listener.onNestedScreenClick("settings_mock_data");
                break;
            case "settings_about_author":
                ToastUtils.showShortToast("MinardWu");
                break;
            default:
                break;
        }
        return false;
    }

    public interface NestedScreenClickListener {
        /**
         * 跳转item点击事件
         * @param key item对应key
         */
        void onNestedScreenClick(String key);
    }

    private NestedScreenClickListener listener;

}
