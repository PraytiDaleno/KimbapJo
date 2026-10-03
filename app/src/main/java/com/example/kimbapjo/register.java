package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class register extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ImageView btnBack;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {

            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty()) {
                etFullName.setError("Nama lengkap tidak boleh kosong");
                return;
            }

            if (phone.isEmpty()) {
                etPhone.setError("No. Telepon tidak boleh kosong");
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email tidak valid");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password tidak boleh kosong");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password minimal 6 karakter");
                return;
            }

            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Password tidak sama");
                return;
            }

            boolean insert = db.registerUser(
                    fullName,
                    phone,
                    email,
                    password
            );

            if (insert) {

                Toast.makeText(
                        register.this,
                        "Pendaftaran berhasil!",
                        Toast.LENGTH_LONG
                ).show();

                startActivity(
                        new Intent(register.this, login.class)
                );

                finish();

            } else {

                Toast.makeText(
                        register.this,
                        "Email sudah terdaftar!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}