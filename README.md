# 114-1 網路程式設計期末專題

## 專案簡介

這是一個基於 Android 平台的專案管理應用程式，提供完整的專案與議題管理功能，支援多人協作、好友系統、資料匯出等功能。

**最新更新（2025/12/16）**：
- ✨ 整合 Supabase 雲端資料庫和認證服務
- 💬 新增 WebSocket 即時聊天室功能
- 🔐 支援 Gmail OAuth 登入
- 🌐 使用 RESTful API 進行資料操作

## 使用的工具

### 後端服務
- **Supabase**（雲端資料庫和認證服務）
  - Supabase Auth（用戶認證，支援 Gmail OAuth）
  - Supabase PostgREST API（RESTful 資料庫 API）
- **Firebase**（舊版，正在遷移到 Supabase）
  - Firebase Authentication（舊版認證）
  - Firebase Firestore（部分功能）

### 本地資料庫
- **SQLite**（本地資料庫，正在遷移到 Supabase）

### 開發工具
- Android Studio
- Java
- Apache POI（Excel 匯出）
- OkHttp（HTTP 客戶端）
- Gson（JSON 處理）
- Java-WebSocket（WebSocket 客戶端）

### 聊天服務
- **WebSocket 聊天伺服器**（Node.js）
  - 支援專案聊天室
  - 支援私訊功能
  - 支援一般聊天室

## 專案版本

- 2025/12/16 前: 「Android APP 期末專題」框架 -> 請見 `Android-APP-期末專題` 分支

## TO-DO

- 2025/12/16: 更新於 [TODO.md](TODO.md) 文件

## 新功能（2025/12/16）

### ✨ Supabase 整合
- 資料庫遷移到 Supabase（PostgreSQL）
- 用戶認證改用 Supabase（支援 Gmail OAuth）
- RESTful API 整合

### 💬 聊天室功能
- WebSocket 即時聊天
- 專案協作聊天室
- 私訊功能
- 一般聊天室

詳細設置指南請參考：
- [Supabase 設置指南](SUPABASE_SETUP.md)
- [聊天伺服器設置指南](CHAT_SERVER_SETUP.md)
- [實現總結](IMPLEMENTATION_SUMMARY.md)

## 主要功能

### 1. 用戶認證系統

#### 登入功能
- **Supabase 認證**（新）
  - Gmail OAuth 登入
  - 電子郵件/密碼登入
  - 自動登入狀態保持
  - 令牌管理和刷新
- **Firebase 認證**（舊版，正在遷移）
  - 電子郵件/密碼登入
  - 自動登入狀態保持
- 登入錯誤訊息提示（密碼錯誤、帳號不存在等）
- 語言切換功能（中文/英文）

#### 註冊功能
- **Supabase 註冊**（新）
  - 新用戶註冊（帳號、電子郵件、密碼）
  - 自動同步到 Supabase 資料庫
- **Firebase 註冊**（舊版，正在遷移）
  - 新用戶註冊（帳號、電子郵件、密碼）
  - Firebase 與本地資料庫同步
- 輸入驗證（必填欄位、密碼長度、電子郵件格式）
- 網路連線檢查
- 自動登入並導向主頁

### 2. 專案管理

#### 專案列表（HomeFragment）
- 顯示當前用戶參與的所有專案
- 專案卡片顯示：專案名稱、專案摘要
- 點擊專案卡片進入專案詳情頁面
- 自動刷新專案列表

#### 建立專案（AddFragment）
- 輸入專案名稱和摘要
- 選擇專案成員（支援多選）
- 自動完成輸入框搜尋用戶帳號
- 已選擇成員列表顯示與移除功能
- 建立專案並建立成員關聯

#### 專案詳情（ProjectActivity）
- 顯示專案資訊
- 議題列表查看
- 新增議題
- 專案設定
- 刪除專案（需確認，僅成員可刪除）
- 顯示專案中的議題數量和成員數量

#### 刪除專案
- 確認對話框
- 級聯刪除相關資料（Issues、UserProject、UserIssue）
- 僅專案成員可執行刪除操作

### 3. 議題管理

