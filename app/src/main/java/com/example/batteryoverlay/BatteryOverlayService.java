package com.example.batteryoverlay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class BatteryOverlayService extends Service {
    public static boolean isRunning = false;
    private WindowManager windowManager;
    private View overlayView;
    private BroadcastReceiver batteryReceiver;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        initOverlayView();
        registerBatteryReceiver();

        TextView closeButton = overlayView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> stopSelf());

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 300;

        try {
            windowManager.addView(overlayView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupDragging();
    }

    private void initOverlayView() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
    }

    private void registerBatteryReceiver() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;

                    TextView batteryText = overlayView.findViewById(R.id.battery_text);
                    ImageView chargingIcon = overlayView.findViewById(R.id.charging_icon);

                    batteryText.setText(String.valueOf(level));
                    
                    if (isCharging) {
                        chargingIcon.setImageResource(R.drawable.ic_charging);
                        chargingIcon.setVisibility(View.VISIBLE);
                    } else {
                        chargingIcon.setVisibility(View.GONE);
                    }
                }
            }
        };

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void setupDragging() {
        final float[] lastTouchX = {0};
        final float[] lastTouchY = {0};
        final int[] initialX = {0};
        final int[] initialY = {0};

        overlayView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX[0] = event.getRawX();
                    lastTouchY[0] = event.getRawY();
                    initialX[0] = params.x;
                    initialY[0] = params.y;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - lastTouchX[0];
                    float dy = event.getRawY() - lastTouchY[0];

                    params.x = initialX[0] + (int) dx;
                    params.y = initialY[0] + (int) dy;

                    try {
                        windowManager.updateViewLayout(overlayView, params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
        }
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 