package com.example.swifttalk;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
  FirebaseAuth auth = FirebaseAuth.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseUser user = auth.getCurrentUser();
    if (user != null) {
      Intent intent = new Intent(MainActivity.this, HomeActivity.class);
      startActivity(intent);
      finish();
    }

    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);

    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
      Intent intent = new Intent(MainActivity.this, HomeActivity.class);
      startActivity(intent);
      finish();
    }

    Button loginButton = findViewById(R.id.login_button);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
      }
    });

    Button registerButton = findViewById(R.id.register_button);
    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
      }
    });

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    getFCMToken();


  }
  void getFCMToken() {
    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        String token = task.getResult();
      }
    });
  }
}