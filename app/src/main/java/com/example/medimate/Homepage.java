package com.example.medimate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medimate.databinding.ActivityHomepageBinding;

public class Homepage extends AppCompatActivity {
    private ActivityHomepageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        binding.toMyMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyMedsActivity.class));
            }
        });

        binding.toMyappointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyAppointmentsActivity.class));
            }
        });

        binding.toFindMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:35.8,10.5?q=pharmacie");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    // Handle the exception
                    Toast.makeText(getApplicationContext(), "Google Maps app not installed", Toast.LENGTH_SHORT).show();

                    // If Google Maps is not installed, open the map URL in a browser
                    String mapUrl = "https://www.google.com/maps/search/?api=1&query=pharmacies";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));

                    PackageManager packageManager = getPackageManager();
                    if (browserIntent.resolveActivity(packageManager) != null) {
                        startActivity(browserIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No app available to handle the request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean isGoogleMapsInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.google.android.apps.maps", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
