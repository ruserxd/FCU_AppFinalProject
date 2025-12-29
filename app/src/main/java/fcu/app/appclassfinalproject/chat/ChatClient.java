package fcu.app.appclassfinalproject.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 聊天客戶端
 * 連接到聊天伺服器並處理訊息收發
 */
public class ChatClient {
    private static final String TAG = "ChatClient";
    private static ChatClient instance;
    
    private WebSocketClient webSocketClient;
    private Context context;
    private String serverUrl;
    private String currentUserId;
    private String currentRoomId;
    private boolean isConnected = false;
    
    private List<ChatMessageListener> messageListeners = new ArrayList<>();
    private List<ConnectionListener> connectionListeners = new ArrayList<>();
    
    private ChatClient(Context context) {
        this.context = context;
        // TODO: 設置您的聊天伺服器 URL
        // 例如: "ws://your-server.com:8080/chat" 或 "wss://your-server.com/chat"
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        this.serverUrl = prefs.getString("chat_server_url", "ws://localhost:8080/chat");
    }
    
    public static synchronized ChatClient getInstance(Context context) {
        if (instance == null) {
            instance = new ChatClient(context);
        }
        return instance;
    }
    
    /**
     * 設置伺服器 URL
     */
    public void setServerUrl(String url) {
        this.serverUrl = url;
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("chat_server_url", url);
        editor.apply();
    }
    
