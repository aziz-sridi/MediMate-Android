package com.example.medimate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REPASSWORD = "repassword";

    private ActivitySignupBinding binding;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataBaseHelper = new DataBaseHelper(this);

        // Restore saved data if available
        if (savedInstanceState != null) {
            restoreSavedData(savedInstanceState);
        }

        // Set up click listeners
        binding.singupbtn.setOnClickListener(view -> signUp());
        binding.signinText.setOnClickListener(view -> navigateToSignIn());
    }

    private void restoreSavedData(Bundle savedInstanceState) {
        binding.email.setText(savedInstanceState.getString(KEY_EMAIL, ""));
        binding.password.setText(savedInstanceState.getString(KEY_PASSWORD, ""));
        binding.repassword.setText(savedInstanceState.getString(KEY_REPASSWORD, ""));
    }

    private void signUp() {
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String repassword = binding.repassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
            showToast(R.string.please_fill_all_fields);
        } else {
            if (password.equals(repassword)) {
                boolean checkUserEmail = dataBaseHelper.checkmail(email);
                if (!checkUserEmail) {
                    handleSignUpSuccess(email, password);
                } else {
                    showToast(R.string.user_already_signed_up);
                }
            } else {
                showToast(R.string.passwords_do_not_match);
            }
        }
    }

    private void handleSignUpSuccess(String email, String password) {
        boolean insert = dataBaseHelper.insertData(email, password);
        if (insert) {
            showToast(R.string.signup_success);
            navigateToSignIn();
            finish(); // Finish the current activity
        } else {
            showToast(R.string.signup_failed);
        }
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save necessary data to the bundle
        outState.putString(KEY_EMAIL, binding.email.getText().toString());
        outState.putString(KEY_PASSWORD, binding.password.getText().toString());
        outState.putString(KEY_REPASSWORD, binding.repassword.getText().toString());
    }
}
