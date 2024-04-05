package com.example.smartgalleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText ed1, ed2, ed3, ed4, ed5;
    Button btnRegister, btnLogin, btnClear;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String firstname, Lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            var intent = new Intent(this, HomeActivity2.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        ed3 = findViewById(R.id.ed3);
        ed4 = findViewById(R.id.ed4);
        ed5 = findViewById(R.id.ed5);
        btnRegister = findViewById(R.id.btn1);
        btnLogin = findViewById(R.id.login);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnLogin.setOnClickListener(v -> {
            Intent I = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(I);
        });

        btnRegister.setOnClickListener(v -> PerformAuth());
    }

    private void PerformAuth() {
        firstname = ed1.getText().toString();
        Lastname = ed2.getText().toString();
        String Email = ed3.getText().toString();
        String Password = ed4.getText().toString();
        String ConfirmPassword = ed5.getText().toString();

        if (firstname.isEmpty() || Lastname.isEmpty()) {
            // Handle the case where first name or last name is empty
            Toast.makeText(this, "First name and Last name are required", Toast.LENGTH_SHORT).show();
        } else if (!Email.matches(emailPattern)) {
            ed3.setError("Enter Valid Email Please");
        } else if (Password.isEmpty() || Password.length() < 6) {
            ed4.setError("Enter valid Password");
        } else if (!Password.equals(ConfirmPassword)) {
            ed5.setError("Password Not Matched");
        } else {
            progressDialog.setMessage("Please wait while registering...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    User user = new User(firstname, Lastname);
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            sendUserToNextActivity();
                            Toast.makeText(MainActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add first and last name to Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent I = new Intent(MainActivity.this, LoginActivity.class);
        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(I);
    }
}
