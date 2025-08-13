package com.example.wan.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FloatButtonService extends Service {
    private WindowManager windowManager;
    private View floatButton;
    private boolean isDebugWindowShown = false;
    private View debugWindow;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable permissionCheckRunnable;
    private static final int POLL_INTERVAL = 3_000; // 3秒轮询间隔
    private static final int MAX_RETRY_COUNT = 20; // 最大重试次数
    private int currentRetryCount = 0;
    private SharedPreferences sp;

    private void setDebugWindow() {
        String scheme = sp.getString("scheme", "https");
        String ip = sp.getString("ip", "192.168.1.1");
        String port = sp.getString("port", "8080");

        EditText etScheme = debugWindow.findViewById(R.id.et_scheme);
        EditText etIp = debugWindow.findViewById(R.id.et_ip);
        EditText etPort = debugWindow.findViewById(R.id.et_port);

        etScheme.setText(scheme);
        etIp.setText(ip);
        etPort.setText(port);

        Button btnSave = debugWindow.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            String resultScheme = etScheme.getText().toString().trim();
            String resultIp = etIp.getText().toString().trim();
            String resultPort = etPort.getText().toString().trim();

            sp.edit().putString("scheme", resultScheme).apply();
            sp.edit().putString("ip", resultIp).apply();
            sp.edit().putString("port", resultPort).apply();
            hideKeyboard();

            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
        });

        // profile1
        Button btnProfile1 = debugWindow.findViewById(R.id.btn_profile1);
        btnProfile1.setOnClickListener(v -> {
            sp.edit().putString("scheme", "https").apply();
            sp.edit().putString("ip", "192.168.1.1").apply();
            sp.edit().putString("port", "8443").apply();
            hideKeyboard();

            setDebugWindow();
        });

        // profile2
        Button btnProfile2 = debugWindow.findViewById(R.id.btn_profile2);
        btnProfile2.setOnClickListener(v -> {
            sp.edit().putString("scheme", "http").apply();
            sp.edit().putString("ip", "192.168.1.2").apply();
            sp.edit().putString("port", "8080").apply();
            hideKeyboard();

            setDebugWindow();
        });

        // profile3
        Button btnProfile3 = debugWindow.findViewById(R.id.btn_profile3);
        btnProfile3.setOnClickListener(v -> {
            sp.edit().putString("scheme", "ssh").apply();
            sp.edit().putString("ip", "192.168.1.254").apply();
            sp.edit().putString("port", "2222").apply();
            hideKeyboard();

            setDebugWindow();
        });

    }

    private void hideKeyboard() {
        if (debugWindow != null && windowManager != null) {
            View focusedView = debugWindow.findFocus();
            if (focusedView == null) {
                return;
            }
            // 清除焦点
            focusedView.clearFocus();
            // 关闭软键盘
            if (focusedView instanceof EditText) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("float_window_prefs", MODE_PRIVATE);
        checkOverlayPermission(); // 启动时检查权限
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // 用户选择不再提示
            if (sp.getBoolean("never_ask_again", false)) {
                return;
            }

            // 延迟5秒后显示提示对话框（等待应用进入主页）
            handler.postDelayed(this::showPermissionDialog, 5_000);
        } else {
            initFloatButton(); // 已有权限，直接初始化
        }
    }

    private void showPermissionDialog() {
        Activity topActivity = getTopActivity();
        if (topActivity == null || topActivity.isFinishing()) {
            return;
        }

        // 弹出请求前往授权
        new AlertDialog.Builder(topActivity) // 关键点：使用 Activity 而非 Service 的 Context
                .setTitle("调试工具需要悬浮窗权限")
                .setMessage("请允许显示在其他应用上方")
                .setCancelable(false)
                .setPositiveButton("前往授权", (dialog, which) -> {
                    requestOverlayPermission();
                    startPermissionPolling();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    stopSelf();
                })
                .setNeutralButton("不再提示", (dialog, which) -> {
                    sp.edit().putBoolean("never_ask_again", true).apply();
                    stopSelf();
                })
                .show();
    }

    private void startPermissionPolling() {
        // 移除之前的轮询任务（如果有）
        if (permissionCheckRunnable != null) {
            handler.removeCallbacks(permissionCheckRunnable);
        }

        currentRetryCount = 0;
        // 轮询任务 检查权限
        permissionCheckRunnable = () -> {
            // 超出次数
            if (currentRetryCount >= MAX_RETRY_COUNT) {
                stopSelf();
                return;
            }

            if (Settings.canDrawOverlays(this)) {
                // 已授权
                initFloatButton();
            } else {
                // 未授权 继续轮询
                currentRetryCount++;
                // 轮询
                handler.postDelayed((Runnable) this, POLL_INTERVAL);
            }
        };
        // 轮询首次执行
        handler.postDelayed(permissionCheckRunnable, POLL_INTERVAL);
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initFloatButton() {
        // 初始化悬浮窗
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 创建悬浮按钮
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(android.R.drawable.ic_menu_preferences);
        imageView.setBackgroundResource(android.R.color.holo_blue_bright);
        floatButton = imageView;

        // 设置悬浮按钮参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                dpToPx(60), // 宽度
                dpToPx(60), // 高度
                // TYPE_APPLICATION_OVERLAY 显示在其它应用上方 需要权限。
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.END;
        params.x = sp.getInt("pos_x", dpToPx(20));
        params.y = sp.getInt("pos_y", dpToPx(100));

        windowManager.addView(floatButton, params);

        // 设置拖动和点击事件
        floatButton.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (initialTouchX - event.getRawX());
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatButton, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // 如果移动距离很小，则认为是点击事件
                        if (Math.abs(event.getRawX() - initialTouchX) < 5 &&
                                Math.abs(event.getRawY() - initialTouchY) < 5) {
                            toggleDebugWindow();
                        }
                        savePosition(params.x, params.y);
                        return true;
                }
                return false;
            }
        });
    }

    private void savePosition(int x, int y) {
        sp.edit()
                .putInt("pos_x", x)
                .putInt("pos_y", y)
                .apply();
    }

    private void toggleDebugWindow() {
        if (isDebugWindowShown) {
            hideDebugWindow();
        } else {
            showDebugWindow();
        }
    }

    private void showDebugWindow() {
        if (debugWindow == null) {
            debugWindow = LayoutInflater.from(this).inflate(R.layout.debug_window, null);

            WindowManager.LayoutParams debugParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // 允许窗口内接收触摸事件
                    PixelFormat.TRANSLUCENT);

            debugParams.gravity = Gravity.TOP;
            debugParams.y = dpToPx(100);

            // 设置调试窗口内容
            TextView appInfo = debugWindow.findViewById(R.id.tv_app_info);
            appInfo.setText(getAppInfo());

            Button closeToolBtn = debugWindow.findViewById(R.id.btn_close_tool);
            closeToolBtn.setOnClickListener(v -> stopSelf());

            Button closeDialogBtn = debugWindow.findViewById(R.id.btn_close_dialog);
            closeDialogBtn.setOnClickListener(v -> hideDebugWindow());

            setDebugWindow();

            windowManager.addView(debugWindow, debugParams);
        }
        isDebugWindowShown = true;
    }

    private void hideDebugWindow() {
        if (debugWindow != null) {
            windowManager.removeView(debugWindow);
            debugWindow = null;
        }
        isDebugWindowShown = false;
    }

    private String getAppInfo() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return "App: " + packageInfo.packageName + "\n" +
                    "Version: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")\n" +
                    "Android: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")\n" +
                    "Device: " + Build.MANUFACTURER + " " + Build.MODEL;
        } catch (PackageManager.NameNotFoundException e) {
            return "App info not available";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatButton != null && windowManager != null) windowManager.removeView(floatButton);
        if (debugWindow != null && windowManager != null) windowManager.removeView(debugWindow);

        // 移除所有回调防止内存泄漏
        if (permissionCheckRunnable != null) {
            handler.removeCallbacks(permissionCheckRunnable);
        }
    }

    /**
     * 获取栈顶 Activity 的工具方法
     */
    private Activity getTopActivity() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            ArrayMap<?, ?> activities = (ArrayMap<?, ?>) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * dp转px工具方法
     *
     * @noinspection SameParameterValue
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

}
