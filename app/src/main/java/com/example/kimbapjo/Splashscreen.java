package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(() -> {

            // Pindah ke halaman Login
            Intent intent = new Intent(Splashscreen.this, login.class);
            startActivity(intent);

            finish();

        }, 3000);
    }
}