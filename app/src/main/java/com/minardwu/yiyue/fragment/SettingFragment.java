package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.MockControllerActivity;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.dialog.YesOrNoDialog;

/**
 * @author MinardWu
 * @date : 2018/5/22
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference);

        findPreference("clear_lrc_cache").setOnPreferenceClickListener((preference)->{
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
                return false;
        });

        findPreference("use_mock_data").setOnPreferenceClickListener((preference)->{
                startActivity(new Intent(getActivity(),MockControllerActivity.class));
                return false;
        });

        findPreference("about_author").setOnPreferenceClickListener((preference)->{
                ToastUtils.showShortToast("MinardWu");
                return false;
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = view.findViewById(android.R.id.list);
        list.setDividerHeight(0);
    }
}
