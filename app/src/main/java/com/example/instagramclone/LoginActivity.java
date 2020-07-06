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

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private Button mLoginButton;
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;


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

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = mEditTextUsername.getText().toString();
                String password = mEditTextPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);

        // LogInInBackground is used to put login task on background thread so user experience is
        // not halted during execution of login task. Leads to better UI experience
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Navigate to Main Activity if user signed in successfully
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
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