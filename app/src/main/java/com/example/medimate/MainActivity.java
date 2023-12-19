    package com.example.medimate;

    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.medimate.databinding.ActivityMainBinding;

    // ... (imports)

    public class MainActivity extends AppCompatActivity {
        private ActivityMainBinding binding;
        private DataBaseHelper dataBaseHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            dataBaseHelper = new DataBaseHelper(this);

            if (isUserLoggedIn()) {
                startHomepageActivity();
            } else {
                startSigninActivity();
            }
        }
            private void startHomepageActivity() {
            startActivity(new Intent(this, Homepage.class));
            finish();
        }

        private void startSigninActivity() {
            startActivity(new Intent(this, SigninActivity.class));
            finish(); // Finish the current activity to prevent going back to it from SignupActivity
        }
        private boolean isUserLoggedIn() {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_signed_in", false);
            return isLoggedIn;
        }


    }
