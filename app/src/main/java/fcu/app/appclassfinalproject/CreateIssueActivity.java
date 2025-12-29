package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// TODO: 使用 Supabase 實現議題建立功能

public class CreateIssueActivity extends AppCompatActivity {

  private EditText etName;
  private EditText etSummary;
  private EditText etStartTime;
  private EditText etEndTime;
  private EditText etStatus;
  private EditText etDesignee;
  private ImageButton btnHome;
  private ImageButton btnSave;

  // TODO: 使用 Supabase 實現議題建立功能

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_create_issue);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // TODO: 初始化 Supabase

    //找對應id
    etName = findViewById(R.id.et_name);
    etSummary = findViewById(R.id.et_summary);
    etStartTime = findViewById(R.id.et_start_time);
    etEndTime = findViewById(R.id.et_endtime);
    etStatus = findViewById(R.id.et_status);
    etDesignee = findViewById(R.id.et_designee);

    btnHome = findViewById(R.id.cr_btn_back);
    btnSave = findViewById(R.id.cr_btn_save);

//      TODO:補preferences
//        SharedPreferences prefs = getSharedPreferences("", MODE_PRIVATE);
//        String projectId = prefs.getString("projectId",null);

    // 假設 sharedPreference 有　"projectId"
    btnHome.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(CreateIssueActivity.this, ProjectActivity.class);
        startActivity(intent);
      }
    });

    // 假設 sharedPreference 有　"projectId"
    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        String name = etName.getText().toString().trim();
        String summary = etSummary.getText().toString().trim();
        String start_time = etStartTime.getText().toString().trim();
        String end_time = etEndTime.getText().toString().trim();
//               TODO:確認有無狀態及被指派者
//                String status = etStatus.getText().toString().trim();
//                String designee = etDesignee.getText().toString().trim();

        // TODO: 使用 Supabase 建立議題
        Toast.makeText(CreateIssueActivity.this, "議題建立功能待實現（使用 Supabase）", Toast.LENGTH_LONG).show();


      }
    });
  }

  private void clearFields() {
    etName.setText("");
    etSummary.setText("");
    etStartTime.setText("");
    etEndTime.setText("");
  }

}