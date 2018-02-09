package com.minardwu.yiyue.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.ButtonLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    @BindView(R.id.button_layout)
    ButtonLayout button_layout;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_clear_history)
    TextView tv_clear_history;

    private static final String TAG = "SearchActivity";

    private List<String> searchHistoryList = new ArrayList<String>();
    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        databaseHelper = new MyDatabaseHelper(SearchActivity.this,"YY.db",null,1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseHelper.setSQLiteDataBase(sqLiteDatabase);

        tv_clear_history.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchHistoryList = databaseHelper.query();
        et_search.setOnEditorActionListener(this);
        button_layout.removeAllViews();
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
        if(view.getId()==R.id.tv_clear_history){
            databaseHelper.clearHistory();
            onResume();
        }else {
            Button btn = (Button) view;
            ToastUtils.show(btn.getText().toString());
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                || event == null
                || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            databaseHelper.insert(textView.getText().toString());
            Log.e(TAG,textView.getText().toString());
            onResume();
            return true;
        }

        return false;
    }
}
