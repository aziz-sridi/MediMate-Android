package com.example.medimate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataBaseHelper = new DataBaseHelper(this);

        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUp();
            }
        });
    }

    private void loginUser() {
        String userEmail = binding.email.getText().toString();
        String userPassword = binding.password.getText().toString();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            showToast(R.string.please_fill_all_fields);
        } else {
            checkCredentials(userEmail, userPassword);
        }
    }

    private void checkCredentials(String email, String password) {
        boolean checkCredentials = dataBaseHelper.checkemailpassword(email, password);
        if (checkCredentials) {
            showToast(R.string.login_successful);
            navigateToHomepage();
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_signed_in", true);
            editor.putString("user_email", email);
            editor.apply();

        } else {
            showToast(R.string.invalid_credentials);
        }
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomepage() {
        Intent intent = new Intent(getApplicationContext(), Homepage.class);
        startActivity(intent);
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
