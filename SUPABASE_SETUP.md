# Supabase 設置指南

## 1. 創建 Supabase 專案

1. 前往 [Supabase](https://supabase.com) 註冊帳號
2. 創建新專案
3. 記下專案的 URL 和 API Key：
   - Project URL: `https://xxxxx.supabase.co`
   - Anon Key: 在 Settings > API 中可以找到

## 2. 設置資料庫表結構

在 Supabase SQL Editor 中執行以下 SQL 來創建所需的表：

```sql
-- Users 表
CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    account TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    firebase_uid TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Projects 表
CREATE TABLE IF NOT EXISTS Projects (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    summary TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Issues 表
CREATE TABLE IF NOT EXISTS Issues (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    summary TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    status TEXT NOT NULL,
    designee TEXT NOT NULL,
    project_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE
);

-- UserProject 表（多對多關係）
CREATE TABLE IF NOT EXISTS UserProject (
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE
);

-- UserIssue 表（多對多關係）
CREATE TABLE IF NOT EXISTS UserIssue (
    user_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, issue_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (issue_id) REFERENCES Issues(id) ON DELETE CASCADE
);

-- Friends 表
CREATE TABLE IF NOT EXISTS Friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 啟用 Row Level Security (RLS)
ALTER TABLE Users ENABLE ROW LEVEL SECURITY;
ALTER TABLE Projects ENABLE ROW LEVEL SECURITY;
ALTER TABLE Issues ENABLE ROW LEVEL SECURITY;
ALTER TABLE UserProject ENABLE ROW LEVEL SECURITY;
ALTER TABLE UserIssue ENABLE ROW LEVEL SECURITY;
ALTER TABLE Friends ENABLE ROW LEVEL SECURITY;

-- 創建策略（允許認證用戶訪問）
CREATE POLICY "Users can read all users" ON Users FOR SELECT USING (true);
CREATE POLICY "Users can insert own data" ON Users FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can update own data" ON Users FOR UPDATE USING (true);

CREATE POLICY "Users can read all projects" ON Projects FOR SELECT USING (true);
CREATE POLICY "Users can insert projects" ON Projects FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can update projects" ON Projects FOR UPDATE USING (true);
CREATE POLICY "Users can delete projects" ON Projects FOR DELETE USING (true);

CREATE POLICY "Users can read all issues" ON Issues FOR SELECT USING (true);
CREATE POLICY "Users can insert issues" ON Issues FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can update issues" ON Issues FOR UPDATE USING (true);
CREATE POLICY "Users can delete issues" ON Issues FOR DELETE USING (true);

CREATE POLICY "Users can read all user projects" ON UserProject FOR SELECT USING (true);
CREATE POLICY "Users can insert user projects" ON UserProject FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can delete user projects" ON UserProject FOR DELETE USING (true);

CREATE POLICY "Users can read all user issues" ON UserIssue FOR SELECT USING (true);
CREATE POLICY "Users can insert user issues" ON UserIssue FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can delete user issues" ON UserIssue FOR DELETE USING (true);

CREATE POLICY "Users can read all friends" ON Friends FOR SELECT USING (true);
CREATE POLICY "Users can insert friends" ON Friends FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can delete friends" ON Friends FOR DELETE USING (true);
```

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

## 注意事項

- 確保 Supabase 專案的 URL 和 API Key 正確設置
- RLS 策略可能需要根據您的需求調整
- 生產環境中應該使用環境變數或安全的配置管理方式存儲 API Key

