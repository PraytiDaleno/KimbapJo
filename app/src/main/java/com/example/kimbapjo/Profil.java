package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profil extends AppCompatActivity {

    private ImageView ivMenu, ivProfile;
    private LinearLayout btnEditProfile, btnAktivitas, btnKotakMasuk;
    private LinearLayout btnPromo, btnBahasa, btnKeluar;
    private TextView tvName, tvEmail;
    private BottomNavigationView bottomNavigation;
    private DatabaseHelper db;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        db = new DatabaseHelper(this);
        session = new UserSession(this);
        initViews();
        setupActions();
        setupBottomNavigation();
    }

    private void initViews() {
        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnAktivitas = findViewById(R.id.btn_aktivitas);
        btnKotakMasuk = findViewById(R.id.btn_kotak_masuk);
        btnPromo = findViewById(R.id.btn_promo);
        btnBahasa = findViewById(R.id.btn_bahasa);
        btnKeluar = findViewById(R.id.btn_keluar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupActions() {
        ivMenu.setOnClickListener(v -> finish());
        ivProfile.setOnClickListener(v -> open(EditProfil.class));
        btnEditProfile.setOnClickListener(v -> open(EditProfil.class));
        btnAktivitas.setOnClickListener(v -> open(Aktivitas.class));
        btnKotakMasuk.setOnClickListener(v -> open(KotakMasuk.class));
        btnPromo.setOnClickListener(v -> open(PromoVoucher.class));
        btnBahasa.setOnClickListener(v -> open(Bahasa.class));
        btnKeluar.setOnClickListener(v -> logout());
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_profile) return true;
            if (id == R.id.navigation_home) openRoot(MainActivity.class);
            else if (id == R.id.navigation_menu) openRoot(Menu.class);
            else if (id == R.id.navigation_cart) openRoot(checkout.class);
            else return false;
            return true;
        });
    }

    private void loadProfileData() {
        int userId = session.getUserId();
        if (userId == -1) {
            logout();
            return;
        }

        Cursor cursor = db.getUserById(userId);
        if (cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            tvEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        }
        cursor.close();
    }

    private void logout() {
        session.clear();
        Intent intent = new Intent(this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void open(Class<?> destination) {
        startActivity(new Intent(this, destination));
    }

    private void openRoot(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.navigation_profile);
        }
    }
}
