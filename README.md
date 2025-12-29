# 114-1_FCU_Network-Programming-Final-Project

## 專案簡介

這是一個基於 Android 平台的專案管理應用程式，提供完整的專案協作、議題追蹤、甘特圖視覺化、即時聊天等功能。應用程式使用 Supabase 作為後端服務，提供身份驗證、雲端資料同步和即時通訊功能。

**專案名稱**：逢甲大學 114-1 網路程式設計課程期末專案

## 主要功能

### 1. 用戶管理
- **註冊/登入系統**：使用 Supabase Authentication 進行用戶身份驗證（支援 Email 和 Gmail OAuth）
- **多語言支援**：支援中文（繁體）和英文切換
- **好友管理**：添加、刪除好友功能
- **帳號刪除**：支援完整刪除帳號及相關資料

### 2. 專案管理
- **建立專案**：建立新專案並設定專案名稱、概述
- **專案成員管理**：添加/移除專案成員（多對多關係）
- **專案列表**：顯示用戶參與的所有專案
- **專案刪除**：刪除專案及其所有相關議題和成員關聯

### 3. 議題（Issue）管理
- **建立議題**：建立新議題，包含名稱、概述、開始/結束時間、狀態、被指派者
- **編輯議題**：修改現有議題資訊
- **刪除議題**：刪除不需要的議題
- **議題列表**：以列表方式顯示專案中的所有議題

### 4. 甘特圖視覺化
- **時間軸視圖**：以甘特圖形式展示專案議題的時間分佈
- **月份顯示**：顯示議題的開始和結束月份
- **狀態標示**：不同顏色標示議題狀態

### 5. 資料匯出
- **Excel 匯出**：將專案和議題資料匯出為 Excel 檔案
- **多工作表**：包含專案列表和每個專案的議題詳情
- **多語言支援**：根據當前語言設定匯出對應語言的標題

### 6. 即時聊天
- **專案聊天室**：每個專案都有專屬聊天室
- **私訊功能**：用戶之間可以發送私訊
- **群組聊天**：創建和管理群組聊天室
- **訊息歷史**：保存和載入歷史訊息

## 技術棧

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

## 套件依賴詳情

### build.gradle (專案層級)
```gradle
plugins {
    alias(libs.plugins.android.application) apply false
    id 'org.jetbrains.kotlin.android' version '1.9.22' apply false
}
```

### app/build.gradle
```gradle
dependencies {
    // Android 官方庫
    implementation libs.appcompat
    implementation libs.material
    implementation libs.activity
    implementation libs.constraintlayout
    
    // Supabase
    implementation platform('io.github.jan-tennert.supabase:bom:2.0.0')
    implementation 'io.github.jan-tennert.supabase:postgrest-kt'
    implementation 'io.github.jan-tennert.supabase:auth-kt'
    implementation 'io.github.jan-tennert.supabase:storage-kt'
    implementation 'io.github.jan-tennert.supabase:realtime-kt'
    
    // Ktor
    implementation 'io.ktor:ktor-client-android:2.3.10'
    implementation 'io.ktor:ktor-client-core:2.3.10'
    
    // KotlinX Serialization
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3'
    
    // Excel 處理
    implementation libs.poi
    implementation libs.poi.ooxml
    
    // 測試
    testImplementation libs.junit
    androidTestImplementation libs.ext.junit
    androidTestImplementation libs.espresso.core
}
```

## 資料庫結構

### Supabase PostgreSQL 資料庫
應用程式使用 **Supabase (PostgreSQL)** 作為主要資料庫，提供以下功能：
- 雲端資料存儲
- 身份驗證服務
- 即時資料同步（Realtime）
- Row Level Security (RLS) 資料安全

#### 主要資料表結構

**1. Users（用戶表）**
- `id` (SERIAL) - 主鍵，用戶唯一識別碼
- `account` (TEXT) - 用戶帳號（唯一）
- `email` (TEXT) - 電子郵件（唯一）
- `firebase_uid` (TEXT) - Firebase 用戶識別碼（可選，用於遷移）
- `created_at` (TIMESTAMP) - 創建時間
- `updated_at` (TIMESTAMP) - 更新時間

**2. Projects（專案表）**
- `id` (SERIAL) - 主鍵，專案唯一識別碼
- `name` (TEXT) - 專案名稱
- `summary` (TEXT) - 專案摘要
- `created_at` (TIMESTAMP) - 創建時間
- `updated_at` (TIMESTAMP) - 更新時間