    /**
     * 連接到聊天伺服器
     */
    public void connect(String userId, String userName) {
        if (isConnected) {
            Log.w(TAG, "已經連接到伺服器");
            return;
        }
        
        this.currentUserId = userId;
        
        try {
            URI serverUri = URI.create(serverUrl);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket 連接已建立");
                    isConnected = true;
                    
                    // 發送加入訊息
                    JsonObject joinMessage = new JsonObject();
                    joinMessage.addProperty("type", "join");
                    joinMessage.addProperty("userId", userId);
                    joinMessage.addProperty("userName", userName);
                    send(joinMessage.toString());
                    
                    notifyConnectionListeners(true);
                }
                
                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "收到訊息: " + message);
                    handleMessage(message);
                }
                
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket 連接已關閉: " + reason);
                    isConnected = false;
                    notifyConnectionListeners(false);
                }
                
                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket 錯誤", ex);
                    isConnected = false;
                    notifyConnectionListeners(false);
                }
            };
            
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "連接錯誤", e);
            isConnected = false;
            notifyConnectionListeners(false);
        }
    }
    
    /**
     * 斷開連接
     */
    public void disconnect() {
        if (webSocketClient != null && isConnected) {
            // 發送離開訊息
            if (currentRoomId != null) {
                leaveRoom();
            }
            
            JsonObject leaveMessage = new JsonObject();
            leaveMessage.addProperty("type", "leave");
            leaveMessage.addProperty("userId", currentUserId);
            send(leaveMessage.toString());
            
            webSocketClient.close();
            isConnected = false;
            notifyConnectionListeners(false);
        }
    }
    
    /**
     * 加入聊天室
     */
    public void joinRoom(String roomId, String roomName) {
        if (!isConnected) {
            Log.w(TAG, "未連接到伺服器");
            return;
        }
        
        this.currentRoomId = roomId;
        
        JsonObject message = new JsonObject();
        message.addProperty("type", "join_room");
        message.addProperty("roomId", roomId);
        message.addProperty("roomName", roomName);
        message.addProperty("userId", currentUserId);
        
        send(message.toString());
    }
    
    /**
     * 離開聊天室
     */
    public void leaveRoom() {
        if (currentRoomId == null) {
            return;
        }
        
        JsonObject message = new JsonObject();
        message.addProperty("type", "leave_room");
        message.addProperty("roomId", currentRoomId);
        message.addProperty("userId", currentUserId);
        
        send(message.toString());
        currentRoomId = null;
    }
    
    /**
     * 發送聊天訊息
     */
    public void sendMessage(String content, String roomId) {
        if (!isConnected) {
            Log.w(TAG, "未連接到伺服器");
            return;
        }
        
        JsonObject message = new JsonObject();
        message.addProperty("type", "message");
        message.addProperty("roomId", roomId != null ? roomId : currentRoomId);
        message.addProperty("userId", currentUserId);
        message.addProperty("content", content);
        message.addProperty("timestamp", System.currentTimeMillis());
        
        send(message.toString());
    }
    
    /**
     * 發送專案相關訊息（專案聊天室）
     */
    public void sendProjectMessage(String content, int projectId) {
        String roomId = "project_" + projectId;
        sendMessage(content, roomId);
    }
    
    /**
     * 發送私訊
     */
    public void sendPrivateMessage(String content, String targetUserId) {
        if (!isConnected) {
            Log.w(TAG, "未連接到伺服器");
            return;
        }
        
        JsonObject message = new JsonObject();
        message.addProperty("type", "private_message");
        message.addProperty("fromUserId", currentUserId);
        message.addProperty("toUserId", targetUserId);
        message.addProperty("content", content);
        message.addProperty("timestamp", System.currentTimeMillis());
        
        send(message.toString());
    }
    
    /**
     * 處理收到的訊息
     */
    private void handleMessage(String messageStr) {
        try {
            JsonObject message = JsonParser.parseString(messageStr).getAsJsonObject();
            String type = message.get("type").getAsString();
            
            switch (type) {
                case "message":
                case "private_message":
                    ChatMessage chatMessage = parseChatMessage(message);
                    notifyMessageListeners(chatMessage);
                    break;
                case "user_joined":
                    String joinedUserId = message.get("userId").getAsString();
                    String joinedUserName = message.has("userName") ? 
                        message.get("userName").getAsString() : "未知用戶";
                    notifyMessageListeners(new ChatMessage(
                        "system", "系統", joinedUserName + " 加入了聊天室", 
                        System.currentTimeMillis(), "system"
                    ));
                    break;
                case "user_left":
                    String leftUserId = message.get("userId").getAsString();
                    String leftUserName = message.has("userName") ? 
                        message.get("userName").getAsString() : "未知用戶";
                    notifyMessageListeners(new ChatMessage(
                        "system", "系統", leftUserName + " 離開了聊天室", 
                        System.currentTimeMillis(), "system"
                    ));
                    break;
                case "error":
                    String error = message.has("error") ? message.get("error").getAsString() : "未知錯誤";
                    Log.e(TAG, "伺服器錯誤: " + error);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "處理訊息錯誤", e);
        }
    }
    
    private ChatMessage parseChatMessage(JsonObject message) {
        String userId = message.get("userId").getAsString();
        String userName = message.has("userName") ? message.get("userName").getAsString() : userId;
        String content = message.get("content").getAsString();
        long timestamp = message.has("timestamp") ? message.get("timestamp").getAsLong() : System.currentTimeMillis();
        String roomId = message.has("roomId") ? message.get("roomId").getAsString() : null;
        
        return new ChatMessage(userId, userName, content, timestamp, roomId);
    }
    
    private void send(String message) {
        if (webSocketClient != null && isConnected) {
            webSocketClient.send(message);
        }
    }
    
    /**
     * 添加訊息監聽器
     */
    public void addMessageListener(ChatMessageListener listener) {
        messageListeners.add(listener);
    }
    
    /**
     * 移除訊息監聽器
     */
    public void removeMessageListener(ChatMessageListener listener) {
        messageListeners.remove(listener);
    }
    
    /**
     * 添加連接監聽器
     */
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }
    
    /**
     * 移除連接監聽器
     */
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
    
    private void notifyMessageListeners(ChatMessage message) {
        for (ChatMessageListener listener : messageListeners) {
            listener.onMessageReceived(message);
        }
    }
    
    private void notifyConnectionListeners(boolean connected) {
        for (ConnectionListener listener : connectionListeners) {
            if (connected) {
                listener.onConnected();
            } else {
                listener.onDisconnected();
            }
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    /**
     * 聊天訊息監聽器接口
     */
    public interface ChatMessageListener {
        void onMessageReceived(ChatMessage message);
    }
    
    /**
     * 連接狀態監聽器接口
     */
    public interface ConnectionListener {
        void onConnected();
        void onDisconnected();
    }
}

