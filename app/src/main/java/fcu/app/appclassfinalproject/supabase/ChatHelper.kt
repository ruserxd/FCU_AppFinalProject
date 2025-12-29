package fcu.app.appclassfinalproject.supabase

import fcu.app.appclassfinalproject.model.ChatRoom
import fcu.app.appclassfinalproject.model.Message
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class ChatRoomData(
    val id: Int? = null,
    val name: String? = null,
    val type: String,
    val created_at: String,
    val created_by: String
)

@Serializable
data class ChatRoomMemberData(
    val id: Int? = null,
    val chatroom_id: Int,
    val user_id: String,
    val joined_at: String
)

@Serializable
data class MessageData(
    val id: Int? = null,
    val chatroom_id: Int,
    val sender_id: String,
    val content: String,
    val created_at: String
)

@Serializable
data class UserData(
    val id: String,
    val account: String? = null,
    val email: String
)

object ChatHelper {

    private val client = SupabaseManager.getClient()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * 建立一對一聊天室
     */
    fun createPrivateChatRoom(userId1: String, userId2: String): Int? {
        return runBlocking {
            try {
                // 檢查是否已存在聊天室
                val existingRooms = client.from("chatrooms")
                    .select {
                        filter {
                            eq("type", "private")
                        }
                    }
                    .decodeList<ChatRoomData>()

                // 檢查兩個用戶是否已經在同一個聊天室
                for (room in existingRooms) {
                    val members = client.from("chatroom_members")
                        .select {
                            filter {
                                eq("chatroom_id", room.id!!)
                            }
                        }
                        .decodeList<ChatRoomMemberData>()

                    val memberIds = members.map { it.user_id }.toSet()
                    if (memberIds.contains(userId1) && memberIds.contains(userId2)) {
                        return@runBlocking room.id
                    }
                }

                // 建立新聊天室
                val newRoom = ChatRoomData(
                    name = null,
                    type = "private",
                    created_at = dateFormat.format(Date()),
                    created_by = userId1
                )

                val createdRoom = client.from("chatrooms")
                    .insert(newRoom) {
                        select(Columns.ALL)
                    }
                    .decodeSingle<ChatRoomData>()

                // 添加兩個成員
                val member1 = ChatRoomMemberData(
                    chatroom_id = createdRoom.id!!,
                    user_id = userId1,
                    joined_at = dateFormat.format(Date())
                )
                val member2 = ChatRoomMemberData(
                    chatroom_id = createdRoom.id!!,
                    user_id = userId2,
                    joined_at = dateFormat.format(Date())
                )

                client.from("chatroom_members").insert(member1)
                client.from("chatroom_members").insert(member2)

                createdRoom.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 建立群組聊天室
     */
    fun createGroupChatRoom(groupName: String, creatorId: String, memberIds: List<String>): Int? {
        return runBlocking {
            try {
                val newRoom = ChatRoomData(
                    name = groupName,
                    type = "group",
                    created_at = dateFormat.format(Date()),
                    created_by = creatorId
                )

                val createdRoom = client.from("chatrooms")
                    .insert(newRoom) {
                        select(Columns.ALL)
                    }
                    .decodeSingle<ChatRoomData>()

                // 添加所有成員
                val now = dateFormat.format(Date())
                val members = (memberIds + creatorId).map { userId ->
                    ChatRoomMemberData(
                        chatroom_id = createdRoom.id!!,
                        user_id = userId,
                        joined_at = now
                    )
                }

                client.from("chatroom_members").insert(members)

                createdRoom.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 獲取用戶的所有聊天室
     */
    fun getChatRoomsByUser(userId: String): List<ChatRoom> {
        return runBlocking {
            try {
                // 獲取用戶參與的聊天室 ID
                val memberRooms = client.from("chatroom_members")
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<ChatRoomMemberData>()

                val roomIds = memberRooms.map { it.chatroom_id }.distinct()

                if (roomIds.isEmpty()) {
                    return@runBlocking emptyList()
                }

                // 獲取聊天室資訊（簡化版本：逐個查詢）
                val rooms = roomIds.flatMap { roomId ->
                    try {
                        client.from("chatrooms")
                            .select {
                                filter {
                                    eq("id", roomId)
                                }
                            }
                            .decodeList<ChatRoomData>()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }.sortedByDescending { it.created_at }

                // 轉換為 ChatRoom 模型
                rooms.map { roomData ->
                    val chatRoom = ChatRoom(
                        roomData.id!!,
                        roomData.name ?: "",
                        roomData.type,
                        roomData.created_at,
                        roomData.created_by.toIntOrNull() ?: 0
                    )

                    // 獲取成員名稱
                    val members = client.from("chatroom_members")
                        .select {
                            filter {
                                eq("chatroom_id", roomData.id!!)
                                neq("user_id", userId)
                            }
                        }
                        .decodeList<ChatRoomMemberData>()

                    val memberIds = members.map { it.user_id }
                    if (memberIds.isNotEmpty()) {
                        val users = memberIds.flatMap { memberId ->
                            try {
                                client.from("users")
                                    .select {
                                        filter {
                                            eq("id", memberId)
                                        }
                                    }
                                    .decodeList<UserData>()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }

                        chatRoom.setMemberNames(users.mapNotNull { it.account })
                    }

                    // 獲取最後一條訊息
                    val lastMessage = getLastMessage(roomData.id!!)
                    if (lastMessage != null) {
                        chatRoom.setLastMessage(lastMessage.content)
                        chatRoom.setLastMessageTime(lastMessage.created_at)
                    }

                    chatRoom
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * 獲取聊天室的最後一條訊息
     */
    private fun getLastMessage(chatroomId: Int): MessageData? {
        return runBlocking {
            try {
                val messages = client.from("messages")
                    .select {
                        filter {
                            eq("chatroom_id", chatroomId)
                        }
                        order("created_at", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeList<MessageData>()

                messages.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 發送訊息
     */
    fun sendMessage(chatroomId: Int, senderId: String, content: String): Long? {
        return runBlocking {
            try {
                val message = MessageData(
                    chatroom_id = chatroomId,
                    sender_id = senderId,
                    content = content,
                    created_at = dateFormat.format(Date())
                )

                val createdMessage = client.from("messages")
                    .insert(message) {
                        select(Columns.ALL)
                    }
                    .decodeSingle<MessageData>()

                createdMessage.id?.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 獲取聊天室的所有訊息
     */
    fun getMessagesByChatRoom(chatroomId: Int, currentUserId: String): List<Message> {
        return runBlocking {
            try {
                val messages = client.from("messages")
                    .select {
                        filter {
                            eq("chatroom_id", chatroomId)
                        }
                        order("created_at", Order.ASCENDING)
                    }
                    .decodeList<MessageData>()

                // 獲取發送者資訊
                val senderIds = messages.map { it.sender_id }.distinct()
                val users = if (senderIds.isNotEmpty()) {
                    // 查詢所有發送者（簡化版本：逐個查詢）
                    senderIds.flatMap { senderId ->
                        try {
                            client.from("users")
                                .select {
                                    filter {
                                        eq("id", senderId)
                                    }
                                }
                                .decodeList<UserData>()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                } else {
                    emptyList()
                }

                val userMap = users.associateBy { it.id }

                // 轉換為 Message 模型
                messages.map { msgData ->
                    val message = Message(
                        msgData.id ?: 0,
                        msgData.chatroom_id,
                        msgData.sender_id.toIntOrNull() ?: 0,
                        msgData.content,
                        msgData.created_at
                    )

                    val user = userMap[msgData.sender_id]
                    message.setSenderName(user?.account ?: user?.email ?: "未知用戶")
                    message.setSentByMe(msgData.sender_id == currentUserId)

                    message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    /**
     * 獲取當前用戶 ID
     */
    fun getCurrentUserId(): String? {
        return runBlocking {
            try {
                val session = client.auth.currentSessionOrNull()
                session?.user?.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

