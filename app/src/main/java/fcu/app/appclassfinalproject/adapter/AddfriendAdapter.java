package fcu.app.appclassfinalproject.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.User;
import java.util.List;

public class AddfriendAdapter extends RecyclerView.Adapter<AddfriendAdapter.ViewHolder> {

  private Context context;
  private List<User> userList;

  public AddfriendAdapter(Context context, List<User> userList) {
    this.context = context;
    this.userList = userList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_addfriend, parent, false);
    Log.d("AddFriendAdapter", "創建 ViewHolder");
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    User user = userList.get(position);

    Log.d("AddFriendAdapter", "綁定數據 - 位置: " + position + ", 用戶: " + user.getAccount());

    holder.tvUserAccount.setText(user.getAccount());
    holder.tvUserEmail.setText(user.getEmail());

    // 確保按鈕可見和可用
    holder.btnAddFriend.setVisibility(View.VISIBLE);
    holder.btnAddFriend.setEnabled(true);

    Log.d("AddFriendAdapter", "按鈕狀態 - 可見性: " + holder.btnAddFriend.getVisibility() +
        ", 是否啟用: " + holder.btnAddFriend.isEnabled());

    holder.btnAddFriend.setOnClickListener(v -> {
      Log.d("AddFriendAdapter", "按鈕被點擊: " + user.getAccount());
      addFriend(user.getID(), position);
    });
  }

  @Override
  public int getItemCount() {
    return userList.size();
  }

  private void addFriend(int friendId, int position) {
    // TODO: Add friend in Supabase
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView tvUserAccount;
    TextView tvUserEmail;
    Button btnAddFriend;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvUserAccount = itemView.findViewById(R.id.tv_user_account);
      tvUserEmail = itemView.findViewById(R.id.tv_user_email);
      btnAddFriend = itemView.findViewById(R.id.btn_add_friend);

      Log.d("AddFriendAdapter", "ViewHolder 創建:");
      Log.d("AddFriendAdapter", "  tvUserAccount: " + (tvUserAccount != null ? "找到" : "未找到"));
      Log.d("AddFriendAdapter", "  tvUserEmail: " + (tvUserEmail != null ? "找到" : "未找到"));
      Log.d("AddFriendAdapter", "  btnAddFriend: " + (btnAddFriend != null ? "找到" : "未找到"));

      if (btnAddFriend != null) {
        btnAddFriend.setBackgroundColor(0xFF2196F3);
        btnAddFriend.setTextColor(0xFFFFFFFF);
        Log.d("AddFriendAdapter", "按鈕樣式已設置");
      }
    }
  }
}