package fcu.app.appclassfinalproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;

public class EditIssueActivity extends AppCompatActivity {

  private EditText edName;
  private EditText edSummary;
  private EditText edStartTime;
  private EditText edEndTime;
  private Spinner spin_Status;
  private EditText edDesignee;
  private Button btnSave;
  private Button btnCancel;
  private MaterialButton btnDelete;

  int id;
  String[] items = {"未開始", "進行中", "已完成"};
  String[] itemsEN = {"TO-DO", "In progress", "Finished"};


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_edit_issue);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    edName = findViewById(R.id.ed_name);
    edSummary = findViewById(R.id.ed_summary);
    edStartTime = findViewById(R.id.ed_start_time);
    edEndTime = findViewById(R.id.ed_end_time);
    spin_Status = findViewById(R.id.spin_EditStatus);
    btnDelete = findViewById(R.id.ed_btn_delete);
    btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());

    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        EditIssueActivity.this, // 或 requireContext()
        android.R.layout.simple_spinner_item,
        getCurrentLanguage().equals("en") ? items : itemsEN // String[] 陣列或 List<String>
    );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spin_Status.setAdapter(adapter);

    edDesignee = findViewById(R.id.ed_designee);

    btnSave = findViewById(R.id.ed_btn_save);
    btnCancel = findViewById(R.id.ed_btn_cancel);

    SupabaseProjectHelper supabaseProjectHelper = new SupabaseProjectHelper();
    // TODO: Get issue from Supabase

    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO: Update issue in Supabase
      }
    });

    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clearFields();
        back();
      }
    });
  }

  private void back() {
    SharedPreferences prefs = getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putInt("issue_Id", -1);
    editor.apply();
    Intent intent = new Intent(EditIssueActivity.this, ProjectActivity.class);
    startActivity(intent);
  }

  private void clearFields() {
    edName.setText("");
    edSummary.setText("");
    edStartTime.setText("");
    edEndTime.setText("");
    spin_Status.setSelection(0);
    edDesignee.setText("");
  }

  private String getCurrentLanguage() {
    return getSharedPrefs().getString("app_language", "zh");
  }

  private SharedPreferences getSharedPrefs() {
    return this.getSharedPreferences("FCUPrefs", MODE_PRIVATE);
  }

  private void showDeleteConfirmDialog() {
    String issueName = edName.getText().toString().trim();
    if (issueName.isEmpty()) {
      issueName = "_";
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.delete_issue_title));
    builder.setMessage(getString(R.string.delete_issue_message, issueName));
    builder.setIcon(android.R.drawable.ic_dialog_alert);

    builder.setPositiveButton(getString(R.string.delete_issue_confirm), (dialog, which) -> {
      deleteIssue();
    });

    builder.setNegativeButton(getString(R.string.delete_issue_cancel), (dialog, which) -> {
      dialog.dismiss();
    });

    AlertDialog dialog = builder.create();
    dialog.show();

    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
        getResources().getColor(android.R.color.holo_red_dark));
  }

  // 刪除 Issue 方法
  private void deleteIssue() {
    // TODO: Delete issue from Supabase
  }
}