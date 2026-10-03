package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminPesanan extends AppCompatActivity {

    private TextView filterSemua, filterBaru, filterDiproses, filterSelesai;
    private LinearLayout containerPesanan;
    private ImageView navDashboard, navPesanan, navStok, navSetting;
    private DatabaseHelper db;
    private String activeFilter = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pesanan);

        db = new DatabaseHelper(this);
        initViews();
        setupFilters();
        setupNavigation();
    }

    private void initViews() {
        filterSemua = findViewById(R.id.filterSemua);
        filterBaru = findViewById(R.id.filterBaru);
        filterDiproses = findViewById(R.id.filterDiproses);
        filterSelesai = findViewById(R.id.filterSelesai);
        containerPesanan = findViewById(R.id.containerPesanan);

        navDashboard = findViewById(R.id.navDashboard);
        navPesanan = findViewById(R.id.navPesanan);
        navStok = findViewById(R.id.navStok);
        navSetting = findViewById(R.id.navSetting);
    }

    private void setupFilters() {
        filterSemua.setOnClickListener(v -> applyFilter("Semua"));
        filterBaru.setOnClickListener(v -> applyFilter("Dibuat"));
        filterDiproses.setOnClickListener(v -> applyFilter("Diproses"));
        filterSelesai.setOnClickListener(v -> applyFilter("Selesai"));
    }

    private void applyFilter(String filter) {
        activeFilter = filter;
        updateFilterAppearance();
        loadOrders();
    }

    private void loadOrders() {
        containerPesanan.removeAllViews();
        Cursor cursor = db.getAllOrders();
        int displayed = 0;

        while (cursor.moveToNext()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            if (!matchesFilter(status)) continue;

            addOrderCard(
                    cursor.getString(cursor.getColumnIndexOrThrow("order_code")),
                    cursor.getString(cursor.getColumnIndexOrThrow("customer_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("items")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("total_amount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("payment_method")),
                    status,
                    cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("done_at")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("estimasi_menit"))
            );
            displayed++;
        }
        cursor.close();

        if (displayed == 0) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada pesanan pada kategori ini");
            empty.setTextColor(Color.GRAY);
            empty.setTextSize(14);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(48), 0, dp(48));
            containerPesanan.addView(empty);
        }
    }

    private boolean matchesFilter(String status) {
        return "Semua".equals(activeFilter)
                || activeFilter.equalsIgnoreCase(status);
    }

    private void addOrderCard(
            String code,
            String customer,
            String items,
            int total,
            String paymentMethod,
            String status,
            long createdAt,
            long doneAt,
            int estimation
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(10), dp(12), dp(10));
        card.setBackgroundResource(R.drawable.bg_order_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, dp(14), 0, 0);
        card.setLayoutParams(cardParams);

        TextView badge = new TextView(this);
        badge.setText(displayStatus(status));
        badge.setTextSize(10);
        badge.setGravity(Gravity.CENTER);
        badge.setTextColor(status.equalsIgnoreCase("Diproses")
                ? Color.rgb(245, 124, 0) : Color.rgb(46, 125, 50));
        badge.setBackgroundResource(status.equalsIgnoreCase("Diproses")
                ? R.drawable.bg_badge_orange : R.drawable.bg_badge_green);
        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(dp(70), dp(22));
        badgeParams.gravity = Gravity.END;
        badge.setLayoutParams(badgeParams);

        TextView codeView = createText("#" + code, 15, Color.rgb(46, 125, 50), true);
        TextView customerView = createText(customer, 15, Color.rgb(17, 17, 17), true);

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(createdAt));
        TextView itemsView = createText(items + "\n" + time, 11, Color.GRAY, false);

        Button detailButton = new Button(this);
        detailButton.setText("Lihat Detail  >");
        detailButton.setAllCaps(false);
        detailButton.setTextSize(12);
        detailButton.setTextColor(Color.WHITE);
        detailButton.setBackgroundResource(R.drawable.bg_button_green);
        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(dp(140), dp(40));
        buttonParams.setMargins(0, dp(8), 0, 0);
        detailButton.setLayoutParams(buttonParams);
        detailButton.setOnClickListener(v -> openDetail(
                code, customer, items, total, paymentMethod,
                status, createdAt, doneAt, estimation
        ));

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, dp(8), 0, 0);
        buttonRow.setLayoutParams(rowParams);

        LinearLayout.LayoutParams detailParams =
                new LinearLayout.LayoutParams(0, dp(40), 1f);
        detailButton.setLayoutParams(detailParams);
        buttonRow.addView(detailButton);

        if (!status.equalsIgnoreCase("Selesai")) {
            Button statusButton = new Button(this);
            boolean isNew = status.equalsIgnoreCase("Dibuat");
            statusButton.setText(isNew ? "Proses" : "Selesaikan");
            statusButton.setAllCaps(false);
            statusButton.setTextSize(12);
            statusButton.setTextColor(Color.WHITE);
            statusButton.setBackgroundResource(R.drawable.bg_button_green);

            LinearLayout.LayoutParams statusParams =
                    new LinearLayout.LayoutParams(0, dp(40), 1f);
            statusParams.setMargins(dp(8), 0, 0, 0);
            statusButton.setLayoutParams(statusParams);
            statusButton.setOnClickListener(v ->
                    updateOrderStatus(code, isNew ? "Diproses" : "Selesai")
            );
            buttonRow.addView(statusButton);
        }

        card.addView(badge);
        card.addView(codeView);
        card.addView(customerView);
        card.addView(itemsView);
        card.addView(buttonRow);
        containerPesanan.addView(card);
    }

    private void updateOrderStatus(String code, String newStatus) {
        if (db.updateOrderStatus(code, newStatus)) {
            Toast.makeText(
                    this,
                    newStatus.equals("Selesai")
                            ? "Pesanan selesai"
                            : "Pesanan sedang diproses",
                    Toast.LENGTH_SHORT
            ).show();
            loadOrders();
        }
    }

    private TextView createText(String text, float size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(null, Typeface.BOLD);
        return view;
    }

    private void openDetail(
            String code,
            String customer,
            String items,
            int total,
            String paymentMethod,
            String status,
            long createdAt,
            long doneAt,
            int estimation
    ) {
        Intent intent = new Intent(this, AdminDetailPesanan.class);
        intent.putExtra("kode", code);
        intent.putExtra("nama", customer);
        intent.putExtra("pesanan", items);
        intent.putExtra("total_amount", total);
        intent.putExtra("payment_method", paymentMethod);
        intent.putExtra("status", status);
        intent.putExtra("created_at", createdAt);
        intent.putExtra("done_at", doneAt);
        intent.putExtra("estimasi_menit", estimation);
        startActivity(intent);
    }

    private void updateFilterAppearance() {
        TextView[] filters = {filterSemua, filterBaru, filterDiproses, filterSelesai};
        String[] values = {"Semua", "Dibuat", "Diproses", "Selesai"};

        for (int i = 0; i < filters.length; i++) {
            boolean selected = values[i].equals(activeFilter);
            filters[i].setBackgroundResource(
                    selected ? R.drawable.bg_button_green : R.drawable.bg_badge_green
            );
            filters[i].setTextColor(
                    selected ? Color.WHITE : Color.rgb(46, 125, 50)
            );
        }
    }

    private String displayStatus(String status) {
        return "Dibuat".equalsIgnoreCase(status) ? "Baru" : status;
    }

    private void setupNavigation() {
        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminDashboard.class));
            finish();
        });
        navPesanan.setOnClickListener(v ->
                Toast.makeText(this, "Anda berada di Daftar Pesanan", Toast.LENGTH_SHORT).show()
        );
        navStok.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminStok.class));
            finish();
        });
        navSetting.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDetailPesanan.class))
        );
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilter(activeFilter);
    }
}
