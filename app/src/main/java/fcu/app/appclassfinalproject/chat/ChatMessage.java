package fcu.app.appclassfinalproject.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 聊天訊息模型類
 */
public class ChatMessage {
    private String userId;
    private String userName;
    private String content;
    private long timestamp;
    private String roomId;
    private boolean isFromMe;
    
    public ChatMessage(String userId, String userName, String content, long timestamp, String roomId) {
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getContent() {
        return content;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public boolean isFromMe() {
        return isFromMe;
    }
    
    public void setFromMe(boolean fromMe) {
        isFromMe = fromMe;
    }
    
    /**
     * 獲取格式化的時間字串
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 獲取格式化的日期時間字串
     */
    public String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 判斷是否為系統訊息
     */
    public boolean isSystemMessage() {
        return "system".equals(userId);
    }
}

