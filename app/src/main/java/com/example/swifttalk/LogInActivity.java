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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

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
              if(user == null) {
                Toast.makeText(LogInActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
              }

              FirebaseFirestore db = FirebaseFirestore.getInstance();
              DocumentReference userRef = db.collection("users").document(user.getUid());
              userRef.get().addOnSuccessListener(documentSnapshot -> {
                if(!documentSnapshot.exists()) {
                  Toast.makeText(LogInActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                  return;
                }

                String name = documentSnapshot.getString("name");
                String lastname = documentSnapshot.getString("last_name");
                Toast.makeText(LogInActivity.this, "Bienvenido: " + name + " " + lastname, Toast.LENGTH_SHORT).show();
                finish();
              }).addOnFailureListener(e -> {
                Toast.makeText(LogInActivity.this, "Error al leer los datos" + e.toString(), Toast.LENGTH_SHORT).show();
              });
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