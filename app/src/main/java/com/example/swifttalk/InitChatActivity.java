package com.example.swifttalk;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swifttalk.logic.models.User;
import com.example.swifttalk.search.SearchAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InitChatActivity extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseAuth auth = FirebaseAuth.getInstance();
  String currentUserEmail = auth.getCurrentUser().getEmail();
  List<User> users = new ArrayList<>();
  RecyclerView recyclerView;
  SearchAdapter searchAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_init_chat);

    recyclerView = findViewById(R.id.recyclerViewInitChat);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    searchAdapter = new SearchAdapter(this, users);
    recyclerView.setAdapter(searchAdapter);

    db.collection("users")
      .whereNotEqualTo("email", currentUserEmail)
      .addSnapshotListener((value, error) -> {
        if (error != null || value == null) return;

        users.clear();
        value.getDocuments()
          .stream()
          .map(User::createFromDatabase)
          .forEach(users::add);

        searchAdapter.notifyDataSetChanged();
      });

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.init_chat), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }
}

