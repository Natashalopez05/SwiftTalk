package com.example.swifttalk;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    EdgeToEdge.enable(this);

    setContentView(R.layout.activity_main);

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    // LogIn button action
    Button loginButton = findViewById(R.id.login_button);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
      }
    });
//
//    // Register button action
//    Button registerButton = findViewById(R.id.register_button);
//    registerButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
//        startActivity(intent);
//      }
//    });
  }
}