package fcu.app.appclassfinalproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.model.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

  private static final String TAG = "MessageAdapter";

  private Context context;
  private List<Message> messageList;
  private int currentUserId;
  private SimpleDateFormat dateFormat;

  public MessageAdapter(Context context, List<Message> messageList, int currentUserId) {
    this.context = context;
    this.messageList = messageList;
    this.currentUserId = currentUserId;
    this.dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    Log.d(TAG, "MessageAdapter 創建 - currentUserId: " + currentUserId);
  }

  @NonNull
  @Override
  public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
    return new MessageViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
    Message message = messageList.get(position);
    boolean isSentByMe = message.getSenderId() == currentUserId;

    // 除錯日誌
    Log.d(TAG, "消息 #" + position +
        " - senderId: " + message.getSenderId() +
        ", currentUserId: " + currentUserId +
        ", isSentByMe: " + isSentByMe);

    // 設置消息內容和時間
    holder.tvMessage.setText(message.getContent());
    holder.tvTime.setText(dateFormat.format(new Date(message.getTimestamp())));

    // 使用 FrameLayout.LayoutParams
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageContainer.getLayoutParams();

    if (isSentByMe) {
      // 我發送的消息 - 靠右，藍色背景
      Log.d(TAG, "設置為「我的消息」- 藍色、靠右");

      params.gravity = Gravity.END;
      holder.messageContainer.setBackgroundResource(R.drawable.message_sent_background);
      holder.tvMessage.setTextColor(context.getResources().getColor(android.R.color.white));
      holder.tvTime.setTextColor(context.getResources().getColor(android.R.color.white));

    } else {
      // 對方發送的消息 - 靠左，灰色背景
      Log.d(TAG, "設置為「對方消息」- 灰色、靠左");

      params.gravity = Gravity.START;
      holder.messageContainer.setBackgroundResource(R.drawable.message_received_background);
      holder.tvMessage.setTextColor(context.getResources().getColor(android.R.color.black));
      holder.tvTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
    }

    holder.messageContainer.setLayoutParams(params);
  }

  @Override
  public int getItemCount() {
    return messageList.size();
  }

  public static class MessageViewHolder extends RecyclerView.ViewHolder {
    LinearLayout messageContainer;
    TextView tvMessage;
    TextView tvTime;

    public MessageViewHolder(@NonNull View itemView) {
      super(itemView);
      messageContainer = itemView.findViewById(R.id.message_container);
      tvMessage = itemView.findViewById(R.id.tv_message);
      tvTime = itemView.findViewById(R.id.tv_time);
    }
  }
}