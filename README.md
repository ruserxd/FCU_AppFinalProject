# 114-1_FCU_Network-Programming-Final-Project

## 專案簡介

這是一個基於 Android 平台的專案管理應用程式，提供完整的專案協作、議題追蹤、甘特圖視覺化等功能。應用程式使用 Supabase 進行身份驗證和雲端資料同步。

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

## 技術棧

### 開發環境
- **IDE**：Android Studio
- **語言**：Java 11
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
- `io.github.jan-tennert.supabase:auth-kt` - Supabase 身份驗證
- `io.github.jan-tennert.supabase:storage-kt` - Supabase 雲端儲存
- `io.github.jan-tennert.supabase:realtime-kt` - Supabase Realtime（即時同步）

#### 第三方庫
- `org.apache.poi:poi:5.4.0` - Apache POI（Excel 檔案處理）
- `org.apache.poi:poi-ooxml:5.4.0` - Apache POI OOXML（Excel 2007+ 格式支援）
- `io.ktor:ktor-client-android:2.3.10` - Ktor HTTP 客戶端
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3` - KotlinX 序列化

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
應用程式使用 Supabase 作為後端資料庫服務，所有資料存儲在 Supabase PostgreSQL 資料庫中。

#### 主要表格結構

**1. users（用戶表）**
- `id` (UUID) - 主鍵
- `account` (TEXT) - 用戶帳號
- `email` (TEXT) - 電子郵件

**2. projects（專案表）**
- `id` (INTEGER) - 主鍵
- `name` (TEXT) - 專案名稱
- `summary` (TEXT) - 專案概述

**3. issues（議題表）**
- `id` (INTEGER) - 主鍵
- `name` (TEXT) - 議題名稱
- `summary` (TEXT) - 議題概述
- `start_time` (TEXT) - 開始時間
- `end_time` (TEXT) - 結束時間
- `status` (TEXT) - 狀態
- `designee` (TEXT) - 被指派者
- `project_id` (INTEGER) - 專案 ID

**4. chatrooms（聊天室表）**
- `id` (INTEGER) - 主鍵
- `name` (TEXT) - 聊天室名稱
- `type` (TEXT) - 類型（private/group）
- `created_at` (TEXT) - 建立時間
- `created_by` (TEXT) - 建立者 ID

**5. messages（訊息表）**
- `id` (INTEGER) - 主鍵
- `chatroom_id` (INTEGER) - 聊天室 ID
- `sender_id` (TEXT) - 發送者 ID
- `content` (TEXT) - 訊息內容
- `created_at` (TEXT) - 建立時間

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
- **配置檔案**：在 `SupabaseManager.kt` 中設定 `SUPABASE_URL` 和 `SUPABASE_KEY`
- **API Key**：從 Supabase 專案設定中獲取

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
│   │   │   │   ├── ProjectHelper.java      # 專案相關資料庫操作
│   │   │   │   └── SupabaseProjectHelper.java  # Supabase 資料庫操作
│   │   │   ├── supabase/             # Supabase 相關
│   │   │   │   ├── SupabaseManager.kt      # Supabase 客戶端管理
│   │   │   │   ├── AuthHelper.kt           # 身份驗證輔助
│   │   │   │   └── ChatHelper.kt           # 聊天室輔助
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
│   │   │   │   └── User.java
│   │   │   ├── CreateIssueActivity.java    # 建立議題頁面
│   │   │   ├── EditIssueActivity.java      # 編輯議題頁面
│   │   │   ├── ExportExcel.java            # Excel 匯出功能
│   │   │   ├── GanttActivity.java          # 甘特圖頁面
│   │   │   ├── HomeActivity.java           # 主頁面
│   │   │   ├── LoginActivity.java          # 登入頁面
│   │   │   ├── ProjectActivity.java        # 專案頁面
│   │   │   ├── RegisterActivity.java       # 註冊頁面
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
4. **Firebase 專案**：需要有效的 Firebase 專案配置

### 安裝步驟

#### 1. 克隆專案
```bash
git clone <repository-url>
cd 114-1_FCU_Network-Programming-Final-Project
```

#### 2. 配置 Firebase
- 確保 `app/google-services.json` 檔案存在且包含正確的 Firebase 專案配置
- 如果沒有，請從 Firebase Console 下載並替換現有檔案

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
- 添加好友：透過帳號或電子郵件搜尋並添加
- 好友列表：顯示所有好友資訊
- 刪除好友：移除好友關係

### 7. 設定功能
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

### 權限處理
- 網路權限：用於 Supabase 服務
- 儲存權限：Excel 匯出功能（Android 10+ 可能不需要）

## 已知問題與限制

1. **部分功能待實現**：部分功能（如議題管理、專案管理）的 Supabase 整合待完善
2. **好友列表**：好友列表功能需要從 Supabase 獲取，目前待實現
3. **離線支援**：應用程式需要網路連線才能使用，所有資料存儲在 Supabase

## 版本資訊

- **版本號**：1.0
- **版本代碼**：1
- **最後更新**：2024

## 開發團隊

逢甲大學 114-1 網路程式設計課程 期末專案

## 授權

本專案為課程作業專案，僅供學習使用。

---

**注意**：使用本應用程式前，請確保已正確配置 Supabase 專案並在 `SupabaseManager.kt` 中設定正確的 `SUPABASE_URL` 和 `SUPABASE_KEY`。