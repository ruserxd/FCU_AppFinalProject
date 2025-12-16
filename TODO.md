# 符合網路程式設計的期末專案要求

## ✅ 已完成項目

### 1. 資料庫改用 Supabase ✅
- [x] 添加 Supabase 依賴（OkHttp, Gson）
- [x] 創建 `SupabaseConfig` 配置類
- [x] 創建 `SupabaseDatabaseHelper` 資料庫操作類
- [x] 實現所有 CRUD 操作（Users, Projects, Issues, Friends 等）
- [x] 提供 Supabase 設置指南（見 `SUPABASE_SETUP.md`）

**注意**：需要將現有的 Activity 和 Fragment 中的 SQLite 操作遷移到 Supabase。參考 `SupabaseDatabaseHelper` 中的方法。

### 2. 使用者驗證改用 Supabase 提供的 Gmail 驗證 ✅
- [x] 創建 `SupabaseAuthHelper` 認證助手類
- [x] 實現 Gmail OAuth 登入
- [x] 實現電子郵件/密碼登入
- [x] 實現註冊功能
- [x] 實現登出和令牌管理

**注意**：需要在 Supabase Dashboard 中配置 Google OAuth Provider。然後更新 `LoginActivity` 和 `RegisterActivity` 使用 `SupabaseAuthHelper` 替代 Firebase Auth。

### 3. 建立聊天室功能 ✅
- [x] 建立中心的伺服器（Node.js WebSocket 伺服器範例）
- [x] 修改客戶端程式碼（`ChatClient`, `ChatActivity`, `ChatAdapter`）
- [x] 實現聊天室 UI（布局文件）
- [x] 提供伺服器設置指南（見 `CHAT_SERVER_SETUP.md`）

#### 聊天室用途：
1. **專案協作聊天**：每個專案都有專屬聊天室（`project_{projectId}`），成員可以討論專案相關事宜
2. **私訊功能**：用戶之間可以發送私訊
3. **一般聊天室**：提供公共聊天空間

## 📝 待完成項目

### 遷移現有代碼
- [ ] 更新 `LoginActivity` 使用 `SupabaseAuthHelper` 替代 Firebase Auth
- [ ] 更新 `RegisterActivity` 使用 `SupabaseAuthHelper`
- [ ] 更新所有 Fragment 和 Activity 使用 `SupabaseDatabaseHelper` 替代 SQLite
- [ ] 在 `ProjectActivity` 或 `ProjectInfoFragment` 中添加聊天室入口按鈕
- [ ] 在 `FriendFragment` 中添加私訊功能

### Supabase 設置
- [ ] 在 Supabase 創建專案並設置資料庫表結構（見 `SUPABASE_SETUP.md`）
- [ ] 配置 Gmail OAuth Provider
- [ ] 在應用程式中設置 Supabase URL 和 API Key

### 聊天伺服器部署
- [ ] 啟動聊天伺服器（見 `CHAT_SERVER_SETUP.md`）
- [ ] 在應用程式中設置伺服器 URL
- [ ] 測試聊天室功能

## 📚 相關文件

- `SUPABASE_SETUP.md` - Supabase 設置指南
- `CHAT_SERVER_SETUP.md` - 聊天伺服器設置指南
- `chat-server-example.js` - WebSocket 聊天伺服器範例
- `package.json` - 聊天伺服器依賴配置