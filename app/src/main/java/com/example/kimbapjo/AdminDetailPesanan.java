package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDetailPesanan extends AppCompatActivity {

    private ImageView navDashboard, navPesanan, navStok, navSetting;
    private TextView txtKodePesanan, txtWaktuPesan, txtEstSelesai, txtSelesai;
    private TextView txtNamaPesanan, txtPesanan, txtTotal, txtEstimasi;
    private TextView txtDurasi, txtRiwayatStatus, txtMetodePembayaran;
    private TextView txtStatusPesanan;
    private MaterialButton btnUpdateStatus;

    private DatabaseHelper db;
    private String orderCode;
    private String currentStatus;
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail_pesanan);

        db = new DatabaseHelper(this);
        orderCode = getIntent().getStringExtra("kode");

        initViews();
        loadOrderFromDatabase();
        setupStatusAction();
        setupNavigation();
    }

    private void initViews() {
        txtKodePesanan = findViewById(R.id.txtKodePesanan);
        txtWaktuPesan = findViewById(R.id.txtWaktuPesan);
        txtEstSelesai = findViewById(R.id.txtEstSelesai);
        txtSelesai = findViewById(R.id.txtSelesai);
        txtNamaPesanan = findViewById(R.id.txtNamaPesanan);
        txtPesanan = findViewById(R.id.txtPesanan);
        txtTotal = findViewById(R.id.txtTotal);
        txtEstimasi = findViewById(R.id.txtEstimasi);
        txtDurasi = findViewById(R.id.txtDurasi);
        txtRiwayatStatus = findViewById(R.id.txtRiwayatStatus);
        txtMetodePembayaran = findViewById(R.id.txtMetodePembayaran);
        txtStatusPesanan = findViewById(R.id.txtStatusPesanan);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        navDashboard = findViewById(R.id.navDashboard);
        navPesanan = findViewById(R.id.navPesanan);
        navStok = findViewById(R.id.navStok);
        navSetting = findViewById(R.id.navSetting);
    }

    private void loadOrderFromDatabase() {
        if (orderCode == null || orderCode.isEmpty()) {
            showEmptyDetail();
            return;
        }

        Cursor cursor = db.getOrderByCode(orderCode);
        if (!cursor.moveToFirst()) {
            cursor.close();
            showEmptyDetail();
            return;
        }

        String customer = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
        String items = cursor.getString(cursor.getColumnIndexOrThrow("items"));
        int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_amount"));
        String paymentMethod = cursor.getString(
                cursor.getColumnIndexOrThrow("payment_method")
        );
        currentStatus = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
        long doneAt = cursor.getLong(cursor.getColumnIndexOrThrow("done_at"));
        int estimation = cursor.getInt(cursor.getColumnIndexOrThrow("estimasi_menit"));
        cursor.close();

        txtKodePesanan.setText("#" + orderCode);
        txtNamaPesanan.setText(customer);
        txtPesanan.setText(items);
        txtTotal.setText(formatRupiah(total));
        txtEstimasi.setText(estimation + " menit");
        txtMetodePembayaran.setText(paymentMethod);
        txtStatusPesanan.setText(displayStatus(currentStatus));
        txtWaktuPesan.setText(formatTime(createdAt));
        txtEstSelesai.setText(formatTime(createdAt + estimation * 60 * 1000L));

        if (doneAt > 0L) {
            txtSelesai.setText(formatTime(doneAt));
            long durationMinutes = Math.max(0L, (doneAt - createdAt) / 60000L);
            txtDurasi.setText(durationMinutes + " menit");
        } else {
            txtSelesai.setText("--:--");
            txtDurasi.setText("Belum selesai");
        }

        updateActionButton();
        loadHistory();
    }

    private void setupStatusAction() {
        btnUpdateStatus.setOnClickListener(v -> {
            String newStatus = "Dibuat".equalsIgnoreCase(currentStatus)
                    ? "Diproses" : "Selesai";

            if (db.updateOrderStatus(orderCode, newStatus)) {
                Toast.makeText(
                        this,
                        newStatus.equals("Selesai")
                                ? "Pesanan selesai"
                                : "Pesanan sedang diproses",
                        Toast.LENGTH_SHORT
                ).show();
                loadOrderFromDatabase();
            }
        });
    }

    private void updateActionButton() {
        if ("Selesai".equalsIgnoreCase(currentStatus)) {
            btnUpdateStatus.setVisibility(View.GONE);
        } else {
            btnUpdateStatus.setVisibility(View.VISIBLE);
            btnUpdateStatus.setText(
                    "Diproses".equalsIgnoreCase(currentStatus)
                            ? "Selesaikan Pesanan"
                            : "Proses Pesanan"
            );
        }
    }

    private void loadHistory() {
        Cursor cursor = db.getOrderHistory(orderCode);
        StringBuilder history = new StringBuilder();

        while (cursor.moveToNext()) {
            if (history.length() > 0) history.append("\n\n");
            history.append("\u25CF ")
                    .append(cursor.getString(cursor.getColumnIndexOrThrow("status")))
                    .append("\n   ")
                    .append(formatTime(cursor.getLong(
                            cursor.getColumnIndexOrThrow("created_at")
                    )));
        }
        cursor.close();

        txtRiwayatStatus.setText(
                history.length() == 0 ? "Belum ada riwayat status" : history.toString()
        );
    }

    private void showEmptyDetail() {
        txtKodePesanan.setText("Pilih pesanan dari Daftar Pesanan");
        txtWaktuPesan.setText("--:--");
        txtEstSelesai.setText("--:--");
        txtSelesai.setText("--:--");
        txtNamaPesanan.setText("-");
        txtPesanan.setText("-");
        txtTotal.setText("Rp 0");
        txtEstimasi.setText("-");
        txtDurasi.setText("-");
        txtMetodePembayaran.setText("-");
        txtStatusPesanan.setText("-");
        txtRiwayatStatus.setText("Belum ada pesanan dipilih");
        btnUpdateStatus.setVisibility(View.GONE);
    }

    private String displayStatus(String status) {
        return "Dibuat".equalsIgnoreCase(status) ? "Baru" : status;
    }

    private void setupNavigation() {
        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminDashboard.class));
            finish();
        });
        navPesanan.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminPesanan.class));
            finish();
        });
        navStok.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminStok.class));
            finish();
        });
        navSetting.setOnClickListener(v -> {});
    }

    private String formatTime(long millis) {
        return timeFormat.format(new Date(millis));
    }

    private String formatRupiah(int amount) {
        return String.format(Locale.getDefault(), "Rp %,d", amount).replace(",", ".");
    }
}
