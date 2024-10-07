package com.example.swifttalk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.chats.ChatsAdapter;
import com.example.swifttalk.logic.models.Chats.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseAuth auth = FirebaseAuth.getInstance();

  RecyclerView recyclerView;
  ChatsAdapter adapter;
  Toolbar toolbar;
  ImageView menuButton;

  private final String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
  final List<Chat> chats = new ArrayList<>();
  private EventListener<QuerySnapshot> listener;


  @SuppressLint("NotifyDataSetChanged")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_home);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    menuButton = findViewById(R.id.menuBtn);
    menuButton.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(HomeActivity.this, menuButton);
      popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

      popupMenu.setOnMenuItemClickListener(item -> {
        if (item.getItemId() == R.id.action_logout) {
          // Eliminar el token aquí
          FirebaseMessaging.getInstance().deleteToken()
                  .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                      Log.d("Logout", "Token eliminado exitosamente");
                    } else {
                      Log.e("Logout", "Error al eliminar el token", task.getException());
                    }
                  });

          FirebaseAuth.getInstance().signOut();
          Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
          Intent intent = new Intent(HomeActivity.this, MainActivity.class);
          startActivity(intent);
          finish();

          finish();
          return true;
        }
        return false;
      });
      popupMenu.show();
    });

    //TODO FLOATING ACTION BUTTON

    recyclerView = findViewById(R.id.recyclerViewChats);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new ChatsAdapter(getApplicationContext(), chats);
    recyclerView.setAdapter(adapter);

    listener = (value, error) -> {
      if (error != null || value == null) return;

      chats.clear();
      value.getDocuments().stream()
        .map(doc -> Chat.createFromDatabase(doc, userEmail))
        .forEach(chats::add);

      adapter.notifyDataSetChanged();
    };
    getChats();

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }

  void getChats() {
    db.collection("chats")
      .whereArrayContains("users", userEmail)
      .orderBy("last_message.timestamp", Query.Direction.DESCENDING)
      .addSnapshotListener(listener);
  }

  @Override
  protected void onResume() {
    super.onResume();
    getChats();
  }
}
