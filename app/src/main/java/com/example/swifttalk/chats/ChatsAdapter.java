package com.example.swifttalk.chats;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swifttalk.ChatActivity;
import com.example.swifttalk.R;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.example.swifttalk.logic.models.Messages.LastMessage;
import com.example.swifttalk.logic.utils.Utils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {
  FirebaseAuth firebase = FirebaseAuth.getInstance();
  private final String currentUserEmail = firebase.getCurrentUser().getEmail();
  private final Context context;
  private final List<Chat> chats;

  public ChatsAdapter(Context context, List<Chat> chats) {
    this.context = context;
    this.chats = chats;
  }

  @NonNull
  @Override
  public ChatViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
    return new ChatViewHolder(LayoutInflater.from(context).inflate(
      R.layout.fr_external_chat,
      viewGroup,
      false
    ));
  }

  @Override
  public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
    final Chat chat = chats.get(i);
    final LastMessage lastMessage = chat.getLastMessage();

    chatViewHolder.nameView.setText(chat.getUserCover());
    chatViewHolder.userCoverView.setImageResource(R.drawable.user_cover);

//    chatViewHolder.itemView.setOnClickListener(v -> {
//      Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
//      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      intent.putExtra("chat", Parcels.wrap(chat));
//      context.startActivity(intent);
//    });

    if(lastMessage == null) {
      chatViewHolder.lastMessageView.setText("");
      chatViewHolder.timestampView.setText("");
      return;
    }

    String lastMessageContext = lastMessage.getContext();
    if (lastMessageContext.length() > 25) lastMessageContext = lastMessageContext.substring(0, 25) + "...";
    if (lastMessage.getUserEmail().equals(currentUserEmail)) {
      lastMessageContext = "You: " + lastMessageContext;
    }

    Timestamp timestamp = chat.getLastMessage().getTimestamp();
    final Date messageDate = timestamp.toDate();
    String dateToShow = Utils.isOlderThan(timestamp, 1)
      ? Utils.getDate(messageDate)
      : Utils.getTimeInHour(messageDate);

    chatViewHolder.lastMessageView.setText(lastMessageContext);
    chatViewHolder.timestampView.setText(dateToShow);
  }

  @Override
  public int getItemCount() {
    return chats.size();
  }
}
