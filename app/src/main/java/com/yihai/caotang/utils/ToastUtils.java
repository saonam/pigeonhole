package com.yihai.caotang.utils;

import android.content.Context;
import android.widget.Toast;

import com.yihai.caotang.R;


public class ToastUtils {

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, String text) {
        show(context, text, R.color.colorAccent);
    }

    public static void show(Context context, String text, int colorRes) {
//        Alerter.create((Activity) context)
//                .setText(text)
//                .setBackgroundColor(colorRes)
//                .setDuration(1200)
//                .show();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void show(Context context, int resId, Object... args) {
        show(context,
                String.format(context.getResources().getString(resId), args),
                Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration,
                            Object... args) {
        show(context,
                String.format(context.getResources().getString(resId), args),
                duration);
    }

    public static void show(Context context, String format, int duration,
                            Object... args) {
        show(context, String.format(format, args), duration);
    }
}