#### 議題列表（ProjectInfoFragment）
- 顯示專案下的所有議題
- 議題卡片顯示：名稱、摘要、開始時間、結束時間、狀態、被指派者
- 點擊專案名稱進入甘特圖視圖
- 點擊議題進入編輯頁面

#### 建立議題（AddIssueFragment）
- 輸入議題名稱（主旨）
- 輸入議題摘要（概述）
- 選擇開始時間和結束時間（日期選擇器）
- 選擇狀態（未開始/進行中/已完成）
- 指派給專案成員（僅能指派給專案成員）
- 日期範圍驗證
- 自動建立 UserIssue 關聯

#### 編輯議題（EditIssueActivity）
- 修改議題所有資訊
- 狀態下拉選單（支援多語言）
- 儲存修改
- 刪除議題（需確認）
- 級聯刪除 UserIssue 關聯

#### 甘特圖視圖（GanttActivity）
- 顯示專案所有議題的時間軸
- 議題名稱列表
- 月份時間軸顯示
- 議題時間區間視覺化
- 狀態顏色標示
- 顯示當前日期

### 4. 好友系統

#### 好友列表（FriendFragment）
- 顯示當前用戶的所有好友
- 好友資訊顯示：帳號、電子郵件
- 刪除好友功能
- 雙向好友關係管理

#### 新增好友（AddFriendFragment）
- 顯示可新增的用戶列表（排除自己與已是好友的用戶）
- 點擊用戶卡片新增好友
- 建立雙向好友關係

### 5. 設定功能（SettingsFragment）

#### 語言切換
- 支援中文（繁體）和英文
- 即時切換並重新載入應用程式
- 語言設定持久化儲存

#### Excel 匯出（ExportExcel）
- 匯出用戶參與的所有專案
- 專案列表工作表：專案 ID、名稱、摘要、成員
- 每個專案的議題工作表：議題詳細資訊
- 支援多語言標題
- 大字體格式（適合列印）
- 自動調整欄寬和行高

#### GitHub 專案匯入
- 輸入 GitHub 用戶名稱
- 透過 GitHub API 獲取用戶的所有 Repository
- 自動建立專案並加入當前用戶為成員
- 處理重複專案（加入現有專案而非重複建立）
- 顯示匯入成功/失敗訊息
- 自動更新專案計數

#### 專案計數顯示
- 顯示當前用戶參與的專案數量
- 自動更新（GitHub 匯入後）

#### 登出功能
- 清除 Firebase 登入狀態
- 清除本地 SharedPreferences
- 返回登入頁面

#### 刪除帳號
- 確認對話框
- 密碼驗證
- 刪除本地資料庫中的用戶資料
- 刪除用戶相關的議題和專案（如果用戶是唯一成員）
- 刪除好友關係
- 刪除 Firebase 帳號
- 清除所有資料並返回登入頁面

### 6. 資料庫架構

#### Supabase 資料庫（新）✨
- **Users**：用戶資料（id, account, email, firebase_uid, created_at, updated_at）
- **Projects**：專案資料（id, name, summary, created_at, updated_at）
- **Issues**：議題資料（id, name, summary, start_time, end_time, status, designee, project_id, created_at, updated_at）
- **UserProject**：用戶與專案的多對多關聯
- **UserIssue**：用戶與議題的多對多關聯
- **Friends**：好友關係（雙向）

**詳細資料庫文檔**：
- 📄 [資料庫總覽](DATABASE_OVERVIEW.md) - 完整的資料庫結構說明
- 📄 [資料庫設置腳本](SUPABASE_DATABASE_SCHEMA.sql) - 完整的 SQL 設置腳本
- 📄 [資料庫 API 文檔](SUPABASE_DATABASE_API.md) - 所有可用的資料庫操作方法
- 📄 [資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md) - 從 SQLite 遷移到 Supabase

#### SQLite 資料庫（舊版，正在遷移）🔄
- 相同的資料表結構
- 本地儲存和快取
- 將逐步遷移到 Supabase

