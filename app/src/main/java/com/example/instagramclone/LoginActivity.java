package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private Button mLoginButton;
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check to see if user has already been logged in to avoid asking to login again
        if(ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Grab references to different components of the login view
        mLoginButton = findViewById(R.id.buttonLogin);
        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        mRegisterButton = findViewById(R.id.buttonRegister);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = mEditTextUsername.getText().toString();
                String password = mEditTextPassword.getText().toString();
                loginUser(username, password);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mEditTextUsername.getText().toString();
                String password = mEditTextPassword.getText().toString();
                registerUser(username, password);
            }
        });
    }

    // Method to handle the logging in of an existing user using the Parse database
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);

        // LogInInBackground is used to put login task on background thread so user experience is
        // not halted during execution of login task. Leads to better UI experience
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_LONG).show();
                    return;
                }
                // Navigate to Main Activity if user signed in successfully
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Successful Login!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to handle registration of new User in the Parse database
    private void registerUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Toast.makeText(LoginActivity.this, "Registration Unsuccessful!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, "Successful Registration!", Toast.LENGTH_LONG).show();
                // Navigate to Main Activity if user registered successfully
                goMainActivity();
            }
        });
    }

    // Method to assist in navigating to the main activity after login
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}