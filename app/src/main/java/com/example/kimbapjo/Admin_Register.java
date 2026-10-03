package com.example.kimbapjo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Admin_Register extends AppCompatActivity {

    EditText etNama, etTelp, etEmail, etPassword, etKonfirmasi;
    Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        etNama = findViewById(R.id.etNama);
        etTelp = findViewById(R.id.etTelp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etKonfirmasi = findViewById(R.id.etKonfirmasi);
        btnDaftar = findViewById(R.id.btnDaftar);

        btnDaftar.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String telp = etTelp.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String konfirmasi = etKonfirmasi.getText().toString().trim();

            if (nama.isEmpty() || telp.isEmpty() || email.isEmpty() || pass.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(konfirmasi)) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Admin berhasil daftar", Toast.LENGTH_SHORT).show();

                // nanti bisa lanjut simpan ke SQLite
                finish();
            }
        });
    }
}