package edu.uga.cs.ugarideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.uga.cs.ugarideshare.models.User;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Activity for user registration.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Set click listener for register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set click listener for login text
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login screen
            }
        });
    }

    /**
     * Validate user input and attempt registration.
     */
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match");
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create user object
        User user = new User(email, password);

        // Register user with Firebase
        FirebaseUtil.registerUser(user, new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Create login session
                sessionManager.createLoginSession(result);

                // Show success message
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Redirect to main activity
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle state during configuration changes.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", etEmail.getText().toString());
        outState.putString("password", etPassword.getText().toString());
        outState.putString("confirmPassword", etConfirmPassword.getText().toString());
    }

    /**
     * Restore state after configuration changes.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etEmail.setText(savedInstanceState.getString("email", ""));
        etPassword.setText(savedInstanceState.getString("password", ""));
        etConfirmPassword.setText(savedInstanceState.getString("confirmPassword", ""));
    }
}