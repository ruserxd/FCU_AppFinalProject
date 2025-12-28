package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.ChatRoom;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomListActivity extends AppCompatActivity {

  private static final String TAG = "ChatRoomListActivity";
  private RecyclerView rvChatRooms;
  private ChatRoomAdapter adapter;
  private List<ChatRoom> chatRoomList;
  private String currentUserId;
  private SupabaseProjectHelper supabaseProjectHelper;

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
    supabaseProjectHelper = new SupabaseProjectHelper();
    loadCurrentUserInfo();
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
    supabaseProjectHelper = new SupabaseProjectHelper();
    currentUserId = supabaseProjectHelper.getCurrentUserId();
    if (currentUserId == null) {
      Log.e(TAG, "無法獲取當前用戶 ID");
    } else {
      Log.d(TAG, "當前用戶 ID: " + currentUserId);
    }
  }

  private void initializeViews() {
    rvChatRooms = findViewById(R.id.rv_chatrooms);
    rvChatRooms.setLayoutManager(new LinearLayoutManager(this));
    if (chatRoomList == null) {
      chatRoomList = new ArrayList<>();
    }
    adapter = new ChatRoomAdapter(this, chatRoomList);
    rvChatRooms.setAdapter(adapter);
  }

  private void loadChatRooms() {
    if (currentUserId == null) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    new Thread(() -> {
      try {
        chatRoomList = supabaseProjectHelper.getChatRoomsByUser(currentUserId);
        runOnUiThread(() -> {
          if (adapter != null) {
            adapter.updateChatRoomList(chatRoomList);
          }
          Log.d(TAG, "載入聊天室列表，數量: " + (chatRoomList != null ? chatRoomList.size() : 0));
        });
      } catch (Exception e) {
        Log.e(TAG, "載入聊天室列表失敗: " + e.getMessage(), e);
        runOnUiThread(() -> {
          android.widget.Toast.makeText(this, "載入聊天室列表失敗", android.widget.Toast.LENGTH_SHORT).show();
        });
      }
    }).start();
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
  }
}

