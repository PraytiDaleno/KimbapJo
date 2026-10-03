package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PromoVoucher extends AppCompatActivity {

    private TextView ivMenu, ivProfile;

    private TextView navHome;
    private TextView navFavorite;
    private TextView navCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_voucher);

        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);

        navHome = findViewById(R.id.nav_home);
        navFavorite = findViewById(R.id.nav_favorite);
        navCart = findViewById(R.id.nav_cart);

        // ================= TOP =================

        ivMenu.setOnClickListener(v -> {
            finish();
        });

        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(PromoVoucher.this, Profil.class);
            startActivity(intent);
        });

        // ================= PROMO =================

        findViewById(R.id.btn_promo1).setOnClickListener(v -> {
            Toast.makeText(
                    PromoVoucher.this,
                    "Promo Kimbap Sayur berhasil dipakai!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        findViewById(R.id.btn_promo2).setOnClickListener(v -> {
            Toast.makeText(
                    PromoVoucher.this,
                    "Voucher Gratis Ongkir berhasil dipakai!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // ================= BOTTOM NAV =================

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(PromoVoucher.this, MainActivity.class);
            startActivity(intent);
        });

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(PromoVoucher.this, Menu.class);
            startActivity(intent);
        });

        navCart.setOnClickListener(v -> {
            Intent intent = new Intent(PromoVoucher.this, checkout.class);
            startActivity(intent);
        });
    }
}