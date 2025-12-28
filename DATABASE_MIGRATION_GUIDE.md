# 資料庫遷移指南

本指南說明如何將應用程式從 SQLite 遷移到 Supabase。

## 目錄

1. [遷移概述](#遷移概述)
2. [遷移步驟](#遷移步驟)
3. [代碼遷移](#代碼遷移)
4. [資料遷移](#資料遷移)
5. [測試檢查清單](#測試檢查清單)

---

## 遷移概述

### 遷移前狀態
- **資料庫**：SQLite（本地）
- **認證**：Firebase Authentication
- **資料操作**：使用 `SqlDataBaseHelper` 和 `SQLiteDatabase`

### 遷移後狀態
- **資料庫**：Supabase（雲端 PostgreSQL）
- **認證**：Supabase Auth（支援 Gmail OAuth）
- **資料操作**：使用 `SupabaseDatabaseHelper` 和 REST API

---

## 遷移步驟

### 步驟 1：設置 Supabase

1. **創建 Supabase 專案**
   - 前往 [Supabase](https://supabase.com) 註冊並創建專案
   - 記下 Project URL 和 Anon Key

2. **設置資料庫結構**
   - 在 Supabase SQL Editor 中執行 `SUPABASE_DATABASE_SCHEMA.sql`
   - 驗證所有表、索引、視圖、函數都已創建

3. **配置認證**
   - 設置 Gmail OAuth Provider（見 [SUPABASE_SETUP.md](SUPABASE_SETUP.md)）

4. **在應用程式中設置 Supabase**
   ```java
   SupabaseConfig.getInstance(context).setConfig(
       "https://your-project.supabase.co",
       "your-anon-key",
       "your-service-key" // 可選
   );
   ```

### 步驟 2：更新依賴

依賴已經在 `build.gradle` 中添加：
- OkHttp
- Gson
- Java-WebSocket

### 步驟 3：代碼遷移

參考下面的「代碼遷移」章節，逐步更新所有使用 SQLite 的地方。

---

## 代碼遷移

### 1. 替換資料庫 Helper

**舊代碼**：
```java
SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
```

**新代碼**：
```java
SupabaseDatabaseHelper dbHelper = new SupabaseDatabaseHelper(context);
```

### 2. 用戶操作遷移

#### 獲取用戶 ID

**舊代碼**：
```java
Cursor cursor = db.rawQuery("SELECT id FROM Users WHERE email = ?", new String[]{email});
if (cursor.moveToFirst()) {
    int userId = cursor.getInt(0);
}
cursor.close();
```

**新代碼**：
```java
JsonObject user = dbHelper.getUserByEmail(email);
if (user != null) {
    int userId = user.get("id").getAsInt();
}
```

或使用：
```java
Integer userId = dbHelper.getUserIdByEmail(email);
```

#### 插入用戶

**舊代碼**：
```java
ContentValues values = new ContentValues();
values.put("account", account);
values.put("email", email);
long userId = db.insert("Users", null, values);
```

**新代碼**：
```java
boolean success = dbHelper.insertUser(account, email, firebaseUid);
```

### 3. 專案操作遷移

#### 獲取用戶的專案列表

**舊代碼**：
```java
List<Project> projects = ProjectHelper.getProjectsByUser(db, userId);
```

**新代碼**：
```java
List<JsonObject> projectJsonList = dbHelper.getProjectsByUser(userId);
// 轉換為 Project 對象
List<Project> projects = new ArrayList<>();
for (JsonObject json : projectJsonList) {
    int id = json.get("id").getAsInt();
    String name = json.get("name").getAsString();
    String summary = json.get("summary").getAsString();
    
    // 獲取成員資訊
    List<Integer> memberIds = dbHelper.getProjectMemberIds(id);
    List<String> memberNames = dbHelper.getProjectMemberNames(id);
    
    projects.add(new Project(id, name, summary, memberIds, memberNames));
}
```

#### 創建專案

**舊代碼**：
```java
ContentValues projectValues = new ContentValues();
projectValues.put("name", name);
projectValues.put("summary", summary);
long projectId = db.insert("Projects", null, projectValues);

// 添加成員
ContentValues userProjectValues = new ContentValues();
userProjectValues.put("user_id", userId);
userProjectValues.put("project_id", projectId);
db.insert("UserProject", null, userProjectValues);
```

**新代碼**：
```java
Integer projectId = dbHelper.insertProject(name, summary);
if (projectId != null) {
    dbHelper.addUserToProject(userId, projectId);
}
```

### 4. 議題操作遷移

#### 獲取專案的議題列表

**舊代碼**：
```java
Cursor cursor = db.rawQuery("SELECT * FROM Issues WHERE project_id = ?", 
    new String[]{String.valueOf(projectId)});
List<Issue> issues = new ArrayList<>();
while (cursor.moveToNext()) {
    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
    // ... 其他欄位
    issues.add(new Issue(name, summary, startTime, endTime, status, designee));
}
cursor.close();
```

**新代碼**：
```java
List<JsonObject> issueJsonList = dbHelper.getIssuesByProject(projectId);
List<Issue> issues = new ArrayList<>();
for (JsonObject json : issueJsonList) {
    String name = json.get("name").getAsString();
    String summary = json.get("summary").getAsString();
    String startTime = json.get("start_time").getAsString();
    String endTime = json.get("end_time").getAsString();
    String status = json.get("status").getAsString();
    String designee = json.get("designee").getAsString();
    issues.add(new Issue(name, summary, startTime, endTime, status, designee));
}
```

#### 創建議題

**舊代碼**：
```java
ContentValues values = new ContentValues();
values.put("name", name);
values.put("summary", summary);
values.put("start_time", startTime);
values.put("end_time", endTime);
values.put("status", status);
values.put("designee", designee);
values.put("project_id", projectId);
long issueId = db.insert("Issues", null, values);
```

**新代碼**：
```java
Integer issueId = dbHelper.insertIssue(name, summary, startTime, endTime, 
    status, designee, projectId);
```

#### 更新議題

**舊代碼**：
```java
ContentValues values = new ContentValues();
values.put("name", name);
values.put("status", status);
db.update("Issues", values, "id=?", new String[]{String.valueOf(issueId)});
```

**新代碼**：
```java
JsonObject updates = new JsonObject();
updates.addProperty("name", name);
updates.addProperty("status", status);
dbHelper.updateIssue(issueId, updates);
```

#### 刪除議題

**舊代碼**：
```java
db.delete("Issues", "id=?", new String[]{String.valueOf(issueId)});
```

**新代碼**：
```java
dbHelper.deleteIssue(issueId);
```

### 5. 好友操作遷移

#### 獲取好友列表

**舊代碼**：
```java
String query = "SELECT u.id, u.account, u.email FROM Users u " +
    "INNER JOIN Friends f ON u.id = f.friend_id " +
    "WHERE f.user_id = ?";
Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
List<User> friends = new ArrayList<>();
while (cursor.moveToNext()) {
    int id = cursor.getInt(0);
    String account = cursor.getString(1);
    String email = cursor.getString(2);
    friends.add(new User(id, account, email));
}
cursor.close();
```

**新代碼**：
```java
List<JsonObject> friendJsonList = dbHelper.getFriends(userId);
List<User> friends = new ArrayList<>();
for (JsonObject json : friendJsonList) {
    int id = json.get("id").getAsInt();
    String account = json.get("account").getAsString();
    String email = json.get("email").getAsString();
    friends.add(new User(id, account, email));
}
```

#### 添加好友

**舊代碼**：
```java
ContentValues values1 = new ContentValues();
values1.put("user_id", userId);
values1.put("friend_id", friendId);
db.insert("Friends", null, values1);

ContentValues values2 = new ContentValues();
values2.put("user_id", friendId);
values2.put("friend_id", userId);
db.insert("Friends", null, values2);
```

**新代碼**：
```java
dbHelper.addFriend(userId, friendId);
// 注意：此方法會自動建立雙向關係
```

### 6. 輔助方法遷移

#### 獲取專案成員名稱

**舊代碼**：
```java
List<String> memberNames = ProjectHelper.getProjectMemberNames(db, projectId);
```

**新代碼**：
```java
List<String> memberNames = dbHelper.getProjectMemberNames(projectId);
```

#### 檢查用戶是否為專案成員

**舊代碼**：
```java
boolean isMember = ProjectHelper.isUserProjectMember(db, userId, projectId);
```

**新代碼**：
```java
boolean isMember = dbHelper.isUserProjectMember(userId, projectId);
```

---

## 資料遷移

如果需要將現有 SQLite 資料遷移到 Supabase：

### 方法一：手動遷移（小量資料）

1. 從 SQLite 導出資料
2. 轉換為 JSON 格式
3. 使用 Supabase API 批量插入

### 方法二：編寫遷移腳本

創建一個臨時的遷移 Activity 或工具類：

```java
public class DataMigrationHelper {
    public static void migrateData(Context context) {
        // 1. 讀取 SQLite 資料
        SqlDataBaseHelper sqlHelper = new SqlDataBaseHelper(context);
        SQLiteDatabase sqlDb = sqlHelper.getReadableDatabase();
        
        // 2. 寫入 Supabase
        SupabaseDatabaseHelper supabaseHelper = new SupabaseDatabaseHelper(context);
        
        // 遷移用戶
        Cursor users = sqlDb.rawQuery("SELECT * FROM Users", null);
        while (users.moveToNext()) {
            String account = users.getString(users.getColumnIndexOrThrow("account"));
            String email = users.getString(users.getColumnIndexOrThrow("email"));
            String firebaseUid = users.getString(users.getColumnIndexOrThrow("firebase_uid"));
            supabaseHelper.insertUser(account, email, firebaseUid);
        }
        users.close();
        
        // 遷移專案、議題等...
        
        sqlDb.close();
    }
}
```

---

## 測試檢查清單

遷移完成後，請測試以下功能：

### 用戶功能
- [ ] 註冊新用戶
- [ ] 登入（Gmail OAuth 和電子郵件/密碼）
- [ ] 獲取用戶資訊
- [ ] 更新用戶資訊

### 專案功能
- [ ] 創建專案
- [ ] 查看專案列表
- [ ] 查看專案詳情
- [ ] 添加專案成員
- [ ] 更新專案資訊
- [ ] 刪除專案

### 議題功能
- [ ] 創建議題
- [ ] 查看議題列表
- [ ] 查看議題詳情
- [ ] 更新議題
- [ ] 刪除議題
- [ ] 指派議題給成員

### 好友功能
- [ ] 查看好友列表
- [ ] 添加好友
- [ ] 刪除好友

### 其他功能
- [ ] GitHub 專案匯入
- [ ] Excel 匯出
- [ ] 專案計數顯示

---

## 常見問題

### Q: 遷移後舊資料會保留嗎？
A: SQLite 資料會保留在本地，但應用程式會使用 Supabase。如果需要，可以編寫遷移腳本將資料遷移。

### Q: 可以同時使用 SQLite 和 Supabase 嗎？
A: 不建議。應該完全遷移到 Supabase 以保持資料一致性。

### Q: 遷移後如何處理錯誤？
A: 所有 `SupabaseDatabaseHelper` 方法都有錯誤處理，會返回安全的預設值。建議在 UI 層面顯示適當的錯誤訊息。

### Q: 網路斷線時怎麼辦？
A: Supabase 需要網路連線。可以考慮：
1. 添加離線快取機制
2. 顯示網路錯誤提示
3. 使用 Supabase Realtime 進行即時同步

---

## 相關文檔

- [Supabase 設置指南](SUPABASE_SETUP.md)
- [Supabase 資料庫結構](SUPABASE_DATABASE_SCHEMA.sql)
- [Supabase API 文檔](SUPABASE_DATABASE_API.md)

