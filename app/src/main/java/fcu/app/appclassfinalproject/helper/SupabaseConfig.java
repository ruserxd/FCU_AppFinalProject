package fcu.app.appclassfinalproject.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Supabase 配置和連接管理類
 * 注意：需要在 Supabase 控制台設置項目 URL 和 API Key
 */
public class SupabaseConfig {
    private static final String TAG = "SupabaseConfig";
    
    // TODO: 替換為您的 Supabase 項目 URL 和 API Key
    // 這些值應該從環境變數或配置文件中讀取，不要硬編碼在生產環境中
    private static final String SUPABASE_URL = "YOUR_SUPABASE_URL"; // 例如: https://xxxxx.supabase.co
    private static final String SUPABASE_ANON_KEY = "YOUR_SUPABASE_ANON_KEY";
    private static final String SUPABASE_SERVICE_KEY = "YOUR_SUPABASE_SERVICE_KEY"; // 僅用於服務端操作
    
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private static SupabaseConfig instance;
    private OkHttpClient client;
    private Gson gson;
    private Context context;
    
    private SupabaseConfig(Context context) {
        this.context = context.getApplicationContext();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public static synchronized SupabaseConfig getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseConfig(context);
        }
        return instance;
    }
    
    public String getSupabaseUrl() {
        // 從 SharedPreferences 讀取配置（如果有的話）
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        String url = prefs.getString("supabase_url", SUPABASE_URL);
        if (url.equals("YOUR_SUPABASE_URL")) {
            Log.w(TAG, "警告：請設置 Supabase URL");
        }
        return url;
    }
    
    public String getAnonKey() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        String key = prefs.getString("supabase_anon_key", SUPABASE_ANON_KEY);
        if (key.equals("YOUR_SUPABASE_ANON_KEY")) {
            Log.w(TAG, "警告：請設置 Supabase Anon Key");
        }
        return key;
    }
    
    public String getServiceKey() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        return prefs.getString("supabase_service_key", SUPABASE_SERVICE_KEY);
    }
    
    /**
     * 設置 Supabase 配置（首次使用時調用）
     */
    public void setConfig(String url, String anonKey, String serviceKey) {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("supabase_url", url);
        editor.putString("supabase_anon_key", anonKey);
        editor.putString("supabase_service_key", serviceKey);
        editor.apply();
        Log.d(TAG, "Supabase 配置已保存");
    }
    
    /**
     * 創建帶認證的請求
     */
    public Request.Builder createAuthenticatedRequest(String endpoint) {
        String url = getSupabaseUrl() + endpoint;
        String token = getAccessToken();
        
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("apikey", getAnonKey())
                .addHeader("Content-Type", "application/json");
        
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        
        return builder;
    }
    
    /**
     * 獲取當前用戶的訪問令牌
     */
    public String getAccessToken() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        return prefs.getString("supabase_access_token", null);
    }
    
    /**
     * 保存訪問令牌
     */
    public void saveAccessToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("supabase_access_token", token);
        editor.apply();
    }
    
    /**
     * 清除訪問令牌
     */
    public void clearAccessToken() {
        SharedPreferences prefs = context.getSharedPreferences("FCUPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("supabase_access_token");
        editor.apply();
    }
    
    public OkHttpClient getClient() {
        return client;
    }
    
    public Gson getGson() {
        return gson;
    }
    
    /**
     * 執行 GET 請求
     */
    public String executeGet(String endpoint) throws IOException {
        Request request = createAuthenticatedRequest(endpoint).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
    
    /**
     * 執行 POST 請求
     */
    public String executePost(String endpoint, JsonObject body) throws IOException {
        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON);
        Request request = createAuthenticatedRequest(endpoint)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("Unexpected code " + response + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }
    
    /**
     * 執行 PUT 請求
     */
    public String executePut(String endpoint, JsonObject body) throws IOException {
        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON);
        Request request = createAuthenticatedRequest(endpoint)
                .put(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("Unexpected code " + response + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }
    
    /**
     * 執行 PATCH 請求
     */
    public String executePatch(String endpoint, JsonObject body) throws IOException {
        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON);
        Request request = createAuthenticatedRequest(endpoint)
                .patch(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("Unexpected code " + response + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }
    
    /**
     * 執行 DELETE 請求
     */
    public String executeDelete(String endpoint) throws IOException {
        Request request = createAuthenticatedRequest(endpoint).delete().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("Unexpected code " + response + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }
}

