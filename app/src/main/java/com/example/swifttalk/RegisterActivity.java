package com.example.swifttalk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

  EditText nameEdit, lastnameEdit, emailEdit, passwordEdit;
  Button registerButton;
  FirebaseAuth auth = FirebaseAuth.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_register);

    nameEdit = findViewById(R.id.name_field);
    lastnameEdit = findViewById(R.id.last_name_field);
    emailEdit = findViewById(R.id.email_field);
    passwordEdit = findViewById(R.id.password_field);
    registerButton = findViewById(R.id.register_button);

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

        auth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
              Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
              return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(RegisterActivity.this, "Usuario registrado: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            finish();
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
