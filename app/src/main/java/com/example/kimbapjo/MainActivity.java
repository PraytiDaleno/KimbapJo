package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private DatabaseHelper db;

    private ImageView ivMenu, ivProfile;
    private TextView btnPesanPromo;
    private View categoryKimbap, categorySideDish, categoryRiceBowl, categoryEsSerut;
    private CardView cardItem1, cardItem2, cardItem3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        initViews();
        setupTopBar();
        setupCategoryButtons();
        setupPromoButton();
        setupRecommendationCards();
        setupBottomNav();
    }

    private void initViews() {
        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);

        btnPesanPromo = findViewById(R.id.btn_pesan_promo);

        categoryKimbap = findViewById(R.id.category_home_kimbap);
        categorySideDish = findViewById(R.id.category_home_sidedish);
        categoryRiceBowl = findViewById(R.id.category_home_ricebowl);
        categoryEsSerut = findViewById(R.id.category_home_esserut);

        cardItem1 = findViewById(R.id.card_item1);
        cardItem2 = findViewById(R.id.card_item2);
        cardItem3 = findViewById(R.id.card_item3);

        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupTopBar() {
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, Menu.class))
            );
        }

        if (ivProfile != null) {
            ivProfile.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, Profil.class))
            );
        }
    }

    private void setupCategoryButtons() {
        if (categoryKimbap != null) {
            categoryKimbap.setOnClickListener(v -> bukaKategori("Kimbap"));
        }

        if (categorySideDish != null) {
            categorySideDish.setOnClickListener(v -> bukaKategori("Side Dish"));
        }

        if (categoryRiceBowl != null) {
            categoryRiceBowl.setOnClickListener(v -> bukaKategori("Rice Bowl"));
        }

        if (categoryEsSerut != null) {
            categoryEsSerut.setOnClickListener(v -> bukaKategori("Es Serut"));
        }
    }

    private void setupPromoButton() {
        if (btnPesanPromo == null) return;

        btnPesanPromo.setOnClickListener(v -> {
            String name = "🥬 Kimbab Sayur";
            int price = 32000;
            int image = R.drawable.kimbab_sayur;

            if (!db.isMenuAvailable(name)) {
                Toast.makeText(MainActivity.this, "Stok Kimbab Sayur habis", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = db.addCart(name, price, image, 1);

            if (result) {
                Toast.makeText(MainActivity.this, "Kimbab Sayur masuk keranjang", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, checkout.class));
            } else {
                Toast.makeText(MainActivity.this, "Gagal masuk keranjang", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecommendationCards() {
        if (cardItem1 != null) {
            cardItem1.setOnClickListener(v -> tambahKeKeranjang(
                    "🍗 Kimbab Goreng Ayam",
                    39500,
                    R.drawable.kimbab_goreng_ayam
            ));
        }

        if (cardItem2 != null) {
            cardItem2.setOnClickListener(v -> tambahKeKeranjang(
                    "🐟 Kimbab Tuna Spicy",
                    33500,
                    R.drawable.kimbab_tuna_spicy
            ));
        }

        if (cardItem3 != null) {
            cardItem3.setOnClickListener(v -> tambahKeKeranjang(
                    "🧀 Kimbab Sosis Keju",
                    34500,
                    R.drawable.kimbab_sosis_keju
            ));
        }
    }

    private void tambahKeKeranjang(String name, int price, int image) {
        if (!db.isMenuAvailable(name)) {
            Toast.makeText(MainActivity.this, "Stok menu habis", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result = db.addCart(name, price, image, 1);

        if (result) {
            Toast.makeText(MainActivity.this, name + " masuk keranjang", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, checkout.class));
        } else {
            Toast.makeText(MainActivity.this, "Gagal masuk keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    private void bukaKategori(String kategori) {
        Intent intent = new Intent(MainActivity.this, Menu.class);
        intent.putExtra("kategori", kategori);
        startActivity(intent);
    }

    private void setupBottomNav() {
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.navigation_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                return true;

            } else if (id == R.id.navigation_menu) {
                openRootScreen(Menu.class);
                return true;

            } else if (id == R.id.navigation_cart) {
                openRootScreen(checkout.class);
                return true;

            } else if (id == R.id.navigation_profile) {
                openRootScreen(Profil.class);
                return true;
            }

            return false;
        });
    }

    private void openRootScreen(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        }
    }
}
