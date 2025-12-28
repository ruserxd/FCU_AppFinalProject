package fcu.app.appclassfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import fcu.app.appclassfinalproject.adapter.IssueMonthAdapter;
import fcu.app.appclassfinalproject.adapter.IssueNameAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.IssueMonth;
import fcu.app.appclassfinalproject.model.IssueName;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GanttActivity extends AppCompatActivity {

  private RecyclerView issueRecyclerView;
  private RecyclerView monthRecyclerView;
  private IssueNameAdapter issueNameAdapter;
  private IssueMonthAdapter issueMonthAdapter;
  private List<IssueName> issueNameList;
  private List<IssueMonth> issueMonthList;
  private MaterialButton btnBackToIssueList;
  private TextView tvIssueName;
  private TextView tvToday;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_gantt);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    issueRecyclerView = findViewById(R.id.rcv_ganttIssue);
    monthRecyclerView = findViewById(R.id.rcv_ganttMonth);
    btnBackToIssueList = findViewById(R.id.btn_backToIssueList);
    tvIssueName = findViewById(R.id.tv_issueName);
    tvToday = findViewById(R.id.tv_nowDate);
    issueNameList = new ArrayList<>();
    issueMonthList = new ArrayList<>();
    issueRecyclerView.setLayoutManager(new LinearLayoutManager(GanttActivity.this));
    monthRecyclerView.setLayoutManager(new LinearLayoutManager(GanttActivity.this));
    SharedPreferences prefs = GanttActivity.this.getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    int project_id = prefs.getInt("project_id", 0);
    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    tvToday.setText(today);
    SupabaseProjectHelper supabaseProjectHelper = new SupabaseProjectHelper();
    // TODO: Get project and issues from Supabase

    btnBackToIssueList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        intentTo(ProjectActivity.class);
      }
    });

  }

  private void intentTo(Class<?> page) {
    Intent intent = new Intent();
    intent.setClass(GanttActivity.this, page);
    startActivity(intent);
  }
}