-- ============================================
-- Supabase 資料庫完整設置腳本
-- 專案管理應用程式資料庫結構
-- ============================================

-- ============================================
-- 1. 創建資料表
-- ============================================

-- Users 表：用戶資料
CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    account TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    firebase_uid TEXT UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Projects 表：專案資料
CREATE TABLE IF NOT EXISTS Projects (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    summary TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Issues 表：議題資料
CREATE TABLE IF NOT EXISTS Issues (
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

-- UserProject 表：用戶與專案的多對多關聯
CREATE TABLE IF NOT EXISTS UserProject (
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE
);

-- UserIssue 表：用戶與議題的多對多關聯
CREATE TABLE IF NOT EXISTS UserIssue (
    user_id INTEGER NOT NULL,
    issue_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, issue_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (issue_id) REFERENCES Issues(id) ON DELETE CASCADE
);

-- Friends 表：好友關係（雙向）
CREATE TABLE IF NOT EXISTS Friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES Users(id) ON DELETE CASCADE,
    CHECK (user_id != friend_id) -- 防止用戶與自己成為好友
);

-- ============================================
-- 2. 創建索引（提升查詢效能）
-- ============================================

-- Users 表索引
CREATE INDEX IF NOT EXISTS idx_users_email ON Users(email);
CREATE INDEX IF NOT EXISTS idx_users_firebase_uid ON Users(firebase_uid);
CREATE INDEX IF NOT EXISTS idx_users_account ON Users(account);

-- Projects 表索引
CREATE INDEX IF NOT EXISTS idx_projects_name ON Projects(name);

-- Issues 表索引
CREATE INDEX IF NOT EXISTS idx_issues_project_id ON Issues(project_id);
CREATE INDEX IF NOT EXISTS idx_issues_status ON Issues(status);
CREATE INDEX IF NOT EXISTS idx_issues_designee ON Issues(designee);
CREATE INDEX IF NOT EXISTS idx_issues_start_time ON Issues(start_time);
CREATE INDEX IF NOT EXISTS idx_issues_end_time ON Issues(end_time);

-- UserProject 表索引
CREATE INDEX IF NOT EXISTS idx_userproject_user_id ON UserProject(user_id);
CREATE INDEX IF NOT EXISTS idx_userproject_project_id ON UserProject(project_id);

-- UserIssue 表索引
CREATE INDEX IF NOT EXISTS idx_userissue_user_id ON UserIssue(user_id);
CREATE INDEX IF NOT EXISTS idx_userissue_issue_id ON UserIssue(issue_id);

-- Friends 表索引
CREATE INDEX IF NOT EXISTS idx_friends_user_id ON Friends(user_id);
CREATE INDEX IF NOT EXISTS idx_friends_friend_id ON Friends(friend_id);

-- ============================================
-- 3. 創建觸發器（自動更新 updated_at）
-- ============================================

-- 創建更新時間戳的函數
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Users 表觸發器
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON Users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Projects 表觸發器
CREATE TRIGGER update_projects_updated_at
    BEFORE UPDATE ON Projects
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Issues 表觸發器
CREATE TRIGGER update_issues_updated_at
    BEFORE UPDATE ON Issues
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 4. 啟用 Row Level Security (RLS)
-- ============================================

ALTER TABLE Users ENABLE ROW LEVEL SECURITY;
ALTER TABLE Projects ENABLE ROW LEVEL SECURITY;
ALTER TABLE Issues ENABLE ROW LEVEL SECURITY;
ALTER TABLE UserProject ENABLE ROW LEVEL SECURITY;
ALTER TABLE UserIssue ENABLE ROW LEVEL SECURITY;
ALTER TABLE Friends ENABLE ROW LEVEL SECURITY;

-- ============================================
-- 5. 創建 RLS 策略
-- ============================================

-- Users 表策略
CREATE POLICY "Users can read all users" 
    ON Users FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert own data" 
    ON Users FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can update own data" 
    ON Users FOR UPDATE 
    USING (true);

-- Projects 表策略
CREATE POLICY "Users can read all projects" 
    ON Projects FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert projects" 
    ON Projects FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can update projects" 
    ON Projects FOR UPDATE 
    USING (true);

CREATE POLICY "Users can delete projects" 
    ON Projects FOR DELETE 
    USING (true);

-- Issues 表策略
CREATE POLICY "Users can read all issues" 
    ON Issues FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert issues" 
    ON Issues FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can update issues" 
    ON Issues FOR UPDATE 
    USING (true);

CREATE POLICY "Users can delete issues" 
    ON Issues FOR DELETE 
    USING (true);

-- UserProject 表策略
CREATE POLICY "Users can read all user projects" 
    ON UserProject FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert user projects" 
    ON UserProject FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can delete user projects" 
    ON UserProject FOR DELETE 
    USING (true);

-- UserIssue 表策略
CREATE POLICY "Users can read all user issues" 
    ON UserIssue FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert user issues" 
    ON UserIssue FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can delete user issues" 
    ON UserIssue FOR DELETE 
    USING (true);

-- Friends 表策略
CREATE POLICY "Users can read all friends" 
    ON Friends FOR SELECT 
    USING (true);

CREATE POLICY "Users can insert friends" 
    ON Friends FOR INSERT 
    WITH CHECK (true);

CREATE POLICY "Users can delete friends" 
    ON Friends FOR DELETE 
    USING (true);

-- ============================================
-- 6. 創建視圖（方便查詢）
-- ============================================

-- 專案詳情視圖（包含成員資訊）
CREATE OR REPLACE VIEW project_details AS
SELECT 
    p.id,
    p.name,
    p.summary,
    p.created_at,
    p.updated_at,
    COUNT(DISTINCT up.user_id) as member_count,
    COUNT(DISTINCT i.id) as issue_count
FROM Projects p
LEFT JOIN UserProject up ON p.id = up.project_id
LEFT JOIN Issues i ON p.id = i.project_id
GROUP BY p.id, p.name, p.summary, p.created_at, p.updated_at;

-- 用戶專案列表視圖
CREATE OR REPLACE VIEW user_projects_view AS
SELECT 
    u.id as user_id,
    u.account,
    u.email,
    p.id as project_id,
    p.name as project_name,
    p.summary as project_summary,
    up.created_at as joined_at
FROM Users u
INNER JOIN UserProject up ON u.id = up.user_id
INNER JOIN Projects p ON up.project_id = p.id;

-- 議題詳情視圖（包含專案資訊）
CREATE OR REPLACE VIEW issue_details AS
SELECT 
    i.id,
    i.name,
    i.summary,
    i.start_time,
    i.end_time,
    i.status,
    i.designee,
    i.project_id,
    p.name as project_name,
    i.created_at,
    i.updated_at
FROM Issues i
INNER JOIN Projects p ON i.project_id = p.id;

-- ============================================
-- 7. 創建函數（業務邏輯）
-- ============================================

-- 獲取用戶參與的專案數量
CREATE OR REPLACE FUNCTION get_user_project_count(user_id_param INTEGER)
RETURNS INTEGER AS $$
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM UserProject
        WHERE user_id = user_id_param
    );
