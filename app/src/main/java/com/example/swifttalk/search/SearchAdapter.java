package com.example.swifttalk.search;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.ChatActivity;
import com.example.swifttalk.R;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.example.swifttalk.logic.models.Chats.PrivateChat;
import com.example.swifttalk.logic.models.User;
import com.example.swifttalk.logic.utils.Utils;
import com.example.swifttalk.search.SearchViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.parceler.Parcels;


import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>  {
  private final Context context;
  private final List<User> users;
  private final FirebaseAuth firebase = FirebaseAuth.getInstance();
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final String currentUserEmail = firebase.getCurrentUser().getEmail();
  private PrivateChat chat;

  public SearchAdapter(Context context, List<User> users) {
    this.context = context;
    this.users = users;
  }

  @NonNull
  @Override
  public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    return new SearchViewHolder(LayoutInflater.from(context).inflate(
      R.layout.fr_user,
      viewGroup,
      false
    ));
  }

  @Override
  public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {
    final User user = users.get(i);
    searchViewHolder.userEmail.setText(user.getEmail());
    searchViewHolder.userName.setText(user.getName());
    searchViewHolder.userCover.setOnClickListener(v -> {
      db.collection("chats")
        .whereArrayContains("users", currentUserEmail)
        .get()
        .addOnCompleteListener(task -> {
          List<DocumentSnapshot> documents = task.getResult().getDocuments();

          for (DocumentSnapshot document : documents) {
            PrivateChat tempChat = (PrivateChat) Chat.createFromDatabase(document, currentUserEmail);
            if (tempChat.getOtherUser(currentUserEmail).equals(user.getEmail())) {
              chat = tempChat;
              break;
            }
          }
          if (chat != null) {
            Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("chat", Parcels.wrap(chat));
            context.startActivity(intent);
            return;
          }

          Map<String, Object> chatData = Utils.setChat(currentUserEmail, user.getEmail());
          db.collection("chats").add(chatData)
            .addOnSuccessListener(documentReference -> {
              documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                  if (documentSnapshot.exists()) {
                    chat = (PrivateChat) Chat.createFromDatabase(documentSnapshot, currentUserEmail);

                    Intent intent = new Intent(context.getApplicationContext(), ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("chat", Parcels.wrap(chat));
                    chat = null; // to ensure that the chat is not reused
                    context.startActivity(intent);
                  }
                })
                .addOnFailureListener(e -> Log.e("SearchAdapter", "Error retrieving chat", e));
            })
            .addOnFailureListener(e -> Log.e("SearchAdapter", "Error creating chat", e));
        });
    });
  }

  @Override
  public int getItemCount() {
    return users.size();
  }
}
