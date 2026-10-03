package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class order_sukses extends AppCompatActivity {

    private static final int ESTIMASI_MENIT = 20;

    private TextView tvPaymentMessage, tvOrderCode, tvOrderStatus;
    private TextView tvTotalAmount, tvPaymentMethod, tvPaymentTime, tvEstimation;

    private MaterialButton btnTrackOrder, btnBackHome;

    private String paymentMethod;
    private int totalAmount;
    private long waktuMulai;
    private long waktuSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sukses);

        tvPaymentMessage = findViewById(R.id.tv_payment_message);
        tvOrderCode = findViewById(R.id.tv_order_code);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvPaymentTime = findViewById(R.id.tv_payment_time);
        tvEstimation = findViewById(R.id.tv_estimation);

        btnTrackOrder = findViewById(R.id.btn_track_order);
        btnBackHome = findViewById(R.id.btn_back_home);

        paymentMethod = getIntent().getStringExtra("payment_method");
        totalAmount = getIntent().getIntExtra("total_amount", 0);
        waktuMulai = getIntent().getLongExtra("waktu_mulai", System.currentTimeMillis());
        waktuSelesai = waktuMulai + (ESTIMASI_MENIT * 60 * 1000L);

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = "QRIS";
        }

        String orderCode = "#KBP001";

        String waktuSekarang = new SimpleDateFormat(
                "dd MMMM yyyy, HH:mm",
                new Locale("id", "ID")
        ).format(new Date());

        tvPaymentMessage.setText("Pembayaran " + paymentMethod + " diterima");
        tvOrderCode.setText("Kode Pesanan: " + orderCode);
        tvOrderStatus.setText("Status: Dibuat");
        tvTotalAmount.setText("Total Dibayar: " + formatRupiah(totalAmount));
        tvPaymentMethod.setText("Metode: " + paymentMethod);
        tvPaymentTime.setText("Waktu Pembayaran: " + waktuSekarang);
        tvEstimation.setText("Estimasi Selesai: " + ESTIMASI_MENIT + " menit");

        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(order_sukses.this, LacakPesanan.class);
            intent.putExtra("estimasi_menit", ESTIMASI_MENIT);
            intent.putExtra("waktu_mulai", waktuMulai);
            intent.putExtra("waktu_selesai", waktuSelesai);
            startActivity(intent);
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(order_sukses.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}
