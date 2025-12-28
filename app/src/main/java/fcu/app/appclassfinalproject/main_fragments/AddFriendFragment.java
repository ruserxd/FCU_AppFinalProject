package fcu.app.appclassfinalproject.main_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.adapter.AddfriendAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.User;
import java.util.ArrayList;
import java.util.List;

public class AddFriendFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  private String mParam1;
  private String mParam2;
  private RecyclerView addFriendList;
  private List<User> availableUsersList;
  private AddfriendAdapter addfriendAdapter;

  public AddFriendFragment() {
    // Required empty public constructor
  }

  public static AddFriendFragment newInstance(String param1, String param2) {
    AddFriendFragment fragment = new AddFriendFragment();
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
    View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

    // 初始化 RecyclerView
    addFriendList = view.findViewById(R.id.rcy_friend_list);
    availableUsersList = new ArrayList<>();
    addFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

    // 獲取當前用戶 ID
    SupabaseProjectHelper supabaseHelper = new SupabaseProjectHelper();
    String currentUserId = supabaseHelper.getCurrentUserId();

    if (currentUserId == null || currentUserId.isEmpty()) {
      Toast.makeText(requireContext(), "請先登入", Toast.LENGTH_SHORT).show();
      return view;
    }

    // 載入可加入的用戶列表
    loadAvailableUsers(currentUserId);

    return view;
  }

  private void loadAvailableUsers(String currentUserId) {
    // TODO: 使用 Supabase 獲取可加入的用戶列表
    availableUsersList.clear();
    addfriendAdapter = new AddfriendAdapter(getContext(), availableUsersList);
    addFriendList.setAdapter(addfriendAdapter);
    Toast.makeText(requireContext(), "用戶列表功能待實現（使用 Supabase）", Toast.LENGTH_SHORT).show();
    Log.d("AddFriendFragment", "用戶列表功能待實現（使用 Supabase）");
  }
}