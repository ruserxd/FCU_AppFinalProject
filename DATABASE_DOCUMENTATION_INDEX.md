# 資料庫文檔索引

本文檔索引列出所有與 Supabase 資料庫相關的文檔和文件。

## 📚 核心文檔

### 1. [資料庫總覽](DATABASE_OVERVIEW.md)
**用途**：資料庫的完整概覽和快速參考
**內容**：
- 資料表結構詳細說明
- 所有欄位、索引、約束
- API 操作總覽
- 資料流程圖
- 快速參考範例

**適合**：需要快速了解資料庫結構的開發者

---

### 2. [資料庫設置腳本](SUPABASE_DATABASE_SCHEMA.sql)
**用途**：完整的 Supabase 資料庫設置 SQL 腳本
**內容**：
- 所有資料表的創建語句
- 索引創建
- 觸發器設置（自動更新時間戳）
- Row Level Security (RLS) 策略
- 視圖創建
- 函數定義
- 註釋說明

**使用方法**：
1. 在 Supabase Dashboard 中打開 SQL Editor
2. 複製整個文件內容
3. 執行 SQL 語句

**適合**：首次設置 Supabase 資料庫

---

### 3. [資料庫 API 文檔](SUPABASE_DATABASE_API.md)
**用途**：詳細的 API 方法文檔
**內容**：
- 所有 `SupabaseDatabaseHelper` 方法的詳細說明
- 參數說明
- 返回值說明
- 使用範例
- 錯誤處理
- 最佳實踐

**適合**：開發時查閱 API 使用方法

---

### 4. [資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md)
**用途**：從 SQLite 遷移到 Supabase 的完整指南
**內容**：
- 遷移概述
- 逐步遷移步驟
- 代碼遷移範例（舊代碼 vs 新代碼）
- 資料遷移方法
- 測試檢查清單
- 常見問題解答

**適合**：正在進行資料庫遷移的開發者

---

## 🔧 設置文檔

### 5. [Supabase 設置指南](SUPABASE_SETUP.md)
**用途**：Supabase 基本設置指南
**內容**：
- 創建 Supabase 專案
- 資料庫表結構設置（引用 `SUPABASE_DATABASE_SCHEMA.sql`）
- Gmail OAuth 配置
- 應用程式配置
- 測試連接

**適合**：首次設置 Supabase

---

## 📋 資料表結構總覽

### Users（用戶表）
- **主鍵**：`id` (SERIAL)
- **唯一約束**：`account`, `email`, `firebase_uid`
- **主要欄位**：account, email, firebase_uid, created_at, updated_at
- **相關操作**：6 個方法

### Projects（專案表）
- **主鍵**：`id` (SERIAL)
- **主要欄位**：name, summary, created_at, updated_at
- **相關操作**：5 個方法

### Issues（議題表）
- **主鍵**：`id` (SERIAL)
- **外鍵**：`project_id` → Projects(id)
- **主要欄位**：name, summary, start_time, end_time, status, designee, project_id
- **狀態值**：未開始/進行中/已完成（中文）或 TO-DO/In progress/Finished（英文）
- **相關操作**：6 個方法

### UserProject（用戶專案關聯表）
- **複合主鍵**：`(user_id, project_id)`
- **外鍵**：user_id → Users(id), project_id → Projects(id)
- **相關操作**：7 個方法

### UserIssue（用戶議題關聯表）
- **複合主鍵**：`(user_id, issue_id)`
- **外鍵**：user_id → Users(id), issue_id → Issues(id)
- **相關操作**：2 個方法

### Friends（好友關係表）
- **複合主鍵**：`(user_id, friend_id)`
- **外鍵**：user_id → Users(id), friend_id → Users(id)
- **檢查約束**：user_id != friend_id
- **相關操作**：3 個方法（自動處理雙向關係）

---

## 🎯 快速開始

### 第一次設置 Supabase

1. **閱讀**：[Supabase 設置指南](SUPABASE_SETUP.md)
2. **執行**：[資料庫設置腳本](SUPABASE_DATABASE_SCHEMA.sql)
3. **驗證**：檢查所有表、索引、視圖是否創建成功

### 開發時查閱

1. **API 方法**：查閱 [資料庫 API 文檔](SUPABASE_DATABASE_API.md)
2. **資料結構**：查閱 [資料庫總覽](DATABASE_OVERVIEW.md)
3. **遷移代碼**：參考 [資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md)

### 遷移現有代碼

1. **閱讀**：[資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md)
2. **參考範例**：指南中包含所有常見操作的遷移範例
3. **測試**：使用指南中的測試檢查清單

---

## 📊 統計資訊

### 資料表數量
- **6 個主要資料表**
- **3 個視圖**
- **4 個函數**

### API 方法數量
- **Users 表**：6 個方法
- **Projects 表**：5 個方法
- **Issues 表**：6 個方法
- **UserProject 表**：7 個方法
- **UserIssue 表**：2 個方法
- **Friends 表**：3 個方法
- **總計**：29 個方法

### 索引數量
- **15 個索引**（提升查詢效能）

### 觸發器數量
- **3 個觸發器**（自動更新 updated_at）

---

## 🔗 相關文檔

### 應用程式文檔
- [README.md](README.md) - 專案總覽
- [TODO.md](TODO.md) - 待辦事項
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - 實現總結

### 功能文檔
- [SUPABASE_SETUP.md](SUPABASE_SETUP.md) - Supabase 設置
- [CHAT_SERVER_SETUP.md](CHAT_SERVER_SETUP.md) - 聊天伺服器設置

---

## 📝 文檔維護

### 更新記錄
- **2025/12/16**：創建完整的 Supabase 資料庫文檔
  - 資料庫設置腳本
  - API 文檔
  - 遷移指南
  - 資料庫總覽

### 維護建議
- 當資料表結構變更時，更新 `SUPABASE_DATABASE_SCHEMA.sql`
- 當新增 API 方法時，更新 `SUPABASE_DATABASE_API.md`
- 當遷移流程變更時，更新 `DATABASE_MIGRATION_GUIDE.md`

---

## ✅ 檢查清單

使用以下清單確保資料庫設置完整：

- [ ] 已創建 Supabase 專案
- [ ] 已執行 `SUPABASE_DATABASE_SCHEMA.sql`
- [ ] 已驗證所有表創建成功
- [ ] 已驗證所有索引創建成功
- [ ] 已驗證 RLS 策略設置正確
- [ ] 已配置 Gmail OAuth
- [ ] 已在應用程式中設置 Supabase URL 和 API Key
- [ ] 已測試基本 CRUD 操作
- [ ] 已閱讀 API 文檔了解所有可用方法

---

## 💡 提示

1. **首次設置**：建議按照 [Supabase 設置指南](SUPABASE_SETUP.md) 逐步進行
2. **開發時**：將 [資料庫 API 文檔](SUPABASE_DATABASE_API.md) 作為參考手冊
3. **遷移時**：參考 [資料庫遷移指南](DATABASE_MIGRATION_GUIDE.md) 中的範例
4. **問題排查**：檢查 [資料庫總覽](DATABASE_OVERVIEW.md) 中的資料結構和約束

---

**最後更新**：2025/12/16

