package fcu.app.appclassfinalproject.main_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import java.util.Calendar;
import java.util.List;

public class AddIssueFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private static final String TAG = "AddIssueFragment";

  private String mParam1;
  private String mParam2;
  private EditText etPurpose;
  private EditText etOverview;
  private EditText etStartTime;
  private EditText etEndTime;
  private AutoCompleteTextView spiStatus;
  private AutoCompleteTextView actvDesignee;

  private Button btnSave;

  String[] items = {"未開始", "進行中", "已完成"};
  String[] itemsEN = {"TO-DO", "In progress", "Finished"};
  private SupabaseProjectHelper supabaseHelper;
  private int currentProjectId = -1;

  public AddIssueFragment() {
    // Required empty public constructor
  }

  public static AddIssueFragment newInstance(String param1, String param2) {
    AddIssueFragment fragment = new AddIssueFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  private void showDatePickerDialog(final EditText editText) {
    // 獲取當前日期
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    // 創建DatePickerDialog
    DatePickerDialog datePickerDialog = new DatePickerDialog(
        requireContext(),
        new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // 格式化日期為 yyyy-MM-dd
            String selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1,
                dayOfMonth);
            editText.setText(selectedDate);
          }
        },
        year, month, day);

    datePickerDialog.show();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_issue, container, false);

    // 初始化 Supabase
    supabaseHelper = new SupabaseProjectHelper();

    // 獲取當前專案ID
    SharedPreferences prefs = view.getContext().getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    currentProjectId = prefs.getInt("project_id", -1);
    Log.d(TAG, "Current project ID: " + currentProjectId);

    if (currentProjectId == -1) {
      Toast.makeText(getContext(), "錯誤：無法獲取專案資訊", Toast.LENGTH_LONG).show();
      Log.e(TAG, "No project ID found in SharedPreferences");
      return view;
    }

    // 初始化UI元件
    initializeViews(view);

    // 設定事件監聽器
    setupEventListeners(view);

    // 設定適配器
    setupAdapters();

    return view;
  }

  private void initializeViews(View view) {
    etPurpose = view.findViewById(R.id.et_purpose);
    etOverview = view.findViewById(R.id.et_overview);
    etStartTime = view.findViewById(R.id.et_start_time);
    etEndTime = view.findViewById(R.id.et_endtime);
    actvDesignee = view.findViewById(R.id.actv_designee);
    spiStatus = view.findViewById(R.id.spin_status);
    btnSave = view.findViewById(R.id.btn_save);
  }

  private void setupEventListeners(View view) {
    // 日期選擇器
    etStartTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDatePickerDialog(etStartTime);
      }
    });

    etEndTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDatePickerDialog(etEndTime);
      }
    });

    // 儲存按鈕
    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        handleSaveIssue();
      }
    });
  }

  private void setupAdapters() {
    // 設定狀態選擇的適配器
    String[] statusItems = getCurrentLanguage().equals("zh") ? items : itemsEN;
    ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
        requireContext(),
        android.R.layout.simple_dropdown_item_1line,
        statusItems
    );
    spiStatus.setAdapter(statusAdapter);

    // 設定狀態選擇的點擊事件
    spiStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedStatus = statusItems[position];
        Log.d(TAG, "Selected status: " + selectedStatus);
      }
    });

    // 設定指派人員 - 只顯示專案成員
    String[] projectMemberList = getProjectMemberList(currentProjectId);
    Log.d(TAG, "Available project members: " + java.util.Arrays.toString(projectMemberList));

    if (projectMemberList.length == 0) {
      Toast.makeText(getContext(), "警告：此專案沒有成員，無法建立議題", Toast.LENGTH_LONG).show();
      btnSave.setEnabled(false);
      return;
    }

    ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(requireContext(),
        android.R.layout.simple_dropdown_item_1line, projectMemberList);
    actvDesignee.setAdapter(memberAdapter);

    // 顯示提示訊息
    Toast.makeText(getContext(),
        "議題只能指派給專案成員（共 " + projectMemberList.length + " 位）",
        Toast.LENGTH_SHORT).show();
  }

  private void handleSaveIssue() {
    String name = etPurpose.getText().toString().trim();
    String summary = etOverview.getText().toString().trim();
    String start_time = etStartTime.getText().toString().trim();
    String end_time = etEndTime.getText().toString().trim();
    String status = spiStatus.getText().toString().trim();
    String designee = actvDesignee.getText().toString().trim();

    Log.d(TAG, "Attempting to save issue:");
    Log.d(TAG, "Name: " + name);
    Log.d(TAG, "Summary: " + summary);
    Log.d(TAG, "Start time: " + start_time);
    Log.d(TAG, "End time: " + end_time);
    Log.d(TAG, "Status: " + status);
    Log.d(TAG, "Designee: " + designee);
    Log.d(TAG, "Project ID: " + currentProjectId);

    // 驗證必填欄位
    if (name.isEmpty() || summary.isEmpty() || start_time.isEmpty() ||
        end_time.isEmpty() || status.isEmpty() || designee.isEmpty()) {
      Toast.makeText(getContext(), "請填寫所有必填欄位", Toast.LENGTH_SHORT).show();
      return;
    }

    // 驗證指派者是否為專案成員
    if (!isValidProjectMember(designee, currentProjectId)) {
      Toast.makeText(getContext(), "錯誤：只能指派給專案成員", Toast.LENGTH_SHORT).show();
      Log.e(TAG,
          "Invalid designee: " + designee + " is not a member of project " + currentProjectId);
      return;
    }

    // 驗證日期
    if (!isValidDateRange(start_time, end_time)) {
      Toast.makeText(getContext(), "錯誤：結束時間不能早於開始時間", Toast.LENGTH_SHORT).show();
      return;
    }

    // TODO: 使用 Supabase 建立議題
    Toast.makeText(getContext(), "議題建立功能待實現（使用 Supabase）", Toast.LENGTH_SHORT).show();
    Log.d(TAG, "議題建立功能待實現（使用 Supabase）");
  }

  /**
   * 獲取專案成員列表
   */
  public String[] getProjectMemberList(int projectId) {
    // TODO: 使用 Supabase 獲取專案成員列表
    if (projectId == -1) {
      Log.e(TAG, "Invalid project ID");
      return new String[0];
    }
    return new String[0];
  }

  /**
   * 驗證指派者是否為專案成員
   */
  private boolean isValidProjectMember(String account, int projectId) {
    // TODO: 使用 Supabase 驗證指派者是否為專案成員
    return false;
  }

  /**
   * 根據帳號獲取用戶ID
   */
  private int getUserIdByAccount(String account) {
    // TODO: 使用 Supabase 根據帳號獲取用戶ID
    return -1;
  }

  /**
   * 驗證日期範圍
   */
  private boolean isValidDateRange(String startDate, String endDate) {
    try {
      return startDate.compareTo(endDate) <= 0;
    } catch (Exception e) {
      Log.e(TAG, "Error validating date range: " + e.getMessage());
      return false;
    }
  }

  private void clearFields() {
    etPurpose.setText("");
    etOverview.setText("");
    etStartTime.setText("");
    etEndTime.setText("");
    spiStatus.setText("");
    actvDesignee.setText("");
    Log.d(TAG, "Form fields cleared");
  }

  private void setCurrentFragment(Fragment fragment) {
    getParentFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_main_project, fragment)
        .commit();
  }

  private String getCurrentLanguage() {
    return getSharedPrefs().getString("app_language", "zh");
  }

  private SharedPreferences getSharedPrefs() {
    return requireActivity().getSharedPreferences("FCUPrefs", MODE_PRIVATE);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // Supabase 不需要手動關閉連接
    Log.d(TAG, "Fragment destroyed");
  }
}