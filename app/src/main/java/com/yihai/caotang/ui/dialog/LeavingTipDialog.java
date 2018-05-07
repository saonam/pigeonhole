package com.yihai.caotang.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;

public class LeavingTipDialog {

    private AlertDialog dialog;
    private TextView mTvText;

    private Context mContext;
    private boolean disableClose;
    private String mText;

    public LeavingTipDialog(Context context) {
        this.mContext = context;
        this.disableClose = false;
    }

    public LeavingTipDialog setText(String text) {
        mText = text;
        return this;
    }

    public AlertDialog show() {
        dialog = new AlertDialog.Builder(this.mContext).setView(R.layout.widget_confirm_leave_view).show();
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.width = ViewUtils.dip2px(mContext, 583);
//        lp.height = ViewUtils.dip2px(mContext, 294);
//        lp.gravity = CENTER;
//        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);  // 设置当返回键按下是否关闭对话框
        dialog.setCanceledOnTouchOutside(true);  // 设置当点击对话框以外区域是否关闭对话框
        mTvText = (TextView) dialog.getWindow().findViewById(R.id.tv_text);
        mTvText.setText(this.mText);

        return dialog;
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
