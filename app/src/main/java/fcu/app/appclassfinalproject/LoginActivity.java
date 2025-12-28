package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import fcu.app.appclassfinalproject.supabase.AuthHelper;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

  // 元件
  private EditText et_login_email;
  private EditText et_login_password;
  private Button btn_login, btn_translate, btn_google_login;
  private TextView tv_to_register;

  private SharedPreferences prefs;
  private static final String TAG = "LoginActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 設定語言 - 必須在 setContentView 之前
    setLocale();

    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_login);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_login), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 找尋對應 id
    et_login_email = findViewById(R.id.et_login_email);
    et_login_password = findViewById(R.id.et_login_password);
    btn_login = findViewById(R.id.btn_login);
    tv_to_register = findViewById(R.id.tv_to_register);
    btn_translate = findViewById(R.id.btn_translate);
    btn_google_login = findViewById(R.id.btn_google_login);

    // 當按下 "尚未註冊" 文字時
    tv_to_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        intentTo(RegisterActivity.class);
      }
    });

    // 語言切換按鈕
    btn_translate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggleLanguage();
      }
    });

    // Gmail 登入按鈕
    btn_google_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signInWithGoogle();
      }
    });

    // 登入檢測
    btn_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = et_login_email.getText().toString().trim();
        String password = et_login_password.getText().toString();

        // 檢查輸入是否為空
        if (email.isEmpty() || password.isEmpty()) {
          Toast.makeText(LoginActivity.this, getString(R.string.register_all_fields_required),
              Toast.LENGTH_SHORT).show();
          return;
        }

        // 使用 Supabase 進行登入
        new Thread(() -> {
          var result = AuthHelper.INSTANCE.signInWithEmail(email, password);
          runOnUiThread(() -> {
            if (result.isSuccess()) {
              Toast.makeText(LoginActivity.this, getString(R.string.login_success),
                  Toast.LENGTH_SHORT).show();
              intentTo(HomeActivity.class);
              finish();
            } else {
              String errorMessage = getString(R.string.login_failed);
              if (result.getExceptionOrNull() != null) {
                String error = result.getExceptionOrNull().getMessage();
                if (error != null) {
                  if (error.contains("Invalid login credentials")) {
                    errorMessage = getString(R.string.password_incorrect);
                  } else if (error.contains("Email not confirmed")) {
                    errorMessage = "請先驗證您的電子郵件";
                  }
                }
              }
              Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
          });
        }).start();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    // 檢查用戶是否已經登入
    new Thread(() -> {
      if (AuthHelper.INSTANCE.isLoggedIn()) {
        runOnUiThread(() -> {
          intentTo(HomeActivity.class);
        });
      }
    }).start();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 檢查用戶是否已經登入
    new Thread(() -> {
      if (AuthHelper.INSTANCE.isLoggedIn()) {
        runOnUiThread(() -> {
          intentTo(HomeActivity.class);
        });
      }
    }).start();
  }

  // 切換至 "指定" 頁面
  private void intentTo(Class<?> page) {
    Intent intent = new Intent();
    intent.setClass(LoginActivity.this, page);
    startActivity(intent);
  }

  // 設定語言
  private void setLocale() {
    prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    String language = prefs.getString("language", "zh");

    Locale locale = new Locale(language);
    Locale.setDefault(locale);

    Configuration config = new Configuration();
    config.setLocale(locale);
    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
  }

  // 切換語言
  private void toggleLanguage() {
    prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    String currentLang = prefs.getString("language", "zh");
    Log.d(TAG, "Current language: " + currentLang);

    String newLang = currentLang.equals("zh") ? "en" : "zh";
    Log.d(TAG, "Switching to language: " + newLang);

    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("language", newLang);
    editor.apply();

    // 重新啟動 Activity 以套用新語言
    recreate();
  }

  // Gmail 登入
  private void signInWithGoogle() {
    btn_google_login.setEnabled(false);
    Toast.makeText(this, "正在使用 Gmail 登入...", Toast.LENGTH_SHORT).show();

    new Thread(() -> {
      var result = AuthHelper.INSTANCE.signInWithGoogle();
      runOnUiThread(() -> {
        btn_google_login.setEnabled(true);
        if (result.isSuccess()) {
          String url = result.getOrNull();
          if (url != null) {
            // 打開瀏覽器進行 OAuth 認證
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(intent);
            Toast.makeText(LoginActivity.this, "請在瀏覽器中完成 Gmail 登入", Toast.LENGTH_LONG).show();
          }
        } else {
          Toast.makeText(LoginActivity.this, "Gmail 登入失敗: " + 
              (result.getExceptionOrNull() != null ? result.getExceptionOrNull().getMessage() : "未知錯誤"),
              Toast.LENGTH_SHORT).show();
        }
      });
    }).start();
  }
}