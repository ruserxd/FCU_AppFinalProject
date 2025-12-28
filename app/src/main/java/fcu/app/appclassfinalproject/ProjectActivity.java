package fcu.app.appclassfinalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.main_fragments.AddIssueFragment;
import fcu.app.appclassfinalproject.main_fragments.ProjectInfoFragment;
import fcu.app.appclassfinalproject.main_fragments.SettingsFragment;

public class ProjectActivity extends AppCompatActivity {

  private static final String TAG = "ProjectActivity";
  private int currentProjectId = -1;
  private String currentProjectName = "";
  private int currentUserId = -1;
  private SupabaseProjectHelper supabaseProjectHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_project);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_project);
    Fragment projectInfoFragment = ProjectInfoFragment.newInstance("", "");
    Fragment settingsFragment = SettingsFragment.newInstance("", "");
    Fragment addIssueFragment = AddIssueFragment.newInstance("", "");
    setCurrentFragment(projectInfoFragment);

    supabaseProjectHelper = new SupabaseProjectHelper();
    loadCurrentProjectInfo();
    loadCurrentUserInfo();

    bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_project_issues) {
          setCurrentFragment(projectInfoFragment);
        } else if (item.getItemId() == R.id.menu_projct_back) {
          intentTo(HomeActivity.class);
        } else if (item.getItemId() == R.id.menu_project_add) {
          setCurrentFragment(addIssueFragment);
        } else if (item.getItemId() == R.id.menu_project_setting) {
          setCurrentFragment(settingsFragment);
        } else if (item.getItemId() == R.id.menu_project_delete) {
          showDeleteProjectConfirmDialog();
        }
        return true;
      }
    });
  }

  /**
   * 載入當前用戶資訊
   */
  private void loadCurrentUserInfo() {
    // TODO: Get current user from Supabase
  }

  /**
   * 載入當前專案資訊
   */
  private void loadCurrentProjectInfo() {
    // TODO: Get current project from Supabase
  }

  /**
   * 顯示刪除專案確認對話框
   */
  private void showDeleteProjectConfirmDialog() {
    // TODO: Delete project from Supabase
  }

  /**
   * 顯示多語言 Toast
   */
  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void setCurrentFragment(Fragment fragment) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_main_project, fragment)
        .commit();
  }

  private void intentTo(Class<?> page) {
    Intent intent = new Intent();
    intent.setClass(ProjectActivity.this, page);
    startActivity(intent);
  }
}