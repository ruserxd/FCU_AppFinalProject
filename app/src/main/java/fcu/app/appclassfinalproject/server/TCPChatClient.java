package fcu.app.appclassfinalproject.server;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCP 客戶端 - 一對一聊天
 * 負責與服務器建立連接、發送和接收消息
 */
public class TCPChatClient {
  private static final String TAG = "TCPChatClient";
  private static TCPChatClient instance;

  // 服務器配置
  private String serverIP;
  private int serverPort;

  // Socket 連接
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  // 線程管理
  private ExecutorService executorService;
  private Handler mainHandler;
  private boolean isConnected = false;

  // 消息監聽器
  private MessageListener messageListener;

  /**
   * 消息監聽接口
   */
  public interface MessageListener {
    void onConnected();
    void onDisconnected();
    void onMessageReceived(String senderId, String content, long timestamp);
    void onMessageSent(String receiverId, long timestamp);
    void onMessageFailed(String receiverId, String error);
    void onError(String error);
  }

  private TCPChatClient() {
    this.executorService = Executors.newFixedThreadPool(2);
    this.mainHandler = new Handler(Looper.getMainLooper());
  }

  /**
   * 獲取單例
   */
  public static synchronized TCPChatClient getInstance() {
    if (instance == null) {
      instance = new TCPChatClient();
    }
    return instance;
  }

  /**
   * 設置服務器地址
   */
  public void setServer(String ip, int port) {
    this.serverIP = ip;
    this.serverPort = port;
  }

  /**
   * 設置消息監聽器
   */
  public void setMessageListener(MessageListener listener) {
    this.messageListener = listener;
  }

  /**
   * 連接到服務器
   */
  public void connect(final String userId, final String userName) {
    executorService.execute(() -> {
      try {
        Log.d(TAG, "正在連接服務器: " + serverIP + ":" + serverPort);

        socket = new Socket(serverIP, serverPort);
        out = new PrintWriter(new OutputStreamWriter(
            socket.getOutputStream(), "UTF-8"), true);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream(), "UTF-8"));

        isConnected = true;
        Log.d(TAG, "服務器連接成功");

        // 發送登入請求
        login(userId, userName);

        // 開始接收消息
        startReceiving();

      } catch (IOException e) {
        Log.e(TAG, "連接失敗: " + e.getMessage());
        isConnected = false;
        mainHandler.post(() -> {
          if (messageListener != null) {
            messageListener.onError("連接失敗: " + e.getMessage());
          }
        });
      }
    });
  }

  /**
   * 登入
   */
  private void login(String userId, String userName) {
    try {
      JSONObject json = new JSONObject();
      json.put("type", "LOGIN");
      json.put("userId", userId);
      json.put("userName", userName);
      sendMessageToServer(json.toString());
    } catch (JSONException e) {
      Log.e(TAG, "登入失敗: " + e.getMessage());
    }
  }

  /**
   * 持續接收服務器消息
   */
  private void startReceiving() {
    executorService.execute(() -> {
      try {
        String message;
        while (isConnected && (message = in.readLine()) != null) {
          Log.d(TAG, "收到消息: " + message);
          handleServerMessage(message);
        }
      } catch (IOException e) {
        Log.e(TAG, "接收消息錯誤: " + e.getMessage());
        disconnect();
      }
    });
  }

  /**
   * 處理服務器消息
   */
  private void handleServerMessage(String message) {
    try {
      JSONObject json = new JSONObject(message);
      String type = json.getString("type");

      mainHandler.post(() -> {
        try {
          switch (type) {
            case "LOGIN_SUCCESS":
              if (messageListener != null) {
                messageListener.onConnected();
              }
              Log.d(TAG, "登入成功");
              break;

            case "MESSAGE_RECEIVED":
              // 收到別人發來的消息
              String senderId = json.getString("senderId");
              String content = json.getString("content");
              long timestamp = json.getLong("timestamp");

              if (messageListener != null) {
                messageListener.onMessageReceived(senderId, content, timestamp);
              }
              break;

            case "MESSAGE_SENT":
              // 自己的消息已送達
              String receiverId = json.getString("receiverId");
              long sentTime = json.getLong("timestamp");

              if (messageListener != null) {
                messageListener.onMessageSent(receiverId, sentTime);
              }
              break;

            case "MESSAGE_FAILED":
              // 消息發送失敗
              String failedReceiver = json.getString("receiverId");
              String error = json.getString("error");

              if (messageListener != null) {
                messageListener.onMessageFailed(failedReceiver, error);
              }
              break;
          }
        } catch (JSONException e) {
          Log.e(TAG, "處理消息錯誤: " + e.getMessage());
        }
      });

    } catch (JSONException e) {
      Log.e(TAG, "解析消息錯誤: " + e.getMessage());
    }
  }

  /**
   * 發送消息到服務器
   */
  private void sendMessageToServer(String message) {
    if (!isConnected || out == null) {
      Log.e(TAG, "未連接到服務器");
      return;
    }

    executorService.execute(() -> {
      try {
        out.println(message);
        Log.d(TAG, "消息已發送: " + message);
      } catch (Exception e) {
        Log.e(TAG, "發送消息失敗: " + e.getMessage());
      }
    });
  }

  /**
   * 發送聊天消息
   */
  public void sendChatMessage(String receiverId, String content) {
    try {
      JSONObject json = new JSONObject();
      json.put("type", "SEND_MESSAGE");
      json.put("receiverId", receiverId);
      json.put("content", content);
      json.put("timestamp", System.currentTimeMillis());

      sendMessageToServer(json.toString());
    } catch (JSONException e) {
      Log.e(TAG, "發送聊天消息失敗: " + e.getMessage());
    }
  }

  /**
   * 斷開連接
   */
  public void disconnect() {
    isConnected = false;

    try {
      // 發送登出消息
      if (out != null) {
        JSONObject json = new JSONObject();
        json.put("type", "LOGOUT");
        out.println(json.toString());
      }

      if (out != null) out.close();
      if (in != null) in.close();
      if (socket != null) socket.close();

      mainHandler.post(() -> {
        if (messageListener != null) {
          messageListener.onDisconnected();
        }
      });

      Log.d(TAG, "已斷開連接");
    } catch (Exception e) {
      Log.e(TAG, "斷開連接錯誤: " + e.getMessage());
    }
  }

  /**
   * 檢查是否已連接
   */
  public boolean isConnected() {
    return isConnected && socket != null && socket.isConnected();
  }

  /**
   * 清理資源
   */
  public void shutdown() {
    disconnect();
    if (executorService != null) {
      executorService.shutdown();
    }
  }
}