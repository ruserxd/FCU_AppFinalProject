package fcu.app.appclassfinalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import fcu.app.appclassfinalproject.supabase.AuthHelper;

public class RegisterActivity extends AppCompatActivity {

  // 元件
  private EditText et_account;
  private EditText et_password;
  private EditText et_email;
  private Button btn_register;
  private TextView tv_to_login;

  private static final String TAG = "RegisterActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_register);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // 找尋對應 id
    et_account = findViewById(R.id.et_register_account);
    et_password = findViewById(R.id.et_register_password);
    et_email = findViewById(R.id.et_register_email);
    btn_register = findViewById(R.id.btn_register);
    tv_to_login = findViewById(R.id.tv_to_register);


    // 設定註冊按鈕
    setupRegisterButton();

    // 切換至登入頁面
    tv_to_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        intentTo(LoginActivity.class);
      }
    });
  }

  // 設定註冊按鈕點擊事件
  private void setupRegisterButton() {
    btn_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String account = et_account.getText().toString().trim();
        String password = et_password.getText().toString();
        String email = et_email.getText().toString().trim();

        // 基本驗證
        if (!validateInput(account, password, email)) {
          return;
        }

        // 檢查網路
        if (!isNetworkAvailable()) {
          Toast.makeText(RegisterActivity.this,
              getString(R.string.register_check_network), Toast.LENGTH_SHORT).show();
          return;
        }

        // 開始註冊
        registerUser(email, password, account);
      }
    });
  }

  // 輸入驗證方法
  private boolean validateInput(String account, String password, String email) {
    if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
      Toast.makeText(this, getString(R.string.register_all_fields_required),
          Toast.LENGTH_SHORT).show();
      return false;
    }

    if (password.length() < 6) {
      Toast.makeText(this, getString(R.string.register_password_min_length),
          Toast.LENGTH_SHORT).show();
      return false;
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(this, getString(R.string.register_email_invalid_format),
          Toast.LENGTH_SHORT).show();
      return false;
    }

    return true;
  }

  // 檢查網路連線
  private boolean isNetworkAvailable() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      NetworkInfo networkInfo = cm.getActiveNetworkInfo();
      return networkInfo != null && networkInfo.isConnected();
    }
    return false;
  }

  // 註冊用戶
  private void registerUser(String email, String password, String account) {
    btn_register.setEnabled(false);
    Toast.makeText(this, getString(R.string.register_registering), Toast.LENGTH_SHORT).show();

    new Thread(() -> {
      var result = AuthHelper.INSTANCE.signUpWithEmail(email, password, account);
      runOnUiThread(() -> {
        btn_register.setEnabled(true);

        if (result.isSuccess()) {
          // 註冊成功
          Log.d(TAG, "Supabase 註冊成功: " + email);
          
          // 保存登入狀態
          String userId = AuthHelper.INSTANCE.getCurrentUserId();
          if (userId != null) {
            saveUserToSharedPreferences(email, userId, account);
            Toast.makeText(RegisterActivity.this,
                getString(R.string.register_success), Toast.LENGTH_SHORT).show();
            intentTo(HomeActivity.class);
          } else {
            Toast.makeText(RegisterActivity.this,
                getString(R.string.register_sync_failed), Toast.LENGTH_LONG).show();
            intentTo(LoginActivity.class);
          }
        } else {
          // 註冊失敗，處理錯誤訊息
          handleRegistrationError(result.getExceptionOrNull());
        }
      });
    }).start();
  }

  // 處理註冊錯誤
  private void handleRegistrationError(Exception exception) {
    String errorMessage = getString(R.string.register_failed);

    if (exception != null) {
      String error = exception.getMessage();
      if (error != null) {
        if (error.contains("already registered") || error.contains("already exists")) {
          errorMessage = getString(R.string.register_email_already_used);
        } else if (error.contains("badly formatted") || error.contains("invalid")) {
          errorMessage = getString(R.string.register_email_badly_formatted);
        } else if (error.contains("weak password") || error.contains("password")) {
          errorMessage = getString(R.string.register_weak_password);
        } else if (error.contains("network")) {
          errorMessage = getString(R.string.register_network_error);
        }
      }
    }

    Log.w(TAG, "註冊失敗", exception);
    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
  }

  // 儲存用戶資料到 SharedPreferences
  private void saveUserToSharedPreferences(String email, String userId, String account) {
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("email", email);
    editor.putString("uid", userId);
    editor.putString("user_account", account);
    editor.putString("user_email", email);
    editor.apply();
  }

  // 切換頁面
  private void intentTo(Class<?> page) {
    Intent intent = new Intent(RegisterActivity.this, page);
    startActivity(intent);
    finish();
  }
}