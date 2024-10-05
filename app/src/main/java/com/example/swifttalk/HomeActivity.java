package com.example.swifttalk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
  final List<Chat> chats = new ArrayList<>();
  private EventListener<QuerySnapshot> listener;

  void getChats() {
    db.collection("chats")
      .whereArrayContains("users", userEmail)
      .orderBy("last_message.timestamp", Query.Direction.DESCENDING)
      .addSnapshotListener(listener);
  }

  @SuppressLint("NotifyDataSetChanged")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_home);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //TODO FLOATING ACTION BUTTON

    RecyclerView recyclerView = findViewById(R.id.recyclerViewChats);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    ChatsAdapter adapter = new ChatsAdapter(getApplicationContext(), chats);
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

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    getChats();
  }
}
