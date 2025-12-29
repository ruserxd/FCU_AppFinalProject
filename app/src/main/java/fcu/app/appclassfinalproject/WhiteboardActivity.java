package fcu.app.appclassfinalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import fcu.app.appclassfinalproject.view.WhiteboardView;
import java.util.Arrays;
import java.util.List;

public class WhiteboardActivity extends AppCompatActivity {

  private static final String TAG = "WhiteboardActivity";
  private WhiteboardView whiteboardView;
  private Button btnClear, btnColor, btnUndo;
  private int chatroomId;
  private String chatroomName;
  private int[] colors = {
      Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
      Color.MAGENTA, Color.CYAN, Color.GRAY
  };
  private int currentColorIndex = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_whiteboard);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 獲取 Intent 資料
    chatroomId = getIntent().getIntExtra("chatroom_id", -1);
    chatroomName = getIntent().getStringExtra("chatroom_name");

    if (chatroomId == -1) {
      Toast.makeText(this, "聊天室 ID 無效", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    // 設定 Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("共享白板 - " + (chatroomName != null ? chatroomName : "聊天室"));
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 初始化視圖
    whiteboardView = findViewById(R.id.whiteboard_view);
    btnClear = findViewById(R.id.btn_clear);
    btnColor = findViewById(R.id.btn_color);
    btnUndo = findViewById(R.id.btn_undo);

    // 設定按鈕事件
    btnClear.setOnClickListener(v -> showClearConfirmDialog());
    btnColor.setOnClickListener(v -> showColorPickerDialog());
    btnUndo.setOnClickListener(v -> whiteboardView.undo());

    // TODO: 連接 Supabase Realtime 同步繪圖數據
    setupRealtimeSync();
  }

  private void showClearConfirmDialog() {
    new AlertDialog.Builder(this)
        .setTitle("清除白板")
        .setMessage("確定要清除所有繪圖嗎？")
        .setPositiveButton("確定", (dialog, which) -> {
          whiteboardView.clear();
          // TODO: 同步清除到 Supabase
        })
        .setNegativeButton("取消", null)
        .show();
  }

  private void showColorPickerDialog() {
    String[] colorNames = {"黑色", "紅色", "藍色", "綠色", "黃色", "紫色", "青色", "灰色"};
    new AlertDialog.Builder(this)
        .setTitle("選擇顏色")
        .setItems(colorNames, (dialog, which) -> {
          currentColorIndex = which;
          whiteboardView.setColor(colors[which]);
          btnColor.setText("顏色: " + colorNames[which]);
        })
        .show();
  }

  private void setupRealtimeSync() {
    // TODO: 實現 Supabase Realtime 同步
    // 1. 訂閱聊天室的白板頻道
    // 2. 監聽其他用戶的繪圖操作
    // 3. 發送自己的繪圖操作
    Log.d(TAG, "白板 Realtime 同步功能待實現");
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

