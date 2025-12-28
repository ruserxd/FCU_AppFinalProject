package fcu.app.appclassfinalproject.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseManager {

    // TODO: 請替換為您的 Supabase 專案 URL 和 API Key
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_KEY"

    private var client: SupabaseClient? = null

    fun getClient(): SupabaseClient {
        if (client == null) {
            client = createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_KEY
            ) {
                install(GoTrue)
                install(Postgrest)
                install(Storage)
                install(Realtime)
            }
        }
        return client!!
    }
}

