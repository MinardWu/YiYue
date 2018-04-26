package com.minardwu.yiyue.fragment;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.http.HttpCallback;
import com.minardwu.yiyue.http.result.FailResult;
import com.minardwu.yiyue.model.AlbumBean;
import com.minardwu.yiyue.utils.ImageUtils;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.UIUtils;


public class AlbumInfoFragment extends DialogFragment {

    private AlbumBean albumBean;
    private Bitmap coverBitmap;

    private View root;
    private ImageView iv_album_cancel;
    private ImageView iv_album_cover;
    private TextView tv_album_name;
    private TextView tv_album_company;
    private TextView tv_album_type;
    private TextView tv_album_info;

    public static AlbumInfoFragment newInstance(AlbumBean albumBean, Bitmap bitmap) {
        AlbumInfoFragment fragment = new AlbumInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("album", albumBean);
        args.putParcelable("cover", bitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.AlbumInfoDialog);
        if (getArguments() != null) {
            albumBean = getArguments().getParcelable("album");
            coverBitmap = getArguments().getParcelable("cover");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_info, container, false);
        if(albumBean != null){
            root = view.findViewById(R.id.root);
            iv_album_cancel = view.findViewById(R.id.iv_album_cancel);
            iv_album_cover = view.findViewById(R.id.iv_album_cover);
            tv_album_name = view.findViewById(R.id.tv_album_name);
            tv_album_company = view.findViewById(R.id.tv_album_company);
            tv_album_type = view.findViewById(R.id.tv_album_type);
            tv_album_info = view.findViewById(R.id.tv_album_info);

            iv_album_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            if(coverBitmap!=null){
                root.setBackgroundDrawable(new BitmapDrawable(ImageUtils.getFullScreenBlurBitmap(coverBitmap)));
                iv_album_cover.setImageBitmap(coverBitmap);
            }else {
                iv_album_cover.setImageURI(Uri.parse(albumBean.getPicUrl()));
                ImageUtils.getBitmapByUrl(albumBean.getPicUrl(), new HttpCallback<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        root.setBackgroundDrawable(new BitmapDrawable(ImageUtils.getFullScreenBlurBitmap(coverBitmap)));
                    }

                    @Override
                    public void onFail(FailResult result) {
                        root.setBackgroundColor(Color.WHITE);
                        tv_album_name.setTextColor(UIUtils.getColor(R.color.grey));
                        tv_album_company.setTextColor(UIUtils.getColor(R.color.grey));
                        tv_album_type.setTextColor(UIUtils.getColor(R.color.grey));
                        tv_album_info.setTextColor(UIUtils.getColor(R.color.grey));
                    }
                });
            }
            tv_album_name.setText(albumBean.getAlbumName());
            tv_album_company.setText(getString(R.string.album_company,albumBean.getAlbumName()));
            tv_album_type.setText(getString(R.string.album_type,albumBean.getSubType()));
            tv_album_info.setText(albumBean.getInfo());
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getDialog().setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getDialog().getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

}
