package com.minardwu.yiyue.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.utils.SystemUtils;


public class OptionDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private LinearLayout ll_header;
    private TextView tv_header_title;
    private TextView tv_header_text;
    private ListView listView;

    private boolean headerVisiable = true;
    private String header_titile;
    private String header_text;
    private ImageAndTextAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);//要在onCreate这里设置
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
        View view = inflater.inflate(R.layout.fragment_more_option_of_localmusic, container);
        ll_header = view.findViewById(R.id.ll_header);
        tv_header_title = view.findViewById(R.id.tv_header_title);
        tv_header_text = view.findViewById(R.id.tv_header_text);
        listView = view.findViewById(R.id.lv_local_music_more_option);

        if(headerVisiable){
            ll_header.setVisibility(View.VISIBLE);
        }else {
            ll_header.setVisibility(View.GONE);
        }
        tv_header_title.setText(header_titile);
        tv_header_text.setText(header_text);
        //adapter = new ImageAndTextAdapter(getContext(),R.array.local_music_more_img,R.array.local_music_more_text);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (SystemUtils.getScreenHeight() * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public void setListViewAdapter(ImageAndTextAdapter adapter){
        this.adapter = adapter;
    }

    public void setHeaderVisiable(boolean visiable){
        this.headerVisiable = visiable;
    }

    public void setHeader_text(String header_text) {
        this.header_text = header_text;
    }

    public void setHeader_titile(String header_titile) {
        this.header_titile = header_titile;
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        listener.onItemClickListener(view,i);
    }

    public interface OptionDialogFragmentClickListener{
        void onItemClickListener(View view,int position);
    }

    private OptionDialogFragmentClickListener listener;

    public void setOptionDialogFragmentClickListener(OptionDialogFragmentClickListener listener){
        this.listener = listener;
    }
}
