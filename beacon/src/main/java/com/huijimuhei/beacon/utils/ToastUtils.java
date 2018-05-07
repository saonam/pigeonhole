package com.huijimuhei.beacon.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zeng on 2017/9/26.
 */

public class ToastUtils {
    private static Toast mToast;

    public static void showToast2(Context context, int resId, int duration) {
        showToast2(context, context.getString(resId), duration);
    }

    public static void showToast2(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
