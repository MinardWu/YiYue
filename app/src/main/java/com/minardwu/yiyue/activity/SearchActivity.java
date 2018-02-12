package com.minardwu.yiyue.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.SearchResultAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.http.Search;
import com.minardwu.yiyue.model.ArtistBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.ButtonLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private static final String TAG = "SearchActivity";

    private List<String> searchHistoryList = new ArrayList<String>();
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SearchResultAdapter adapter;

    View headerView;
    ImageView iv_artist;
    TextView tv_artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        databaseHelper = new MyDatabaseHelper(SearchActivity.this,"QO.db",null,1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseHelper.setSQLiteDataBase(sqLiteDatabase);

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
        searchHistoryList = databaseHelper.querySearchHistory();
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
            databaseHelper.clearSearchHistory();
            onResume();
        }else {
            Button btn = (Button) view;
            String content = btn.getText().toString();
            executeSearch(content);
            databaseHelper.updateSearchHistory(content);
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
            databaseHelper.insertSearchHistory(content);
            executeSearch(content);
            onResume();
            return true;
        }

        return false;
    }

    private void executeSearch(final String content){
        Search.serach(content, new Search.SearchCallback() {
            @Override
            public void onSuccess(final List<MusicBean> list, final ArtistBean artistBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new SearchResultAdapter(list);
                        adapter.setOnSearchResultViewClickListener(new SearchResultAdapter.OnSearchResultViewClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                AppCache.getPlayOnlineMusicService().stop();
                                AppCache.getPlayOnlineMusicService().play((int) list.get(position).getId());
                            }

                            @Override
                            public void onMoreClick(View view, int position) {

                            }
                        });
                        if (lv_search_result.getHeaderViewsCount() == 0) {
                            lv_search_result.addHeaderView(headerView);
                        }
                        tv_artist.setText(artistBean.getName());
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
                        lv_search_result.setAdapter(adapter);
                        ll_search_history.setVisibility(View.GONE);
                        lv_search_result.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onFail(String e) {
                ToastUtils.show(e.toString());
            }
        });
    }
}
