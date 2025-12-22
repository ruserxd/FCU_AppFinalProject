package fcu.app.appclassfinalproject;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import fcu.app.appclassfinalproject.helper.ChatHelper;
import fcu.app.appclassfinalproject.helper.SqlDataBaseHelper;
import fcu.app.appclassfinalproject.model.Message;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

  private static final String TAG = "ChatActivity";
  private RecyclerView rvMessages;
  private EditText etMessageInput;
  private Button btnSend;
  private MessageAdapter adapter;
  private List<Message> messageList;
  private SqlDataBaseHelper dbHelper;
  private SQLiteDatabase db;
  private int chatroomId;
  private String chatroomName;
  private String chatroomType;
  private int currentUserId;

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
    loadCurrentUserInfo();
    initializeDatabase();
    initializeViews();
    loadMessages();

    // 發送按鈕
    btnSend.setOnClickListener(v -> sendMessage());
  }

  @Override
  protected void onResume() {
    super.onResume();
    loadMessages();
  }

  private void loadCurrentUserInfo() {
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    currentUserId = prefs.getInt("user_id", -1);
  }

  private void initializeDatabase() {
    dbHelper = new SqlDataBaseHelper(this);
    db = dbHelper.getWritableDatabase();
  }

  private void initializeViews() {
    rvMessages = findViewById(R.id.rv_messages);
    etMessageInput = findViewById(R.id.et_message_input);
    btnSend = findViewById(R.id.btn_send);

    rvMessages.setLayoutManager(new LinearLayoutManager(this));
    adapter = new MessageAdapter(this, messageList);
    rvMessages.setAdapter(adapter);
  }

  private void loadMessages() {
    if (currentUserId == -1) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    messageList = ChatHelper.getMessagesByChatRoom(db, chatroomId, currentUserId);
    if (adapter != null) {
      adapter.updateMessageList(messageList);
      // 滾動到底部
      if (messageList != null && !messageList.isEmpty()) {
        rvMessages.scrollToPosition(messageList.size() - 1);
      }
    }
    Log.d(TAG, "載入訊息列表，數量: " + (messageList != null ? messageList.size() : 0));
  }

  private void sendMessage() {
    String content = etMessageInput.getText().toString().trim();
    if (content.isEmpty()) {
      Toast.makeText(this, "請輸入訊息內容", Toast.LENGTH_SHORT).show();
      return;
    }

    if (currentUserId == -1) {
      Toast.makeText(this, "用戶資訊錯誤", Toast.LENGTH_SHORT).show();
      return;
    }

    long messageId = ChatHelper.sendMessage(db, chatroomId, currentUserId, content);
    if (messageId != -1) {
      etMessageInput.setText("");
      loadMessages(); // 重新載入訊息列表
      Toast.makeText(this, "訊息已發送", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "發送失敗，請重試", Toast.LENGTH_SHORT).show();
    }
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

