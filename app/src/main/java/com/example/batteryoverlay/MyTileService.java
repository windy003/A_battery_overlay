import android.service.quicksettings.TileService;
import android.service.quicksettings.Tile;
import android.content.Intent;

import com.example.batteryoverlay.BatteryOverlayService;

public class MyTileService extends TileService {
    
    @Override
    public void onStartListening() {
        super.onStartListening();
        // 当用户打开快速设置面板时调用
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        // 当用户关闭快速设置面板时调用
    }

    @Override
    public void onClick() {
        super.onClick();
        // 当用户点击瓷贴时调用
        Intent intent = new Intent(this, BatteryOverlayService.class);
        if (isOverlayEnabled()) {
            stopService(intent);
        } else {
            startService(intent);
        }
        updateTile();
    }

    private boolean isOverlayEnabled() {
        // 检查服务是否正在运行的逻辑
        // 这里需要你实现具体的检查逻辑
        return false;
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(isOverlayEnabled() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }
} 