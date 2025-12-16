# 114-1 網路程式設計期末專題

## 專案簡介

這是一個基於 Android 平台的專案管理應用程式，提供完整的專案與議題管理功能，支援多人協作、好友系統、資料匯出等功能。

## 使用的工具

- Firebase Authentication（用戶認證）
- Firebase Firestore（資料庫，部分功能）
- SQLite（本地資料庫）
- Android Studio
- Java
- Apache POI（Excel 匯出）

## 專案版本

- 2025/12/16 前: 「Android APP 期末專題」框架 -> 請見 `Android-APP-期末專題` 分支

## TO-DO

- 2025/12/16: 更新於 [TODO.md](TODO.md) 文件

## 主要功能

### 1. 用戶認證系統

#### 登入功能
- Firebase 電子郵件/密碼登入
- 自動登入狀態保持
- 登入錯誤訊息提示（密碼錯誤、帳號不存在等）
- 語言切換功能（中文/英文）

#### 註冊功能
- 新用戶註冊（帳號、電子郵件、密碼）
- 輸入驗證（必填欄位、密碼長度、電子郵件格式）
- 網路連線檢查
- Firebase 與本地資料庫同步
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

#### 主要資料表
- **Users**：用戶資料（id, account, email, firebase_uid）
- **Projects**：專案資料（id, name, summary）
- **Issues**：議題資料（id, name, summary, start_time, end_time, status, designee, project_id）
- **UserProject**：用戶與專案的多對多關聯
- **UserIssue**：用戶與議題的多對多關聯
- **Friends**：好友關係（雙向）

#### 資料同步
- Firebase Authentication 與本地 SQLite 資料庫同步
- UserSyncHelper 處理用戶資料同步邏輯

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
- HTTP 連線處理

## 技術架構

### 架構模式
- Activity + Fragment 架構
- SQLite 本地資料庫
- Firebase 雲端認證
- SharedPreferences 本地設定儲存

### 主要類別

#### Activity
- `LoginActivity`：登入頁面
- `RegisterActivity`：註冊頁面
- `HomeActivity`：主頁面
- `ProjectActivity`：專案詳情頁面
- `CreateIssueActivity`：建立議題頁面（部分功能）
- `EditIssueActivity`：編輯議題頁面
- `GanttActivity`：甘特圖視圖

#### Fragment
- `HomeFragment`：專案列表
- `AddFragment`：建立專案
- `SettingsFragment`：設定頁面
- `ProjectInfoFragment`：專案議題列表
- `AddIssueFragment`：建立議題
- `FriendFragment`：好友列表
- `AddFriendFragment`：新增好友

#### Helper
- `SqlDataBaseHelper`：資料庫管理
- `ProjectHelper`：專案相關操作
- `UserSyncHelper`：用戶資料同步
- `ExportExcel`：Excel 匯出功能

#### Adapter
- `ProjectAdapter`：專案列表適配器
- `IssueAdapter`：議題列表適配器
- `FriendAdapter`：好友列表適配器
- `AddfriendAdapter`：可新增好友列表適配器
- `IssueNameAdapter`：甘特圖議題名稱適配器
- `IssueMonthAdapter`：甘特圖月份適配器

#### Model
- `User`：用戶模型
- `Project`：專案模型
- `Issue`：議題模型
- `IssueName`：甘特圖議題名稱模型
- `IssueMonth`：甘特圖月份模型

## 使用流程

1. **註冊/登入**：新用戶註冊或現有用戶登入
2. **查看專案**：在主頁面查看參與的專案列表
3. **建立專案**：在新增頁面建立新專案並選擇成員
4. **進入專案**：點擊專案卡片進入專案詳情頁面
5. **管理議題**：查看、新增、編輯、刪除議題
6. **查看甘特圖**：點擊專案名稱查看時間軸視圖
7. **好友管理**：在設定頁面管理好友
8. **匯出資料**：匯出專案資料為 Excel 檔案
9. **匯入 GitHub**：從 GitHub 匯入專案
10. **設定**：切換語言、登出、刪除帳號等

## 注意事項

- 需要網路連線才能使用 Firebase 認證和 GitHub 匯入功能
- 議題只能指派給專案成員
- 刪除專案會同時刪除所有相關議題
- 刪除帳號會永久刪除所有相關資料
- Excel 匯出檔案儲存在應用程式文件目錄

## 未來擴展

- 即時同步功能（Firebase Realtime Database）
- 推播通知
- 檔案上傳功能
- 議題評論功能
- 專案權限管理
- 資料統計圖表