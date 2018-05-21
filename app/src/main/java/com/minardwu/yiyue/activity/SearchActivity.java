package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.SearchResultAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.executor.MoreOptionOfActSearchExecutor;
import com.minardwu.yiyue.fragment.OptionDialogFragment;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.http.GetOnlineArtist;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.Search;
import com.minardwu.yiyue.http.result.ResultCode;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.utils.UIUtils;
import com.minardwu.yiyue.widget.ButtonLayout;
import com.minardwu.yiyue.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/2/9
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    @BindView(R.id.iv_toolbar_back)
    ImageView iv_toolbar_back;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.button_layout)
    ButtonLayout button_layout;
    @BindView(R.id.tv_clear_history)
    TextView tv_clear_history;
    @BindView(R.id.ll_search_history)
    LinearLayout ll_search_history;
    @BindView(R.id.lv_search_result)
    ListView lv_search_result;
    @BindView(R.id.loading_view)
    LoadingView loading_view;
    @BindView(R.id.empty_view)
    LinearLayout empty_view;

    private static final String TAG = "SearchActivity";

    private List<String> searchHistoryList = new ArrayList<String>();
    private SearchResultAdapter adapter;
    private PlayOnlineMusicService playOnlineMusicService;

    private View headerView;
    private SimpleDraweeView iv_artist;
    private TextView tv_artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        playOnlineMusicService = AppCache.getPlayOnlineMusicService();
        searchHistoryList = MyDatabaseHelper.init(getContext()).querySearchHistory();
        empty_view.setVisibility(searchHistoryList.size()>0?View.GONE:View.VISIBLE);
        ll_search_history.setVisibility(searchHistoryList.size()>0?View.VISIBLE:View.GONE);
        headerView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.list_search_result_header,null);
        iv_artist = headerView.findViewById(R.id.iv_search_artist);
        tv_artist = headerView.findViewById(R.id.tv_search_artist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        et_search.setOnEditorActionListener(this);
        iv_toolbar_back.setOnClickListener(this);
        tv_clear_history.setOnClickListener(this);
        button_layout.removeAllViews();
        searchHistoryList = MyDatabaseHelper.init(getContext()).querySearchHistory();
        for (String history:searchHistoryList){
            Button button = new Button(this);
            button.setText(history);
            button.setMinHeight(0);
            button.setMinWidth(0);
            button.setMinimumHeight(0);//View中的方法 改变View中的mMinHeight
            button.setMinimumWidth(0);//View中的方法  改变View中的mMinWidth
            button.setBackgroundResource(R.drawable.btn_search_history);
            button.setOnClickListener(this);
            button_layout.addView(button);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_toolbar_back){
            finish();
        }else if(view.getId()==R.id.tv_clear_history){
            MyDatabaseHelper.init(getContext()).clearSearchHistory();
            ll_search_history.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }else {
            Button btn = (Button) view;
            String content = btn.getText().toString();
            et_search.setText(content);
            executeSearch(content);
            MyDatabaseHelper.init(getContext()).updateSearchHistory(content);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, final int actionId, KeyEvent event) {
        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                || event == null
                || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            String content = textView.getText().toString();
            MyDatabaseHelper.init(getContext()).insertSearchHistory(content);
            executeSearch(content);
            onResume();
            return true;
        }

        return false;
    }

    private void executeSearch(final String content){
        empty_view.setVisibility(View.GONE);
        ll_search_history.setVisibility(View.GONE);
        lv_search_result.setVisibility(View.GONE);
        loading_view.setVisibility(View.VISIBLE);
        Search.serach(content, new Search.SearchCallback() {
            @Override
            public void onSuccess(final List<MusicBean> list, final ArtistBean artistBean) {
                loading_view.setVisibility(View.GONE);
                lv_search_result.setVisibility(View.VISIBLE);
                //搜索得到的歌手
                tv_artist.setText("歌手："+artistBean.getName());
                GetOnlineArtist.getArtistInfoById(artistBean.getId(), new HttpCallback<ArtistBean>() {
                    @Override
                    public void onSuccess(ArtistBean artistBean) {
                        iv_artist.setImageURI(artistBean.getPicUrl());
                    }

                    @Override
                    public void onFail(FailResult result) {}
                });
                headerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        Intent intent = new Intent(SearchActivity.this,ArtistActivity.class);
                        intent.putExtra("artistName",artistBean.getName());
                        intent.putExtra("artistId",artistBean.getId());
                        startActivity(intent);
                    }
                });
                //搜索得到的歌曲
                if (lv_search_result.getHeaderViewsCount() == 0) {
                    lv_search_result.addHeaderView(headerView);
                }
                adapter = new SearchResultAdapter(list);
                lv_search_result.setAdapter(adapter);
                adapter.setOnSearchResultViewClickListener(new SearchResultAdapter.OnSearchResultViewClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(playOnlineMusicService.getPlayingMusicId()==list.get(position).getId()){
                            finish();
                        }else {
                            if(playOnlineMusicService.isPlayList()){
                                playOnlineMusicService.playOtherWhenPlayList(list.get(position));
                                adapter.notifyDataSetChanged();
                            }else {
                                playOnlineMusicService.stop();
                                playOnlineMusicService.play(list.get(position).getId());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onMoreClick(View view, final int musicPosition) {
                        final OptionDialogFragment fragment = new OptionDialogFragment();
                        fragment.setHeader_titile("歌曲：");
                        fragment.setHeader_text(list.get(musicPosition).getTitle());
                        fragment.setListViewAdapter(new ImageAndTextAdapter(SearchActivity.this,R.array.activity_search_more_img,R.array.activity_search_more_text));
                        fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                            @Override
                            public void onItemClickListener(View view, int position) {
                                fragment.dismiss();
                                //点击播放的话直接使用上面的逻辑即可
                                if (position==0){
                                    onItemClick(view,musicPosition);
                                }else{
                                    MoreOptionOfActSearchExecutor.execute(SearchActivity.this,position,list.get(musicPosition));
                                }
                            }
                        });
                        fragment.show(getSupportFragmentManager(), "OptionDialogFragment");
                    }
                });
            }

            @Override
            public void onFail(final FailResult result) {
                switch (result.getResultCode()){
                    case ResultCode.NETWORK_ERROR:
                        ToastUtils.showShortToast(UIUtils.getString(R.string.network_error));
                        break;
                    case ResultCode.SEARCH_ERROR:
                        Log.e(TAG,result.getException());
                        ToastUtils.showShortToast(UIUtils.getString(R.string.server_error));
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
