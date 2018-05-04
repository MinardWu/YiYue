package com.minardwu.yiyue.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.OnlineMusicListAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.enums.PlayModeEnum;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OnlineMusicListDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<MusicBean> list = new ArrayList<MusicBean>();
    private OnlineMusicListAdapter adapter = new OnlineMusicListAdapter(getContext(),this,list);;
    private TextView tv_list_song_count;
    private ImageView iv_playmode;
    private ImageView iv_clear_list;
    private int songCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);//要在onCreate这里设置
        if (getArguments()!=null){
            list.clear();
            list = getArguments().getParcelableArrayList("musicList");
        }
        adapter = new OnlineMusicListAdapter(getContext(),this,list);
        songCount = list.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);

        View view = inflater.inflate(R.layout.fragment_online_music_list, container);
        tv_list_song_count = view.findViewById(R.id.tv_online_music_play_list_song_count);
        iv_playmode = view.findViewById(R.id.iv_play_mode);
        iv_clear_list = view.findViewById(R.id.iv_clear_list);
        recyclerView = view.findViewById(R.id.rv_online_music_list);
        adapter = new OnlineMusicListAdapter(getContext(),this,list);
        linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        iv_playmode.setOnClickListener(this);
        iv_clear_list.setOnClickListener(this);
        tv_list_song_count.setText(getContext().getResources().getString(R.string.online_music_play_list_song_count,songCount));
        iv_playmode.setImageLevel(Preferences.getOnlinePlayMode());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public static OnlineMusicListDialogFragment newInstance(ArrayList<MusicBean> list){
        OnlineMusicListDialogFragment fragment = new OnlineMusicListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("musicList",list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setMusicList(List<MusicBean> musicBeanList){
        list.clear();
        list.addAll(musicBeanList);
        songCount = list.size();
    }

    public void updateMusicList(List<MusicBean> musicBeanList){
        list.clear();
        list.addAll(musicBeanList);
        adapter.notifyDataSetChanged();
        songCount = list.size();
        if (tv_list_song_count!=null){
            tv_list_song_count.setText(YiYueApplication.getAppContext().getResources().getString(R.string.online_music_play_list_song_count,songCount));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (SystemUtils.getScreenHeight() * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listener.onItemClickListener(view,i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_mode:
                switchPlayMode();
                break;
            case R.id.iv_clear_list:
                AppCache.getPlayOnlineMusicService().clearMusicList();
                break;
        }
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getOnlinePlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.showShortToast(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtils.showShortToast(R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.showShortToast(R.string.mode_loop);
                break;
        }
        Preferences.saveOnlineMode(mode.value());
        iv_playmode.setImageLevel(mode.value());
    }

    public interface OptionDialogFragmentClickListener{
        void onItemClickListener(View view, int position);
    }

    private OptionDialogFragmentClickListener listener;

    public void setOptionDialogFragmentClickListener(OptionDialogFragmentClickListener listener){
        this.listener = listener;
    }
}
