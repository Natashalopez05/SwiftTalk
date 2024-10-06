package com.example.swifttalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.conversation.MessageAdapter;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.example.swifttalk.logic.models.Chats.PrivateChat;
import com.example.swifttalk.logic.models.Messages.Message;
import com.example.swifttalk.logic.models.Messages.MessageType;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseAuth auth = FirebaseAuth.getInstance();

  private String currentUserEmail = auth.getCurrentUser().getEmail();
  private RecyclerView recyclerView;
  private MessageAdapter messageAdapter;
  private List<Message> messages = new ArrayList<>();

  Chat chat;
  ImageView backButton, imageButton, sendButton;
  TextView userName;
  EditText messageInput;
  Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_chat);

    intent = getIntent();
    chat = Parcels.unwrap(intent.getParcelableExtra("chat"));

    userName = findViewById(R.id.userName);
    backButton = findViewById(R.id.back_button);
    imageButton = findViewById(R.id.send_image);
    sendButton = findViewById(R.id.send_message);
    messageInput = findViewById(R.id.message_input);

    backButton.setOnClickListener(v -> finish());
    sendButton.setOnClickListener(v -> sendMessage(messageInput.getText().toString()));

    userName.setText(((PrivateChat) chat).getOtherUser(currentUserEmail).split("@")[0]);
    // TODO: Add group chat logic

    recyclerView = findViewById(R.id.messagesRecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    messageAdapter = new MessageAdapter(getApplicationContext(), messages);
    recyclerView.setAdapter(messageAdapter);

    db.collection("chats").document(chat.getId()).collection("messages")
        .orderBy("timestamp")
        .addSnapshotListener((value, error) -> {
          if  (error != null || value == null) return;
          messages.clear();

          value.getDocuments()
            .stream()
            .map(Message::createFromDatabase)
            .forEach(messages::add);

          if (!messages.isEmpty())
            recyclerView.smoothScrollToPosition(messages.size() - 1);

          messageAdapter.notifyDataSetChanged();
        });


    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privateChat), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }

  private void sendMessage(String message){
    if (message.isEmpty()) return;

    //TODO: Add image message logic, identify the type of message using http to see if is an image url
    Map<String, Object> messageMap = setTextMessage(message, currentUserEmail);

    db.collection("chats").document(chat.getId()).collection("messages")
      .add(messageMap)
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          messageInput.setText("");
          recyclerView.smoothScrollToPosition(messages.size() - 1);

          //TODO: Add push notification logic
        } else {
          Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
        }
      });

    db.collection("chats").document(chat.getId())
      .update("last_message", messageMap);
  }

  private Map<String, Object> setTextMessage(String message, String sender) {
    Map<String, Object> messageMap = new HashMap<>();
    messageMap.put("context", message);
    messageMap.put("user", sender);
    messageMap.put("timestamp", Timestamp.now());
    messageMap.put("type", MessageType.TEXT);

    return messageMap;
  }

  private Map<String, Object> setImageMessage() {
    //TODO
    return null;
  }
}