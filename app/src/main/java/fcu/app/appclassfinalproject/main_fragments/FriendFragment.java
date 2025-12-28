package fcu.app.appclassfinalproject.main_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.adapter.FriendAdapter;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private static final String TAG = "FriendFragment";

  private RecyclerView recyclerView;
  private List<User> friendList;
  private FriendAdapter adapter;
  private String mParam1;
  private String mParam2;

  public FriendFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment friend.
   */
  public static FriendFragment newInstance(String param1, String param2) {
    FriendFragment fragment = new FriendFragment();
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
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_friend, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // 初始化 RecyclerView
    recyclerView = view.findViewById(R.id.rcy_friends);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    friendList = new ArrayList<>();

    // 獲取當前用戶 ID
    SupabaseProjectHelper supabaseHelper = new SupabaseProjectHelper();
    String currentUserId = supabaseHelper.getCurrentUserId();

    if (currentUserId == null || currentUserId.isEmpty()) {
      Toast.makeText(requireContext(), "請先登入", Toast.LENGTH_SHORT).show();
    } else {
      // 載入真實的朋友列表
      loadFriendsList(currentUserId);
    }

    // 配置 Adapter
    adapter = new FriendAdapter(getContext(), friendList);
    adapter.setFriendFragment(this); // 設置 Fragment 引用
    recyclerView.setAdapter(adapter);
  }

  private void loadFriendsList(String currentUserId) {
    // TODO: 使用 Supabase 獲取好友列表
    friendList.clear();
    adapter.notifyDataSetChanged();
    Toast.makeText(requireContext(), "好友列表功能待實現（使用 Supabase）", Toast.LENGTH_SHORT).show();
    Log.d(TAG, "好友列表功能待實現（使用 Supabase）");
  }

  /**
   * 刪除好友
   */
  public void removeFriend(User friend, int position) {
    // TODO: 使用 Supabase 刪除好友
    Toast.makeText(requireContext(), "刪除好友功能待實現（使用 Supabase）", Toast.LENGTH_SHORT).show();
    
    // 暫時從列表中移除
    friendList.remove(position);
    if (adapter != null) {
      adapter.notifyItemRemoved(position);
      adapter.notifyItemRangeChanged(position, friendList.size());
    }
  }
}