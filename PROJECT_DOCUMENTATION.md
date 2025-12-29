# 專案完整說明文件

## 目錄

1. [專案概述](#專案概述)
2. [技術架構](#技術架構)
3. [資料庫設定詳細說明](#資料庫設定詳細說明)
4. [專案結構](#專案結構)
5. [安裝與配置](#安裝與配置)
6. [主要功能說明](#主要功能說明)
7. [API 使用說明](#api-使用說明)
8. [開發指南](#開發指南)

---

## 專案概述

### 專案名稱
114-1 逢甲大學網路程式設計課程期末專案

### 專案簡介
這是一個基於 Android 平台的專案管理應用程式，提供完整的專案協作、議題追蹤、甘特圖視覺化、即時聊天等功能。應用程式使用 Supabase 作為後端服務，提供身份驗證、雲端資料同步和即時通訊功能。

### 主要功能
- ✅ **用戶管理**：註冊、登入（支援 Email 和 Gmail OAuth）、好友管理、帳號刪除
- ✅ **專案管理**：建立專案、專案成員管理、專案列表、專案刪除
- ✅ **議題管理**：建立議題、編輯議題、刪除議題、議題列表
- ✅ **甘特圖視覺化**：時間軸視圖、月份顯示、狀態標示
- ✅ **資料匯出**：Excel 匯出功能，支援多語言
- ✅ **即時聊天**：專案聊天室、私訊功能、群組聊天

---

## 技術架構

### 開發環境
- **IDE**：Android Studio
- **語言**：Java 11 / Kotlin
- **平台**：Android (minSdk 26, targetSdk 35, compileSdk 35)
- **建置工具**：Gradle 8.8.2

### 核心框架與庫

#### Android 官方庫
- `androidx.appcompat:appcompat:1.7.0` - 相容性支援庫
- `com.google.android.material:material:1.12.0` - Material Design 元件
- `androidx.activity:activity:1.10.1` - Activity 支援
- `androidx.constraintlayout:constraintlayout:2.2.1` - 約束佈局
- `androidx.recyclerview` - RecyclerView（用於列表顯示）

#### Supabase 服務
- `io.github.jan-tennert.supabase:bom:2.0.0` - Supabase BOM（統一版本管理）
- `io.github.jan-tennert.supabase:postgrest-kt` - Supabase PostgREST（資料庫操作）
- `io.github.jan-tennert.supabase:gotrue-kt` - Supabase 身份驗證
- `io.github.jan-tennert.supabase:storage-kt` - Supabase 雲端儲存
- `io.github.jan-tennert.supabase:realtime-kt` - Supabase Realtime（即時同步）

#### 第三方庫
- `org.apache.poi:poi:5.4.0` - Apache POI（Excel 檔案處理）
- `org.apache.poi:poi-ooxml:5.4.0` - Apache POI OOXML（Excel 2007+ 格式支援）
- `io.ktor:ktor-client-android:2.3.10` - Ktor HTTP 客戶端
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3` - KotlinX 序列化
- `com.squareup.okhttp3:okhttp` - HTTP 客戶端（用於 REST API）
- `com.google.code.gson:gson` - JSON 處理
- `org.java-websocket:Java-WebSocket` - WebSocket 客戶端

#### 測試框架
- `junit:junit:4.13.2` - JUnit 單元測試
- `androidx.test.ext:junit:1.2.1` - Android JUnit 擴展
- `androidx.test.espresso:espresso-core:3.6.1` - Espresso UI 測試

---

## 資料庫設定詳細說明

### 資料庫架構概述

本專案使用 **Supabase (PostgreSQL)** 作為主要資料庫，提供以下功能：
- 雲端資料存儲
- 身份驗證服務
- 即時資料同步（Realtime）
- Row Level Security (RLS) 資料安全

### 資料庫設置步驟

#### 步驟 1：創建 Supabase 專案

1. **註冊 Supabase 帳號**
   - 前往 [Supabase](https://supabase.com) 註冊帳號
   - 使用 GitHub 帳號或 Email 註冊

2. **創建新專案**
   - 點擊「New Project」
   - 輸入專案名稱
   - 選擇資料庫密碼（請妥善保存）
   - 選擇區域（建議選擇離您最近的區域）
   - 點擊「Create new project」

3. **獲取專案憑證**
   - 專案創建完成後，前往 **Settings > API**
   - 記下以下資訊：
     - **Project URL**：`https://xxxxx.supabase.co`
     - **anon public key**：用於客戶端應用程式
     - **service_role key**：僅用於服務端操作（請保密）

#### 步驟 2：設置資料庫表結構

##### 方法一：使用完整設置腳本（推薦）

1. **打開 SQL Editor**
   - 在 Supabase Dashboard 中，點擊左側選單的 **SQL Editor**
   - 點擊 **New Query**

2. **執行設置腳本**
   - 打開專案根目錄的 `SUPABASE_DATABASE_SCHEMA.sql` 檔案
   - 複製整個檔案內容
   - 貼上到 SQL Editor
   - 點擊 **Run** 執行

3. **驗證設置結果**
   ```sql
   -- 檢查所有表是否創建成功
   SELECT table_name 
   FROM information_schema.tables 
   WHERE table_schema = 'public' 
   AND table_name IN ('Users', 'Projects', 'Issues', 'UserProject', 'UserIssue', 'Friends');
   ```

##### 方法二：逐步設置（進階）

如果您想了解每個步驟，可以參考 `SUPABASE_DATABASE_SCHEMA.sql` 檔案中的各個部分逐步執行。

### 資料庫表結構詳解

#### 1. Users（用戶表）

**用途**：儲存應用程式用戶的基本資訊

**表結構**：
```sql
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    account TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    firebase_uid TEXT UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

**欄位說明**：
- `id`：用戶唯一識別碼（自動遞增）
- `account`：用戶帳號（必填，唯一）
- `email`：用戶電子郵件（必填，唯一）
- `firebase_uid`：Firebase 用戶識別碼（可選，用於遷移）
- `created_at`：創建時間（自動設置）
- `updated_at`：更新時間（自動更新）

**索引**：
- `idx_users_email` - 電子郵件索引（加速查詢）
- `idx_users_firebase_uid` - Firebase UID 索引
- `idx_users_account` - 帳號索引

**觸發器**：
- `update_users_updated_at` - 自動更新 `updated_at` 欄位

#### 2. Projects（專案表）

**用途**：儲存專案的基本資訊

**表結構**：
```sql
CREATE TABLE Projects (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    summary TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

**欄位說明**：
- `id`：專案唯一識別碼（自動遞增）
- `name`：專案名稱（必填）
- `summary`：專案摘要（必填）
- `created_at`：創建時間（自動設置）
- `updated_at`：更新時間（自動更新）

**索引**：
- `idx_projects_name` - 專案名稱索引

**觸發器**：
- `update_projects_updated_at` - 自動更新 `updated_at` 欄位

#### 3. Issues（議題表）

**用途**：儲存專案中的議題資訊

**表結構**：
```sql
CREATE TABLE Issues (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    summary TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('未開始', '進行中', '已完成', 'TO-DO', 'In progress', 'Finished')),
    designee TEXT NOT NULL,
    project_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE
);
```

**欄位說明**：
- `id`：議題唯一識別碼（自動遞增）
- `name`：議題名稱（必填）
- `summary`：議題摘要（必填）
- `start_time`：開始時間（格式：YYYY-MM-DD）
- `end_time`：結束時間（格式：YYYY-MM-DD）
- `status`：議題狀態（必填，限制為特定值）
- `designee`：被指派者帳號（必填）
- `project_id`：所屬專案ID（必填，外鍵）
- `created_at`：創建時間（自動設置）
- `updated_at`：更新時間（自動更新）

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
- `project_id` → `Projects(id)` ON DELETE CASCADE（刪除專案時自動刪除相關議題）

**觸發器**：
- `update_issues_updated_at` - 自動更新 `updated_at` 欄位

#### 4. UserProject（用戶專案關聯表）

**用途**：建立用戶與專案的多對多關係

**表結構**：
```sql
CREATE TABLE UserProject (
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE
);
```

**欄位說明**：
- `user_id`：用戶ID（複合主鍵的一部分）
- `project_id`：專案ID（複合主鍵的一部分）
- `created_at`：關聯創建時間（自動設置）

**複合主鍵**：`(user_id, project_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `project_id` → `Projects(id)` ON DELETE CASCADE

**索引**：
- `idx_userproject_user_id` - 用戶ID索引
- `idx_userproject_project_id` - 專案ID索引

#### 5. UserIssue（用戶議題關聯表）

**用途**：建立用戶與議題的多對多關係

**表結構**：
```sql
CREATE TABLE UserIssue (
    user_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, issue_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (issue_id) REFERENCES Issues(id) ON DELETE CASCADE
);
```

**欄位說明**：
- `user_id`：用戶ID（複合主鍵的一部分）
- `issue_id`：議題ID（複合主鍵的一部分）
- `created_at`：關聯創建時間（自動設置）

**複合主鍵**：`(user_id, issue_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `issue_id` → `Issues(id)` ON DELETE CASCADE

**索引**：
- `idx_userissue_user_id` - 用戶ID索引
- `idx_userissue_issue_id` - 議題ID索引

#### 6. Friends（好友關係表）

**用途**：儲存用戶之間的好友關係（雙向）

**表結構**：
```sql
CREATE TABLE Friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES Users(id) ON DELETE CASCADE,
    CHECK (user_id != friend_id) -- 防止用戶與自己成為好友
);
```

**欄位說明**：
- `user_id`：用戶ID（複合主鍵的一部分）
- `friend_id`：好友ID（複合主鍵的一部分）
- `created_at`：好友關係創建時間（自動設置）

**複合主鍵**：`(user_id, friend_id)`

**外鍵約束**：
- `user_id` → `Users(id)` ON DELETE CASCADE
- `friend_id` → `Users(id)` ON DELETE CASCADE

**檢查約束**：`user_id != friend_id`（防止用戶與自己成為好友）

**索引**：
- `idx_friends_user_id` - 用戶ID索引
- `idx_friends_friend_id` - 好友ID索引

### 資料庫視圖

Supabase 提供了以下視圖方便查詢：

#### 1. project_details
專案詳情視圖，包含：
- 專案基本資訊
- 成員數量
- 議題數量

#### 2. user_projects_view
用戶專案列表視圖，包含：
- 用戶資訊
- 專案資訊
- 加入時間

#### 3. issue_details
議題詳情視圖，包含：
- 議題基本資訊
- 專案資訊

### 資料庫函數

Supabase 提供了以下函數：

#### 1. get_user_project_count(user_id)
獲取用戶參與的專案數量

#### 2. get_project_member_count(project_id)
獲取專案成員數量

#### 3. get_project_issue_count(project_id)
獲取專案議題數量

#### 4. is_user_project_member(user_id, project_id)
檢查用戶是否為專案成員

### Row Level Security (RLS) 設定

所有表都啟用了 RLS，確保資料安全。當前策略允許：
- ✅ 所有認證用戶讀取資料
- ✅ 所有認證用戶插入資料
- ✅ 所有認證用戶更新資料
- ✅ 所有認證用戶刪除資料

**注意**：生產環境可能需要更嚴格的策略，例如：
- 用戶只能讀取自己參與的專案
- 用戶只能更新自己創建的專案
- 用戶只能刪除自己創建的專案

### 在應用程式中配置 Supabase

#### 方法一：使用 SupabaseConfig（Java）

在應用程式首次運行時，需要設置 Supabase 配置：

```java
SupabaseConfig.getInstance(context).setConfig(
    "https://your-project.supabase.co",
    "your-anon-key",
    "your-service-key" // 可選，僅用於服務端操作
);
```

配置會自動保存到 SharedPreferences，下次啟動時會自動載入。

#### 方法二：使用 SupabaseManager（Kotlin）

在 `SupabaseManager.kt` 中直接設置：

```kotlin
private const val SUPABASE_URL = "https://your-project.supabase.co"
private const val SUPABASE_KEY = "your-anon-key"
```

**重要**：請將 `YOUR_SUPABASE_URL` 和 `YOUR_SUPABASE_KEY` 替換為實際值。

### 資料庫連接測試

設置完成後，可以使用以下方法測試連接：

1. **測試身份驗證**
   ```java
   SupabaseAuthHelper authHelper = new SupabaseAuthHelper(context);
   boolean success = authHelper.signInWithEmail("test@example.com", "password");
   ```

2. **測試資料庫查詢**
   ```java
   SupabaseDatabaseHelper dbHelper = new SupabaseDatabaseHelper(context);
   List<JsonObject> users = dbHelper.getAllUsers();
   ```

3. **在 Supabase Dashboard 中檢查**
   - 前往 **Table Editor** 查看資料表
   - 前往 **Authentication > Users** 查看註冊的用戶
   - 前往 **Logs** 查看 API 請求記錄

### 資料庫遷移（從 SQLite）

如果您有現有的 SQLite 資料需要遷移，請參考 `DATABASE_MIGRATION_GUIDE.md` 文件。

### 資料庫備份與還原

#### 備份
1. 在 Supabase Dashboard 中，前往 **Database > Backups**
2. 點擊 **Create Backup**
3. 選擇備份類型（完整備份或增量備份）

#### 還原
1. 在 Supabase Dashboard 中，前往 **Database > Backups**
2. 選擇要還原的備份
3. 點擊 **Restore**

### 資料庫效能優化

#### 索引優化
所有常用查詢欄位都已建立索引，包括：
- 用戶查詢：email、account、firebase_uid
- 專案查詢：name
- 議題查詢：project_id、status、designee、start_time、end_time
- 關聯查詢：user_id、project_id、issue_id、friend_id

#### 查詢優化建議
1. 使用視圖進行複雜查詢
2. 使用函數進行統計查詢
3. 避免在迴圈中執行資料庫操作
4. 使用批量操作代替多次單一操作

---

## 專案結構

```
app/
├── src/
│   ├── main/
│   │   ├── java/fcu/app/appclassfinalproject/
│   │   │   ├── adapter/              # RecyclerView 適配器
│   │   │   │   ├── AddfriendAdapter.java
│   │   │   │   ├── FriendAdapter.java
│   │   │   │   ├── IssueAdapter.java
│   │   │   │   ├── IssueMonthAdapter.java
│   │   │   │   ├── IssueNameAdapter.java
│   │   │   │   ├── ProjectAdapter.java
│   │   │   │   ├── ChatAdapter.java
│   │   │   │   ├── ChatRoomAdapter.java
│   │   │   │   └── MessageAdapter.java
│   │   │   ├── helper/               # 輔助類別
│   │   │   │   ├── SupabaseConfig.java      # Supabase 配置管理
│   │   │   │   ├── SupabaseAuthHelper.java  # Supabase 身份驗證
│   │   │   │   ├── SupabaseDatabaseHelper.java  # Supabase 資料庫操作
│   │   │   │   └── SupabaseProjectHelper.java
│   │   │   ├── supabase/             # Supabase 相關（Kotlin）
│   │   │   │   ├── SupabaseManager.kt      # Supabase 客戶端管理
│   │   │   │   ├── AuthHelper.kt           # 身份驗證輔助
│   │   │   │   └── ChatHelper.kt           # 聊天室輔助
│   │   │   ├── main_fragments/       # 主要 Fragment
│   │   │   │   ├── AddFragment.java         # 新增專案
│   │   │   │   ├── AddFriendFragment.java   # 新增好友
│   │   │   │   ├── AddIssueFragment.java    # 新增議題
│   │   │   │   ├── FriendFragment.java      # 好友列表
│   │   │   │   ├── HomeFragment.java        # 首頁
│   │   │   │   ├── ProjectInfoFragment.java # 專案資訊
│   │   │   │   └── SettingsFragment.java    # 設定頁面
│   │   │   ├── model/                # 資料模型
│   │   │   │   ├── Issue.java
│   │   │   │   ├── IssueMonth.java
│   │   │   │   ├── IssueName.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── User.java
│   │   │   │   ├── ChatRoom.java
│   │   │   │   └── Message.java
│   │   │   ├── chat/                 # 聊天功能
│   │   │   │   ├── ChatClient.java
│   │   │   │   └── ChatMessage.java
│   │   │   ├── view/                 # 自訂視圖
│   │   │   │   └── WhiteboardView.java
│   │   │   ├── CreateIssueActivity.java    # 建立議題頁面
│   │   │   ├── EditIssueActivity.java      # 編輯議題頁面
│   │   │   ├── ExportExcel.java            # Excel 匯出功能
│   │   │   ├── GanttActivity.java          # 甘特圖頁面
│   │   │   ├── HomeActivity.java           # 主頁面
│   │   │   ├── LoginActivity.java          # 登入頁面
│   │   │   ├── ProjectActivity.java        # 專案頁面
│   │   │   ├── RegisterActivity.java       # 註冊頁面
│   │   │   ├── ChatActivity.java           # 聊天頁面
│   │   │   ├── ChatRoomListActivity.java   # 聊天室列表
│   │   │   ├── CreateGroupActivity.java    # 建立群組
│   │   │   └── WhiteboardActivity.java     # 白板頁面
│   │   ├── res/                      # 資源檔案
│   │   │   ├── layout/               # 佈局檔案
│   │   │   ├── drawable/             # 圖形資源
│   │   │   ├── values/               # 字串、顏色等資源
│   │   │   └── menu/                 # 選單資源
│   │   └── AndroidManifest.xml       # Android 清單檔案
│   └── test/                         # 測試檔案
└── build.gradle                      # 應用程式建置配置
```

---

## 安裝與配置

### 前置需求

1. **Android Studio**
   - 建議使用最新版本（支援 Gradle 8.8.2）
   - 下載：[Android Studio](https://developer.android.com/studio)

2. **JDK**
   - Java 11 或以上版本
   - Android Studio 通常已包含

3. **Android SDK**
   - compileSdk 35
   - minSdk 26
   - targetSdk 35
   - 在 Android Studio 中通過 SDK Manager 安裝

4. **Supabase 專案**
   - 需要有效的 Supabase 專案配置
   - 參考 [資料庫設定詳細說明](#資料庫設定詳細說明)

### 安裝步驟

#### 1. 克隆專案

```bash
git clone <repository-url>
cd 114-1_FCU_Network-Programming-Final-Project
```

#### 2. 配置 Supabase

1. **創建 Supabase 專案**
   - 參考 [資料庫設定詳細說明 - 步驟 1](#步驟-1創建-supabase-專案)

2. **設置資料庫結構**
   - 參考 [資料庫設定詳細說明 - 步驟 2](#步驟-2設置資料庫表結構)

3. **配置應用程式**
   
   **方法一：在 SupabaseConfig.java 中設置**
   ```java
   private static final String SUPABASE_URL = "https://your-project.supabase.co";
   private static final String SUPABASE_ANON_KEY = "your-anon-key";
   ```
   
   **方法二：在 SupabaseManager.kt 中設置**
   ```kotlin
   private const val SUPABASE_URL = "https://your-project.supabase.co"
   private const val SUPABASE_KEY = "your-anon-key"
   ```
   
   **方法三：在應用程式中動態設置**
   ```java
   SupabaseConfig.getInstance(context).setConfig(
       "https://your-project.supabase.co",
       "your-anon-key",
       "your-service-key" // 可選
   );
   ```

#### 3. 配置 Gmail OAuth（可選）

1. **在 Google Cloud Console 創建 OAuth 憑證**
   - 前往 [Google Cloud Console](https://console.cloud.google.com)
   - 創建新專案或選擇現有專案
   - 前往 **API 和服務 > 憑證**
   - 點擊 **建立憑證 > OAuth 用戶端 ID**
   - 選擇應用程式類型：**網頁應用程式**
   - 添加授權的重定向 URI：`https://YOUR_PROJECT_REF.supabase.co/auth/v1/callback`
   - 記下 Client ID 和 Client Secret

2. **在 Supabase 中配置 Google Provider**
   - 在 Supabase Dashboard 中，前往 **Authentication > Providers**
   - 啟用 **Google** Provider
   - 填入 Client ID 和 Client Secret
   - 保存設置

#### 4. 同步 Gradle

1. 開啟 Android Studio
2. 開啟專案後，Android Studio 會自動同步 Gradle 依賴
3. 如果沒有自動同步，點選 `File > Sync Project with Gradle Files`

#### 5. 建置專案

```bash
# 使用 Gradle Wrapper
./gradlew build

# 或使用 Android Studio 的 Build > Make Project
```

#### 6. 執行應用程式

**方式一：使用 Android Studio**
1. 連接 Android 裝置或啟動模擬器（API Level 26+）
2. 點選 `Run > Run 'app'` 或按 `Shift + F10`

**方式二：使用命令列**
```bash
# 安裝到連接的裝置
./gradlew installDebug

# 或直接執行
./gradlew installDebug && adb shell am start -n fcu.app.appclassfinalproject/.LoginActivity
```

### 執行環境要求

- **Android 版本**：Android 8.0 (API 26) 或以上
- **網路連線**：首次註冊/登入需要網路連線（Supabase Authentication）
- **權限**：
  - `INTERNET` - 網路存取
  - `ACCESS_NETWORK_STATE` - 檢查網路狀態
  - `WRITE_EXTERNAL_STORAGE` - Excel 匯出功能（Android 10+ 可能不需要）

---

## 主要功能說明

### 1. 用戶管理

#### 註冊流程
1. 輸入帳號、電子郵件、密碼（至少 6 字元）
2. 系統驗證輸入格式
3. 透過 Supabase Authentication 建立帳號
4. 自動在 Users 表中創建用戶記錄
5. 自動登入並進入主頁面

#### 登入流程
1. 輸入電子郵件和密碼，或使用 Gmail OAuth 登入
2. Supabase Authentication 驗證
3. 儲存登入狀態到 SharedPreferences
4. 進入主頁面

#### 好友管理
- **添加好友**：透過帳號或電子郵件搜尋並添加
- **好友列表**：顯示所有好友資訊
- **刪除好友**：移除好友關係（自動刪除雙向關係）

#### 帳號刪除
- 刪除 Supabase Authentication 帳號
- 刪除 Users 表中的用戶記錄
- 級聯刪除所有相關資料（專案、議題、好友關係等）

### 2. 專案管理

#### 建立專案
1. 輸入專案名稱和概述
2. 選擇專案成員（可多選）
3. 系統驗證並建立專案
4. 自動建立 UserProject 關聯

#### 專案列表
- 顯示當前用戶參與的所有專案
- 點選專案進入專案詳情頁面

#### 專案刪除
- 僅專案成員可刪除專案
- 刪除專案會同時刪除所有相關議題和成員關聯（級聯刪除）

### 3. 議題管理

#### 建立議題
1. 輸入議題名稱、概述
2. 選擇開始/結束時間
3. 選擇狀態（如：待處理、進行中、已完成等）
4. 指派給專案成員
5. 自動建立 UserIssue 關聯

#### 編輯議題
- 修改議題的所有欄位
- 儲存變更到資料庫

#### 議題列表
- 顯示專案中的所有議題
- 可點選進入編輯或查看詳情

### 4. 甘特圖功能
- 以時間軸方式視覺化專案議題
- 顯示議題的開始和結束月份
- 不同顏色標示議題狀態
- 可返回議題列表

### 5. Excel 匯出
- 匯出用戶參與的所有專案
- 包含專案列表工作表
- 每個專案有獨立的議題工作表
- 支援多語言標題（根據當前語言設定）
- 檔案儲存在 `Documents` 目錄

### 6. 即時聊天
- **專案聊天室**：每個專案都有專屬聊天室
- **私訊功能**：用戶之間可以發送私訊
- **群組聊天**：創建和管理群組聊天室
- **訊息歷史**：保存和載入歷史訊息

### 7. 設定功能
- **語言切換**：中文/英文切換
- **Excel 匯出**：匯出專案資料
- **GitHub 匯入**：匯入 GitHub 資料（功能預留）
- **登出**：登出當前用戶
- **刪除帳號**：完整刪除帳號及所有相關資料

---

## API 使用說明

### Supabase Authentication API

#### 註冊
```java
SupabaseAuthHelper authHelper = new SupabaseAuthHelper(context);
boolean success = authHelper.signUpWithEmail(email, password, account);
```

#### 登入
```java
// Email/密碼登入
boolean success = authHelper.signInWithEmail(email, password);

// Gmail OAuth 登入
authHelper.signInWithGoogle(activity, requestCode);
```

#### 登出
```java
authHelper.signOut();
```

#### 獲取當前用戶
```java
String userId = authHelper.getCurrentUserId();
```

### Supabase Database API

#### 初始化
```java
SupabaseDatabaseHelper dbHelper = new SupabaseDatabaseHelper(context);
```

#### 用戶操作
```java
// 插入用戶
boolean success = dbHelper.insertUser(account, email, firebaseUid);

// 根據 email 查詢
JsonObject user = dbHelper.getUserByEmail(email);

// 根據 ID 查詢
JsonObject user = dbHelper.getUserById(userId);

// 根據帳號查詢 ID
Integer userId = dbHelper.getUserIdByAccount(account);

// 獲取所有用戶
List<JsonObject> users = dbHelper.getAllUsers();
```

#### 專案操作
```java
// 插入專案
Integer projectId = dbHelper.insertProject(name, summary);

// 獲取用戶的專案
List<JsonObject> projects = dbHelper.getProjectsByUser(userId);

// 獲取專案詳情
JsonObject project = dbHelper.getProjectById(projectId);

// 更新專案
JsonObject updates = new JsonObject();
updates.addProperty("name", "新名稱");
dbHelper.updateProject(projectId, updates);

// 刪除專案
dbHelper.deleteProject(projectId);
```

#### 議題操作
```java
// 插入議題
Integer issueId = dbHelper.insertIssue(
    name, summary, startTime, endTime, status, designee, projectId
);

// 獲取專案的議題
List<JsonObject> issues = dbHelper.getIssuesByProject(projectId);

// 獲取議題詳情
JsonObject issue = dbHelper.getIssueById(issueId);

// 更新議題
JsonObject updates = new JsonObject();
updates.addProperty("status", "已完成");
dbHelper.updateIssue(issueId, updates);

// 刪除議題
dbHelper.deleteIssue(issueId);
```

#### 專案成員操作
```java
// 添加用戶到專案
dbHelper.addUserToProject(userId, projectId);

// 獲取專案成員
List<JsonObject> members = dbHelper.getProjectMembers(projectId);

// 獲取成員ID列表
List<Integer> memberIds = dbHelper.getProjectMemberIds(projectId);

// 獲取成員名稱列表
List<String> memberNames = dbHelper.getProjectMemberNames(projectId);

// 移除用戶
dbHelper.removeUserFromProject(userId, projectId);

// 檢查是否為成員
boolean isMember = dbHelper.isUserProjectMember(userId, projectId);

// 獲取成員數量
int count = dbHelper.getProjectMemberCount(projectId);
```

#### 好友操作
```java
// 添加好友（自動建立雙向關係）
dbHelper.addFriend(userId, friendId);

// 獲取好友列表
List<JsonObject> friends = dbHelper.getFriends(userId);

// 刪除好友（自動刪除雙向關係）
dbHelper.removeFriend(userId, friendId);
```

詳細的 API 文檔請參考 `SUPABASE_DATABASE_API.md`。

---

## 開發指南

### 資料存儲機制

- **Supabase PostgreSQL**：所有資料存儲在 Supabase 雲端資料庫
- **即時同步**：使用 Supabase Realtime 進行聊天訊息和白板繪圖的即時同步
- **雲端優先**：所有資料操作直接使用 Supabase API

### SharedPreferences 使用

- **儲存位置**：`FCUPrefs`
- **儲存內容**：
  - `email`：用戶電子郵件
  - `uid`：Supabase 用戶 ID
  - `project_id`：當前選中的專案 ID
  - `language`：當前語言設定（"zh" 或 "en"）
  - `supabase_url`：Supabase URL（可選）
  - `supabase_anon_key`：Supabase Anon Key（可選）

### 權限處理

- **網路權限**：用於 Supabase 服務
- **儲存權限**：Excel 匯出功能（Android 10+ 可能不需要）

### 錯誤處理

所有 `SupabaseDatabaseHelper` 方法都有錯誤處理：
1. 記錄錯誤日誌（使用 `Log.e`）
2. 返回安全的預設值（`null`、`false`、空列表等）
3. 不會拋出異常，確保應用程式穩定性

建議在 UI 層面處理錯誤並顯示適當的訊息。

### 開發注意事項

1. **認證**：所有操作都需要有效的 Supabase 訪問令牌
2. **權限**：確保 RLS 策略允許當前用戶執行操作
3. **網路**：所有操作都需要網路連線
4. **效能**：大量資料操作時考慮使用批量 API 或後台任務
5. **安全性**：生產環境中應該使用環境變數或安全的配置管理方式存儲 API Key

### 已知問題與限制

1. **部分功能待實現**：部分功能（如議題管理、專案管理）的 Supabase 整合待完善
2. **離線支援**：應用程式需要網路連線才能使用，所有資料存儲在 Supabase
3. **RLS 策略**：當前策略較為寬鬆，生產環境可能需要更嚴格的策略

---

## 相關文檔

### 核心文檔
- [README.md](README.md) - 專案總覽
- [DATABASE_OVERVIEW.md](DATABASE_OVERVIEW.md) - 資料庫總覽
- [SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql) - 資料庫設置腳本
- [SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md) - 資料庫 API 文檔
- [DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md) - 資料庫遷移指南
- [DATABASE_DOCUMENTATION_INDEX.md](DATABASE_DOCUMENTATION_INDEX.md) - 資料庫文檔索引

### 設置文檔
- [SUPABASE_SETUP.md](SUPABASE_SETUP.md) - Supabase 設置指南
- [CHAT_SERVER_SETUP.md](CHAT_SERVER_SETUP.md) - 聊天伺服器設置

### 其他文檔
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - 實現總結
- [TODO.md](TODO.md) - 待辦事項

---

## 版本資訊

- **版本號**：1.0
- **版本代碼**：1
- **最後更新**：2025/12/16

---

## 開發團隊

逢甲大學 114-1 網路程式設計課程 期末專案

---

## 授權

本專案為課程作業專案，僅供學習使用。

---

**注意**：使用本應用程式前，請確保已正確配置 Supabase 專案並在應用程式中設定正確的 `SUPABASE_URL` 和 `SUPABASE_KEY`。詳細的資料庫設定步驟請參考 [資料庫設定詳細說明](#資料庫設定詳細說明) 章節。

