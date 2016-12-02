package learn.li.login2test.RoundIndicator;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 李天烨 on 2016/11/30.
 */

public class RoundViewUtils {
    //一些工具方法
    private View view;
    protected int dp2px(int dp, View v){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                v.getResources().getDisplayMetrics());
    }
    protected int sp2px(int sp, View v){
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                v.getResources().getDisplayMetrics());
    }
    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
