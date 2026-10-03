package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class login extends AppCompatActivity {

    private TextInputEditText etEmailPhone, etPassword;
    private MaterialButton btnAdmin, btnCustomer, btnGoogle;
    private TextView tvForgotPassword, tvRegister;
    private DatabaseHelper db;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        session = new UserSession(this);

        etEmailPhone = findViewById(R.id.et_email_phone);
        etPassword = findViewById(R.id.et_password);
        btnAdmin = findViewById(R.id.btn_admin);
        btnCustomer = findViewById(R.id.btn_customer);
        btnGoogle = findViewById(R.id.btn_google);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);

        btnAdmin.setOnClickListener(v -> loginAs("admin"));
        btnCustomer.setOnClickListener(v -> loginAs("customer"));

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show()
        );
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Login Google belum tersedia", Toast.LENGTH_SHORT).show()
        );

        int facebookId = getResources().getIdentifier("btn_facebook", "id", getPackageName());
        if (facebookId != 0) {
            View btnFacebook = findViewById(facebookId);
            btnFacebook.setOnClickListener(v ->
                    Toast.makeText(this, "Login Facebook belum tersedia", Toast.LENGTH_SHORT).show()
            );
        }

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, register.class))
        );
    }

    private void loginAs(String expectedRole) {
        String emailPhone = String.valueOf(etEmailPhone.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        if (emailPhone.isEmpty()) {
            etEmailPhone.setError("Email / No. Telepon tidak boleh kosong");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            return;
        }

        Cursor cursor = db.getUserByCredentials(emailPhone, password);
        if (!cursor.moveToFirst()) {
            cursor.close();
            Toast.makeText(this, "Email/telepon atau password salah", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        cursor.close();

        if (!expectedRole.equalsIgnoreCase(role)) {
            Toast.makeText(
                    this,
                    expectedRole.equals("admin")
                            ? "Akun ini bukan akun admin"
                            : "Akun ini bukan akun customer",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        session.saveLogin(userId, role);
        Intent intent = new Intent(
                this,
                role.equalsIgnoreCase("admin") ? AdminDashboard.class : MainActivity.class
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
