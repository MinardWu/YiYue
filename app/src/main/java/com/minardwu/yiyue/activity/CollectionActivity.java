package com.minardwu.yiyue.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.fragment.CollectedAlbumFragment;
import com.minardwu.yiyue.fragment.CollectedArtistFragment;
import com.minardwu.yiyue.fragment.CollectedSongFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author MinardWu
 * @date : 2018/4/6
 */

public class CollectionActivity extends SampleActivity{

    @BindView(R.id.vp_collection)
    ViewPager vp_collection;
    @BindView(R.id.tl_tab)
    TabLayout tl_tab;

    private List<String> tabIndicators = new ArrayList<String>();
    private List<android.support.v4.app.Fragment> fragmentList = new ArrayList<android.support.v4.app.Fragment>();
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVP();
        initTab();
    }

    private void initVP(){
        fragmentList.add(new CollectedSongFragment());
        fragmentList.add(new CollectedArtistFragment());
        fragmentList.add(new CollectedAlbumFragment());
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabIndicators.get(position);
            }
        };
        vp_collection.setAdapter(fragmentPagerAdapter);
    }

    private void initTab(){
        tabIndicators.add("歌曲");
        tabIndicators.add("歌手");
        tabIndicators.add("专辑");
        ViewCompat.setElevation(tl_tab, 0);
        tl_tab.setupWithViewPager(vp_collection);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_collection;
    }

    @Override
    protected void setToolbarTitle(TextView left, TextView mid, TextView right) {
        super.setToolbarTitle(left, mid, right);
        left.setText("我的收藏");
        mid.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
    }

}
