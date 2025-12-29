# 資料庫總覽

本文檔提供專案資料庫的完整概覽，包括結構、功能和遷移狀態。

## 資料庫架構

### 當前狀態

- **主要資料庫**：Supabase (PostgreSQL) ✅
- **舊版資料庫**：SQLite（正在遷移中）🔄
- **認證服務**：Supabase Auth（支援 Gmail OAuth）✅

### 資料表結構

專案包含以下 6 個主要資料表：

1. **Users** - 用戶資料
2. **Projects** - 專案資料
3. **Issues** - 議題資料
4. **UserProject** - 用戶與專案關聯（多對多）
5. **UserIssue** - 用戶與議題關聯（多對多）
6. **Friends** - 好友關係（雙向）

詳細結構請參考：[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)

---

## 資料表詳細說明

### 1. Users（用戶表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| id | SERIAL | 用戶唯一識別碼 | PRIMARY KEY |
| account | TEXT | 用戶帳號 | NOT NULL, UNIQUE |
| email | TEXT | 用戶電子郵件 | NOT NULL, UNIQUE |
| firebase_uid | TEXT | Firebase 用戶識別碼 | UNIQUE, 可選 |
| created_at | TIMESTAMP | 創建時間 | DEFAULT NOW() |
| updated_at | TIMESTAMP | 更新時間 | DEFAULT NOW() |

**索引**：
- `idx_users_email` - 電子郵件索引
- `idx_users_firebase_uid` - Firebase UID 索引
- `idx_users_account` - 帳號索引

**主要操作**：
- 插入用戶：`insertUser()`
- 根據 email 查詢：`getUserByEmail()`
- 根據 ID 查詢：`getUserById()`
- 根據帳號查詢：`getUserIdByAccount()`

---

### 2. Projects（專案表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| id | SERIAL | 專案唯一識別碼 | PRIMARY KEY |
| name | TEXT | 專案名稱 | NOT NULL |
| summary | TEXT | 專案摘要 | NOT NULL |
| created_at | TIMESTAMP | 創建時間 | DEFAULT NOW() |
| updated_at | TIMESTAMP | 更新時間 | DEFAULT NOW() |

**索引**：
- `idx_projects_name` - 專案名稱索引

**主要操作**：
- 插入專案：`insertProject()`
- 根據用戶查詢：`getProjectsByUser()`
- 根據 ID 查詢：`getProjectById()`
- 更新專案：`updateProject()`
- 刪除專案：`deleteProject()`

---

### 3. Issues（議題表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| id | SERIAL | 議題唯一識別碼 | PRIMARY KEY |
| name | TEXT | 議題名稱 | NOT NULL |
| summary | TEXT | 議題摘要 | NOT NULL |
| start_time | TEXT | 開始時間 | NOT NULL (YYYY-MM-DD) |
| end_time | TEXT | 結束時間 | NOT NULL (YYYY-MM-DD) |
| status | TEXT | 議題狀態 | NOT NULL, CHECK |
| designee | TEXT | 被指派者帳號 | NOT NULL |
| project_id | INTEGER | 所屬專案ID | NOT NULL, FOREIGN KEY |
| created_at | TIMESTAMP | 創建時間 | DEFAULT NOW() |
| updated_at | TIMESTAMP | 更新時間 | DEFAULT NOW() |

**狀態值**：
- 中文：`未開始`、`進行中`、`已完成`
- 英文：`TO-DO`、`In progress`、`Finished`

**索引**：
- `idx_issues_project_id` - 專案ID索引
- `idx_issues_status` - 狀態索引
- `idx_issues_designee` - 被指派者索引
- `idx_issues_start_time` - 開始時間索引
- `idx_issues_end_time` - 結束時間索引

**外鍵約束**：
- `project_id` → `Projects(id)` ON DELETE CASCADE

**主要操作**：
- 插入議題：`insertIssue()`
- 根據專案查詢：`getIssuesByProject()`
- 根據 ID 查詢：`getIssueById()`
- 更新議題：`updateIssue()`
- 刪除議題：`deleteIssue()`

---

### 4. UserProject（用戶專案關聯表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| user_id | INTEGER | 用戶ID | PRIMARY KEY (部分) |
| project_id | INTEGER | 專案ID | PRIMARY KEY (部分) |
| created_at | TIMESTAMP | 關聯創建時間 | DEFAULT NOW() |

