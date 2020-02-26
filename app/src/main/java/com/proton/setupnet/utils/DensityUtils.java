package com.proton.setupnet.utils;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;

/**
 * px和dp转换工具
 *
 * @author 仇少鹏
 * @date 2017-2-23
 */
public class DensityUtils {

    /**
     * dp转px
     */
    public static int dp2px(Context ctx, float dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);// 4.9->5 4.4->4
        return px;
    }

    public static float px2dp(Context ctx, int px) {
        float density = ctx.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

}
