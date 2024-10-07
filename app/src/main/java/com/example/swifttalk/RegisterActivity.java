package com.example.swifttalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

  EditText nameEdit, lastnameEdit, emailEdit, passwordEdit;
  Button registerButton;
  ProgressBar progressBar;
  FirebaseAuth auth = FirebaseAuth.getInstance();
  FirebaseFirestore db = FirebaseFirestore.getInstance();

  public void switchLoadingState(boolean isLoading) {
    if(isLoading) {
      progressBar.setVisibility(View.VISIBLE);
      registerButton.setEnabled(false);
      registerButton.setVisibility(View.INVISIBLE);
    } else {
      progressBar.setVisibility(View.INVISIBLE);
      registerButton.setEnabled(true);
      registerButton.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseUser user = auth.getCurrentUser();
    if (user != null) {
      Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
      startActivity(intent);
      finish();
    }
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_register);

    nameEdit = findViewById(R.id.name_field);
    lastnameEdit = findViewById(R.id.last_name_field);
    emailEdit = findViewById(R.id.email_field);
    passwordEdit = findViewById(R.id.password_field);
    registerButton = findViewById(R.id.register_button);
    progressBar = findViewById(R.id.progressBar);

    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String name = nameEdit.getText().toString().trim();
        String lastname = lastnameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (name.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
          Toast.makeText(RegisterActivity.this, "Por favor introduce todos los campos", Toast.LENGTH_SHORT).show();
          return;
        }

        switchLoadingState(true);
        auth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
              switchLoadingState(false);
              Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
              return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
              switchLoadingState(false);
              Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
              return;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("last_name", lastname);
            userData.put("email", email);
            userData.put("created_at", new Date());

            db.collection("users").document(user.getUid())
              .set(userData)
              .addOnSuccessListener(aVoid -> {
                switchLoadingState(false);
                Toast.makeText(RegisterActivity.this, "Usuario registrado: " + name + " " + lastname, Toast.LENGTH_SHORT).show();
                finish();
              })
              .addOnFailureListener(e -> {
                switchLoadingState(false);
                Toast.makeText(RegisterActivity.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
              });
          });
      }
    });

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
  }
}