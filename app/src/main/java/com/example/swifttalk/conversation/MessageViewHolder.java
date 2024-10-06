package com.example.swifttalk.conversation;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.R;
import org.jetbrains.annotations.NotNull;

public class MessageViewHolder extends RecyclerView.ViewHolder {
  TextView messageContext, messageTimestamp;
  ImageView messageImage;

  public MessageViewHolder(@NotNull View itemView) {
    super(itemView);

    messageContext = itemView.findViewById(R.id.messageContext);
    messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
    messageImage = itemView.findViewById(R.id.messageImage);
  }
}
