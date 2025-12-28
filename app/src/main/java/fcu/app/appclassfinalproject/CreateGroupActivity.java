package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
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
import fcu.app.appclassfinalproject.adapter.FriendAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.User;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

  private static final String TAG = "CreateGroupActivity";
  private EditText etGroupName;
  private RecyclerView rvSelectedMembers;
  private RecyclerView rvAvailableMembers;
  private Button btnCreateGroup;
  private SupabaseProjectHelper supabaseProjectHelper;
  private String currentUserId;
  private List<User> selectedMembers;
  private List<User> availableMembers;
  private FriendAdapter selectedMembersAdapter;
  private FriendAdapter availableMembersAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_create_group);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 設定 Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("建立群組");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 初始化
    supabaseProjectHelper = new SupabaseProjectHelper();
    loadCurrentUserInfo();
    initializeViews();
    loadAvailableMembers();

    // 建立群組按鈕
    btnCreateGroup.setOnClickListener(v -> createGroup());
  }

  private void loadCurrentUserInfo() {
    currentUserId = supabaseProjectHelper.getCurrentUserId();
    if (currentUserId == null) {
      Log.e(TAG, "無法獲取當前用戶 ID");
    } else {
      Log.d(TAG, "當前用戶 ID: " + currentUserId);
    }
  }

  private void initializeViews() {
    etGroupName = findViewById(R.id.et_group_name);
    rvSelectedMembers = findViewById(R.id.rv_selected_members);
    rvAvailableMembers = findViewById(R.id.rv_available_members);
    btnCreateGroup = findViewById(R.id.btn_create_group);

    selectedMembers = new ArrayList<>();
    availableMembers = new ArrayList<>();

    // 設定已選成員列表
    rvSelectedMembers.setLayoutManager(new LinearLayoutManager(this));
    selectedMembersAdapter = new FriendAdapter(this, selectedMembers);
    rvSelectedMembers.setAdapter(selectedMembersAdapter);

    // 設定可用成員列表 - 使用自定義適配器
    rvAvailableMembers.setLayoutManager(new LinearLayoutManager(this));
    availableMembersAdapter = new FriendAdapter(this, availableMembers) {
      @Override
      public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        // 修改按鈕為選擇功能
        holder.btnDelete.setText("選擇");
        holder.btnDelete.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        // 移除項目點擊事件（避免進入聊天）
        holder.itemView.setOnClickListener(null);
        // 修改按鈕點擊事件為選擇成員
        holder.btnDelete.setOnClickListener(v -> {
          User user = availableMembers.get(position);
          if (!selectedMembers.contains(user)) {
            selectedMembers.add(user);
            selectedMembersAdapter.notifyItemInserted(selectedMembers.size() - 1);
            Toast.makeText(CreateGroupActivity.this, "已選擇: " + user.getAccount(),
                Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(CreateGroupActivity.this, "該成員已選擇", Toast.LENGTH_SHORT).show();
          }
        });
      }
    };
    availableMembersAdapter.setFriendFragment(null);
    rvAvailableMembers.setAdapter(availableMembersAdapter);
  }

  private void loadAvailableMembers() {
    if (currentUserId == null) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    // TODO: 從 Supabase 獲取好友列表
    // 目前先顯示空列表，需要實現好友查詢功能
    availableMembers.clear();
    availableMembersAdapter.notifyDataSetChanged();
    Log.d(TAG, "載入可用成員，數量: " + availableMembers.size());
    Toast.makeText(this, "好友列表功能待實現", Toast.LENGTH_SHORT).show();
  }

  private void createGroup() {
    String groupName = etGroupName.getText().toString().trim();
    if (groupName.isEmpty()) {
      Toast.makeText(this, "請輸入群組名稱", Toast.LENGTH_SHORT).show();
      return;
    }

    if (selectedMembers.isEmpty()) {
      Toast.makeText(this, "請至少選擇一個成員", Toast.LENGTH_SHORT).show();
      return;
    }

    if (currentUserId == null) {
      Toast.makeText(this, "用戶資訊錯誤", Toast.LENGTH_SHORT).show();
      return;
    }

    // 收集成員 ID（轉換為 String）
    List<String> memberIds = new ArrayList<>();
    for (User user : selectedMembers) {
      memberIds.add(String.valueOf(user.getID()));
    }

    // 建立群組
    new Thread(() -> {
      try {
        Integer chatroomId = supabaseProjectHelper.createGroupChatRoom(groupName, currentUserId, memberIds);
        runOnUiThread(() -> {
          if (chatroomId != null && chatroomId != -1) {
            Toast.makeText(CreateGroupActivity.this, "群組建立成功", Toast.LENGTH_SHORT).show();
            // 返回聊天室列表
            Intent intent = new Intent(CreateGroupActivity.this, ChatRoomListActivity.class);
            startActivity(intent);
            finish();
          } else {
            Toast.makeText(CreateGroupActivity.this, "建立群組失敗，請重試", Toast.LENGTH_SHORT).show();
          }
        });
      } catch (Exception e) {
        Log.e(TAG, "建立群組失敗: " + e.getMessage(), e);
        runOnUiThread(() -> {
          Toast.makeText(CreateGroupActivity.this, "建立群組失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