#### 資料同步
- **Supabase**（新）：雲端資料庫，即時同步，支援多設備
- **Firebase Authentication**（舊版）：與本地 SQLite 資料庫同步
- **UserSyncHelper**：處理用戶資料同步邏輯（舊版）
- **SupabaseDatabaseHelper**：處理 Supabase 資料庫操作（新）✨
  - 提供完整的 CRUD 操作
  - 支援所有業務邏輯方法
  - 詳細 API 文檔見 [SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md)

### 7. 多語言支援

- 支援繁體中文和英文
- 所有 UI 文字、錯誤訊息、確認對話框均支援多語言
- 語言設定儲存在 SharedPreferences
- 應用程式啟動時自動載入設定語言

### 8. 使用者介面

#### 主頁面（HomeActivity）
- 底部導航欄：首頁、新增、設定
- Fragment 切換：HomeFragment、AddFragment、SettingsFragment

#### 專案頁面（ProjectActivity）
- 底部導航欄：議題列表、返回、新增議題、設定、刪除專案
- Fragment 切換：ProjectInfoFragment、AddIssueFragment、SettingsFragment

#### 設計特色
- Material Design 風格
- Edge-to-Edge 顯示
- 響應式佈局
- 清晰的視覺層次

### 9. 安全性功能

- Firebase Authentication 安全認證
- 密碼驗證（刪除帳號時）
- 用戶權限檢查（專案刪除、議題指派等）
- 資料驗證（輸入格式、必填欄位等）

### 10. 網路功能

- GitHub API 整合（匯入專案）
- 網路狀態檢查（註冊時）
- HTTP 連線處理（OkHttp）
- Supabase REST API 整合

### 11. 聊天室功能（新）✨

#### 專案協作聊天室
- 每個專案自動擁有專屬聊天室（`project_{projectId}`）
- 專案成員可以在聊天室中討論專案相關事宜
- 即時訊息傳遞（WebSocket）
- 顯示用戶加入/離開通知

#### 私訊功能
- 用戶之間可以發送私訊
- 一對一即時通訊
- 訊息時間戳記

#### 一般聊天室
- 提供公共聊天空間
- 所有用戶可以參與

#### 聊天室特性
- WebSocket 即時連接
- 連接狀態顯示
- 訊息歷史顯示
- 系統訊息通知
- 支援多種訊息類型（自己發送、接收、系統訊息）

#### 使用場景
1. **專案協作**：專案成員在專案聊天室中討論任務、進度、問題
2. **即時溝通**：快速溝通，無需切換到其他應用
3. **團隊協作**：促進團隊成員之間的協作和溝通

## 技術架構

### 架構模式
- Activity + Fragment 架構
- **Supabase 雲端資料庫**（新，PostgreSQL）
- **Supabase 認證服務**（新，支援 Gmail OAuth）
- SQLite 本地資料庫（舊版，正在遷移）
- Firebase 雲端認證（舊版，正在遷移）
- SharedPreferences 本地設定儲存
- WebSocket 即時通訊（聊天室功能）

### 主要類別

#### Activity
- `LoginActivity`：登入頁面
- `RegisterActivity`：註冊頁面
- `HomeActivity`：主頁面
- `ProjectActivity`：專案詳情頁面
- `CreateIssueActivity`：建立議題頁面（部分功能）
- `EditIssueActivity`：編輯議題頁面
- `GanttActivity`：甘特圖視圖
- `ChatActivity`：聊天室頁面（新）✨

#### Fragment
- `HomeFragment`：專案列表
- `AddFragment`：建立專案
- `SettingsFragment`：設定頁面
- `ProjectInfoFragment`：專案議題列表
- `AddIssueFragment`：建立議題
- `FriendFragment`：好友列表
- `AddFriendFragment`：新增好友

#### Helper
- `SqlDataBaseHelper`：SQLite 資料庫管理（舊版）
- `SupabaseConfig`：Supabase 配置和 HTTP 請求管理（新）✨
- `SupabaseAuthHelper`：Supabase 認證助手（新）✨
- `SupabaseDatabaseHelper`：Supabase 資料庫操作（新）✨
- `ProjectHelper`：專案相關操作
- `UserSyncHelper`：用戶資料同步（舊版）
- `ExportExcel`：Excel 匯出功能

