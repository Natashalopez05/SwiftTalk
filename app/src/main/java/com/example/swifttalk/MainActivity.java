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

    // Firebase Auth
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
          Log.d("AuthStateListener", "User :" + currentUser.getEmail());
        }
      }
    };

    FirebaseAuth auth = FirebaseAuth.getInstance();
    auth.addAuthStateListener(authStateListener);
    //FirebaseUser currentUser = auth.getCurrentUser();

    Button loginButton = findViewById(R.id.login_button);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        auth.createUserWithEmailAndPassword("vladimir@ce.pucmm.edu.do", "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                      Log.i("AuthStateListener", "User: " + user.getEmail());
                      Toast.makeText(MainActivity.this, "Logged in: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                  }
                });
        startActivity(intent);
      }
    });

    // Register button action
    Button registerButton = findViewById(R.id.register_button);
    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
      }
    });
    





  }


}