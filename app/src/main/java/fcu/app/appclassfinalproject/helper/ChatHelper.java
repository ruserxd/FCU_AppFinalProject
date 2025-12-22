package fcu.app.appclassfinalproject.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fcu.app.appclassfinalproject.model.ChatRoom;
import fcu.app.appclassfinalproject.model.Message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatHelper {

  private static final String TAG = "ChatHelper";

  /**
   * 建立一對一聊天室
   */
  public static int createPrivateChatRoom(SQLiteDatabase db, int userId1, int userId2) {
    try {
      // 檢查是否已存在聊天室
      String checkQuery = "SELECT cr.id FROM ChatRooms cr " +
          "INNER JOIN ChatRoomMembers crm1 ON cr.id = crm1.chatroom_id " +
          "INNER JOIN ChatRoomMembers crm2 ON cr.id = crm2.chatroom_id " +
          "WHERE cr.type = 'private' " +
          "AND crm1.user_id = ? AND crm2.user_id = ? " +
          "AND crm1.user_id != crm2.user_id";

      Cursor cursor = db.rawQuery(checkQuery, new String[]{
          String.valueOf(userId1), String.valueOf(userId2)
      });

      if (cursor.moveToFirst()) {
        int existingRoomId = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "找到現有的一對一聊天室: " + existingRoomId);
        return existingRoomId;
      }
      cursor.close();

      // 建立新聊天室
      String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
          .format(new Date());

      ContentValues roomValues = new ContentValues();
      roomValues.put("name", null); // 一對一聊天室沒有名稱
      roomValues.put("type", "private");
      roomValues.put("created_at", currentTime);
      roomValues.put("created_by", userId1);

      long roomId = db.insert("ChatRooms", null, roomValues);

      if (roomId != -1) {
        // 添加兩個成員
        addMemberToChatRoom(db, (int) roomId, userId1);
        addMemberToChatRoom(db, (int) roomId, userId2);
        Log.d(TAG, "建立一對一聊天室成功: " + roomId);
        return (int) roomId;
      }

    } catch (Exception e) {
      Log.e(TAG, "建立一對一聊天室失敗: " + e.getMessage(), e);
    }
    return -1;
  }

  /**
   * 建立群組聊天室
   */
  public static int createGroupChatRoom(SQLiteDatabase db, String groupName, int creatorId,
      List<Integer> memberIds) {
    try {
      String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
          .format(new Date());

      ContentValues roomValues = new ContentValues();
      roomValues.put("name", groupName);
      roomValues.put("type", "group");
      roomValues.put("created_at", currentTime);
      roomValues.put("created_by", creatorId);

      long roomId = db.insert("ChatRooms", null, roomValues);

      if (roomId != -1) {
        // 添加所有成員
        for (Integer memberId : memberIds) {
          addMemberToChatRoom(db, (int) roomId, memberId);
        }
        // 添加創建者
        addMemberToChatRoom(db, (int) roomId, creatorId);
        Log.d(TAG, "建立群組聊天室成功: " + roomId);
        return (int) roomId;
      }

    } catch (Exception e) {
      Log.e(TAG, "建立群組聊天室失敗: " + e.getMessage(), e);
    }
    return -1;
  }

  /**
   * 添加成員到聊天室
   */
  private static void addMemberToChatRoom(SQLiteDatabase db, int chatroomId, int userId) {
    try {
      String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
          .format(new Date());

      ContentValues values = new ContentValues();
      values.put("chatroom_id", chatroomId);
      values.put("user_id", userId);
      values.put("joined_at", currentTime);

      db.insertWithOnConflict("ChatRoomMembers", null, values,
          SQLiteDatabase.CONFLICT_IGNORE);
    } catch (Exception e) {
      Log.e(TAG, "添加成員失敗: " + e.getMessage(), e);
    }
  }

  /**
   * 獲取用戶的所有聊天室
   */
  public static List<ChatRoom> getChatRoomsByUser(SQLiteDatabase db, int userId) {
    List<ChatRoom> chatRooms = new ArrayList<>();
    try {
      String query = "SELECT DISTINCT cr.id, cr.name, cr.type, cr.created_at, cr.created_by " +
          "FROM ChatRooms cr " +
          "INNER JOIN ChatRoomMembers crm ON cr.id = crm.chatroom_id " +
          "WHERE crm.user_id = ? " +
          "ORDER BY cr.created_at DESC";

      Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

      while (cursor.moveToNext()) {
        int id = cursor.getInt(0);
        String name = cursor.isNull(1) ? null : cursor.getString(1);
        String type = cursor.getString(2);
        String createdAt = cursor.getString(3);
        int createdBy = cursor.getInt(4);

        ChatRoom chatRoom = new ChatRoom(id, name, type, createdAt, createdBy);

        // 獲取成員名稱列表
        List<String> memberNames = getChatRoomMemberNames(db, id, userId);
        chatRoom.setMemberNames(memberNames);

        // 獲取最後一條訊息
        Message lastMessage = getLastMessage(db, id);
        if (lastMessage != null) {
          chatRoom.setLastMessage(lastMessage.getContent());
          chatRoom.setLastMessageTime(lastMessage.getCreatedAt());
        }

        chatRooms.add(chatRoom);
      }
      cursor.close();

    } catch (Exception e) {
      Log.e(TAG, "獲取聊天室列表失敗: " + e.getMessage(), e);
    }
    return chatRooms;
  }

  /**
   * 獲取聊天室成員名稱（排除當前用戶）
   */
  private static List<String> getChatRoomMemberNames(SQLiteDatabase db, int chatroomId,
      int currentUserId) {
    List<String> memberNames = new ArrayList<>();
    try {
      String query = "SELECT u.account FROM Users u " +
          "INNER JOIN ChatRoomMembers crm ON u.id = crm.user_id " +
          "WHERE crm.chatroom_id = ? AND u.id != ?";

      Cursor cursor = db.rawQuery(query,
          new String[]{String.valueOf(chatroomId), String.valueOf(currentUserId)});

      while (cursor.moveToNext()) {
        memberNames.add(cursor.getString(0));
      }
      cursor.close();

    } catch (Exception e) {
      Log.e(TAG, "獲取成員名稱失敗: " + e.getMessage(), e);
    }
    return memberNames;
  }

  /**
   * 獲取聊天室的最後一條訊息
   */
  private static Message getLastMessage(SQLiteDatabase db, int chatroomId) {
    try {
      String query = "SELECT id, sender_id, content, created_at " +
          "FROM Messages " +
          "WHERE chatroom_id = ? " +
          "ORDER BY created_at DESC " +
          "LIMIT 1";

      Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(chatroomId)});

      if (cursor.moveToFirst()) {
        int id = cursor.getInt(0);
        int senderId = cursor.getInt(1);
        String content = cursor.getString(2);
        String createdAt = cursor.getString(3);

        cursor.close();
        return new Message(id, chatroomId, senderId, content, createdAt);
      }
      cursor.close();

    } catch (Exception e) {
      Log.e(TAG, "獲取最後訊息失敗: " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * 發送訊息
   */
  public static long sendMessage(SQLiteDatabase db, int chatroomId, int senderId,
      String content) {
    try {
      String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
          .format(new Date());

      ContentValues values = new ContentValues();
      values.put("chatroom_id", chatroomId);
      values.put("sender_id", senderId);
      values.put("content", content);
      values.put("created_at", currentTime);

      long messageId = db.insert("Messages", null, values);
      Log.d(TAG, "發送訊息成功: " + messageId);
      return messageId;

    } catch (Exception e) {
      Log.e(TAG, "發送訊息失敗: " + e.getMessage(), e);
      return -1;
    }
  }

  /**
   * 獲取聊天室的所有訊息
   */
  public static List<Message> getMessagesByChatRoom(SQLiteDatabase db, int chatroomId,
      int currentUserId) {
    List<Message> messages = new ArrayList<>();
    try {
      String query = "SELECT m.id, m.sender_id, m.content, m.created_at, u.account " +
          "FROM Messages m " +
          "INNER JOIN Users u ON m.sender_id = u.id " +
          "WHERE m.chatroom_id = ? " +
          "ORDER BY m.created_at ASC";

      Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(chatroomId)});

      while (cursor.moveToNext()) {
        int id = cursor.getInt(0);
        int senderId = cursor.getInt(1);
        String content = cursor.getString(2);
        String createdAt = cursor.getString(3);
        String senderName = cursor.getString(4);

        Message message = new Message(id, chatroomId, senderId, content, createdAt);
        message.setSenderName(senderName);
        message.setSentByMe(senderId == currentUserId);

        messages.add(message);
      }
      cursor.close();

    } catch (Exception e) {
      Log.e(TAG, "獲取訊息列表失敗: " + e.getMessage(), e);
    }
    return messages;
  }
}