END;
$$ LANGUAGE plpgsql;

-- 獲取專案的成員數量
CREATE OR REPLACE FUNCTION get_project_member_count(project_id_param INTEGER)
RETURNS INTEGER AS $$
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM UserProject
        WHERE project_id = project_id_param
    );
END;
$$ LANGUAGE plpgsql;

-- 獲取專案的議題數量
CREATE OR REPLACE FUNCTION get_project_issue_count(project_id_param INTEGER)
RETURNS INTEGER AS $$
BEGIN
    RETURN (
        SELECT COUNT(*)
        FROM Issues
        WHERE project_id = project_id_param
    );
END;
$$ LANGUAGE plpgsql;

-- 檢查用戶是否為專案成員
CREATE OR REPLACE FUNCTION is_user_project_member(
    user_id_param INTEGER,
    project_id_param INTEGER
)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM UserProject
        WHERE user_id = user_id_param
        AND project_id = project_id_param
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 8. 註釋（文檔說明）
-- ============================================

COMMENT ON TABLE Users IS '用戶資料表，儲存應用程式用戶的基本資訊';
COMMENT ON TABLE Projects IS '專案資料表，儲存專案的基本資訊';
COMMENT ON TABLE Issues IS '議題資料表，儲存專案中的議題資訊';
COMMENT ON TABLE UserProject IS '用戶與專案的多對多關聯表';
COMMENT ON TABLE UserIssue IS '用戶與議題的多對多關聯表';
COMMENT ON TABLE Friends IS '好友關係表，儲存用戶之間的好友關係';

COMMENT ON COLUMN Users.id IS '用戶唯一識別碼';
COMMENT ON COLUMN Users.account IS '用戶帳號（唯一）';
COMMENT ON COLUMN Users.email IS '用戶電子郵件（唯一）';
COMMENT ON COLUMN Users.firebase_uid IS 'Firebase 用戶識別碼（可選，用於遷移）';

COMMENT ON COLUMN Projects.id IS '專案唯一識別碼';
COMMENT ON COLUMN Projects.name IS '專案名稱';
COMMENT ON COLUMN Projects.summary IS '專案摘要說明';

COMMENT ON COLUMN Issues.id IS '議題唯一識別碼';
COMMENT ON COLUMN Issues.name IS '議題名稱';
COMMENT ON COLUMN Issues.summary IS '議題摘要說明';
COMMENT ON COLUMN Issues.start_time IS '議題開始時間（格式：YYYY-MM-DD）';
COMMENT ON COLUMN Issues.end_time IS '議題結束時間（格式：YYYY-MM-DD）';
COMMENT ON COLUMN Issues.status IS '議題狀態（未開始/進行中/已完成）';
COMMENT ON COLUMN Issues.designee IS '被指派者帳號';
COMMENT ON COLUMN Issues.project_id IS '所屬專案ID';

-- ============================================
-- 完成
-- ============================================

-- 執行完成後，可以驗證表是否創建成功：
-- SELECT table_name FROM information_schema.tables 
-- WHERE table_schema = 'public' 
-- AND table_name IN ('Users', 'Projects', 'Issues', 'UserProject', 'UserIssue', 'Friends');

