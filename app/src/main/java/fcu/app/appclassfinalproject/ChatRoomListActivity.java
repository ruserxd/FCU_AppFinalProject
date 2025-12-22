package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import fcu.app.appclassfinalproject.adapter.ChatRoomAdapter;
import fcu.app.appclassfinalproject.helper.ChatHelper;
import fcu.app.appclassfinalproject.helper.SqlDataBaseHelper;
import fcu.app.appclassfinalproject.model.ChatRoom;
import java.util.List;

public class ChatRoomListActivity extends AppCompatActivity {

  private static final String TAG = "ChatRoomListActivity";
  private RecyclerView rvChatRooms;
  private ChatRoomAdapter adapter;
  private List<ChatRoom> chatRoomList;
  private SqlDataBaseHelper dbHelper;
  private SQLiteDatabase db;
  private int currentUserId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_chatroom_list);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 設定 Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("聊天室");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 初始化
    loadCurrentUserInfo();
    initializeDatabase();
    initializeViews();
    loadChatRooms();

    // 建立群組按鈕
    FloatingActionButton fabCreateGroup = findViewById(R.id.fab_create_group);
    fabCreateGroup.setOnClickListener(v -> {
      Intent intent = new Intent(ChatRoomListActivity.this, CreateGroupActivity.class);
      startActivity(intent);
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 重新載入聊天室列表
    loadChatRooms();
  }

  private void loadCurrentUserInfo() {
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    currentUserId = prefs.getInt("user_id", -1);

    if (currentUserId == -1) {
      // 嘗試通過 email 獲取 user_id
      String email = prefs.getString("email", "");
      if (!email.isEmpty()) {
        currentUserId = getUserIdByEmail(email);
        if (currentUserId != -1) {
          SharedPreferences.Editor editor = prefs.edit();
          editor.putInt("user_id", currentUserId);
          editor.apply();
        }
      }
    }
  }

  private int getUserIdByEmail(String email) {
    SqlDataBaseHelper helper = new SqlDataBaseHelper(this);
    try (SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT id FROM Users WHERE email = ?",
            new String[]{email})) {

      if (cursor.moveToFirst()) {
        return cursor.getInt(0);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error getting user ID by email: " + e.getMessage());
    }
    return -1;
  }

  private void initializeDatabase() {
    dbHelper = new SqlDataBaseHelper(this);
    db = dbHelper.getReadableDatabase();
  }

  private void initializeViews() {
    rvChatRooms = findViewById(R.id.rv_chatrooms);
    rvChatRooms.setLayoutManager(new LinearLayoutManager(this));
    adapter = new ChatRoomAdapter(this, chatRoomList);
    rvChatRooms.setAdapter(adapter);
  }

  private void loadChatRooms() {
    if (currentUserId == -1) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    chatRoomList = ChatHelper.getChatRoomsByUser(db, currentUserId);
    if (adapter != null) {
      adapter.updateChatRoomList(chatRoomList);
    }
    Log.d(TAG, "載入聊天室列表，數量: " + (chatRoomList != null ? chatRoomList.size() : 0));
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (db != null) {
      db.close();
    }
  }
}

