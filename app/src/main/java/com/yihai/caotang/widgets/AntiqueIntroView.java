package com.yihai.caotang.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.antique.Antique;

public class AntiqueIntroView extends RelativeLayout {

    private View mRoot;
    private ImageView ivBanner;
    private TextView tvContent;
    private TextView tvPoetry;

    public AntiqueIntroView(Context context) {
        this(context, null);
    }

    public AntiqueIntroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AntiqueIntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_antique_intro_view, this, true);
        initView();
    }

    private void initView() {
        tvContent = (TextView) mRoot.findViewById(R.id.tv_content);
        tvPoetry = (TextView) mRoot.findViewById(R.id.tv_poetry);
        ivBanner = (ImageView) mRoot.findViewById(R.id.iv_banner);
    }

    public void update(Antique antique) {
        //图片
        if (TextUtils.isEmpty(antique.getImage_uri())) {
            ivBanner.setVisibility(View.GONE);
        } else {
            AppContext.getInstance().load(ivBanner, antique.getRealImageUri());
            ivBanner.setVisibility(View.VISIBLE);
        }
        //内容
        if (TextUtils.isEmpty(antique.getDisplay_content())) {
            tvPoetry.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            String content = antique.getDisplay_content().replace("\\n", "\n");
            int index = content.indexOf("<poetry>");
            if (index < 0) {
                tvPoetry.setVisibility(View.GONE);
                tvContent.setText(content);
            } else {
                tvPoetry.setVisibility(View.VISIBLE);
                tvPoetry.setText(content.substring(0, index));
                tvContent.setText(content.substring(index + 8, content.length()));
            }
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void dismiss() {
        setVisibility(GONE);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }
}
