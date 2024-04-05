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

public class LoginActivity extends AppCompatActivity {
    EditText ed6, ed7;
    Button btnLogin, btnClear;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed6 = findViewById(R.id.ed6);
        ed7 = findViewById(R.id.ed7);
        btnLogin = findViewById(R.id.Login);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();

            }
        });


    }
    private void performLogin() {

        String Email = ed6.getText().toString();
        String Password = ed7.getText().toString();
        if (!Email.matches(emailPattern)) {
            ed6.setError("Enter Valid Email Please");
        } else if (Password.isEmpty() || Password.length() < 6) {

            ed7.setError("Enter valid Password");
        } else {
            progressDialog.setMessage(" Please wait While Login.....");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        pushToNewActivity();
                        Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"You access is not valid"+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }


    }

    private void pushToNewActivity() {
        Intent I = new Intent(LoginActivity.this,HomeActivity2.class);
        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(I);
    }
}