**複合主鍵**：`(user_id, project_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `project_id` → `Projects(id)` ON DELETE CASCADE

**索引**：
- `idx_userproject_user_id` - 用戶ID索引
- `idx_userproject_project_id` - 專案ID索引

**主要操作**：
- 添加用戶到專案：`addUserToProject()`
- 獲取專案成員：`getProjectMembers()`
- 獲取成員ID列表：`getProjectMemberIds()`
- 獲取成員名稱列表：`getProjectMemberNames()`
- 移除用戶：`removeUserFromProject()`
- 檢查成員資格：`isUserProjectMember()`
- 獲取成員數量：`getProjectMemberCount()`

---

### 5. UserIssue（用戶議題關聯表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| user_id | INTEGER | 用戶ID | PRIMARY KEY (部分) |
| issue_id | INTEGER | 議題ID | PRIMARY KEY (部分) |
| created_at | TIMESTAMP | 關聯創建時間 | DEFAULT NOW() |

**複合主鍵**：`(user_id, issue_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `issue_id` → `Issues(id)` ON DELETE CASCADE

**索引**：
- `idx_userissue_user_id` - 用戶ID索引
- `idx_userissue_issue_id` - 議題ID索引

**主要操作**：
- 添加用戶到議題：`addUserToIssue()`
- 移除用戶：`removeUserFromIssue()`

---

### 6. Friends（好友關係表）

| 欄位 | 類型 | 說明 | 約束 |
|------|------|------|------|
| user_id | INTEGER | 用戶ID | PRIMARY KEY (部分) |
| friend_id | INTEGER | 好友ID | PRIMARY KEY (部分) |
| created_at | TIMESTAMP | 好友關係創建時間 | DEFAULT NOW() |

**複合主鍵**：`(user_id, friend_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `friend_id` → `Users(id)` ON DELETE CASCADE

**檢查約束**：`user_id != friend_id`（防止用戶與自己成為好友）

**索引**：
- `idx_friends_user_id` - 用戶ID索引
- `idx_friends_friend_id` - 好友ID索引

**主要操作**：
- 添加好友：`addFriend()`（自動建立雙向關係）
- 獲取好友列表：`getFriends()`
- 刪除好友：`removeFriend()`（自動刪除雙向關係）

---

## 資料庫視圖

Supabase 提供了以下視圖方便查詢：

### 1. project_details
專案詳情視圖，包含：
- 專案基本資訊
- 成員數量
- 議題數量

### 2. user_projects_view
用戶專案列表視圖，包含：
- 用戶資訊
- 專案資訊
- 加入時間

### 3. issue_details
議題詳情視圖，包含：
- 議題基本資訊
- 專案資訊

---

## 資料庫函數

Supabase 提供了以下函數：

### 1. get_user_project_count(user_id)
獲取用戶參與的專案數量

### 2. get_project_member_count(project_id)
獲取專案成員數量

### 3. get_project_issue_count(project_id)
獲取專案議題數量

### 4. is_user_project_member(user_id, project_id)
檢查用戶是否為專案成員

---

## 安全性

### Row Level Security (RLS)

所有表都啟用了 RLS，確保資料安全。當前策略允許：
- 所有認證用戶讀取資料
- 所有認證用戶插入資料
- 所有認證用戶更新資料
- 所有認證用戶刪除資料

**注意**：生產環境可能需要更嚴格的策略。

### 外鍵約束

所有外鍵都設置了 `ON DELETE CASCADE`，確保：
- 刪除專案時，相關議題和關聯自動刪除
- 刪除用戶時，相關關聯自動刪除
- 刪除議題時，相關關聯自動刪除

---

## API 操作總覽

### Users 表
- ✅ `insertUser()` - 插入用戶
- ✅ `getUserByEmail()` - 根據 email 查詢
- ✅ `getUserById()` - 根據 ID 查詢
- ✅ `getUserIdByAccount()` - 根據帳號查詢 ID
- ✅ `getUserIdByFirebaseUid()` - 根據 Firebase UID 查詢 ID
- ✅ `getAllUsers()` - 獲取所有用戶

### Projects 表
- ✅ `insertProject()` - 插入專案
- ✅ `getProjectsByUser()` - 獲取用戶的專案
- ✅ `getProjectById()` - 根據 ID 查詢
- ✅ `updateProject()` - 更新專案
- ✅ `deleteProject()` - 刪除專案

### Issues 表
- ✅ `insertIssue()` - 插入議題
- ✅ `getIssuesByProject()` - 獲取專案的議題
- ✅ `getIssueById()` - 根據 ID 查詢
- ✅ `updateIssue()` - 更新議題
- ✅ `deleteIssue()` - 刪除議題
- ✅ `getProjectIssueCount()` - 獲取議題數量

### UserProject 表
- ✅ `addUserToProject()` - 添加用戶到專案
- ✅ `getProjectMembers()` - 獲取專案成員
- ✅ `getProjectMemberIds()` - 獲取成員ID列表
- ✅ `getProjectMemberNames()` - 獲取成員名稱列表
- ✅ `removeUserFromProject()` - 移除用戶
- ✅ `isUserProjectMember()` - 檢查成員資格
- ✅ `getProjectMemberCount()` - 獲取成員數量

### UserIssue 表
- ✅ `addUserToIssue()` - 添加用戶到議題
- ✅ `removeUserFromIssue()` - 移除用戶

### Friends 表
- ✅ `addFriend()` - 添加好友（雙向）
- ✅ `getFriends()` - 獲取好友列表
- ✅ `removeFriend()` - 刪除好友（雙向）

---

## 相關文檔

1. **[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)**
   - 完整的資料庫設置腳本
   - 包含所有表、索引、觸發器、RLS、視圖、函數

2. **[SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md)**
   - 詳細的 API 文檔
   - 每個方法的說明、參數、返回值、使用範例

3. **[DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md)**
   - 從 SQLite 遷移到 Supabase 的完整指南
   - 代碼遷移範例

4. **[SUPABASE_SETUP.md](SUPABASE_SETUP.md)**
   - Supabase 設置指南
   - Gmail OAuth 配置

---

## 快速參考

### 常用操作

```java
// 初始化
SupabaseDatabaseHelper db = new SupabaseDatabaseHelper(context);

// 獲取用戶的專案
List<JsonObject> projects = db.getProjectsByUser(userId);

// 創建專案並添加成員
Integer projectId = db.insertProject("專案名稱", "摘要");
if (projectId != null) {
    db.addUserToProject(userId, projectId);
}

// 創建議題
Integer issueId = db.insertIssue("議題名稱", "摘要", "2025-01-01", 
    "2025-01-15", "進行中", "john_doe", projectId);

// 獲取專案成員
List<String> members = db.getProjectMemberNames(projectId);
```

---

## 資料流程圖

```
Users
  ├── UserProject ──→ Projects
  │                      ├── Issues
  │                      │     └── UserIssue ──→ Users
  │                      └── UserProject ──→ Users
  └── Friends ──→ Users
```

---

## 版本資訊

- **資料庫版本**：Supabase (PostgreSQL)
- **API 版本**：PostgREST
- **最後更新**：2025/12/16

