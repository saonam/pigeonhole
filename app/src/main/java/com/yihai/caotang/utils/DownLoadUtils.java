package com.yihai.caotang.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class DownLoadUtils {

    /**
     * 安装apk
     */
    public static void installApk(Context c, String filePath) {
        //apk文件的本地路径
        File apkfile = new File(filePath);
        //会根据用户的数据类型打开android系统相应的Activity。
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //设置intent的数据类型是应用程序application
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(c, c.getPackageName() + ".fileprovider", apkfile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            //为这个新apk开启一个新的activity栈
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        }
        //开始安装
        c.startActivity(intent);
        //关闭旧版本的应用程序的进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
