package fcu.app.appclassfinalproject.supabase

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.OAuthProvider
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AuthHelper {

    private val client = SupabaseManager.getClient()

    /**
     * 使用電子郵件和密碼登入
     */
    fun signInWithEmail(email: String, password: String): Result<Unit> {
        return runBlocking {
            try {
                client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 使用電子郵件和密碼註冊
     */
    fun signUpWithEmail(email: String, password: String, account: String? = null): Result<Unit> {
        return runBlocking {
            try {
                client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("account", account ?: email.split("@")[0])
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 使用 Gmail OAuth 登入
     */
    fun signInWithGoogle(): Result<String> {
        return runBlocking {
            try {
                // TODO: 實現 Gmail OAuth 登入
                // 需要配置 Supabase OAuth 設定
                Result.failure(Exception("Gmail OAuth 功能待實現"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 檢查是否已登入
     */
    fun isLoggedIn(): Boolean {
        return runBlocking {
            try {
                client.auth.currentSessionOrNull() != null
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 登出
     */
    fun signOut(): Result<Unit> {
        return try {
            runBlocking {
                // Change SupabaseClientManager.client to just client
                client.auth.signOut()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 獲取當前用戶 ID
     */
    fun getCurrentUserId(): String? {
        return runBlocking {
            try {
                client.auth.currentSessionOrNull()?.user?.id
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * 獲取當前用戶電子郵件
     */
    fun getCurrentUserEmail(): String? {
        return runBlocking {
            try {
                client.auth.currentSessionOrNull()?.user?.email
            } catch (e: Exception) {
                null
            }
        }
    }
}

