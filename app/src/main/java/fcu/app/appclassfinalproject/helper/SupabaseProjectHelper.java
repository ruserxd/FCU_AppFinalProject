package fcu.app.appclassfinalproject.helper;

import fcu.app.appclassfinalproject.model.ChatRoom;
import fcu.app.appclassfinalproject.model.Message;
import fcu.app.appclassfinalproject.supabase.ChatHelper;
import java.util.List;

public class SupabaseProjectHelper {

    public SupabaseProjectHelper() {
        // Helper class for Supabase operations
    }

    /**
     * 建立一對一聊天室
     */
    public Integer createPrivateChatRoom(String userId1, String userId2) {
        return ChatHelper.INSTANCE.createPrivateChatRoom(userId1, userId2);
    }

    /**
     * 建立群組聊天室
     */
    public Integer createGroupChatRoom(String groupName, String creatorId, List<String> memberIds) {
        return ChatHelper.INSTANCE.createGroupChatRoom(groupName, creatorId, memberIds);
    }

    /**
     * 獲取用戶的所有聊天室
     */
    public List<ChatRoom> getChatRoomsByUser(String userId) {
        return ChatHelper.INSTANCE.getChatRoomsByUser(userId);
    }

    /**
     * 發送訊息
     */
    public Long sendMessage(int chatroomId, String senderId, String content) {
        return ChatHelper.INSTANCE.sendMessage(chatroomId, senderId, content);
    }

    /**
     * 獲取聊天室的所有訊息
     */
    public List<Message> getMessagesByChatRoom(int chatroomId, String currentUserId) {
        return ChatHelper.INSTANCE.getMessagesByChatRoom(chatroomId, currentUserId);
    }

    /**
     * 獲取當前用戶 ID
     */
    public String getCurrentUserId() {
        return ChatHelper.INSTANCE.getCurrentUserId();
    }
}
