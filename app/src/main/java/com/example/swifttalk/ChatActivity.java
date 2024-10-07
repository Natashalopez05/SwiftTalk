package com.example.swifttalk;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swifttalk.conversation.MessageAdapter;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.example.swifttalk.logic.models.Chats.PrivateChat;
import com.example.swifttalk.logic.models.Messages.Message;
import com.example.swifttalk.logic.models.User;
import com.example.swifttalk.logic.utils.Utils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseAuth auth = FirebaseAuth.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  StorageReference storageReference = storage.getReference("uploads");

  private final String currentUserEmail = auth.getCurrentUser().getEmail();
  private RecyclerView recyclerView;
  private MessageAdapter messageAdapter;
  private final List<Message> messages = new ArrayList<>();

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
      Uri imageUri = data.getData();
      uploadFile(imageUri);
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
  private void uploadFile(Uri imageUri) {
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
    String otherUserEmail = ((PrivateChat) chat).getOtherUser(currentUserEmail);

    db.collection("users").whereEqualTo("email", otherUserEmail)
      .get()
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult().getDocuments().get(0);
          User user = User.createFromDatabase(document);
          String token = user.getFmcToken();
          String titulo = "Nuevo mensaje de " + currentUserEmail;
          String mensaje = message;
          sendNotificationToUser(token, titulo, mensaje);
        }
      });

    db.collection("chats").document(chat.getId()).collection("messages")
      .add(messageMap)
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          messageInput.setText("");
          recyclerView.smoothScrollToPosition(messages.size() - 1);
        } else {
          Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
        }
      });

    db.collection("chats").document(chat.getId())
      .update("last_message", messageMap);
  }

  private void sendNotificationToUser(String token, String title, String message) {
    new SendNotificationToUser().execute(token, title, message);
  }
  private class SendNotificationToUser extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
      String token = params[0];
      String title = params[1];
      String body = params[2];

      try {
        Log.d(TAG, "Token Chino: " + token);
        String url = "https://fcm.googleapis.com/v1/projects/swifttalk-de338/messages:send";
        String serverKey = getAccessToken();

        JSONObject json = new JSONObject();
        JSONObject message = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);
        message.put("token", token);
        message.put("notification", notification);
        json.put("message", message);

        Log.d(TAG, "JSON Payload: " + json.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> Log.d(TAG, "Notification sent successfully"),
                error -> {
                  Log.e(TAG, "Error sending notification", error);
                  if (error.networkResponse != null) {
                    Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                  }
                }
        ) {
          @Override
          public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + serverKey);
            headers.put("Content-Type", "application/json");
            return headers;
          }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(jsonObjectRequest);
      } catch (IOException | JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    public String getAccessToken() throws IOException {
      final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
      final String[] SCOPES = { MESSAGING_SCOPE };

      InputStream serviceAccountStream = ChatActivity.class
              .getClassLoader().getResourceAsStream("service-account.json");
      GoogleCredentials googleCredentials = GoogleCredentials
              .fromStream(serviceAccountStream)
              .createScoped(Arrays.asList(SCOPES));
      googleCredentials.refreshIfExpired();

      return googleCredentials.getAccessToken().getTokenValue();
    }
  }

}

