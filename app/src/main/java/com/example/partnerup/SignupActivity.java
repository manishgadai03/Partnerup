package com.example.partnerup;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private Button btn;
    private FirebaseAuth mAuth;
    private EditText signupemail;
    private EditText signuppassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

        // Initialize views after setting the content view
        btn = findViewById(R.id.createaccount);
        signupemail = findViewById(R.id.signupemail);
        signuppassword = findViewById(R.id.signuppassword);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signupemail.getText().toString();
                String password = signuppassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    makeText(SignupActivity.this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccount(email, password);
            }
        });
    }
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Send verification email
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> verificationTask) {
                                                if (verificationTask.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    updateUI(user);
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
