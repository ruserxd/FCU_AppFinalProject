package fcu.app.appclassfinalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.adapter.ChatAdapter;
import fcu.app.appclassfinalproject.chat.ChatClient;
import fcu.app.appclassfinalproject.chat.ChatMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天室 Activity
 * 支援專案聊天室和私訊功能
 */
public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    
    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvRoomName;
    private TextView tvConnectionStatus;
    
    private ChatClient chatClient;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList;
    private String currentRoomId;
    private String currentRoomName;
    private String currentUserId;
    private String currentUserName;
    
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
        
        // 獲取傳入的參數
        currentRoomId = getIntent().getStringExtra("roomId");
        currentRoomName = getIntent().getStringExtra("roomName");
        if (currentRoomId == null) {
            // 如果沒有指定房間，使用專案 ID 創建專案聊天室
            SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
            int projectId = prefs.getInt("project_id", -1);
            if (projectId != -1) {
                currentRoomId = "project_" + projectId;
                currentRoomName = "專案聊天室";
            } else {
                currentRoomId = "general";
                currentRoomName = "一般聊天室";
            }
        }
        
        // 獲取用戶資訊
        SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("supabase_user_id", null);
        if (currentUserId == null) {
            currentUserId = prefs.getString("uid", "unknown");
        }
        currentUserName = prefs.getString("email", "用戶");
        
        initViews();
        setupChatClient();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_chat_message);
        btnSend = findViewById(R.id.btn_send_message);
        tvRoomName = findViewById(R.id.tv_chat_room_name);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        
        tvRoomName.setText(currentRoomName);
        
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(this, messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    
    private void setupChatClient() {
        chatClient = ChatClient.getInstance(this);
        
        // 連接監聽器
        chatClient.addConnectionListener(new ChatClient.ConnectionListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    tvConnectionStatus.setText("已連接");
                    tvConnectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    Toast.makeText(ChatActivity.this, "已連接到聊天伺服器", Toast.LENGTH_SHORT).show();
                    
                    // 加入聊天室
                    chatClient.joinRoom(currentRoomId, currentRoomName);
                });
            }
            
            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    tvConnectionStatus.setText("未連接");
                    tvConnectionStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(ChatActivity.this, "與聊天伺服器斷開連接", Toast.LENGTH_SHORT).show();
                });
            }
        });
        
        // 訊息監聽器
        chatClient.addMessageListener(new ChatClient.ChatMessageListener() {
            @Override
            public void onMessageReceived(ChatMessage message) {
                runOnUiThread(() -> {
                    // 只顯示當前房間的訊息
                    if (currentRoomId == null || currentRoomId.equals(message.getRoomId()) || 
                        message.getRoomId() == null) {
                        message.setFromMe(message.getUserId().equals(currentUserId));
                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    }
                });
            }
        });
        
        // 連接到伺服器
        if (!chatClient.isConnected()) {
            chatClient.connect(currentUserId, currentUserName);
        } else {
            // 如果已經連接，直接加入房間
            chatClient.joinRoom(currentRoomId, currentRoomName);
        }
    }
    
    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        
        if (!chatClient.isConnected()) {
            Toast.makeText(this, "未連接到伺服器，請稍後再試", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 發送訊息
        chatClient.sendMessage(content, currentRoomId);
        
        // 清空輸入框
        etMessage.setText("");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatClient != null) {
            chatClient.leaveRoom();
            // 注意：這裡不斷開連接，因為可能其他 Activity 也在使用
            // 如果需要完全斷開，可以調用 chatClient.disconnect()
        }
    }
}

