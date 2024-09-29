package com.example.swifttalk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity  extends AppCompatActivity {

  EditText emailEdit, passwordEdit;
  Button loginButton;
  FirebaseAuth auth = FirebaseAuth.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_login);

    emailEdit = findViewById(R.id.email_field);
    passwordEdit = findViewById(R.id.password_field);
    loginButton = findViewById(R.id.login_button);

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
          Toast.makeText(LogInActivity.this, "Por favor introduce un correo y una contraseña", Toast.LENGTH_SHORT).show();
          return;
        }

        auth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if(!task.isSuccessful()) {
                Toast.makeText(LogInActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
              }

              FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
              Log.i("AuthStateListener", "Usuario: " + user.getEmail());
              Toast.makeText(LogInActivity.this, "Bienvenido: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
              finish();
            }
          });
      }
    });

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }
}