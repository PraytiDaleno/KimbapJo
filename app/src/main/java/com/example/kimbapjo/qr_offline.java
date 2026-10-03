package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class qr_offline extends AppCompatActivity {

    private ImageView btnBack, btnProfile;
    private TextView tvTotalPayment;
    private MaterialButton btnSudahBayar, btnBatalkan;

    private int totalAmount = 0;
    private int tableNumber = 1;

    private String paymentMethod = "QRIS";
    private String checkoutType = "Online";
    private String orderCode = "KBP001";
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_offline);
        db = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btn_back);
        btnProfile = findViewById(R.id.btn_profile);
        tvTotalPayment = findViewById(R.id.tv_total_payment);
        btnSudahBayar = findViewById(R.id.btn_sudah_bayar);
        btnBatalkan = findViewById(R.id.btn_batalkan);

        totalAmount = getIntent().getIntExtra("total_amount", 0);
        tableNumber = getIntent().getIntExtra("table_number", 1);

        paymentMethod = getIntent().getStringExtra("payment_method");
        checkoutType = getIntent().getStringExtra("checkout_type");
        orderCode = getIntent().getStringExtra("order_code");

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = "QRIS";
        }

        if (checkoutType == null || checkoutType.isEmpty()) {
            checkoutType = "Online";
        }

        if (orderCode == null || orderCode.isEmpty()) {
            orderCode = "KBP001";
        }

        tvTotalPayment.setText("Total Pembayaran : " + formatRupiah(totalAmount));

        btnBack.setOnClickListener(v -> finish());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(qr_offline.this, Profil.class);
            startActivity(intent);
        });

        btnSudahBayar.setOnClickListener(v -> {
            db.createOrderFromTransactions(
                    orderCode,
                    db.getUserNameById(new UserSession(this).getUserId()),
                    paymentMethod,
                    totalAmount,
                    20
            );

            if (checkoutType.equalsIgnoreCase("Offline")) {
                Intent intent = new Intent(qr_offline.this, OrderSukses_Offline.class);
                intent.putExtra("payment_method", paymentMethod);
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("table_number", tableNumber);
                intent.putExtra("order_code", orderCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(qr_offline.this, order_sukses.class);
                intent.putExtra("payment_method", paymentMethod);
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("checkout_type", checkoutType);
                intent.putExtra("table_number", tableNumber);
                intent.putExtra("order_code", orderCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnBatalkan.setOnClickListener(v -> {
            Toast.makeText(
                    qr_offline.this,
                    "Pembayaran dibatalkan",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        });
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}
