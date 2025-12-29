package fcu.app.appclassfinalproject.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient

object SupabaseManager {

    // TODO: 請替換為您的 Supabase 專案 URL 和 API Key
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_KEY"

    private var client: SupabaseClient? = null

    fun getClient(): SupabaseClient {
        if (client == null) {
            // Supabase Kotlin SDK 2.0.0 會根據 classpath 中的依賴自動安裝模組
            // 不需要手動 install，只需要創建客戶端即可
            client = createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_KEY
            ) {
                // 模組會根據依賴自動安裝
            }
        }
        return client!!
    }
}

