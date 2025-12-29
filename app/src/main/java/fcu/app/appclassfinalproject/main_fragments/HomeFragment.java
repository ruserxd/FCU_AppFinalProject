package fcu.app.appclassfinalproject.main_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.adapter.ProjectAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.Project;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private static final String TAG = "HomeFragment";

  private String mParam1;
  private String mParam2;

  private RecyclerView recyclerView;
  private TextView tvName;
  private ProjectAdapter adapter;
  private List<Project> projectList;
  private SupabaseProjectHelper supabaseHelper;

  public HomeFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment HomeFragment.
   */
  public static HomeFragment newInstance(String param1, String param2) {
    HomeFragment fragment = new HomeFragment();
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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // 初始化 UI 元件
    recyclerView = view.findViewById(R.id.recycle);
    tvName = view.findViewById(R.id.tv_name);
    projectList = new ArrayList<>();
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    // 獲取當前用戶資訊
    SharedPreferences prefs = requireContext().getSharedPreferences("FCUPrefs", MODE_PRIVATE);
    String email = prefs.getString("email", "使用者");

    // 轉換為小寫以確保查找一致性
    String normalizedEmail = email.toLowerCase().trim();
    Log.d(TAG, "Original email: '" + email + "', Normalized email: '" + normalizedEmail + "'");

    tvName.setText(email); // 顯示原始 email

    // TODO: 使用 Supabase 獲取專案列表
    supabaseHelper = new SupabaseProjectHelper();
    projectList = new ArrayList<>();
    
    // 暫時顯示空列表
    Log.d(TAG, "專案列表功能待實現（使用 Supabase）");

    // 設定適配器
    adapter = new ProjectAdapter(getContext(), projectList);
    recyclerView.setAdapter(adapter);
  }

  /**
   * 刷新專案列表
   */
  public void refreshProjectList() {
    // TODO: 使用 Supabase 刷新專案列表
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
    Log.d(TAG, "專案列表刷新功能待實現（使用 Supabase）");
  }

  @Override
  public void onResume() {
    super.onResume();
    // 當 Fragment 重新顯示時刷新專案列表
    refreshProjectList();
  }
}