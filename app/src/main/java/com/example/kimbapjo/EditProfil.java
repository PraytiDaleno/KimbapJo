package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditProfil extends AppCompatActivity {

    private ImageView ivMenu, ivProfile;
    private EditText etNama, etEmail, etUsername, etPassword, etPhone;
    private TextView btnSave;
    private BottomNavigationView bottomNavigation;
    private DatabaseHelper db;
    private UserSession session;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        db = new DatabaseHelper(this);
        session = new UserSession(this);
        initViews();
        loadUserData();
        setupActions();
        setupBottomNavigation();
    }

    private void initViews() {
        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);
        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupActions() {
        ivMenu.setOnClickListener(v -> finish());
        ivProfile.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.navigation_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_profile) {
                finish();
                return true;
            }
            if (id == R.id.navigation_home) openRoot(MainActivity.class);
            else if (id == R.id.navigation_menu) openRoot(Menu.class);
            else if (id == R.id.navigation_cart) openRoot(checkout.class);
            else return false;
            return true;
        });
    }

    private void loadUserData() {
        userId = session.getUserId();
        if (userId == -1) {
            redirectToLogin();
            return;
        }

        Cursor cursor = db.getUserById(userId);
        if (cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            etNama.setText(fullname);
            etUsername.setText(fullname);
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            etPassword.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        }
        cursor.close();
    }

    private void saveProfile() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.updateUser(userId, nama, phone, email, password)) {
            Toast.makeText(this, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Profil.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email sudah digunakan atau data gagal disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void openRoot(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void redirectToLogin() {
        session.clear();
        Intent intent = new Intent(this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