**3. Issues（議題表）**
- `id` (SERIAL) - 主鍵，議題唯一識別碼
- `name` (TEXT) - 議題名稱
- `summary` (TEXT) - 議題摘要
- `start_time` (TEXT) - 開始時間（格式：YYYY-MM-DD）
- `end_time` (TEXT) - 結束時間（格式：YYYY-MM-DD）
- `status` (TEXT) - 議題狀態（未開始/進行中/已完成 或 TO-DO/In progress/Finished）
- `designee` (TEXT) - 被指派者帳號
- `project_id` (INTEGER) - 所屬專案ID（外鍵）
- `created_at` (TIMESTAMP) - 創建時間
- `updated_at` (TIMESTAMP) - 更新時間

**4. UserProject（用戶專案關聯表）**
- `user_id` (INTEGER) - 用戶ID（複合主鍵）
- `project_id` (INTEGER) - 專案ID（複合主鍵）
- `created_at` (TIMESTAMP) - 關聯創建時間

**5. UserIssue（用戶議題關聯表）**
- `user_id` (INTEGER) - 用戶ID（複合主鍵）
- `issue_id` (INTEGER) - 議題ID（複合主鍵）
- `created_at` (TIMESTAMP) - 關聯創建時間

**6. Friends（好友關係表）**
- `user_id` (INTEGER) - 用戶ID（複合主鍵）
- `friend_id` (INTEGER) - 好友ID（複合主鍵）
- `created_at` (TIMESTAMP) - 好友關係創建時間

#### 資料庫視圖與函數
- **視圖**：`project_details`、`user_projects_view`、`issue_details`
- **函數**：`get_user_project_count()`、`get_project_member_count()`、`get_project_issue_count()`、`is_user_project_member()`

**詳細的資料庫結構請參考**：[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)

## Supabase API 使用

### Supabase Authentication
- **註冊**：`AuthHelper.signUpWithEmail(email, password, account)`
- **登入**：`AuthHelper.signInWithEmail(email, password)`
- **Gmail 登入**：`AuthHelper.signInWithGoogle()`
- **登出**：`AuthHelper.signOut()`
- **當前用戶**：`AuthHelper.getCurrentUserId()`

### Supabase PostgREST
- **查詢資料**：使用 `SupabaseManager.getClient().from("table_name").select()`
- **插入資料**：使用 `SupabaseManager.getClient().from("table_name").insert()`
- **更新資料**：使用 `SupabaseManager.getClient().from("table_name").update()`
- **刪除資料**：使用 `SupabaseManager.getClient().from("table_name").delete()`

### Supabase Realtime
- **即時同步**：使用 Supabase Realtime 進行聊天訊息和白板繪圖的即時同步

### Supabase 配置
- **配置方式一**：在 `SupabaseManager.kt` 中設定 `SUPABASE_URL` 和 `SUPABASE_KEY`
- **配置方式二**：在 `SupabaseConfig.java` 中設定（推薦，支援動態配置）
- **配置方式三**：在 `SettingsFragment.java` 中初始化配置
- **API Key**：從 Supabase 專案設定中獲取（Settings > API）

**詳細的 Supabase 設置步驟請參考**：[SUPABASE_SETUP.md](SUPABASE_SETUP.md)

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
│   │   │   │   └── ProjectAdapter.java
│   │   │   ├── helper/               # 輔助類別
│   │   │   │   ├── SupabaseConfig.java          # Supabase 配置管理
│   │   │   │   ├── SupabaseAuthHelper.java      # Supabase 身份驗證
│   │   │   │   ├── SupabaseDatabaseHelper.java # Supabase 資料庫操作
│   │   │   │   └── SupabaseProjectHelper.java  # Supabase 專案操作
│   │   │   ├── supabase/             # Supabase 相關（Kotlin）
│   │   │   │   ├── SupabaseManager.kt      # Supabase 客戶端管理
│   │   │   │   ├── AuthHelper.kt           # 身份驗證輔助
│   │   │   │   └── ChatHelper.kt           # 聊天室輔助
│   │   │   ├── chat/                 # 聊天功能
│   │   │   │   ├── ChatClient.java
│   │   │   │   └── ChatMessage.java
│   │   │   ├── view/                 # 自訂視圖
│   │   │   │   └── WhiteboardView.java
│   │   │   ├── main_fragments/       # 主要 Fragment
│   │   │   │   ├── AddFragment.java         # 新增專案
│   │   │   │   ├── AddFriendFragment.java  # 新增好友
│   │   │   │   ├── AddIssueFragment.java   # 新增議題
│   │   │   │   ├── FriendFragment.java     # 好友列表
│   │   │   │   ├── HomeFragment.java       # 首頁
│   │   │   │   ├── ProjectInfoFragment.java # 專案資訊
│   │   │   │   └── SettingsFragment.java   # 設定頁面
│   │   │   ├── model/                # 資料模型
│   │   │   │   ├── Issue.java
│   │   │   │   ├── IssueMonth.java
│   │   │   │   ├── IssueName.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── User.java
│   │   │   │   ├── ChatRoom.java
│   │   │   │   └── Message.java
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
│   │   │   ├── CreateGroupActivity.java   # 建立群組
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

