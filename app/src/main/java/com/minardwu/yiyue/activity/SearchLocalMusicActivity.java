package com.minardwu.yiyue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.SearchLocalMusicAdapter;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.MusicUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author MinardWu
 * @date : 2018/5/2
 */

public class SearchLocalMusicActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.iv_toolbar_back)
    ImageView iv_toolbar_back;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.rv_local_music_search_result)
    RecyclerView rv_local_music_search_result;
    @BindView(R.id.tv_empty)
    TextView tv_empty;

    private SearchLocalMusicAdapter adapter;
    private String content;
    private ArrayList<MusicBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_local_music);
        ButterKnife.bind(this);
        list = new ArrayList<MusicBean>();
        adapter = new SearchLocalMusicAdapter(this,list);
        et_search.addTextChangedListener(this);
        rv_local_music_search_result.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_local_music_search_result.setAdapter(adapter);
        iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        content = et_search.getText().toString();
        if (TextUtils.isEmpty(content)){
            rv_local_music_search_result.setVisibility(View.GONE);
            tv_empty.setVisibility(View.GONE);
        }else {
            list = MusicUtils.searchLocalMusic(content);
            if (list.size()>0){
                adapter.updateList(list);
                rv_local_music_search_result.setVisibility(View.VISIBLE);
                tv_empty.setVisibility(View.GONE);
            }else {
                rv_local_music_search_result.setVisibility(View.GONE);
                tv_empty.setText(getContext().getString(R.string.local_search_no_result,content));
                tv_empty.setVisibility(View.VISIBLE);
            }
        }
    }
}
