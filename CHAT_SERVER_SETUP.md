# 聊天伺服器設置指南

## 聊天室功能說明

聊天室功能可以用於：
1. **專案協作聊天**：每個專案都有專屬的聊天室，成員可以在其中討論專案相關事宜
2. **私訊功能**：用戶之間可以發送私訊
3. **一般聊天室**：提供一個公共聊天空間

## 設置聊天伺服器

### 方法 1: 使用提供的 Node.js 範例

1. **安裝 Node.js**（如果尚未安裝）
   - 前往 [Node.js 官網](https://nodejs.org/) 下載並安裝

2. **安裝依賴**
   ```bash
   npm install ws
   ```

3. **啟動伺服器**
   ```bash
   node chat-server-example.js
   ```
   伺服器將在 `ws://localhost:8080` 上運行

4. **部署到雲端**（可選）
   - 可以使用 Heroku、Railway、或任何支援 Node.js 的雲端服務
   - 確保 WebSocket 連接埠開放

### 方法 2: 使用 Supabase Realtime（推薦）

Supabase 提供內建的 Realtime 功能，可以替代自建伺服器：

1. 在 Supabase Dashboard 中啟用 Realtime
2. 修改 `ChatClient` 類使用 Supabase Realtime API
3. 使用 Supabase 的訂閱功能來接收即時訊息

### 方法 3: 使用其他 WebSocket 服務

- **Pusher**: 提供 WebSocket 即時通訊服務
- **Socket.io**: 更強大的 WebSocket 框架
- **Firebase Realtime Database**: Google 的即時資料庫

## 在應用程式中設置伺服器 URL

1. **開發環境**（本地測試）
   ```java
   chatClient.setServerUrl("ws://10.0.2.2:8080/chat"); // Android 模擬器使用 10.0.2.2 訪問本機
   // 或
   chatClient.setServerUrl("ws://localhost:8080/chat"); // 實體設備需要改為實際 IP
   ```

2. **生產環境**
   ```java
   chatClient.setServerUrl("wss://your-server.com/chat"); // 使用 WSS (安全 WebSocket)
   ```

## 使用聊天室功能

### 1. 專案聊天室

在 `ProjectActivity` 或 `ProjectInfoFragment` 中添加聊天按鈕：

```java
Button btnChat = findViewById(R.id.btn_project_chat);
btnChat.setOnClickListener(v -> {
    Intent intent = new Intent(this, ChatActivity.class);
    intent.putExtra("roomId", "project_" + projectId);
    intent.putExtra("roomName", projectName + " 聊天室");
    startActivity(intent);
});
```

### 2. 私訊功能

在 `FriendFragment` 中添加私訊按鈕：

```java
// 在好友列表中點擊私訊
chatClient.sendPrivateMessage("你好", friendUserId);
```

### 3. 一般聊天室

創建一個入口進入一般聊天室：

```java
Intent intent = new Intent(this, ChatActivity.class);
intent.putExtra("roomId", "general");
intent.putExtra("roomName", "一般聊天室");
startActivity(intent);
```

## 伺服器功能擴展建議

可以添加以下功能來增強聊天室：

1. **訊息持久化**：將訊息儲存到資料庫
2. **訊息歷史**：載入歷史訊息
3. **檔案分享**：支援圖片和檔案傳輸
4. **@提及功能**：提及特定用戶
5. **表情符號**：支援表情符號
6. **已讀回條**：顯示訊息已讀狀態
7. **群組管理**：創建和管理群組聊天室

## 安全注意事項

1. **使用 WSS**：生產環境必須使用安全的 WebSocket (wss://)
2. **身份驗證**：在伺服器端驗證用戶身份
3. **速率限制**：防止訊息濫發
4. **內容過濾**：過濾不當內容
5. **加密**：對敏感訊息進行加密

## 故障排除

### 連接失敗
- 檢查伺服器是否運行
- 檢查防火牆設置
- 確認 URL 和埠號正確
- 檢查網路連接

### 訊息未收到
- 確認已加入正確的聊天室
- 檢查伺服器日誌
- 確認 WebSocket 連接狀態

### 性能問題
- 限制訊息歷史數量
- 使用分頁載入
- 優化訊息渲染