## 啟動方式

### 前置需求
1. **Android Studio**：建議使用最新版本（支援 Gradle 8.8.2）
2. **JDK**：Java 11 或以上版本
3. **Android SDK**：
   - compileSdk 35
   - minSdk 26
   - targetSdk 35
4. **Supabase 專案**：需要有效的 Supabase 專案配置
   - 前往 [Supabase](https://supabase.com) 註冊並創建專案
   - 獲取 Project URL 和 API Key

### 安裝步驟

#### 1. 克隆專案
```bash
git clone <repository-url>
cd 114-1_FCU_Network-Programming-Final-Project
```

#### 2. 配置 Supabase

**步驟 1：創建 Supabase 專案**
1. 前往 [Supabase](https://supabase.com) 註冊帳號
2. 創建新專案，記下 Project URL 和 API Key

**步驟 2：設置資料庫結構**
1. 在 Supabase Dashboard 中，前往 **SQL Editor**
2. 複製並執行 `SUPABASE_DATABASE_SCHEMA.sql` 檔案中的所有 SQL 語句
3. 驗證所有表、索引、視圖、函數都已創建成功

**步驟 3：在應用程式中配置 Supabase**

**方法一：在 SettingsFragment 中配置（已預設）**
- 配置已在 `SettingsFragment.java` 中初始化
- 如需修改，請編輯 `initSupabaseConfig()` 方法

**方法二：在 SupabaseConfig.java 中配置**
```java
private static final String SUPABASE_URL = "https://your-project.supabase.co";
private static final String SUPABASE_ANON_KEY = "your-anon-key";
```

**方法三：動態配置**
```java
SupabaseConfig.getInstance(context).setConfig(
    "https://your-project.supabase.co",
    "your-anon-key",
    null // service_key 可選
);
```

**詳細的 Supabase 設置步驟請參考**：[SUPABASE_SETUP.md](SUPABASE_SETUP.md)

#### 3. 同步 Gradle
- 開啟 Android Studio
- 開啟專案後，Android Studio 會自動同步 Gradle 依賴
- 如果沒有自動同步，點選 `File > Sync Project with Gradle Files`

#### 4. 建置專案
```bash
# 使用 Gradle Wrapper
./gradlew build

# 或使用 Android Studio 的 Build > Make Project
```

#### 5. 執行應用程式

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

## 主要功能說明

### 1. 用戶註冊與登入
- **註冊流程**：
  1. 輸入帳號、電子郵件、密碼（至少 6 字元）
  2. 系統驗證輸入格式
  3. 透過 Supabase Authentication 建立帳號
  4. 自動登入並進入主頁面

- **登入流程**：
  1. 輸入電子郵件和密碼，或使用 Gmail OAuth 登入
  2. Supabase Authentication 驗證
  3. 儲存登入狀態到 SharedPreferences
  4. 進入主頁面

### 2. 專案管理
- **建立專案**：
  - 輸入專案名稱和概述
  - 選擇專案成員（可多選）
  - 系統驗證並建立專案及成員關聯

- **專案列表**：
  - 顯示當前用戶參與的所有專案
  - 點選專案進入專案詳情頁面

- **專案刪除**：
  - 僅專案成員可刪除專案
  - 刪除專案會同時刪除所有相關議題和成員關聯

### 3. 議題管理
- **建立議題**：
  - 輸入議題名稱、概述
  - 選擇開始/結束時間
  - 選擇狀態（如：待處理、進行中、已完成等）
  - 指派給專案成員

- **編輯議題**：
  - 修改議題的所有欄位
  - 儲存變更到資料庫

- **議題列表**：
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

### 6. 好友管理
- **添加好友**：透過帳號或電子郵件搜尋並添加
- **好友列表**：顯示所有好友資訊
- **刪除好友**：移除好友關係（自動刪除雙向關係）

### 7. 即時聊天
- **專案聊天室**：每個專案都有專屬聊天室，成員可以在其中討論專案相關事宜
- **私訊功能**：用戶之間可以發送私訊
- **群組聊天**：創建和管理群組聊天室
- **訊息歷史**：保存和載入歷史訊息

**聊天伺服器設置請參考**：[CHAT_SERVER_SETUP.md](CHAT_SERVER_SETUP.md)

### 8. 設定功能
- **語言切換**：中文/英文切換
- **Excel 匯出**：匯出專案資料
- **GitHub 匯入**：匯入 GitHub 資料（功能預留）
- **登出**：登出當前用戶
- **刪除帳號**：完整刪除帳號及所有相關資料

## 開發注意事項

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
- 網路權限：用於 Supabase 服務
- 儲存權限：Excel 匯出功能（Android 10+ 可能不需要）

## 相關文檔

### 📚 核心文檔

- **[PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)** - 專案完整說明文件
  - 包含專案概述、技術架構、資料庫設定詳細說明、安裝與配置、API 使用說明等完整資訊

- **[DATABASE_OVERVIEW.md](DATABASE_OVERVIEW.md)** - 資料庫總覽
  - 資料表結構詳細說明、API 操作總覽、資料流程圖、快速參考範例

- **[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)** - 資料庫設置腳本
  - 完整的 Supabase 資料庫設置 SQL 腳本
  - 包含所有表、索引、觸發器、RLS 策略、視圖、函數

- **[SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md)** - 資料庫 API 文檔
  - 所有 `SupabaseDatabaseHelper` 方法的詳細說明
  - 參數說明、返回值說明、使用範例、錯誤處理

- **[DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md)** - 資料庫遷移指南
  - 從 SQLite 遷移到 Supabase 的完整指南
  - 代碼遷移範例、資料遷移方法、測試檢查清單

- **[DATABASE_DOCUMENTATION_INDEX.md](DATABASE_DOCUMENTATION_INDEX.md)** - 資料庫文檔索引
  - 所有資料庫相關文檔的索引和快速參考

### 🔧 設置文檔

- **[SUPABASE_SETUP.md](SUPABASE_SETUP.md)** - Supabase 設置指南
  - 創建 Supabase 專案、資料庫表結構設置、Gmail OAuth 配置、應用程式配置

- **[CHAT_SERVER_SETUP.md](CHAT_SERVER_SETUP.md)** - 聊天伺服器設置指南
  - 聊天伺服器設置步驟、使用說明、功能擴展建議、故障排除

### 📋 其他文檔

- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - 實現總結
  - 已完成的工作、待完成的工作、使用建議、注意事項

- **[TODO.md](TODO.md)** - 待辦事項
  - 專案待完成的功能和改進項目

---

## 快速開始

### 第一次設置

1. **閱讀設置指南**：[SUPABASE_SETUP.md](SUPABASE_SETUP.md)
2. **執行資料庫腳本**：[SUPABASE_DATABASE_SCHEMA.sql](SUPABASE_DATABASE_SCHEMA.sql)
3. **配置應用程式**：在 `SettingsFragment.java` 或 `SupabaseConfig.java` 中設置 Supabase URL 和 API Key
4. **同步 Gradle**：在 Android Studio 中同步專案
5. **執行應用程式**：連接裝置或啟動模擬器並運行

### 開發時查閱

- **API 方法**：查閱 [SUPABASE_DATABASE_API.md](SUPABASE_DATABASE_API.md)
- **資料結構**：查閱 [DATABASE_OVERVIEW.md](DATABASE_OVERVIEW.md)
- **遷移代碼**：參考 [DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md)

---

## 已知問題與限制

1. **離線支援**：應用程式需要網路連線才能使用，所有資料存儲在 Supabase
2. **RLS 策略**：當前策略較為寬鬆，生產環境可能需要更嚴格的策略
3. **部分功能**：部分功能（如 GitHub 匯入）仍在開發中

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

**重要提示**：使用本應用程式前，請確保已正確配置 Supabase 專案並在應用程式中設定正確的 `SUPABASE_URL` 和 `SUPABASE_KEY`。詳細的設置步驟請參考 [SUPABASE_SETUP.md](SUPABASE_SETUP.md)。
