package fcu.app.appclassfinalproject.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fcu.app.appclassfinalproject.EditIssueActivity;
import fcu.app.appclassfinalproject.R;
import fcu.app.appclassfinalproject.helper.SupabaseProjectHelper;
import fcu.app.appclassfinalproject.model.Issue;
import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

  private List<Issue> issueList;
  private Context context;
  private static final String TAG = "IssueAdapter";
  private SupabaseProjectHelper supabaseProjectHelper;

  public IssueAdapter(Context context, List<Issue> list) {
    this.context = context;
    this.issueList = list;
    this.supabaseProjectHelper = new SupabaseProjectHelper();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_issue, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Issue issue = issueList.get(position);

    // 設定基本資訊
    holder.tvName.setText(issue.getName());
    holder.tvSummary.setText(issue.getSummary());
    holder.tvStatus.setText(issue.getStatus());
    String lang = getCurrentLanguage();
    if (lang.equals("en")) {
      if (issue.getStatus().equals("TO-DO")) {
        holder.tvStatus.setText("未開始");
      }
      if (issue.getStatus().equals("In progress")) {
        holder.tvStatus.setText("進行中");
      }
      if (issue.getStatus().equals("Finished")) {
        holder.tvStatus.setText("已完成");
      }
    }
    if (lang.equals("zh")) {
      if (issue.getStatus().equals("未開始")) {
        holder.tvStatus.setText("TO-DO");
      }
      if (issue.getStatus().equals("進行中")) {
        holder.tvStatus.setText("In progress");
      }
      if (issue.getStatus().equals("已完成")) {
        holder.tvStatus.setText("Finished");
      }
    }

    // 處理負責人顯示
    setDesigneeText(holder, issue);

    // 設定點擊事件
    setClickListener(holder, issue);
  }

  private void setDesigneeText(ViewHolder holder, Issue issue) {
    // TODO: Get designee from Supabase
  }

  private void setClickListener(ViewHolder holder, Issue issue) {
    holder.itemView.setOnClickListener(v -> {
      // TODO: Get issue id from Supabase
    });
  }

  @Override
  public int getItemCount() {
    return issueList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvSummary, tvStatus, tvDesignee;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvName = itemView.findViewById(R.id.tv_issuename);
      tvSummary = itemView.findViewById(R.id.tv_issuesummary);
      tvStatus = itemView.findViewById(R.id.tv_status);
      tvDesignee = itemView.findViewById(R.id.tv_designee);
    }
  }

  private String getCurrentLanguage() {
    return getSharedPrefs().getString("app_language", "zh");
  }

  private SharedPreferences getSharedPrefs() {
    return context.getSharedPreferences("FCUPrefs", MODE_PRIVATE);
  }

}