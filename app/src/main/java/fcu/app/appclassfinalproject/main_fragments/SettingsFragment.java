package fcu.app.appclassfinalproject.main_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fcu.app.appclassfinalproject.ChatRoomListActivity;
import fcu.app.appclassfinalproject.ExportExcel;
import fcu.app.appclassfinalproject.LoginActivity;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.supabase.AuthHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;

public class SettingsFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private static final String TAG = "SettingsFragment";
  private Button btn_logout, btn_userFriend, btn_add_friend, btn_export_excel, btnChangeLanguage, btnProjectNumber, btnGithub, btn_del_account, btn_chatrooms;

  private String mParam1;
  private String mParam2;

  public SettingsFragment() {
    // Required empty public constructor
  }

  public static SettingsFragment newInstance(String param1, String param2) {
    SettingsFragment fragment = new SettingsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    initViews(view);

    // 設定按鈕點擊事件
    setupClickListeners();
  }

  private void initViews(View view) {
    btn_logout = view.findViewById(R.id.btn_logout);
    btn_userFriend = view.findViewById(R.id.btn_userFriends);
    btn_add_friend = view.findViewById(R.id.btn_add_friend);
    btn_export_excel = view.findViewById(R.id.btn_excel);
    btnChangeLanguage = view.findViewById(R.id.btn_changeLanguage);
    btnProjectNumber = view.findViewById(R.id.btn_projectNumber);
    btnProjectNumber.setText(getString(R.string.setting_countporject, getCurrentProjectCount()));
    btnGithub = view.findViewById(R.id.btn_github);
    btn_del_account = view.findViewById(R.id.btn_delAccount);
    btn_chatrooms = view.findViewById(R.id.btn_chatrooms);
  }

  private void setupClickListeners() {
    // 登出按鈕
    btn_logout.setOnClickListener(v -> logout());

    // 好友列表
    btn_userFriend.setOnClickListener(v -> navigateToFragment(new FriendFragment()));

    // 新增好友
    btn_add_friend.setOnClickListener(v -> navigateToFragment(new AddFriendFragment()));

    // 匯出 Excel
    btn_export_excel.setOnClickListener(v -> exportExcel());

    // 切換語言
    btnChangeLanguage.setOnClickListener(v -> changeLanguageSetting());

    //匯入GitHub專案
    btnGithub.setOnClickListener(v -> GithubInsert());

    // 刪除帳號
    btn_del_account.setOnClickListener(v -> showDeleteAccountConfirmDialog());

    // 聊天室
    btn_chatrooms.setOnClickListener(v -> {
      Intent intent = new Intent(requireActivity(), ChatRoomListActivity.class);
      startActivity(intent);
    });
  }

  private void logout() {
    new Thread(() -> {
      var result = AuthHelper.INSTANCE.signOut();
      requireActivity().runOnUiThread(() -> {
        if (result.isSuccess()) {
          getSharedPrefs().edit().clear().apply();
          Log.d(TAG, "用戶已登出");
          showToast(getString(R.string.logout_success));
          
          // 回到登入頁面
          Intent intent = new Intent(requireActivity(), LoginActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);
          requireActivity().finish();
        } else {
          showToast("登出失敗: " + (result.getExceptionOrNull() != null ? result.getExceptionOrNull().getMessage() : "未知錯誤"));
        }
      });
    }).start();
  }

  private void navigateToFragment(Fragment fragment) {
    requireActivity().getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_main, fragment)
        .addToBackStack(null)
        .commit();
  }

  private void exportExcel() {
    // TODO: 使用 Supabase 實現 Excel 匯出功能
    Toast.makeText(getContext(), "Excel 匯出功能待實現（使用 Supabase）", Toast.LENGTH_SHORT).show();
  }

  private void changeLanguageSetting() {
    String currentLang = getCurrentLanguage();
    Log.d(TAG, "當前語言: " + currentLang);
    String newLang = currentLang.equals("zh") ? "en" : "zh";
    Log.d(TAG, "切換語言: " + newLang);

    Locale locale = new Locale(newLang);
    Locale.setDefault(locale);

    Configuration config = new Configuration();
    config.setLocale(locale);
    requireActivity().getResources().updateConfiguration(config,
        requireActivity().getResources().getDisplayMetrics());

    saveLanguage(newLang);
    showToast(getString(R.string.changeLanguage_success));
    updateLanguageAndReload(newLang);
  }

  // 預設中文
  private String getCurrentLanguage() {
    return getSharedPrefs().getString("app_language", "zh");
  }

  @SuppressLint("ApplySharedPref")
  private void saveLanguage(String language) {
    getSharedPrefs().edit().putString("app_language", language).commit();
  }

  private SharedPreferences getSharedPrefs() {
    return requireActivity().getSharedPreferences("FCUPrefs", MODE_PRIVATE);
  }

  private void updateLanguageAndReload(String language) {
    // 整個 activity 重來，延遲避免未更新就跳轉頁面
    new android.os.Handler().postDelayed(() -> {
      Intent intent = new Intent(requireActivity(), requireActivity().getClass());
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      requireActivity().finish();
    }, 500);
  }

  /**
   * 獲取當前用戶參與的專案數量
   */
  private String getCurrentProjectCount() {
    // TODO: 使用 Supabase 獲取專案數量
    return "0";
  }

  private void GithubInsert() {
    // TODO: 使用 Supabase 實現 GitHub 匯入功能
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle(getString(R.string.github_input_title));

    final EditText input = new EditText(requireContext());
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    builder.setView(input);

    builder.setPositiveButton(getString(R.string.github_confirm), (dialog, which) -> {
      String username = input.getText().toString().trim();
      if (!username.isEmpty()) {
        fetchGithub(username);
      }
    });

    builder.setNegativeButton(getString(R.string.github_cancel),
        (dialog, which) -> dialog.cancel());
    builder.show();
  }

  // 抓取 GitHub API 上的資訊透過獲取 JSON -> name 的資訊
  private void fetchGithub(String username) {
    CompletableFuture
        .supplyAsync(() -> {
          try {
            return fetchGithubRepos(username);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .thenAccept(this::saveReposToDatabase)
        .thenRun(() -> {
          showToast(getString(R.string.github_import_success));
          // 更新專案計數
          updateProjectCount();
        })
        .exceptionally(throwable -> {
          showToast(getString(R.string.github_import_error, throwable.getMessage()));
          return null;
        });
  }

  // 從 GitHub API 獲取使用者的所有專案
  private JSONArray fetchGithubRepos(String username) throws Exception {
    URL url = new URL("https://api.github.com/users/" + username + "/repos");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuilder json = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      json.append(line);
    }
    reader.close();

    return new JSONArray(json.toString());
  }

  /**
   * 將 GitHub 專案存入 Supabase
   */
  private void saveReposToDatabase(JSONArray repos) {
    // TODO: 使用 Supabase 實現 GitHub 專案匯入功能
    try {
      Log.d(TAG, "GitHub 匯入功能待實現（使用 Supabase）");
      showToast("GitHub 匯入功能待實現（使用 Supabase）");
    } catch (Exception e) {
      Log.e(TAG, "Error saving GitHub repos: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * 更新專案計數顯示
   */
  private void updateProjectCount() {
    requireActivity().runOnUiThread(() -> {
      if (btnProjectNumber != null) {
        btnProjectNumber.setText(
            getString(R.string.setting_countporject, getCurrentProjectCount()));
      }
    });
  }

  // 顯示 Toast 訊息
  private void showToast(String message) {
    requireActivity().runOnUiThread(() ->
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
  }

  // 顯示刪除帳號確認對話框
  private void showDeleteAccountConfirmDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle(getString(R.string.delete_account_title));
    builder.setMessage(getString(R.string.delete_account_message));
    builder.setIcon(android.R.drawable.ic_dialog_alert);

    builder.setPositiveButton(getString(R.string.delete_account_confirm), (dialog, which) -> {
      showPasswordConfirmDialog();
    });

    builder.setNegativeButton(getString(R.string.delete_account_cancel), (dialog, which) -> {
      dialog.dismiss();
    });

    AlertDialog dialog = builder.create();
    dialog.show();

    // 設定確定按鈕為紅色，表示危險操作
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
        requireContext().getResources().getColor(android.R.color.holo_red_dark));
  }

  // 顯示密碼確認對話框
  private void showPasswordConfirmDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle(getString(R.string.password_confirm_title));
    builder.setMessage(getString(R.string.password_confirm_message));

    final EditText passwordInput = new EditText(requireContext());
    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    passwordInput.setHint(getString(R.string.password_hint));
    builder.setView(passwordInput);

    builder.setPositiveButton(getString(R.string.password_confirm_delete), (dialog, which) -> {
      String password = passwordInput.getText().toString().trim();
      if (password.isEmpty()) {
        showToast(getString(R.string.password_required));
        showPasswordConfirmDialog(); // 重新顯示對話框
      } else {
        deleteAccount(password);
      }
    });

    builder.setNegativeButton(getString(R.string.delete_account_cancel),
        (dialog, which) -> dialog.dismiss());

    AlertDialog dialog = builder.create();
    dialog.show();

    // 設定確定按鈕為紅色
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
        requireContext().getResources().getColor(android.R.color.holo_red_dark));
  }

  // 刪除帳號主要方法
  private void deleteAccount(String password) {
    // 顯示進度提示
    showToast(getString(R.string.deleting_account));

    // 禁用刪除按鈕，避免重複點擊
    btn_del_account.setEnabled(false);

    // TODO: 使用 Supabase 實現帳號刪除功能
    // 1. 驗證密碼
    // 2. 刪除 Supabase 用戶資料
    // 3. 清除本地 SharedPreferences
    
    new Thread(() -> {
      // 暫時實現：清除本地資料並登出
      getSharedPrefs().edit().clear().apply();
      var result = AuthHelper.INSTANCE.signOut();
      
      requireActivity().runOnUiThread(() -> {
        if (result.isSuccess()) {
          showToast(getString(R.string.account_deleted_success));
          navigateToLogin();
        } else {
          showToast(getString(R.string.account_delete_failed));
          btn_del_account.setEnabled(true);
        }
      });
    }).start();
  }

  // TODO: 使用 Supabase 實現用戶資料刪除功能

  private void navigateToLogin() {
    Intent intent = new Intent(requireActivity(), LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    requireActivity().finish();
  }
}