package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class KotakMasuk extends AppCompatActivity {

    private ImageView ivMenu, ivProfile;
    private LinearLayout containerNotifikasi;
    private TextView navHome, navFavorite, navCart;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kotak_masuk);

        db = new DatabaseHelper(this);

        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);
        containerNotifikasi = findViewById(R.id.container_notifikasi);

        navHome = findViewById(R.id.nav_home);
        navFavorite = findViewById(R.id.nav_favorite);
        navCart = findViewById(R.id.nav_cart);

        ivMenu.setOnClickListener(v -> finish());

        ivProfile.setOnClickListener(v ->
                startActivity(new Intent(KotakMasuk.this, Profil.class))
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(KotakMasuk.this, MainActivity.class))
        );

        navFavorite.setOnClickListener(v ->
                startActivity(new Intent(KotakMasuk.this, Menu.class))
        );

        navCart.setOnClickListener(v ->
                startActivity(new Intent(KotakMasuk.this, checkout.class))
        );

        loadNotifikasiCart();
    }

    private void loadNotifikasiCart() {
        Cursor cursor = db.getCart();

        if (cursor.getCount() == 0) {
            TextView kosong = new TextView(this);
            kosong.setText("Tidak ada notifikasi");
            kosong.setTextColor(0xFF2E7D32);
            kosong.setTextSize(13);
            kosong.setPadding(0, 20, 0, 0);
            containerNotifikasi.addView(kosong);
            cursor.close();
            return;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);
        card.setPadding(10, 10, 10, 10);
        card.setBackgroundColor(0xFF2E7D32);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                75
        );
        cardParams.setMargins(0, 6, 0, 0);
        card.setLayoutParams(cardParams);

        ImageView logo = new ImageView(this);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(60, 60);
        logo.setLayoutParams(logoParams);
        logo.setImageResource(R.drawable.logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        textBox.setPadding(12, 0, 0, 0);

        TextView title = new TextView(this);
        title.setText("KimbapJo");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(13);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView message = new TextView(this);
        message.setText("Jangan lupa checkout pesananmu!");
        message.setTextColor(0xFFFFFFFF);
        message.setTextSize(10);

        textBox.addView(title);
        textBox.addView(message);

        card.addView(logo);
        card.addView(textBox);

        card.setOnClickListener(v ->
                startActivity(new Intent(KotakMasuk.this, checkout.class))
        );

        containerNotifikasi.addView(card);
        cursor.close();
    }
}