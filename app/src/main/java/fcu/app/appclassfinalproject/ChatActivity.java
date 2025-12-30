package fcu.app.appclassfinalproject;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.adapter.MessageAdapter;
import fcu.app.appclassfinalproject.helper.SqlDataBaseHelper;
import fcu.app.appclassfinalproject.model.Message;
import fcu.app.appclassfinalproject.server.TCPChatClient;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

  private static final String TAG = "ChatActivity";

  // TCP 配置
  private static final String SERVER_IP = "192.168.1.102";  // ⬅️ 改成你的服務器 IP
  private static final int SERVER_PORT = 8888;

  private RecyclerView recyclerView;
  private MessageAdapter adapter;
  private List<Message> messageList;
  private EditText etMessage;
  private ImageButton btnSend;
  private TextView tvFriendName;

  private String currentUserUid;
  private int currentUserId;
  private int friendId;
  private String friendName;
  private String friendUid;
  private SqlDataBaseHelper dbHelper;

  private TCPChatClient tcpClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    // 獲取傳遞的好友信息
    friendId = getIntent().getIntExtra("friend_id", -1);
    friendName = getIntent().getStringExtra("friend_name");

    Log.d(TAG, "===== ChatActivity 啟動 =====");
    Log.d(TAG, "好友 ID: " + friendId);
    Log.d(TAG, "好友名稱: " + friendName);

    if (friendId == -1) {
      Toast.makeText(this, "無法獲取好友訊息", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    // 獲取當前用戶信息
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    currentUserUid = prefs.getString("uid", "");

    Log.d(TAG, "當前用戶 UID: " + currentUserUid);

    if (currentUserUid.isEmpty()) {
      Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    dbHelper = new SqlDataBaseHelper(this);
    currentUserId = getCurrentUserId();
    friendUid = getFriendUid();

    Log.d(TAG, "當前用戶 ID: " + currentUserId);
    Log.d(TAG, "好友 UID: " + friendUid);

    if (currentUserId == -1 || friendUid == null) {
      Toast.makeText(this, "無法取得 user 訊息", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    initViews();
    loadLocalMessages();  // 先載入本地歷史記錄
    setupSendButton();
    connectToServer();    // ✅ 連接到 TCP 服務器
  }

  private void initViews() {
    tvFriendName = findViewById(R.id.tv_friend_name);
    recyclerView = findViewById(R.id.rcy_messages);
    etMessage = findViewById(R.id.et_message);
    btnSend = findViewById(R.id.btn_send);
    ImageButton btnBack = findViewById(R.id.btn_back);
    LinearLayout toolbar = findViewById(R.id.toolbar);

    tvFriendName.setText(friendName);

    if (btnBack != null) {
      btnBack.setOnClickListener(v -> {
        Log.d(TAG, "返回鍵被按下");
        finish();
      });
    }

    if (toolbar != null) {
      toolbar.setOnClickListener(v -> {
        Log.d(TAG, "工具被點擊");
        finish();
      });
    }

    messageList = new ArrayList<>();
    adapter = new MessageAdapter(this, messageList, currentUserId);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);

    Log.d(TAG, "Views 初始化完成 - currentUserId: " + currentUserId);
  }

  private int getCurrentUserId() {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT id FROM Users WHERE firebase_uid = ?",
        new String[]{currentUserUid});

    int userId = -1;
    if (cursor.moveToFirst()) {
      userId = cursor.getInt(0);
    }
    cursor.close();
    db.close();
    return userId;
  }

  private String getFriendUid() {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT firebase_uid FROM Users WHERE id = ?",
        new String[]{String.valueOf(friendId)});

    String uid = null;
    if (cursor.moveToFirst()) {
      uid = cursor.getString(0);
    }
    cursor.close();
    db.close();
    return uid;
  }

  /**
   * ✅ 連接到 TCP 服務器
   */
  private void connectToServer() {
    Log.d(TAG, "===== 開始連接 TCP 服務器 =====");

    tcpClient = TCPChatClient.getInstance();
    tcpClient.setServer(SERVER_IP, SERVER_PORT);

    tcpClient.setMessageListener(new TCPChatClient.MessageListener() {
      @Override
      public void onConnected() {
        Log.d(TAG, "✅ TCP 連接成功");
        runOnUiThread(() -> {
          Toast.makeText(ChatActivity.this, "已連接到聊天服務器", Toast.LENGTH_SHORT).show();
        });
      }

      @Override
      public void onDisconnected() {
        Log.d(TAG, "❌ TCP 連接斷開");
        runOnUiThread(() -> {
          Toast.makeText(ChatActivity.this, "與服務器斷開連接", Toast.LENGTH_SHORT).show();
        });
      }

      @Override
      public void onMessageReceived(String senderId, String content, long timestamp) {
        Log.d(TAG, "📨 收到消息 from " + senderId + ": " + content);

        // 檢查是否是當前聊天對象發來的消息
        if (senderId.equals(friendUid)) {
          Log.d(TAG, "✅ 是好友 " + friendName + " 發來的消息");

          runOnUiThread(() -> {
            // 儲存到本地數據庫
            saveMessageToLocal(friendId, currentUserId, content, timestamp);

            // 添加到消息列表並顯示
            Message message = new Message(0, friendId, currentUserId, content, timestamp);
            messageList.add(message);
            adapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            Toast.makeText(ChatActivity.this,
                "收到來自 " + friendName + " 的消息",
                Toast.LENGTH_SHORT).show();
          });
        } else {
          Log.d(TAG, "⚠️ 不是當前好友的消息，忽略");
        }
      }

      @Override
      public void onMessageSent(String receiverId, long timestamp) {
        Log.d(TAG, "✅ 消息已送達: " + receiverId);
      }

      @Override
      public void onMessageFailed(String receiverId, String error) {
        Log.e(TAG, "❌ 消息發送失敗: " + error);
        runOnUiThread(() -> {
          Toast.makeText(ChatActivity.this, "發送失敗: " + error, Toast.LENGTH_SHORT).show();
        });
      }

      @Override
      public void onError(String error) {
        Log.e(TAG, "❌ TCP 錯誤: " + error);
        runOnUiThread(() -> {
          Toast.makeText(ChatActivity.this, "連接錯誤: " + error, Toast.LENGTH_LONG).show();
        });
      }
    });

    // 連接到服務器
    String accountName = getAccountName();
    Log.d(TAG, "使用帳號連接: " + accountName);
    tcpClient.connect(currentUserUid, accountName);
  }

  private String getAccountName() {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT account FROM Users WHERE id = ?",
        new String[]{String.valueOf(currentUserId)});

    String account = "User";
    if (cursor.moveToFirst()) {
      account = cursor.getString(0);
    }
    cursor.close();
    db.close();
    return account;
  }

  /**
   * 載入本地歷史消息
   */
  private void loadLocalMessages() {
    Log.d(TAG, "===== 載入本地歷史消息 =====");
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    String query = "SELECT * FROM Messages " +
        "WHERE (sender_id = ? AND receiver_id = ?) " +
        "OR (sender_id = ? AND receiver_id = ?) " +
        "ORDER BY timestamp ASC";

    Cursor cursor = db.rawQuery(query, new String[]{
        String.valueOf(currentUserId), String.valueOf(friendId),
        String.valueOf(friendId), String.valueOf(currentUserId)
    });

    messageList.clear();

    if (cursor.moveToFirst()) {
      do {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int senderId = cursor.getInt(cursor.getColumnIndexOrThrow("sender_id"));
        int receiverId = cursor.getInt(cursor.getColumnIndexOrThrow("receiver_id"));
        String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));

        Message message = new Message(id, senderId, receiverId, content, timestamp);
        messageList.add(message);

        String direction = (senderId == currentUserId) ? "我發送" : "對方發送";
        Log.d(TAG, "歷史消息 - " + direction + ": " + content);

      } while (cursor.moveToNext());
    }

    cursor.close();
    db.close();

    Log.d(TAG, "總共載入 " + messageList.size() + " 條歷史消息");

    adapter.notifyDataSetChanged();

    if (!messageList.isEmpty()) {
      recyclerView.scrollToPosition(messageList.size() - 1);
    }
  }

  private void setupSendButton() {
    btnSend.setOnClickListener(v -> {
      String content = etMessage.getText().toString().trim();

      if (content.isEmpty()) {
        Toast.makeText(ChatActivity.this, "請輸入訊息", Toast.LENGTH_SHORT).show();
        return;
      }

      sendMessage(content);
    });
  }

  /**
   * ✅ 通過 TCP 發送消息
   */
  private void sendMessage(String content) {
    Log.d(TAG, "===== 發送消息（通過 TCP）=====");
    Log.d(TAG, "發送者 UID: " + currentUserUid);
    Log.d(TAG, "接收者 UID: " + friendUid);
    Log.d(TAG, "內容: " + content);

    // 檢查 TCP 連接
    if (!tcpClient.isConnected()) {
      Toast.makeText(this, "未連接到服務器，請稍後再試", Toast.LENGTH_SHORT).show();
      Log.e(TAG, "❌ TCP 未連接");
      return;
    }

    long timestamp = System.currentTimeMillis();

    // ✅ 通過 TCP 發送消息
    tcpClient.sendChatMessage(friendUid, content);
    Log.d(TAG, "✅ 消息已通過 TCP 發送");

    // 儲存到本地數據庫（作為歷史記錄）
    saveMessageToLocal(currentUserId, friendId, content, timestamp);

    // 清空輸入框
    etMessage.setText("");

    // 添加到消息列表並顯示
    Message message = new Message(0, currentUserId, friendId, content, timestamp);
    messageList.add(message);
    adapter.notifyItemInserted(messageList.size() - 1);
    recyclerView.scrollToPosition(messageList.size() - 1);

    Toast.makeText(this, "消息已發送", Toast.LENGTH_SHORT).show();
  }

  /**
   * 儲存消息到本地數據庫
   */
  private void saveMessageToLocal(int senderId, int receiverId, String content, long timestamp) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    try {
      db.execSQL("INSERT INTO Messages (sender_id, receiver_id, content, timestamp) VALUES (?, ?, ?, ?)",
          new Object[]{senderId, receiverId, content, timestamp});
      Log.d(TAG, "✅ 消息已儲存到本地數據庫");
    } catch (Exception e) {
      Log.e(TAG, "❌ 儲存消息失敗: " + e.getMessage(), e);
    } finally {
      db.close();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 保持連接，不斷開
    // 這樣可以在其他聊天室也收到消息
    Log.d(TAG, "ChatActivity 銷毀，但保持 TCP 連接");
  }
}