package com.example.swifttalk.search;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.R;
import org.jetbrains.annotations.NotNull;

public class SearchViewHolder extends RecyclerView.ViewHolder {
  TextView userEmail, userName;
  ImageView userCover;

  public SearchViewHolder(@NotNull View itemView) {
    super(itemView);

    userEmail = itemView.findViewById(R.id.user_email);
    userName = itemView.findViewById(R.id.user_name);
    userCover = itemView.findViewById(R.id.user_cover);
  }
}
