# Supabase 資料庫 API 文檔

本文檔說明所有可用的資料庫操作，對應 `SupabaseDatabaseHelper` 類中的方法。

## 目錄

1. [Users 表操作](#users-表操作)
2. [Projects 表操作](#projects-表操作)
3. [Issues 表操作](#issues-表操作)
4. [UserProject 表操作](#userproject-表操作)
5. [UserIssue 表操作](#userissue-表操作)
6. [Friends 表操作](#friends-表操作)
7. [輔助方法](#輔助方法)

---

## Users 表操作

### `insertUser(String account, String email, String firebaseUid)`
**功能**：插入新用戶

**參數**：
- `account`: 用戶帳號（必填，唯一）
- `email`: 用戶電子郵件（必填，唯一）
- `firebaseUid`: Firebase 用戶識別碼（可選，用於遷移）

**返回**：`boolean` - 成功返回 `true`，失敗返回 `false`

**使用範例**：
```java
SupabaseDatabaseHelper dbHelper = new SupabaseDatabaseHelper(context);
boolean success = dbHelper.insertUser("john_doe", "john@example.com", null);
```

---

### `getUserByEmail(String email)`
**功能**：根據電子郵件獲取用戶

**參數**：
- `email`: 用戶電子郵件

**返回**：`JsonObject` - 用戶資料，如果不存在返回 `null`

**使用範例**：
```java
JsonObject user = dbHelper.getUserByEmail("john@example.com");
if (user != null) {
    int userId = user.get("id").getAsInt();
    String account = user.get("account").getAsString();
}
```

---

### `getUserById(int userId)`
**功能**：根據 ID 獲取用戶

**參數**：
- `userId`: 用戶 ID

**返回**：`JsonObject` - 用戶資料，如果不存在返回 `null`

---

### `getUserIdByAccount(String account)`
**功能**：根據帳號獲取用戶 ID

**參數**：
- `account`: 用戶帳號

**返回**：`Integer` - 用戶 ID，如果不存在返回 `null`

---

### `getUserIdByFirebaseUid(String firebaseUid)`
**功能**：根據 Firebase UID 獲取用戶 ID

**參數**：
- `firebaseUid`: Firebase 用戶識別碼

**返回**：`Integer` - 用戶 ID，如果不存在返回 `null`

---

### `getAllUsers()`
**功能**：獲取所有用戶列表

**返回**：`List<JsonObject>` - 用戶列表，按帳號排序

**使用場景**：新增好友時顯示可添加的用戶列表

---

## Projects 表操作

### `insertProject(String name, String summary)`
**功能**：插入新專案

**參數**：
- `name`: 專案名稱
- `summary`: 專案摘要

**返回**：`Integer` - 新創建的專案 ID，失敗返回 `null`

**使用範例**：
```java
Integer projectId = dbHelper.insertProject("新專案", "這是專案說明");
if (projectId != null) {
    // 專案創建成功，可以添加成員
    dbHelper.addUserToProject(userId, projectId);
}
```

---

### `getProjectsByUser(int userId)`
**功能**：獲取用戶參與的所有專案

**參數**：
- `userId`: 用戶 ID

**返回**：`List<JsonObject>` - 專案列表

**使用場景**：在 HomeFragment 中顯示用戶的專案列表

---

### `getProjectById(int projectId)`
**功能**：獲取專案詳情

**參數**：
- `projectId`: 專案 ID

**返回**：`JsonObject` - 專案資料，如果不存在返回 `null`

---

### `updateProject(int projectId, JsonObject updates)`
**功能**：更新專案資訊

**參數**：
- `projectId`: 專案 ID
- `updates`: 要更新的欄位（JsonObject）

**返回**：`boolean` - 成功返回 `true`

**使用範例**：
```java
JsonObject updates = new JsonObject();
updates.addProperty("name", "更新後的專案名稱");
updates.addProperty("summary", "更新後的摘要");
dbHelper.updateProject(projectId, updates);
```

---

### `deleteProject(int projectId)`
**功能**：刪除專案（會級聯刪除相關的 Issues、UserProject、UserIssue）

**參數**：
- `projectId`: 專案 ID

**返回**：`boolean` - 成功返回 `true`

---

## Issues 表操作

### `insertIssue(String name, String summary, String startTime, String endTime, String status, String designee, int projectId)`
**功能**：插入新議題

**參數**：
- `name`: 議題名稱
- `summary`: 議題摘要
- `startTime`: 開始時間（格式：YYYY-MM-DD）
- `endTime`: 結束時間（格式：YYYY-MM-DD）
- `status`: 狀態（"未開始"/"進行中"/"已完成" 或 "TO-DO"/"In progress"/"Finished"）
- `designee`: 被指派者帳號
- `projectId`: 專案 ID

**返回**：`Integer` - 新創建的議題 ID，失敗返回 `null`

**使用範例**：
```java
Integer issueId = dbHelper.insertIssue(
    "修復登入問題",
    "修復用戶無法登入的 bug",
    "2025-01-01",
    "2025-01-15",
    "進行中",
    "john_doe",
    projectId
);

if (issueId != null) {
    // 建立用戶與議題的關聯
    int designeeUserId = dbHelper.getUserIdByAccount("john_doe");
    if (designeeUserId != null) {
        dbHelper.addUserToIssue(designeeUserId, issueId);
    }
}
```

---

### `getIssuesByProject(int projectId)`
**功能**：獲取專案的所有議題

**參數**：
- `projectId`: 專案 ID

**返回**：`List<JsonObject>` - 議題列表，按 ID 排序

**使用場景**：在 ProjectInfoFragment 中顯示專案的議題列表

---

### `getIssueById(int issueId)`
**功能**：獲取議題詳情

**參數**：
- `issueId`: 議題 ID

**返回**：`JsonObject` - 議題資料，如果不存在返回 `null`

---

### `updateIssue(int issueId, JsonObject updates)`
**功能**：更新議題資訊

**參數**：
- `issueId`: 議題 ID
- `updates`: 要更新的欄位（JsonObject）

**返回**：`boolean` - 成功返回 `true`

**使用範例**：
```java
JsonObject updates = new JsonObject();
updates.addProperty("status", "已完成");
updates.addProperty("summary", "更新後的摘要");
dbHelper.updateIssue(issueId, updates);
```

---

### `deleteIssue(int issueId)`
**功能**：刪除議題（會級聯刪除相關的 UserIssue）

**參數**：
- `issueId`: 議題 ID

**返回**：`boolean` - 成功返回 `true`

---

## UserProject 表操作

### `addUserToProject(int userId, int projectId)`
**功能**：添加用戶到專案

**參數**：
- `userId`: 用戶 ID
- `projectId`: 專案 ID

**返回**：`boolean` - 成功返回 `true`

**使用場景**：創建專案時添加成員

---

### `getProjectMembers(int projectId)`
**功能**：獲取專案的所有成員

**參數**：
- `projectId`: 專案 ID

**返回**：`List<JsonObject>` - 成員列表（包含用戶完整資訊）

---

### `removeUserFromProject(int userId, int projectId)`
**功能**：移除用戶從專案

**參數**：
- `userId`: 用戶 ID
- `projectId`: 專案 ID

**返回**：`boolean` - 成功返回 `true`

---

## UserIssue 表操作

### `addUserToIssue(int userId, int issueId)`
**功能**：添加用戶到議題（建立關聯）

**參數**：
- `userId`: 用戶 ID
- `issueId`: 議題 ID

**返回**：`boolean` - 成功返回 `true`

**使用場景**：創建議題時，將被指派者與議題建立關聯

---

### `removeUserFromIssue(int userId, int issueId)`
**功能**：移除用戶從議題

**參數**：
- `userId`: 用戶 ID
- `issueId`: 議題 ID

**返回**：`boolean` - 成功返回 `true`

---

## Friends 表操作

### `addFriend(int userId, int friendId)`
**功能**：添加好友（建立雙向好友關係）

**參數**：
- `userId`: 用戶 ID
- `friendId`: 好友 ID

**返回**：`boolean` - 成功返回 `true`

**注意**：此方法會自動建立雙向好友關係

---

### `getFriends(int userId)`
**功能**：獲取用戶的好友列表

**參數**：
- `userId`: 用戶 ID

**返回**：`List<JsonObject>` - 好友列表（包含好友完整資訊）

**使用場景**：在 FriendFragment 中顯示好友列表

---

### `removeFriend(int userId, int friendId)`
**功能**：刪除好友（刪除雙向好友關係）

**參數**：
- `userId`: 用戶 ID
- `friendId`: 好友 ID

**返回**：`boolean` - 成功返回 `true`

**注意**：此方法會自動刪除雙向好友關係

---

## 輔助方法

### `getProjectMemberIds(int projectId)`
**功能**：獲取專案的所有成員 ID 列表

**參數**：
- `projectId`: 專案 ID

**返回**：`List<Integer>` - 成員 ID 列表

---

### `getProjectMemberNames(int projectId)`
**功能**：獲取專案的所有成員帳號名稱列表

**參數**：
- `projectId`: 專案 ID

**返回**：`List<String>` - 成員帳號列表，按帳號排序

**使用場景**：在 AddIssueFragment 中顯示可指派的成員列表

---

### `isUserProjectMember(int userId, int projectId)`
**功能**：檢查用戶是否為專案成員

**參數**：
- `userId`: 用戶 ID
- `projectId`: 專案 ID

**返回**：`boolean` - 是成員返回 `true`

**使用場景**：驗證用戶是否有權限執行專案相關操作

---

### `getProjectMemberCount(int projectId)`
**功能**：獲取專案成員數量

**參數**：
- `projectId`: 專案 ID

**返回**：`int` - 成員數量

---

### `getProjectIssueCount(int projectId)`
**功能**：獲取專案的議題數量

**參數**：
- `projectId`: 專案 ID

**返回**：`int` - 議題數量

---

## 錯誤處理

所有方法在發生錯誤時會：
1. 記錄錯誤日誌（使用 `Log.e`）
2. 返回安全的預設值（`null`、`false`、空列表等）
3. 不會拋出異常，確保應用程式穩定性

## 使用建議

### 1. 初始化
```java
SupabaseDatabaseHelper dbHelper = new SupabaseDatabaseHelper(context);
```

### 2. 檢查返回值
```java
Integer projectId = dbHelper.insertProject("專案名稱", "摘要");
if (projectId != null) {
    // 成功
} else {
    // 失敗，顯示錯誤訊息
}
```

### 3. 處理空值
```java
JsonObject user = dbHelper.getUserByEmail(email);
if (user != null) {
    // 使用用戶資料
} else {
    // 用戶不存在
}
```

### 4. 批量操作
```java
// 添加多個成員到專案
for (String account : memberAccounts) {
    Integer userId = dbHelper.getUserIdByAccount(account);
    if (userId != null) {
        dbHelper.addUserToProject(userId, projectId);
    }
}
```

---

## 資料庫視圖

Supabase 資料庫還提供了以下視圖，可以直接查詢：

- `project_details`: 專案詳情（包含成員數量和議題數量）
- `user_projects_view`: 用戶專案列表
- `issue_details`: 議題詳情（包含專案資訊）

---

## 資料庫函數

Supabase 資料庫還提供了以下函數，可以在 SQL 查詢中使用：

- `get_user_project_count(user_id)`: 獲取用戶參與的專案數量
- `get_project_member_count(project_id)`: 獲取專案成員數量
- `get_project_issue_count(project_id)`: 獲取專案議題數量
- `is_user_project_member(user_id, project_id)`: 檢查用戶是否為專案成員

---

## 注意事項

1. **認證**：所有操作都需要有效的 Supabase 訪問令牌
2. **權限**：確保 RLS 策略允許當前用戶執行操作
3. **網路**：所有操作都需要網路連線
4. **錯誤處理**：建議在 UI 層面處理錯誤並顯示適當的訊息
5. **效能**：大量資料操作時考慮使用批量 API 或後台任務

