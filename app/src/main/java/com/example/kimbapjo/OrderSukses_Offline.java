package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderSukses_Offline extends AppCompatActivity {

    private TextView tvPaymentInfo;
    private TextView tvTableNumber;
    private TextView tvOrderCode;
    private TextView tvTotalAmount;
    private TextView tvPaymentMethod;
    private TextView tvTime;

    private ImageView ivTableIcon;

    private MaterialButton btnReceipt;

    private String paymentMethod;
    private int totalAmount;
    private String orderCode;
    private int tableNumber;
    private String waktuBayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sukses_offline);

        tvPaymentInfo = findViewById(R.id.tv_payment_info);
        tvTableNumber = findViewById(R.id.tv_table_number);
        tvOrderCode = findViewById(R.id.tv_order_code);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTime = findViewById(R.id.tv_time);
        ivTableIcon = findViewById(R.id.iv_table_icon);
        btnReceipt = findViewById(R.id.btn_receipt);

        paymentMethod = getIntent().getStringExtra("payment_method");
        totalAmount = getIntent().getIntExtra("total_amount", 0);
        orderCode = getIntent().getStringExtra("order_code");
        tableNumber = getIntent().getIntExtra("table_number", 1);

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = "QRIS";
        }

        if (orderCode == null || orderCode.isEmpty()) {
            orderCode = "KBP001";
        }

        waktuBayar = new SimpleDateFormat(
                "dd MMMM yyyy, HH:mm",
                new Locale("id", "ID")
        ).format(new Date());

        tvPaymentInfo.setText("Pembayaran " + paymentMethod + " diterima");
        tvTableNumber.setText("TABLE #" + tableNumber);
        tvOrderCode.setText("Kode Pesanan: " + orderCode);
        tvTotalAmount.setText("Total Dibayar: " + formatRupiah(totalAmount));
        tvPaymentMethod.setText("Metode: " + paymentMethod);
        tvTime.setText("Waktu: " + waktuBayar);

        final String finalPaymentMethod = paymentMethod;
        final int finalTotalAmount = totalAmount;
        final String finalOrderCode = orderCode;
        final int finalTableNumber = tableNumber;
        final String finalWaktuBayar = waktuBayar;

        btnReceipt.setOnClickListener(v -> {

            Intent intent = new Intent(
                    OrderSukses_Offline.this,
                    kuitansi.class
            );

            intent.putExtra("payment_method", finalPaymentMethod);
            intent.putExtra("total_amount", finalTotalAmount);
            intent.putExtra("order_code", finalOrderCode);
            intent.putExtra("table_number", finalTableNumber);
            intent.putExtra("waktu_bayar", finalWaktuBayar);

            startActivity(intent);
        });
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount)
                .replace(",", ".");
    }
}