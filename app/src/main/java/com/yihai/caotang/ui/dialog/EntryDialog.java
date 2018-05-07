package com.yihai.caotang.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;


public class EntryDialog {

    private AlertDialog dialog;
    private TextView mTvText;
    private Button mBtnConfirm;
    private Button mBtnCancel;
    private Button mBtnClose;

    private Context mContext;
    private String mText;
    private String mConfirmButtonText;
    private String mCancelButtonText;
    private OnDialogBtnClickListener mCancelListener;
    private OnDialogBtnClickListener mConfirmListener;
    private boolean disableClose;

    public EntryDialog(Context context) {
        this.mContext = context;
        this.disableClose = false;
    }

    public EntryDialog setText(String text) {
        mText = text;
        return this;
    }

    public EntryDialog setConfirmButton(String title, final OnDialogBtnClickListener listener) {
        mConfirmButtonText = title;
        mConfirmListener = listener;
        return this;
    }

    public EntryDialog disableClose() {
        this.disableClose = true;
        return this;
    }

    public EntryDialog setCancelButton(String title, final OnDialogBtnClickListener listener) {
        mCancelButtonText = title;
        mCancelListener = listener;
        return this;
    }

    public AlertDialog show() {
        dialog = new AlertDialog.Builder(this.mContext).setView(R.layout.dialog_confirm).show();
        dialog.setCancelable(false);  // 设置当返回键按下是否关闭对话框
        dialog.setCanceledOnTouchOutside(false);  // 设置当点击对话框以外区域是否关闭对话框


        mBtnConfirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        mTvText = (TextView) dialog.getWindow().findViewById(R.id.tv_text);
        mBtnClose = (Button) dialog.getWindow().findViewById(R.id.btn_close);
        mTvText.setText(this.mText);
        if (this.mConfirmListener != null) {
            mBtnConfirm.setVisibility(View.VISIBLE);
            mBtnConfirm.setText(this.mConfirmButtonText);
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConfirmListener.onClick(v, dialog);
                    AppContext.getInstance().playEffect();
                }
            });
        } else {
            mBtnConfirm.setVisibility(View.GONE);
        }

        if (this.mCancelListener != null) {
            mBtnCancel.setVisibility(View.VISIBLE);
            mBtnCancel.setText(this.mCancelButtonText);
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCancelListener.onClick(v, dialog);
                    AppContext.getInstance().playEffect();
                }
            });
            AppContext.getInstance().playEffect();
        } else {
            mBtnCancel.setVisibility(View.GONE);
        }

        mBtnClose.setVisibility(this.disableClose ? View.INVISIBLE : View.VISIBLE);

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppContext.getInstance().playEffect();
            }
        });

        return dialog;
    }

    public interface OnDialogBtnClickListener {
        void onClick(View v, Dialog dialog);
    }

}
