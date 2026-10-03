package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Aktivitas extends AppCompatActivity {

    private ImageView ivMenu, ivProfile;
    private LinearLayout containerAktivitas;
    private TextView navHome, navFavorite, navCart;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktivitas);

        db = new DatabaseHelper(this);

        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);
        containerAktivitas = findViewById(R.id.container_aktivitas);

        navHome = findViewById(R.id.nav_home);
        navFavorite = findViewById(R.id.nav_favorite);
        navCart = findViewById(R.id.nav_cart);

        ivMenu.setOnClickListener(v -> finish());

        ivProfile.setOnClickListener(v ->
                startActivity(new Intent(Aktivitas.this, Profil.class))
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(Aktivitas.this, MainActivity.class))
        );

        navFavorite.setOnClickListener(v ->
                startActivity(new Intent(Aktivitas.this, Menu.class))
        );

        navCart.setOnClickListener(v ->
                startActivity(new Intent(Aktivitas.this, checkout.class))
        );

        loadAktivitas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAktivitas();
    }

    private void loadAktivitas() {
        containerAktivitas.removeAllViews();

        Cursor cursor = db.getTransactions();

        if (cursor == null || cursor.getCount() == 0) {
            TextView kosong = new TextView(this);
            kosong.setText("Belum ada aktivitas pesanan");
            kosong.setTextSize(15);
            kosong.setTextColor(0xFF2E7D32);
            kosong.setGravity(Gravity.CENTER);
            kosong.setPadding(0, 50, 0, 0);

            containerAktivitas.addView(kosong);

            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
            String checkoutType = cursor.getString(cursor.getColumnIndexOrThrow("checkout_type"));
            int totalAmount = cursor.getInt(cursor.getColumnIndexOrThrow("total_amount"));

            addActivityCard(
                    nama,
                    qty,
                    image,
                    price,
                    paymentMethod,
                    checkoutType,
                    totalAmount
            );
        }

        cursor.close();
    }

    private void addActivityCard(
            String nama,
            int qty,
            int image,
            int price,
            String paymentMethod,
            String checkoutType,
            int totalAmount
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundColor(0xFF2E7D32);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        120
                );

        cardParams.setMargins(0, 10, 0, 0);
        card.setLayoutParams(cardParams);

        ImageView img = new ImageView(this);

        LinearLayout.LayoutParams imgParams =
                new LinearLayout.LayoutParams(90, 90);

        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageResource(image);

        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        textBox.setPadding(16, 0, 0, 0);

        LinearLayout.LayoutParams textBoxParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        textBox.setLayoutParams(textBoxParams);

        TextView tvNama = new TextView(this);
        tvNama.setText(nama + " - Porsi " + qty);
        tvNama.setTextColor(0xFFFFFFFF);
        tvNama.setTextSize(15);
        tvNama.setTypeface(null, Typeface.BOLD);

        TextView tvHarga = new TextView(this);
        tvHarga.setText(formatRupiah(price));
        tvHarga.setTextColor(0xFFFFFFFF);
        tvHarga.setTextSize(13);

        TextView tvMetode = new TextView(this);
        tvMetode.setText(checkoutType + " • " + paymentMethod);
        tvMetode.setTextColor(0xFFFFFFFF);
        tvMetode.setTextSize(13);

        TextView tvTotal = new TextView(this);
        tvTotal.setText("Total: " + formatRupiah(totalAmount));
        tvTotal.setTextColor(0xFFFFFFFF);
        tvTotal.setTextSize(13);

        TextView tvPesan = new TextView(this);
        tvPesan.setText("Pesan Ulang");
        tvPesan.setTextColor(0xFFFFFFFF);
        tvPesan.setTextSize(13);
        tvPesan.setTypeface(null, Typeface.BOLD);

        textBox.addView(tvNama);
        textBox.addView(tvHarga);
        textBox.addView(tvMetode);
        textBox.addView(tvTotal);
        textBox.addView(tvPesan);

        card.addView(img);
        card.addView(textBox);

        card.setOnClickListener(v -> {
            db.addCart(nama, price, image, qty);
            Intent intent = new Intent(Aktivitas.this, checkout.class);
            startActivity(intent);
        });

        containerAktivitas.addView(card);
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}