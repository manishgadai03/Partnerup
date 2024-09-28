package com.example.partnerup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView create;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, redirect to MainActivity
            updateUI(currentUser);
        }

        // Initialize UI components
        loginEmail = findViewById(R.id.loginpemail);
        loginPassword = findViewById(R.id.loginpassword);
        loginButton = findViewById(R.id.Login);
        create=findViewById(R.id.create);
        final Drawable eyeClosedDrawable = ContextCompat.getDrawable(this, R.drawable.hidden);
        final Drawable eyeOpenDrawable = ContextCompat.getDrawable(this, R.drawable.show);


        // Adjust view for system insets (optional, ensure 'main' view exists)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            signInUser(email, password);
        });
        loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOpenDrawable, null);

        // Handle touch events
        loginPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the click was on the drawable (right side of EditText)
                if (event.getRawX() >= (loginPassword.getRight() - loginPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(loginPassword, eyeClosedDrawable, eyeOpenDrawable);
                    return true;
                }
            }
            return false;
        });
    }
    private void togglePasswordVisibility(EditText passwordEditText, Drawable eyeClosedDrawable, Drawable eyeOpenDrawable) {
        if (isPasswordVisible) {
            // Hide password
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeClosedDrawable, null);
            isPasswordVisible = false;
        } else {
            // Show password
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeOpenDrawable, null);
            isPasswordVisible = true;
        }
        // Move cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    /**
     * Method to sign in the user using Firebase Authentication.
     *
     * @param email    User's email address
     * @param password User's password
     */
    private void signInUser(String email, String password) {
        // Show a loading indicator if necessary

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in succeeded
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Sign-in failed
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                    // Hide the loading indicator if necessary
                });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is logged in, navigate to MainActivity
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity so user can't return with back button
        } else {
            // User is not logged in, stay on LoginActivity
            // You can also clear input fields or show additional messages here
        }
    }
}
