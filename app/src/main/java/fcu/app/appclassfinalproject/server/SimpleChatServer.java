package fcu.app.appclassfinalproject.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/**
 * 一對一 TCP 聊天服務器（獨立運行）
 * 編譯: javac -cp json-20230227.jar SimpleChatServer.java
 * 運行: java -cp .:json-20230227.jar SimpleChatServer
 * Windows: java -cp .;json-20230227.jar SimpleChatServer
 */
public class SimpleChatServer {
  private static final int PORT = 8888;
  private static Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

  public static void main(String[] args) {
    System.out.println("===========================================");
    System.out.println("      TCP 聊天服務器");
    System.out.println("===========================================");
    System.out.println("監聽端口: " + PORT);
    System.out.println("啟動時間: " + new Date());
    System.out.println("等待客戶端連接...\n");

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        String clientIP = clientSocket.getInetAddress().getHostAddress();
        System.out.println("[" + getCurrentTime() + "] 新連接: " + clientIP);

        ClientHandler handler = new ClientHandler(clientSocket);
        new Thread(handler).start();
      }
    } catch (IOException e) {
      System.err.println("❌ 服務器錯誤: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static String getCurrentTime() {
    return new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
  }

  /**
   * 客戶端處理器
   */
  static class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String userId;
    private String userName;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

        String message;
        while ((message = in.readLine()) != null) {
          handleMessage(message);
        }
      } catch (IOException e) {
        System.out.println("[" + getCurrentTime() + "] 客戶端斷開: " + userName + " (" + userId + ")");
      } finally {
        cleanup();
      }
    }

    private void handleMessage(String message) {
      try {
        JSONObject json = new JSONObject(message);
        String type = json.getString("type");

        switch (type) {
          case "LOGIN":
            handleLogin(json);
            break;
          case "SEND_MESSAGE":
            handleSendMessage(json);
            break;
          case "LOGOUT":
            handleLogout();
            break;
        }
      } catch (JSONException e) {
        System.err.println("❌ 消息解析錯誤: " + e.getMessage());
      }
    }

    private void handleLogin(JSONObject json) throws JSONException {
      userId = json.getString("userId");
      userName = json.optString("userName", userId);

      onlineUsers.put(userId, this);

      JSONObject response = new JSONObject();
      response.put("type", "LOGIN_SUCCESS");
      response.put("userId", userId);
      response.put("message", "登入成功");
      sendMessage(response.toString());

      System.out.println("[" + getCurrentTime() + "] ✅ 用戶登入: " + userName + " (ID: " + userId + ")");
      System.out.println("   當前在線: " + onlineUsers.size() + " 人");
    }

    private void handleSendMessage(JSONObject json) throws JSONException {
      String receiverId = json.getString("receiverId");
      String content = json.getString("content");
      long timestamp = json.optLong("timestamp", System.currentTimeMillis());

      ClientHandler receiver = onlineUsers.get(receiverId);

      if (receiver != null) {
        JSONObject message = new JSONObject();
        message.put("type", "MESSAGE_RECEIVED");
        message.put("senderId", userId);
        message.put("content", content);
        message.put("timestamp", timestamp);

        receiver.sendMessage(message.toString());

        JSONObject ack = new JSONObject();
        ack.put("type", "MESSAGE_SENT");
        ack.put("receiverId", receiverId);
        ack.put("timestamp", timestamp);
        sendMessage(ack.toString());

        System.out.println("[" + getCurrentTime() + "] 📨 轉發消息: " + userName + " → " + receiverId);
        System.out.println("   內容: " + (content.length() > 50 ? content.substring(0, 50) + "..." : content));
      } else {
        JSONObject error = new JSONObject();
        error.put("type", "MESSAGE_FAILED");
        error.put("receiverId", receiverId);
        error.put("error", "用戶離線");
        sendMessage(error.toString());

        System.out.println("[" + getCurrentTime() + "] ❌ 發送失敗: 用戶 " + receiverId + " 離線");
      }
    }

    private void handleLogout() {
      System.out.println("[" + getCurrentTime() + "] 👋 用戶登出: " + userName);
      cleanup();
    }

    public void sendMessage(String message) {
      if (out != null) {
        out.println(message);
      }
    }

    private void cleanup() {
      try {
        if (userId != null) {
          onlineUsers.remove(userId);
          System.out.println("[" + getCurrentTime() + "] 移除用戶: " + userName);
          System.out.println("   當前在線: " + onlineUsers.size() + " 人\n");
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}