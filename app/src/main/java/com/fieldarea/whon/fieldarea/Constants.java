package com.fieldarea.whon.fieldarea;

import android.Manifest;

import java.text.DecimalFormat;

/**
 * Created by Whon on 2018/6/27.
 */

public class Constants {
    public static final int REQUEST_PERMISSION_SUCCESS = 0;

    public static final String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public static final String[] calculate={"平方米", "亩","人"};
    public static final String[] shape={"三角田", "梯形田"};
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
}
