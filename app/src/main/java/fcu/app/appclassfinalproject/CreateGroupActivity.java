package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import fcu.app.appclassfinalproject.adapter.FriendAdapter;
import fcu.app.appclassfinalproject.helper.ChatHelper;
import fcu.app.appclassfinalproject.helper.SqlDataBaseHelper;
import fcu.app.appclassfinalproject.model.User;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

  private static final String TAG = "CreateGroupActivity";
  private EditText etGroupName;
  private RecyclerView rvSelectedMembers;
  private RecyclerView rvAvailableMembers;
  private Button btnCreateGroup;
  private SqlDataBaseHelper dbHelper;
  private SQLiteDatabase db;
  private int currentUserId;
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
    loadCurrentUserInfo();
    initializeDatabase();
    initializeViews();
    loadAvailableMembers();

    // 建立群組按鈕
    btnCreateGroup.setOnClickListener(v -> createGroup());
  }

  private void loadCurrentUserInfo() {
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    currentUserId = prefs.getInt("user_id", -1);

    if (currentUserId == -1) {
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
    if (currentUserId == -1) {
      Log.e(TAG, "無法獲取用戶 ID");
      return;
    }

    try {
      // 獲取所有好友
      String query = "SELECT DISTINCT u.id, u.account, u.email " +
          "FROM Users u " +
          "INNER JOIN Friends f ON (u.id = f.friend_id AND f.user_id = ?) " +
          "OR (u.id = f.user_id AND f.friend_id = ?) " +
          "WHERE u.id != ?";

      Cursor cursor = db.rawQuery(query,
          new String[]{String.valueOf(currentUserId), String.valueOf(currentUserId),
              String.valueOf(currentUserId)});

      availableMembers.clear();
      while (cursor.moveToNext()) {
        int id = cursor.getInt(0);
        String account = cursor.getString(1);
        String email = cursor.getString(2);
        availableMembers.add(new User(id, account, email));
      }
      cursor.close();

      availableMembersAdapter.notifyDataSetChanged();
      Log.d(TAG, "載入可用成員，數量: " + availableMembers.size());

    } catch (Exception e) {
      Log.e(TAG, "載入可用成員失敗: " + e.getMessage(), e);
      Toast.makeText(this, "載入成員列表失敗", Toast.LENGTH_SHORT).show();
    }
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

    if (currentUserId == -1) {
      Toast.makeText(this, "用戶資訊錯誤", Toast.LENGTH_SHORT).show();
      return;
    }

    // 收集成員 ID
    List<Integer> memberIds = new ArrayList<>();
    for (User user : selectedMembers) {
      memberIds.add(user.getID());
    }

    // 建立群組
    int chatroomId = ChatHelper.createGroupChatRoom(db, groupName, currentUserId, memberIds);
    if (chatroomId != -1) {
      Toast.makeText(this, "群組建立成功", Toast.LENGTH_SHORT).show();
      // 返回聊天室列表
      Intent intent = new Intent(this, ChatRoomListActivity.class);
      startActivity(intent);
      finish();
    } else {
      Toast.makeText(this, "建立群組失敗，請重試", Toast.LENGTH_SHORT).show();
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

