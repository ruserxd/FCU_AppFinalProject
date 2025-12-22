package fcu.app.appclassfinalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.ChatActivity;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.model.ChatRoom;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

  private static final String TAG = "ChatRoomAdapter";
  private Context context;
  private List<ChatRoom> chatRoomList;

  public ChatRoomAdapter(Context context, List<ChatRoom> chatRoomList) {
    this.context = context;
    this.chatRoomList = chatRoomList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_chatroom, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ChatRoom chatRoom = chatRoomList.get(position);

    // 設定聊天室名稱
    if (chatRoom.getType().equals("private")) {
      // 一對一聊天，顯示對方名稱
      if (chatRoom.getMemberNames() != null && !chatRoom.getMemberNames().isEmpty()) {
        holder.tvChatRoomName.setText(chatRoom.getMemberNames().get(0));
      } else {
        holder.tvChatRoomName.setText(chatRoom.getName());
      }
    } else {
      // 群組聊天，顯示群組名稱
      holder.tvChatRoomName.setText(chatRoom.getName() != null ? chatRoom.getName() : "未命名群組");
    }

    // 顯示最後一條訊息
    if (chatRoom.getLastMessage() != null && !chatRoom.getLastMessage().isEmpty()) {
      holder.tvLastMessage.setText(chatRoom.getLastMessage());
    } else {
      holder.tvLastMessage.setText("還沒有訊息");
    }

    // 顯示最後訊息時間
    if (chatRoom.getLastMessageTime() != null && !chatRoom.getLastMessageTime().isEmpty()) {
      holder.tvLastMessageTime.setText(chatRoom.getLastMessageTime());
    } else {
      holder.tvLastMessageTime.setText("");
    }

    // 點擊進入聊天室
    holder.itemView.setOnClickListener(v -> {
      Intent intent = new Intent(context, ChatActivity.class);
      intent.putExtra("chatroom_id", chatRoom.getId());
      intent.putExtra("chatroom_name", holder.tvChatRoomName.getText().toString());
      intent.putExtra("chatroom_type", chatRoom.getType());
      context.startActivity(intent);
    });
  }

  @Override
  public int getItemCount() {
    return chatRoomList.size();
  }

  public void updateChatRoomList(List<ChatRoom> newChatRoomList) {
    this.chatRoomList = newChatRoomList;
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView tvChatRoomName;
    TextView tvLastMessage;
    TextView tvLastMessageTime;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvChatRoomName = itemView.findViewById(R.id.tv_chatroom_name);
      tvLastMessage = itemView.findViewById(R.id.tv_last_message);
      tvLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
    }
  }
}

