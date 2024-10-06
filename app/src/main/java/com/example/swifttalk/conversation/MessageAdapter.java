package com.example.swifttalk.conversation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.R;
import com.example.swifttalk.logic.models.Messages.Message;
import com.example.swifttalk.logic.models.Messages.TextMessage;
import com.example.swifttalk.logic.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private Context context;
    private List<Message> messages;
    private FirebaseAuth firebase = FirebaseAuth.getInstance();
    private String currentUserEmail = Objects.requireNonNull(firebase.getCurrentUser()).getEmail();

  public MessageAdapter(Context context, List<Message> messages) {
    this.context = context;
    this.messages = messages;
  }

  @Override
  public int getItemViewType(int i) {
    String senderEmail = messages.get(i).getUserEmail();

    return senderEmail.equalsIgnoreCase(currentUserEmail)
      ? R.layout.fr_chat_receiver_container
      : R.layout.fr_chat_sender_container;
  }

  @NonNull
  @Override
  public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
      return new MessageViewHolder(LayoutInflater.from(context).inflate(
        viewType == R.layout.fr_chat_sender_container
                ? R.layout.fr_chat_sender_container
                : R.layout.fr_chat_receiver_container,
        viewGroup,
        false
      ));
  }

  @Override
  public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
      final Message message = messages.get(i);

      final Date messageDate = message.getTimestamp().toDate();
      final boolean itsFromToday = Utils.isOlderThan(message.getTimestamp(), 1);
      final String timeToShow = itsFromToday ? Utils.getDate(messageDate) : Utils.getTimeInHour(messageDate);
      messageViewHolder.messageTimestamp.setText(timeToShow);

    if (message instanceof TextMessage) {
      messageViewHolder.messageContext.setText(((TextMessage) message).getContext());
      messageViewHolder.messageContext.setVisibility(View.VISIBLE);
      messageViewHolder.messageImage.setVisibility(View.GONE);
    }
    // TODO: Add ImageMessage logic
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }
}
