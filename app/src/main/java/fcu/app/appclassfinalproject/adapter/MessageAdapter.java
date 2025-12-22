package fcu.app.appclassfinalproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

  private static final String TAG = "MessageAdapter";
  private Context context;
  private List<Message> messageList;
  private static final int TYPE_SENT = 1;
  private static final int TYPE_RECEIVED = 2;

  public MessageAdapter(Context context, List<Message> messageList) {
    this.context = context;
    this.messageList = messageList;
  }

  @Override
  public int getItemViewType(int position) {
    Message message = messageList.get(position);
    return message.isSentByMe() ? TYPE_SENT : TYPE_RECEIVED;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view;
    if (viewType == TYPE_SENT) {
      view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
    } else {
      view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
    }
    return new ViewHolder(view, viewType);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Message message = messageList.get(position);

    holder.tvMessageContent.setText(message.getContent());
    
    if (holder.viewType == TYPE_RECEIVED && message.getSenderName() != null) {
      holder.tvSenderName.setText(message.getSenderName());
      holder.tvSenderName.setVisibility(View.VISIBLE);
    } else {
      holder.tvSenderName.setVisibility(View.GONE);
    }

    // 顯示時間（簡化顯示，只顯示時間部分）
    if (message.getCreatedAt() != null && message.getCreatedAt().length() >= 16) {
      String time = message.getCreatedAt().substring(11, 16); // 提取 HH:mm
      holder.tvMessageTime.setText(time);
    } else {
      holder.tvMessageTime.setText("");
    }
  }

  @Override
  public int getItemCount() {
    return messageList.size();
  }

  public void addMessage(Message message) {
    messageList.add(message);
    notifyItemInserted(messageList.size() - 1);
  }

  public void updateMessageList(List<Message> newMessageList) {
    this.messageList = newMessageList;
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView tvMessageContent;
    TextView tvSenderName;
    TextView tvMessageTime;
    int viewType;

    public ViewHolder(@NonNull View itemView, int viewType) {
      super(itemView);
      this.viewType = viewType;
      tvMessageContent = itemView.findViewById(R.id.tv_message_content);
      tvSenderName = itemView.findViewById(R.id.tv_sender_name);
      tvMessageTime = itemView.findViewById(R.id.tv_message_time);
    }
  }
}