#### Adapter
- `ProjectAdapter`：專案列表適配器
- `IssueAdapter`：議題列表適配器
- `FriendAdapter`：好友列表適配器
- `AddfriendAdapter`：可新增好友列表適配器
- `IssueNameAdapter`：甘特圖議題名稱適配器
- `IssueMonthAdapter`：甘特圖月份適配器
- `ChatAdapter`：聊天訊息適配器（新）✨

#### Chat（聊天室功能）✨
- `ChatClient`：WebSocket 聊天客戶端
- `ChatMessage`：聊天訊息模型

#### Model
- `User`：用戶模型
- `Project`：專案模型
- `Issue`：議題模型
- `IssueName`：甘特圖議題名稱模型
- `IssueMonth`：甘特圖月份模型

## 使用流程

1. **註冊/登入**：新用戶註冊或現有用戶登入（支援 Gmail OAuth）
2. **查看專案**：在主頁面查看參與的專案列表
3. **建立專案**：在新增頁面建立新專案並選擇成員
4. **進入專案**：點擊專案卡片進入專案詳情頁面
5. **管理議題**：查看、新增、編輯、刪除議題
6. **查看甘特圖**：點擊專案名稱查看時間軸視圖
7. **專案聊天**：在專案頁面進入專案聊天室，與成員即時溝通（新）✨
8. **好友管理**：在設定頁面管理好友
9. **私訊功能**：與好友發送私訊（新）✨
10. **匯出資料**：匯出專案資料為 Excel 檔案
11. **匯入 GitHub**：從 GitHub 匯入專案
12. **設定**：切換語言、登出、刪除帳號等

## 設置指南

### Supabase 設置
1. 創建 Supabase 專案
2. 設置資料庫表結構（執行 `SUPABASE_DATABASE_SCHEMA.sql`）
3. 配置 Gmail OAuth Provider
4. 在應用程式中設置 Supabase URL 和 API Key

**相關文檔**：
- 📄 [Supabase 設置指南](SUPABASE_SETUP.md) - 基本設置步驟
- 📄 [資料庫總覽](DATABASE_OVERVIEW.md) - 資料庫結構總覽
- 📄 [資料庫設置腳本](SUPABASE_DATABASE_SCHEMA.sql) - 完整的 SQL 設置腳本
- 📄 [資料庫 API 文檔](SUPABASE_DATABASE_API.md) - 所有可用的資料庫操作方法

### 聊天伺服器設置
1. 安裝 Node.js 和依賴
2. 啟動 WebSocket 聊天伺服器
3. 在應用程式中設置伺服器 URL

詳細步驟請參考：[聊天伺服器設置指南](CHAT_SERVER_SETUP.md)

### 資料庫遷移指南
從 SQLite 遷移到 Supabase 的詳細說明請參考：
- 📄 [資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md) - 完整的遷移步驟和代碼範例
- 📄 [實現總結](IMPLEMENTATION_SUMMARY.md) - 整體實現總結

## 注意事項

- 需要網路連線才能使用 Supabase 認證、資料庫操作和 GitHub 匯入功能
- 聊天室功能需要運行 WebSocket 伺服器
- 議題只能指派給專案成員
- 刪除專案會同時刪除所有相關議題
- 刪除帳號會永久刪除所有相關資料
- Excel 匯出檔案儲存在應用程式文件目錄
- Supabase 配置需要在首次使用前設置（見設置指南）

## 專案狀態

### ✅ 已完成
- Supabase 資料庫整合
- Supabase 認證（支援 Gmail OAuth）
- WebSocket 聊天室功能
- 專案協作聊天室
- 私訊功能

### 🔄 進行中
- 從 Firebase/SQLite 遷移到 Supabase
- 更新現有 Activity 和 Fragment 使用 Supabase

### 📋 待完成
- 完成所有代碼遷移
- 測試所有功能
- 部署到生產環境

## 未來擴展

- 訊息持久化（儲存聊天記錄到資料庫）
- 訊息歷史載入
- 檔案分享功能（圖片、文件）
- @提及功能
- 表情符號支援
- 已讀回條
- 推播通知
- 議題評論功能
- 專案權限管理
- 資料統計圖表
- 使用 Supabase Realtime 替代自建 WebSocket 伺服器