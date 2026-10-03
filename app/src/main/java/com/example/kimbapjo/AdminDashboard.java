package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboard extends AppCompatActivity {

    private Button btnProsesPesanan;
    private ImageView navDashboard, navPesanan, navSetting, navStok;
    private View btnAdminLogout;
    private TextView tvPesananHariIni, tvDiproses, tvSelesai, tvStokMenipis;
    private LinearLayout[] orderCards;
    private TextView[] orderStatuses, orderCodes, orderCustomers, orderItems;
    private DatabaseHelper db;
    private String latestOrderCode;
    private String latestOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = new DatabaseHelper(this);
        initViews();
        setupNavigation();

        btnProsesPesanan.setOnClickListener(v -> processLatestOrder());
        btnAdminLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void initViews() {
        btnProsesPesanan = findViewById(R.id.btnProsesPesanan);
        tvPesananHariIni = findViewById(R.id.tvPesananHariIni);
        tvDiproses = findViewById(R.id.tvDiproses);
        tvSelesai = findViewById(R.id.tvSelesai);
        tvStokMenipis = findViewById(R.id.tvStokMenipis);

        navDashboard = findViewById(R.id.navDashboard);
        navPesanan = findViewById(R.id.navPesanan);
        navSetting = findViewById(R.id.navSetting);
        navStok = findViewById(R.id.navStok);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        orderCards = new LinearLayout[]{
                findViewById(R.id.cardOrder1),
                findViewById(R.id.cardOrder2),
                findViewById(R.id.cardOrder3)
        };
        orderStatuses = new TextView[]{
                findViewById(R.id.tvStatusOrder1),
                findViewById(R.id.tvStatusOrder2),
                findViewById(R.id.tvStatusOrder3)
        };
        orderCodes = new TextView[]{
                findViewById(R.id.tvCodeOrder1),
                findViewById(R.id.tvCodeOrder2),
                findViewById(R.id.tvCodeOrder3)
        };
        orderCustomers = new TextView[]{
                findViewById(R.id.tvCustomerOrder1),
                findViewById(R.id.tvCustomerOrder2),
                findViewById(R.id.tvCustomerOrder3)
        };
        orderItems = new TextView[]{
                findViewById(R.id.tvItemsOrder1),
                findViewById(R.id.tvItemsOrder2),
                findViewById(R.id.tvItemsOrder3)
        };
    }

    private void loadDashboard() {
        tvPesananHariIni.setText(String.valueOf(db.countTodayOrders()));
        tvDiproses.setText(String.valueOf(db.countTodayOrdersByStatus("Diproses")));
        tvSelesai.setText(String.valueOf(db.countTodayOrdersByStatus("Selesai")));
        tvStokMenipis.setText(String.valueOf(db.countLowStock(3)));

        for (LinearLayout card : orderCards) {
            card.setVisibility(View.GONE);
        }

        latestOrderCode = null;
        latestOrderStatus = null;
        Cursor cursor = db.getLatestOrders(3);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        int index = 0;

        while (cursor.moveToNext() && index < orderCards.length) {
            String code = cursor.getString(cursor.getColumnIndexOrThrow("order_code"));
            String customer = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
            String items = cursor.getString(cursor.getColumnIndexOrThrow("items"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));

            if (index == 0) {
                latestOrderCode = code;
                latestOrderStatus = status;
            }

            orderCards[index].setVisibility(View.VISIBLE);
            orderStatuses[index].setText(displayStatus(status));
            orderCodes[index].setText("#" + code);
            orderCustomers[index].setText(customer);
            orderItems[index].setText(items + "\n" + timeFormat.format(new Date(createdAt)));
            index++;
        }
        cursor.close();

        btnProsesPesanan.setVisibility(
                latestOrderCode == null || "Selesai".equalsIgnoreCase(latestOrderStatus)
                        ? View.GONE : View.VISIBLE
        );
        btnProsesPesanan.setText(
                "Diproses".equalsIgnoreCase(latestOrderStatus)
                        ? "Selesaikan Pesanan"
                        : "Proses Pesanan"
        );
    }

    private String displayStatus(String status) {
        return "Dibuat".equalsIgnoreCase(status) ? "Baru" : status;
    }

    private void processLatestOrder() {
        if (latestOrderCode == null) return;

        String newStatus = "Diproses".equalsIgnoreCase(latestOrderStatus)
                ? "Selesai" : "Diproses";

        if (db.updateOrderStatus(latestOrderCode, newStatus)) {
            Toast.makeText(
                    this,
                    newStatus.equals("Selesai")
                            ? "Pesanan selesai"
                            : "Pesanan sedang diproses",
                    Toast.LENGTH_SHORT
            ).show();
            loadDashboard();
        }
    }

    private void setupNavigation() {
        navDashboard.setOnClickListener(v ->
                Toast.makeText(this, "Anda berada di Dashboard", Toast.LENGTH_SHORT).show()
        );
        navPesanan.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPesanan.class))
        );
        navSetting.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDetailPesanan.class))
        );
        navStok.setOnClickListener(v ->
                startActivity(new Intent(this, AdminStok.class))
        );
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Admin")
                .setMessage("Apakah Anda yakin ingin keluar dari akun admin?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .show();
    }

    private void logout() {
        new UserSession(this).clear();
        Intent intent = new Intent(this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }
}
