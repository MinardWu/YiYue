package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.Build;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.DrawerItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.constants.Extras;
import com.minardwu.yiyue.constants.StopTimeAction;
import com.minardwu.yiyue.event.ChageToolbarTextEvent;
import com.minardwu.yiyue.executor.DrawerItemExecutor;
import com.minardwu.yiyue.fragment.LocalMusicFragment;
import com.minardwu.yiyue.fragment.OnlineMusicFragment;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.EventCallback;
import com.minardwu.yiyue.service.QuitTimer;
import com.minardwu.yiyue.utils.MusicUtils;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.ParseUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final String INDEX = "index";
    public static final String LOCAL = "local";
    public static final String ONLINE = "online";
    private int currentFragment = 0;
    private DrawerItemAdapter drawerItemAdapter;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<android.support.v4.app.Fragment> fragmentList;
    private DrawerItemExecutor drawerItemExecutor = new DrawerItemExecutor();
    private boolean isFront;

    @BindView(R.id.tv_toolbar) TextView tv_toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.iv_top_right) ImageView iv_top_right;
    @BindView(R.id.iv_menu) ImageView iv_menu;
    @BindView(R.id.lv_drawer) ListView listView;
    @BindView(R.id.vp) ViewPager viewPager;
    @BindView(R.id.rl_setting) RelativeLayout rl_setting;
    @BindView(R.id.rl_exit) RelativeLayout rl_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Notifier.init(getPlayLocalMusicService());
        QuitTimer.getInstance().init(new EventCallback<Long>() {
            @Override
            public void onEvent(Long aLong) {
                if (aLong== StopTimeAction.CLEAR_INFO){
                    drawerItemAdapter.setDrawerItemBeanInfo(3,"");
                }else if (aLong==StopTimeAction.UNTIL_SONG_END){
                    drawerItemAdapter.setDrawerItemBeanInfo(3,"播完后关闭");
                }else {
                    drawerItemAdapter.setDrawerItemBeanInfo(3,ParseUtils.formatTime("(mm:ss)",aLong));
                }
            }
        });
        if(!SystemUtils.checkReadPermission()){
            SystemUtils.requestReadPermission(this,REQUEST_READ_EXTERNAL_STORAGE);
        }
        parseIntent();
        initData();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent();
    }

    void parseIntent(){
        Intent newIntent = getIntent();
        if (newIntent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            String type = newIntent.getStringExtra(Extras.EXTRA_NOTIFICATION);
            if (type.equals("LOCAL")){
                currentFragment = 0;
                viewPager.setCurrentItem(0);
                changeIcon(0);
            }else if(type.equals("ONLINE")){
                currentFragment = 1;
                viewPager.setCurrentItem(1);
                changeIcon(1);
            }
            setIntent(new Intent());
        }

        if (newIntent.hasExtra(MainActivity.INDEX)) {
            String index = newIntent.getStringExtra(INDEX);
            if (index.equals(LOCAL)){
                currentFragment = 0;
                viewPager.setCurrentItem(0);
                changeIcon(0);
            }else if(index.equals(ONLINE)){
                currentFragment = 1;
                viewPager.setCurrentItem(1);
                changeIcon(1);
            }
        }
    }

    private void initData() {
        fragmentList = new ArrayList<android.support.v4.app.Fragment>();
        fragmentList.add(new LocalMusicFragment());
        fragmentList.add(new OnlineMusicFragment());
    }

    private void initView(){
        iv_top_right.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_exit.setOnClickListener(this);
        changeIcon(currentFragment);
        MusicBean playingLocalMusic = MusicUtils.getLocalMusicPlayingMusic();
        if(playingLocalMusic != null){
            tv_toolbar.setText(playingLocalMusic.getTitle());
        }
        //4.4以上、5.0以下的需要为drawlayout设置沉浸式
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //将侧边栏顶部延伸至status bar
            drawerLayout.setFitsSystemWindows(true);
            //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
            drawerLayout.setClipToPadding(false);
        }
        //初始化侧边栏
        drawerItemAdapter = new DrawerItemAdapter(this,R.array.drawer_img,R.array.drawer_title,R.array.drawer_type);

        drawerItemAdapter.setSwitchClickListener(new DrawerItemAdapter.OnSwitchClickListener() {
            @Override
            public void onClick(int position,boolean isCheck) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        if(isCheck){
                            Preferences.savePlayWhenOnlyHaveWifi(true);
                        }else {
                            Preferences.savePlayWhenOnlyHaveWifi(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        drawerItemAdapter.setOnNormalClickListener(new DrawerItemAdapter.OnNormalClickListener() {
            @Override
            public void onClick(int position, String title) {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerItemExecutor.execute(position,title,MainActivity.this);
            }
        });
        listView.setAdapter(drawerItemAdapter);
        //初始化ViewPaper
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tv_toolbar.setText(MusicUtils.getLocalMusicPlayingMusicTitle());
                        currentFragment = 0;
                        changeIcon(currentFragment);
                        break;
                    case 1:
                        tv_toolbar.setText(getResources().getString(R.string.fm_name));
                        currentFragment = 1;
                        changeIcon(currentFragment);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        checkOrientation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    private void checkOrientation(){
        OrientationEventListener mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(currentFragment==0 && !getPlayOnlineMusicService().isPlaying()){
                    //横屏与方向横屏
                    if (orientation > 270 && orientation < 300 && isFront) {
                        disable();
                        startActivity(new Intent(MainActivity.this,TapeActivity.class));
                    } else if (orientation > 60 && orientation < 120 && isFront) {
                        disable();
                        startActivity(new Intent(MainActivity.this,TapeActivity.class));
                    }
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    private void changeIcon(int currentFragment){
        if(currentFragment==0){
            iv_top_right.setImageResource(R.drawable.ic_tape);
        }else if(currentFragment==1){
            iv_top_right.setImageResource(R.drawable.ic_search);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChageToolbarTextEvent event) {
        if (currentFragment==0){
            tv_toolbar.setText(event.getMusicBean().getTitle());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG,"onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            }else {
                //退回桌面而不是销毁MainActivity
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //shouldShowRequestPermissionRationale方法：1.用户拒绝返回true。2.若点击不再提醒返回false
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (b) {
                        ToastUtils.showLongToast("权限获取失败，若想正常使用请开通文件权限");
                    } else {
                        ToastUtils.showLongToast("权限获取失败，若想正常使用请前往应用设置界面手动开启权限");
                    }
                } else {
                    ToastUtils.showLongToast("授权成功");
                    AppCache.getLocalMusicList().clear();
                    AppCache.getLocalMusicList().addAll(MusicUtils.scanMusic(getApplicationContext()));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_top_right:
                if(currentFragment==0){
                    startActivity(new Intent(this,TapeActivity.class));
                }else if(currentFragment==1){
                    startActivity(new Intent(this,SearchActivity.class));
                }
                break;
            case R.id.rl_setting:

                break;
            case R.id.rl_exit:
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
