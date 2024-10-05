package com.example.swifttalk.chats;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swifttalk.R;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.example.swifttalk.logic.utils.Utils;
import com.google.firebase.Timestamp;
import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {

  private Context context;
  private List<Chat> chats;

  public ChatsAdapter(Context context, List<Chat> chats) {
    this.context = context;
    this.chats = chats;
  }

  @NonNull
  @Override
  public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
    return new ChatViewHolder(LayoutInflater.from(context).inflate(
      R.layout.fr_external_chat,
      viewGroup,
      false
    ));
  }

  @Override
  public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
    final Chat chat = chats.get(i);
    chatViewHolder.nameView.setText(chat.getUserCover());
    chatViewHolder.userCoverView.setImageResource(R.drawable.user_cover);

    //TODO ADD EVENT LISTENER TO OPEN CHAT ACTIVITY

    if(chat.getLastMessage() == null) {
      chatViewHolder.lastMessageView.setText("");
      chatViewHolder.timestampView.setText("");
      return;
    }

    String lastMessage = chat.getLastMessage().getContext();
    Timestamp timestamp = chat.getLastMessage().getTimestamp();
    String dateToShow = Utils.isOlderThan(timestamp, 1)
      ? Utils.getDate(timestamp.toDate())
      : Utils.getDateWithTime(timestamp.toDate());
    final Date messageDate = timestamp.toDate();

    chatViewHolder.lastMessageView.setText(lastMessage);
    chatViewHolder.timestampView.setText(dateToShow);
  }

  @Override
  public int getItemCount() {
    return chats.size();
  }
}
