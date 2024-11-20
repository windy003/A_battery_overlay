package com.example.batteryoverlay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.batteryoverlay.BatteryOverlayService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查是否有悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        } else {
            startBatteryService();
        }
    }

    private void startBatteryService() {
        try {
            Intent serviceIntent = new Intent(this, BatteryOverlayService.class);
            startService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "启动服务失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Settings.canDrawOverlays(this)) {
                startBatteryService();
            } else {
                Toast.makeText(this, "需要悬浮窗权限才能显示电量", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 