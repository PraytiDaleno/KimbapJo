package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Bahasa extends AppCompatActivity {

    private TextView ivMenu, ivProfile;
    private TextView btnEnglish, btnIndonesia;
    private TextView navHome, navFavorite, navCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bahasa);

        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);

        btnEnglish = findViewById(R.id.btn_english);
        btnIndonesia = findViewById(R.id.btn_indonesia);

        navHome = findViewById(R.id.nav_home);
        navFavorite = findViewById(R.id.nav_favorite);
        navCart = findViewById(R.id.nav_cart);

        ivMenu.setOnClickListener(v -> finish());

        ivProfile.setOnClickListener(v ->
                startActivity(new Intent(Bahasa.this, Profil.class))
        );

        btnEnglish.setOnClickListener(v ->
                Toast.makeText(Bahasa.this, "Language changed to English", Toast.LENGTH_SHORT).show()
        );

        btnIndonesia.setOnClickListener(v ->
                Toast.makeText(Bahasa.this, "Bahasa diubah ke Indonesia", Toast.LENGTH_SHORT).show()
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(Bahasa.this, MainActivity.class))
        );

        navFavorite.setOnClickListener(v ->
                startActivity(new Intent(Bahasa.this, Menu.class))
        );

        navCart.setOnClickListener(v ->
                startActivity(new Intent(Bahasa.this, checkout.class))
        );
    }
}