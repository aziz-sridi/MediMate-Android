package com.example.medimate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        binding.logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                if (!isUserLoggedIn()) {
                    startSigninActivity();
                }
                finish();
            }
        });

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,Homepage.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit();
        editor.putBoolean("is_signed_in", false);
        editor.apply();
    }
    private void clearSessionDataInDatabase() {
        // Assuming you have a DatabaseHelper class with a method to clear session data
        DataBaseHelper databaseHelper = new DataBaseHelper(this);

        // Get the user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "");

        // Perform the database operation to clear session data
        databaseHelper.setUserLoggedOut(userEmail);
    }
    private void startSigninActivity() {
        Intent intent = new Intent(ProfileActivity.this, SigninActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it
    }
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_signed_in", false);
    }

    private void startHomepageActivity() {
        Intent intent = new Intent(ProfileActivity.this, Homepage.class);
        startActivity(intent);
        finish();
    }
}