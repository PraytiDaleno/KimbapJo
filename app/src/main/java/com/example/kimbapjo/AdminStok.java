package com.example.kimbapjo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminStok extends AppCompatActivity {

    private LinearLayout containerProduk;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stok);

        db = new DatabaseHelper(this);
        containerProduk = findViewById(R.id.containerProduk);

        tampilkanProduk();
    }

    private void tampilkanProduk() {
        containerProduk.removeAllViews();

        Cursor cursor = db.getAllStock();

        if (cursor == null || cursor.getCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada produk");
            empty.setTextSize(16);
            empty.setPadding(10, 20, 10, 20);
            containerProduk.addView(empty);
            return;
        }

        while (cursor.moveToNext()) {
            String namaMenu = cursor.getString(cursor.getColumnIndexOrThrow("menu_name"));
            String kategori = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            int stok = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));

            tambahCardProduk(namaMenu, kategori, stok);
        }

        cursor.close();
    }

    private void tambahCardProduk(String namaMenu, String kategori, int stokAwal) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 18);
        card.setLayoutParams(cardParams);
        card.setBackgroundColor(0xFFF1F8E9);

        TextView tvNama = new TextView(this);
        tvNama.setText(namaMenu);
        tvNama.setTextSize(16);
        tvNama.setTextColor(0xFF1B5E20);
        tvNama.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvKategori = new TextView(this);
        tvKategori.setText("Kategori: " + kategori);
        tvKategori.setTextSize(13);
        tvKategori.setTextColor(0xFF555555);

        TextView tvStok = new TextView(this);
        tvStok.setText("Stok: " + stokAwal);
        tvStok.setTextSize(15);
        tvStok.setTextColor(stokAwal <= 0 ? 0xFFD32F2F : 0xFF2E7D32);
        tvStok.setPadding(0, 10, 0, 10);

        LinearLayout rowButton = new LinearLayout(this);
        rowButton.setOrientation(LinearLayout.HORIZONTAL);

        Button btnMinus = new Button(this);
        btnMinus.setText("-");

        Button btnPlus = new Button(this);
        btnPlus.setText("+");

        rowButton.addView(btnMinus);
        rowButton.addView(btnPlus);

        final int[] stok = {stokAwal};

        btnMinus.setOnClickListener(v -> {
            if (stok[0] > 0) {
                stok[0]--;
                db.insertOrUpdateStock(namaMenu, kategori, stok[0]);
                tvStok.setText("Stok: " + stok[0]);
                tvStok.setTextColor(stok[0] <= 0 ? 0xFFD32F2F : 0xFF2E7D32);
            } else {
                Toast.makeText(this, "Stok sudah habis", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlus.setOnClickListener(v -> {
            stok[0]++;
            db.insertOrUpdateStock(namaMenu, kategori, stok[0]);
            tvStok.setText("Stok: " + stok[0]);
            tvStok.setTextColor(0xFF2E7D32);
        });

        card.addView(tvNama);
        card.addView(tvKategori);
        card.addView(tvStok);
        card.addView(rowButton);

        containerProduk.addView(card);
    }
}