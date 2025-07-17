package com.yalantis.ucrop.statusbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yalantis.ucrop.util.DensityUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 沉浸式相关
 */

public class ImmersiveManager {
    private final static String TAG_FAKE_STATUS_BAR_VIEW = "TAG_FAKE_STATUS_BAR_VIEW";
    private final static String TAG_MARGIN_ADDED = "TAG_MARGIN_ADDED";

    /**
     * 设置状态栏和导航栏的padding
     */
    public static void statusBarAndNavigationBarPadding(View view) {
        final int originalTopPadding = view.getPaddingTop();
        final int originalLeftPadding = view.getPaddingLeft();
        final int originalRightPadding = view.getPaddingRight();
        final int originalBottomPadding = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(
                    originalLeftPadding,
                    statusBars.top + originalTopPadding,
                    originalRightPadding,
                    navBars.bottom + originalBottomPadding
            );
            return insets;
        });
        // 主动请求 Insets 分发，防止没有触发
        ViewCompat.requestApplyInsets(view);
    }

    /**
     * 设置状态栏的padding
     */
    public static void statusBarPadding(View view) {
        final int originalTopPadding = view.getPaddingTop();
        final int originalLeftPadding = view.getPaddingLeft();
        final int originalRightPadding = view.getPaddingRight();
        final int originalBottomPadding = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(
                    originalLeftPadding,
                    statusBars.top + originalTopPadding,
                    originalRightPadding,
                    originalBottomPadding
            );
            return insets;
        });
        // 主动请求 Insets 分发，防止没有触发
        ViewCompat.requestApplyInsets(view);
    }

    /**
     * 设置导航栏的padding
     */
    public static void navigationBarPadding(View view) {
        final int originalTopPadding = view.getPaddingTop();
        final int originalLeftPadding = view.getPaddingLeft();
        final int originalRightPadding = view.getPaddingRight();
        final int originalBottomPadding = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(
                    originalLeftPadding,
                    originalTopPadding,
                    originalRightPadding,
                    navBars.bottom + originalBottomPadding
            );
            return insets;
        });
        // 主动请求 Insets 分发，防止没有触发
        ViewCompat.requestApplyInsets(view);
    }

    public static void setDecorFitsSystemWindows(Activity activity, boolean decorFitsSystemWindows, boolean isDarkStatusBarIcon) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), decorFitsSystemWindows);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(isDarkStatusBarIcon); // 状态栏图标为深色
        controller.setAppearanceLightNavigationBars(isDarkStatusBarIcon); // 导航栏图标为深色
    }

    public static void immersiveAboveAPI35(Activity activity, View view, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        setDecorFitsSystemWindows(activity, false, isDarkStatusBarIcon);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            //Android15设置无效
            activity.getWindow().setStatusBarColor(statusBarColor); // 状态栏背景色
            activity.getWindow().setNavigationBarColor(navigationBarColor); // 导航栏背景色
        }
        statusBarAndNavigationBarPadding(view);
    }

    /**
     * 注意：使用最好将布局xml 跟布局加入    android:fitsSystemWindows="true" ，这样可以避免有些手机上布局顶边的问题
     *
     * @param baseActivity        这个会留出来状态栏和底栏的空白
     * @param statusBarColor      状态栏的颜色
     * @param navigationBarColor  导航栏的颜色
     * @param isDarkStatusBarIcon 状态栏图标颜色是否是深（黑）色  false状态栏图标颜色为白色
     */
    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        immersiveAboveAPI23(baseActivity, false, false, statusBarColor, navigationBarColor, isDarkStatusBarIcon);
    }


    /**
     * @param baseActivity
     * @param statusBarColor     状态栏的颜色
     * @param navigationBarColor 导航栏的颜色
     */
    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        try {
            Window window = baseActivity.getWindow();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4版本及以上 5.0版本及以下
                if (isDarkStatusBarIcon) {
                    initBarBelowLOLLIPOP(baseActivity);
                } else {
                    window.setFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            } else {
                if (isMarginStatusBar && isMarginNavigationBar) {
                    //5.0版本及以上
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, true
                            , true
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isDarkStatusBarIcon) {
                        initBarBelowLOLLIPOP(baseActivity);
                    } else {
                        window.requestFeature(Window.FEATURE_NO_TITLE);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                        LightStatusBarUtils.setLightStatusBar(baseActivity, false
                                , false
                                , statusBarColor == Color.TRANSPARENT
                                , isDarkStatusBarIcon);

                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    }
                } else if (!isMarginStatusBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, false
                            , true
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else {
                    //留出来状态栏 不留出来导航栏 没找到办法。。
                    return;
                }

                window.setStatusBarColor(statusBarColor);
                window.setNavigationBarColor(navigationBarColor);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initBarBelowLOLLIPOP(Activity activity) {
        //透明状态栏
        Window mWindow = activity.getWindow();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //创建一个假的状态栏
        setupStatusBarView(activity);
    }

    private static void setupStatusBarView(Activity activity) {
        Window mWindow = activity.getWindow();
        View statusBarView = mWindow.getDecorView().findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (statusBarView == null) {
            statusBarView = new View(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    DensityUtil.getStatusBarHeight(activity));
            params.gravity = Gravity.TOP;
            statusBarView.setLayoutParams(params);
            statusBarView.setVisibility(View.VISIBLE);
            statusBarView.setTag(TAG_MARGIN_ADDED);
            ((ViewGroup) mWindow.getDecorView()).addView(statusBarView);
        }
        statusBarView.setBackgroundColor(Color.TRANSPARENT);
    }
}
