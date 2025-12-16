package fcu.app.appclassfinalproject.helper;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Supabase 資料庫操作助手類
 * 提供 CRUD 操作來替代 SQLite
 */
public class SupabaseDatabaseHelper {
    private static final String TAG = "SupabaseDatabaseHelper";
    private SupabaseConfig config;
    private Context context;
    
    public SupabaseDatabaseHelper(Context context) {
        this.context = context;
        this.config = SupabaseConfig.getInstance(context);
    }
    
    // ==================== Users 表操作 ====================
    
    /**
     * 插入用戶
     */
    public boolean insertUser(String account, String email, String firebaseUid) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("account", account);
            body.addProperty("email", email);
            if (firebaseUid != null) {
                body.addProperty("firebase_uid", firebaseUid);
            }
            
            String response = config.executePost("/rest/v1/Users", body);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "插入用戶錯誤", e);
            return false;
        }
    }
    
    /**
     * 根據 email 獲取用戶
     */
    public JsonObject getUserByEmail(String email) {
        try {
            String endpoint = "/rest/v1/Users?email=eq." + email + "&select=*";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            if (array.size() > 0) {
                return array.get(0).getAsJsonObject();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "獲取用戶錯誤", e);
            return null;
        }
    }
    
    /**
     * 根據 ID 獲取用戶
     */
    public JsonObject getUserById(int userId) {
        try {
            String endpoint = "/rest/v1/Users?id=eq." + userId + "&select=*";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            if (array.size() > 0) {
                return array.get(0).getAsJsonObject();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "獲取用戶錯誤", e);
            return null;
        }
    }
    
    // ==================== Projects 表操作 ====================
    
    /**
     * 插入專案
     */
    public Integer insertProject(String name, String summary) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("name", name);
            body.addProperty("summary", summary);
            
            String response = config.executePost("/rest/v1/Projects", body);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            if (array.size() > 0) {
                return array.get(0).getAsJsonObject().get("id").getAsInt();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "插入專案錯誤", e);
            return null;
        }
    }
    
    /**
     * 獲取用戶的所有專案
     */
    public List<JsonObject> getProjectsByUser(int userId) {
        try {
            String endpoint = "/rest/v1/UserProject?user_id=eq." + userId + "&select=project_id,Projects(*)";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            
            List<JsonObject> projects = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject item = element.getAsJsonObject();
                if (item.has("Projects")) {
                    projects.add(item.getAsJsonObject("Projects"));
                }
            }
            return projects;
        } catch (Exception e) {
            Log.e(TAG, "獲取專案錯誤", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 獲取專案詳情
     */
    public JsonObject getProjectById(int projectId) {
        try {
            String endpoint = "/rest/v1/Projects?id=eq." + projectId + "&select=*";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            if (array.size() > 0) {
                return array.get(0).getAsJsonObject();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "獲取專案錯誤", e);
            return null;
        }
    }
    
    /**
     * 刪除專案
     */
    public boolean deleteProject(int projectId) {
        try {
            String endpoint = "/rest/v1/Projects?id=eq." + projectId;
            config.executeDelete(endpoint);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "刪除專案錯誤", e);
            return false;
        }
    }
    
    // ==================== UserProject 表操作 ====================
    
    /**
     * 添加用戶到專案
     */
    public boolean addUserToProject(int userId, int projectId) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("user_id", userId);
            body.addProperty("project_id", projectId);
            
            String response = config.executePost("/rest/v1/UserProject", body);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "添加用戶到專案錯誤", e);
            return false;
        }
    }
    
    /**
     * 獲取專案成員
     */
    public List<JsonObject> getProjectMembers(int projectId) {
        try {
            String endpoint = "/rest/v1/UserProject?project_id=eq." + projectId + "&select=user_id,Users(*)";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            
            List<JsonObject> members = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject item = element.getAsJsonObject();
                if (item.has("Users")) {
                    members.add(item.getAsJsonObject("Users"));
                }
            }
            return members;
        } catch (Exception e) {
            Log.e(TAG, "獲取專案成員錯誤", e);
            return new ArrayList<>();
        }
    }
    
    // ==================== Issues 表操作 ====================
    
    /**
     * 插入議題
     */
    public Integer insertIssue(String name, String summary, String startTime, 
                              String endTime, String status, String designee, int projectId) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("name", name);
            body.addProperty("summary", summary);
            body.addProperty("start_time", startTime);
            body.addProperty("end_time", endTime);
            body.addProperty("status", status);
            body.addProperty("designee", designee);
            body.addProperty("project_id", projectId);
            
            String response = config.executePost("/rest/v1/Issues", body);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            if (array.size() > 0) {
                return array.get(0).getAsJsonObject().get("id").getAsInt();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "插入議題錯誤", e);
            return null;
        }
    }
    
    /**
     * 獲取專案的所有議題
     */
    public List<JsonObject> getIssuesByProject(int projectId) {
        try {
            String endpoint = "/rest/v1/Issues?project_id=eq." + projectId + "&select=*&order=id";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            
            List<JsonObject> issues = new ArrayList<>();
            for (JsonElement element : array) {
                issues.add(element.getAsJsonObject());
            }
            return issues;
        } catch (Exception e) {
            Log.e(TAG, "獲取議題錯誤", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 更新議題
     */
    public boolean updateIssue(int issueId, JsonObject updates) {
        try {
            String endpoint = "/rest/v1/Issues?id=eq." + issueId;
            config.executePatch(endpoint, updates);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "更新議題錯誤", e);
            return false;
        }
    }
    
    /**
     * 刪除議題
     */
    public boolean deleteIssue(int issueId) {
        try {
            String endpoint = "/rest/v1/Issues?id=eq." + issueId;
            config.executeDelete(endpoint);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "刪除議題錯誤", e);
            return false;
        }
    }
    
    // ==================== Friends 表操作 ====================
    
    /**
     * 添加好友
     */
    public boolean addFriend(int userId, int friendId) {
        try {
            // 添加雙向好友關係
            JsonObject body1 = new JsonObject();
            body1.addProperty("user_id", userId);
            body1.addProperty("friend_id", friendId);
            config.executePost("/rest/v1/Friends", body1);
            
            JsonObject body2 = new JsonObject();
            body2.addProperty("user_id", friendId);
            body2.addProperty("friend_id", userId);
            config.executePost("/rest/v1/Friends", body2);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "添加好友錯誤", e);
            return false;
        }
    }
    
    /**
     * 獲取用戶的好友列表
     */
    public List<JsonObject> getFriends(int userId) {
        try {
            String endpoint = "/rest/v1/Friends?user_id=eq." + userId + "&select=friend_id,Users(*)";
            String response = config.executeGet(endpoint);
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            
            List<JsonObject> friends = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject item = element.getAsJsonObject();
                if (item.has("Users")) {
                    friends.add(item.getAsJsonObject("Users"));
                }
            }
            return friends;
        } catch (Exception e) {
            Log.e(TAG, "獲取好友列表錯誤", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 刪除好友
     */
    public boolean removeFriend(int userId, int friendId) {
        try {
            // 刪除雙向好友關係
            String endpoint1 = "/rest/v1/Friends?user_id=eq." + userId + "&friend_id=eq." + friendId;
            config.executeDelete(endpoint1);
            
            String endpoint2 = "/rest/v1/Friends?user_id=eq." + friendId + "&friend_id=eq." + userId;
            config.executeDelete(endpoint2);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "刪除好友錯誤", e);
            return false;
        }
    }
    
    // ==================== UserIssue 表操作 ====================
    
    /**
     * 添加用戶到議題
     */
    public boolean addUserToIssue(int userId, int issueId) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("user_id", userId);
            body.addProperty("issue_id", issueId);
            
            String response = config.executePost("/rest/v1/UserIssue", body);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "添加用戶到議題錯誤", e);
            return false;
        }
    }
}

