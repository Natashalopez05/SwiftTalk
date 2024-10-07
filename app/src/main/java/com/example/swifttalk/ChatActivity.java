package com.example.swifttalk;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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
import com.example.swifttalk.logic.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseAuth auth = FirebaseAuth.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  StorageReference storageReference = storage.getReference("uploads");

  private final String currentUserEmail = auth.getCurrentUser().getEmail();
  private RecyclerView recyclerView;
  private MessageAdapter messageAdapter;
  private final List<Message> messages = new ArrayList<>();
  private Uri imageUri;

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
    imageButton.setOnClickListener(v -> pickFile());

    userName.setText(((PrivateChat) chat).getOtherUser(currentUserEmail).split("@")[0]);

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

          if (!messages.isEmpty()) recyclerView.smoothScrollToPosition(messages.size() - 1);

          messageAdapter.notifyDataSetChanged();
        });

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privateChat), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == 1 && resultCode == RESULT_OK
      && data != null && data.getData() != null){
      imageUri = data.getData();
      uploadFile();
    }
  }

  private void pickFile() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, 1);
  }
  private String getFileExtension(Uri uri){
      ContentResolver contentResolver = getContentResolver();
      MimeTypeMap mimeType = MimeTypeMap.getSingleton();
      return mimeType.getExtensionFromMimeType(contentResolver.getType(uri));
  }
  private void uploadFile() {
    if (imageUri == null) return;

    String path = System.currentTimeMillis() + "." + getFileExtension(imageUri);
    StorageReference file = storageReference.child(path);

    file.putFile(imageUri)
      .addOnSuccessListener(taskSnapshot -> file.getDownloadUrl().addOnSuccessListener(uri -> {
        String imageUrl = uri.toString();
        sendMessage(imageUrl);
      }))
      .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show());
  }
  private void sendMessage(String message){
    if (message.isEmpty()) return;
    Map<String, Object> messageMap;

    messageMap = Utils.setMessage(message, currentUserEmail);

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
}