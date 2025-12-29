package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.adapter.MessageAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

  private static final String TAG = "ChatActivity";
  private RecyclerView rvMessages;
  private EditText etMessageInput;
  private Button btnSend;
  private MessageAdapter adapter;
  private List<Message> messageList;
  private int chatroomId;
  private String chatroomName;
  private String chatroomType;
  private String currentUserId;
  private SupabaseProjectHelper supabaseProjectHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_chat);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 獲取 Intent 資料
    chatroomId = getIntent().getIntExtra("chatroom_id", -1);
    chatroomName = getIntent().getStringExtra("chatroom_name");
    chatroomType = getIntent().getStringExtra("chatroom_type");

    if (chatroomId == -1) {
      Toast.makeText(this, "聊天室 ID 無效", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    // 設定 Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(chatroomName != null ? chatroomName : "聊天室");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 初始化
    supabaseProjectHelper = new SupabaseProjectHelper();
    loadCurrentUserInfo();
    initializeViews();
    loadMessages();

    // 發送按鈕
    btnSend.setOnClickListener(v -> sendMessage());

    // 白板按鈕
    Button btnWhiteboard = findViewById(R.id.btn_whiteboard);
    btnWhiteboard.setOnClickListener(v -> {
      Intent intent = new Intent(ChatActivity.this, WhiteboardActivity.class);
      intent.putExtra("chatroom_id", chatroomId);
      intent.putExtra("chatroom_name", chatroomName);
      startActivity(intent);
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    loadMessages();
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
    rvMessages = findViewById(R.id.rv_messages);
    etMessageInput = findViewById(R.id.et_message_input);
    btnSend = findViewById(R.id.btn_send);

    rvMessages.setLayoutManager(new LinearLayoutManager(this));
    if (messageList == null) {
      messageList = new java.util.ArrayList<>();
    }
    adapter = new MessageAdapter(this, messageList);
    rvMessages.setAdapter(adapter);
  }

  private void loadMessages() {
    if (currentUserId == null) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    new Thread(() -> {
      try {
        messageList = supabaseProjectHelper.getMessagesByChatRoom(chatroomId, currentUserId);
        runOnUiThread(() -> {
          if (adapter != null) {
            adapter.updateMessageList(messageList);
            // 滾動到底部
            if (messageList != null && !messageList.isEmpty()) {
              rvMessages.scrollToPosition(messageList.size() - 1);
            }
          }
          Log.d(TAG, "載入訊息列表，數量: " + (messageList != null ? messageList.size() : 0));
        });
      } catch (Exception e) {
        Log.e(TAG, "載入訊息列表失敗: " + e.getMessage(), e);
        runOnUiThread(() -> {
          Toast.makeText(this, "載入訊息失敗", Toast.LENGTH_SHORT).show();
        });
      }
    }).start();
  }

  private void sendMessage() {
    String content = etMessageInput.getText().toString().trim();
    if (content.isEmpty()) {
      Toast.makeText(this, "請輸入訊息內容", Toast.LENGTH_SHORT).show();
      return;
    }

    if (currentUserId == null) {
      Toast.makeText(this, "用戶資訊錯誤", Toast.LENGTH_SHORT).show();
      return;
    }

    new Thread(() -> {
      try {
        Long messageId = supabaseProjectHelper.sendMessage(chatroomId, currentUserId, content);
        runOnUiThread(() -> {
          if (messageId != null && messageId != -1) {
            etMessageInput.setText("");
            loadMessages(); // 重新載入訊息列表
            Toast.makeText(this, "訊息已發送", Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(this, "發送失敗，請重試", Toast.LENGTH_SHORT).show();
          }
        });
      } catch (Exception e) {
        Log.e(TAG, "發送訊息失敗: " + e.getMessage(), e);
        runOnUiThread(() -> {
          Toast.makeText(this, "發送失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

