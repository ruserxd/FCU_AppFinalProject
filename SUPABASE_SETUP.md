# Supabase 設置指南

## 1. 創建 Supabase 專案

1. 前往 [Supabase](https://supabase.com) 註冊帳號
2. 創建新專案
3. 記下專案的 URL 和 API Key：
   - Project URL: `https://xxxxx.supabase.co`
   - Anon Key: 在 Settings > API 中可以找到

## 2. 設置資料庫表結構

### 方法一：使用完整設置腳本（推薦）

在 Supabase Dashboard 中：
1. 前往 **SQL Editor**
2. 點擊 **New Query**
3. 複製並執行 `SUPABASE_DATABASE_SCHEMA.sql` 文件中的所有 SQL 語句

這個腳本包含：
- ✅ 所有資料表的創建
- ✅ 索引（提升查詢效能）
- ✅ 觸發器（自動更新時間戳）
- ✅ Row Level Security (RLS) 策略
- ✅ 視圖（方便查詢）
- ✅ 函數（業務邏輯）
- ✅ 註釋（文檔說明）

**詳細說明請參考**：[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)

### 方法二：逐步設置

如果您想逐步設置，可以參考 `SUPABASE_DATABASE_SCHEMA.sql` 文件中的各個部分。

## 3. 設置 Gmail OAuth

1. 在 Supabase Dashboard 中，前往 Authentication > Providers
2. 啟用 Google Provider
3. 設置 Google OAuth：
   - 前往 [Google Cloud Console](https://console.cloud.google.com)
   - 創建 OAuth 2.0 憑證
   - 添加授權的重定向 URI: `https://YOUR_PROJECT_REF.supabase.co/auth/v1/callback`
   - 將 Client ID 和 Client Secret 填入 Supabase

## 4. 在應用程式中設置 Supabase

在應用程式首次運行時，需要設置 Supabase 配置。可以在 `SettingsFragment` 中添加配置選項，或直接在 `SupabaseConfig` 類中設置：

```java
SupabaseConfig.getInstance(context).setConfig(
    "https://your-project.supabase.co",
    "your-anon-key",
    "your-service-key" // 可選，僅用於服務端操作
);
```

## 5. 測試連接

運行應用程式並測試：
1. 註冊/登入功能
2. 創建專案
3. 創建議題
4. 好友功能

## 5. 測試連接

運行應用程式並測試：
1. 註冊/登入功能
2. 創建專案
3. 創建議題
4. 好友功能

## 6. 相關文檔

- **[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)**：完整的資料庫設置腳本
  - 包含所有表結構、索引、觸發器、RLS 策略、視圖、函數
  - 建議使用此文件進行資料庫設置

- **[SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md)**：資料庫 API 文檔
  - 詳細說明所有可用的資料庫操作方法
  - 包含使用範例和最佳實踐

- **[DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md)**：資料庫遷移指南
  - 從 SQLite 遷移到 Supabase 的完整指南
  - 包含代碼遷移範例和測試檢查清單

## 注意事項

- 確保 Supabase 專案的 URL 和 API Key 正確設置
- RLS 策略可能需要根據您的需求調整
- 生產環境中應該使用環境變數或安全的配置管理方式存儲 API Key
- 建議先閱讀 `SUPABASE_DATABASE_SCHEMA.sql` 了解完整的資料庫結構
- 遷移代碼時參考 `DATABASE_MIGRATION_GUIDE.md` 中的範例

