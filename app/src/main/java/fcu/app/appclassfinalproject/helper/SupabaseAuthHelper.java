package fcu.app.appclassfinalproject.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;

/**
 * Supabase 認證助手類
 * 處理 Gmail OAuth 登入和其他認證操作
 */
public class SupabaseAuthHelper {
    private static final String TAG = "SupabaseAuthHelper";
    private SupabaseConfig config;
    private Context context;
    
    public SupabaseAuthHelper(Context context) {
        this.context = context;
        this.config = SupabaseConfig.getInstance(context);
    }
    
    /**
     * 使用 Gmail OAuth 登入
     * 注意：這需要在 Supabase 控制台配置 Google OAuth
     */
    public AuthResult signInWithGmail(String providerToken) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("provider", "google");
            body.addProperty("provider_token", providerToken);
            
            String response = config.executePost("/auth/v1/token?grant_type=id_token", body);
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            if (jsonResponse.has("access_token")) {
                String accessToken = jsonResponse.get("access_token").getAsString();
                String refreshToken = jsonResponse.has("refresh_token") ? 
                    jsonResponse.get("refresh_token").getAsString() : null;
                String userId = jsonResponse.has("user") ? 
                    jsonResponse.getAsJsonObject("user").get("id").getAsString() : null;
                String email = jsonResponse.has("user") ? 
                    jsonResponse.getAsJsonObject("user").get("email").getAsString() : null;
                
                // 保存令牌
                config.saveAccessToken(accessToken);
                saveUserInfo(userId, email, refreshToken);
                
                return new AuthResult(true, "登入成功", userId, email, accessToken);
            } else {
                String error = jsonResponse.has("error_description") ? 
                    jsonResponse.get("error_description").getAsString() : "登入失敗";
                return new AuthResult(false, error, null, null, null);
            }
        } catch (IOException e) {
            Log.e(TAG, "Gmail 登入錯誤", e);
            return new AuthResult(false, "網路錯誤: " + e.getMessage(), null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Gmail 登入錯誤", e);
            return new AuthResult(false, "登入失敗: " + e.getMessage(), null, null, null);
        }
    }
    
    /**
     * 使用電子郵件和密碼登入
     */
    public AuthResult signInWithEmail(String email, String password) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("password", password);
            
            String response = config.executePost("/auth/v1/token?grant_type=password", body);
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            if (jsonResponse.has("access_token")) {
                String accessToken = jsonResponse.get("access_token").getAsString();
                String refreshToken = jsonResponse.has("refresh_token") ? 
                    jsonResponse.get("refresh_token").getAsString() : null;
                String userId = jsonResponse.has("user") ? 
                    jsonResponse.getAsJsonObject("user").get("id").getAsString() : null;
                
                config.saveAccessToken(accessToken);
                saveUserInfo(userId, email, refreshToken);
                
                return new AuthResult(true, "登入成功", userId, email, accessToken);
            } else {
                String error = jsonResponse.has("error_description") ? 
                    jsonResponse.get("error_description").getAsString() : "登入失敗";
                return new AuthResult(false, error, null, null, null);
            }
        } catch (IOException e) {
            Log.e(TAG, "電子郵件登入錯誤", e);
            return new AuthResult(false, "網路錯誤: " + e.getMessage(), null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "電子郵件登入錯誤", e);
            return new AuthResult(false, "登入失敗: " + e.getMessage(), null, null, null);
        }
    }
    
    /**
     * 註冊新用戶
     */
    public AuthResult signUp(String email, String password, String account) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("password", password);
            
            // 添加用戶元數據
            JsonObject metadata = new JsonObject();
            metadata.addProperty("account", account);
            body.add("data", metadata);
            
            String response = config.executePost("/auth/v1/signup", body);
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            if (jsonResponse.has("access_token")) {
                String accessToken = jsonResponse.get("access_token").getAsString();
                String userId = jsonResponse.has("user") ? 
                    jsonResponse.getAsJsonObject("user").get("id").getAsString() : null;
                
                config.saveAccessToken(accessToken);
                saveUserInfo(userId, email, null);
                
                return new AuthResult(true, "註冊成功", userId, email, accessToken);
            } else {
                String error = jsonResponse.has("error_description") ? 
                    jsonResponse.get("error_description").getAsString() : "註冊失敗";
                return new AuthResult(false, error, null, null, null);
            }
        } catch (IOException e) {
            Log.e(TAG, "註冊錯誤", e);
            return new AuthResult(false, "網路錯誤: " + e.getMessage(), null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "註冊錯誤", e);
            return new AuthResult(false, "註冊失敗: " + e.getMessage(), null, null, null);
        }
    }
    
    /**
     * 登出
     */
    public void signOut() {
        config.clearAccessToken();
        clearUserInfo();
        Log.d(TAG, "用戶已登出");
    }
    
    /**
     * 獲取當前用戶
     */
    public String getCurrentUserId() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        return prefs.getString("supabase_user_id", null);
    }
    
    /**
     * 檢查是否已登入
     */
    public boolean isSignedIn() {
        String token = config.getAccessToken();
        return token != null && !token.isEmpty();
    }
    
    /**
     * 刷新訪問令牌
     */
    public boolean refreshToken() {
        try {
            SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
            String refreshToken = prefs.getString("supabase_refresh_token", null);
            
            if (refreshToken == null) {
                return false;
            }
            
            JsonObject body = new JsonObject();
            body.addProperty("refresh_token", refreshToken);
            
            String response = config.executePost("/auth/v1/token?grant_type=refresh_token", body);
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            if (jsonResponse.has("access_token")) {
                String accessToken = jsonResponse.get("access_token").getAsString();
                config.saveAccessToken(accessToken);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "刷新令牌錯誤", e);
            return false;
        }
    }
    
    private void saveUserInfo(String userId, String email, String refreshToken) {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (userId != null) {
            editor.putString("supabase_user_id", userId);
        }
        if (email != null) {
            editor.putString("email", email);
        }
        if (refreshToken != null) {
            editor.putString("supabase_refresh_token", refreshToken);
        }
        editor.apply();
    }
    
    private void clearUserInfo() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("supabase_user_id");
        editor.remove("supabase_refresh_token");
        editor.apply();
    }
    
    /**
     * 認證結果類
     */
    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final String userId;
        public final String email;
        public final String accessToken;
        
        public AuthResult(boolean success, String message, String userId, String email, String accessToken) {
            this.success = success;
            this.message = message;
            this.userId = userId;
            this.email = email;
            this.accessToken = accessToken;
        }
    }
}

