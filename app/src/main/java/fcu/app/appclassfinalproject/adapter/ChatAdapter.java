package fcu.app.appclassfinalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.chat.ChatMessage;
import java.util.List;

/**
 * 聊天訊息適配器
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messageList;
    private String currentUserId;
    private int layoutMessageSent;
    private int layoutMessageReceived;
    private int layoutSystemMessage;
    
    public ChatAdapter(android.content.Context context, List<ChatMessage> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.layoutMessageSent = R.layout.item_chat_message_sent;
        this.layoutMessageReceived = R.layout.item_chat_message_received;
        this.layoutSystemMessage = R.layout.item_chat_message_system;
    }
    
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.isSystemMessage()) {
            return 2; // 系統訊息
        } else if (message.isFromMe()) {
            return 0; // 自己發送的訊息
        } else {
            return 1; // 接收的訊息
        }
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(layoutMessageSent, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(layoutMessageReceived, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(layoutSystemMessage, parent, false);
        }
        return new ViewHolder(view, viewType);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        
        if (holder.viewType == 2) {
            // 系統訊息
            holder.tvSystemMessage.setText(message.getContent());
        } else {
            // 一般訊息
            holder.tvMessage.setText(message.getContent());
            holder.tvTime.setText(message.getFormattedTime());
            
            if (holder.viewType == 1) {
                // 接收的訊息顯示用戶名
                holder.tvUserName.setText(message.getUserName());
            }
        }
    }
    
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;
        TextView tvUserName;
        TextView tvSystemMessage;
        int viewType;
        
        ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            
            if (viewType == 2) {
                tvSystemMessage = itemView.findViewById(R.id.tv_system_message);
            } else {
                tvMessage = itemView.findViewById(R.id.tv_message);
                tvTime = itemView.findViewById(R.id.tv_time);
                if (viewType == 1) {
                    tvUserName = itemView.findViewById(R.id.tv_user_name);
                }
            }
        }
    }
}

