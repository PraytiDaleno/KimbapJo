package com.example.kimbapjo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivMenu, ivProfile;
    private BottomNavigationView bottomNav;
    private GridLayout menuGrid;

    private View categoryKimbap, categorySideDish, categoryRiceBowl, categoryEsSerut;

    private CardView card1, card2, card3, card4, card5, card6, card7, card8;
    private CardView card9, card10, card11, card12, card13, card14, card15, card16;

    private DatabaseHelper db;

    private String kategoriDipilih = "Semua";

    private String[] menuNames;
    private String[] menuCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = new DatabaseHelper(this);

        initViews();
        setupMenuData();
        setupMenuClickListeners();
        setupCategoryClick();
        setupSearch();
        setupBottomNav();
        setupTopBar();

        String kategori = getIntent().getStringExtra("kategori");

        if (kategori != null && !kategori.isEmpty()) {
            showCategory(kategori);
        } else {
            filterByCategory("Semua");
        }
    }

    private void initViews() {
        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);

        etSearch = findViewById(R.id.et_search);
        bottomNav = findViewById(R.id.bottom_navigation);
        menuGrid = findViewById(R.id.menu_grid);

        categoryKimbap = findViewById(R.id.menu_category_kimbap);
        categorySideDish = findViewById(R.id.menu_category_sidedish);
        categoryRiceBowl = findViewById(R.id.menu_category_ricebowl);
        categoryEsSerut = findViewById(R.id.menu_category_esserut);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);
        card7 = findViewById(R.id.card7);
        card8 = findViewById(R.id.card8);
        card9 = findViewById(R.id.card9);
        card10 = findViewById(R.id.card10);
        card11 = findViewById(R.id.card11);
        card12 = findViewById(R.id.card12);
        card13 = findViewById(R.id.card13);
        card14 = findViewById(R.id.card14);
        card15 = findViewById(R.id.card15);
        card16 = findViewById(R.id.card16);
    }

    private void setupMenuData() {
        menuNames = new String[]{
                "kimbab ayam katsu",
                "kimbab tuna spicy",
                "kimbab beef bulgogi",
                "nasi ayam katsu sambal bawang",
                "nasi ayam katsu mentega",
                "nasi ayam katsu polos",
                "nasi ayam teriyaki telur",
                "nasi telur dadar",
                "kimbab goreng keju",
                "kimbab goreng ayam",
                "kimbab sayur",
                "side dish",
                "es serut",
                "kimbab goreng tuna spicy",
                "kimbab seafood",
                "kimbab bulgogi"
        };

        menuCategories = new String[]{
                "Kimbap",
                "Kimbap",
                "Kimbap",
                "Rice Bowl",
                "Rice Bowl",
                "Rice Bowl",
                "Rice Bowl",
                "Rice Bowl",
                "Kimbap",
                "Kimbap",
                "Kimbap",
                "Side Dish",
                "Es Serut",
                "Kimbap",
                "Kimbap",
                "Kimbap"
        };
    }

    private void setupCategoryClick() {
        if (categoryKimbap != null) {
            categoryKimbap.setOnClickListener(v -> showCategory("Kimbap"));
        }

        if (categorySideDish != null) {
            categorySideDish.setOnClickListener(v -> showCategory("Side Dish"));
        }

        if (categoryRiceBowl != null) {
            categoryRiceBowl.setOnClickListener(v -> showCategory("Rice Bowl"));
        }

        if (categoryEsSerut != null) {
            categoryEsSerut.setOnClickListener(v -> showCategory("Es Serut"));
        }
    }

    private void setupMenuClickListeners() {
        setupCard(card1, "🍗 Kimbab Ayam Katsu", 31500, "Rp 31.500",
                "Kimbab dengan isian ayam katsu crispy, nasi, sayuran segar, dan saus spesial.",
                R.drawable.kimbab_ayam_katsu);

        setupCard(card2, "🐟 Kimbab Tuna Spicy", 32000, "Rp 32.000",
                "Kimbab dengan isian tuna pedas, sayuran segar, dan saus spicy khas Kimbap.Jo.",
                R.drawable.kimbab_tuna_spicy);

        setupCard(card3, "🥩 Kimbab Beef Bulgogi", 45000, "Rp 45.000",
                "Kimbab dengan isian daging beef bulgogi manis gurih, telur, dan sayuran.",
                R.drawable.nasi_ayam_katsu_blackpepper);

        setupCard(card4, "🍚 Nasi Ayam Katsu Sambal Bawang", 28500, "Rp 28.500",
                "Nasi hangat dengan ayam katsu crispy dan sambal bawang pedas gurih.",
                R.drawable.nasi_ayam_katsu_sambal_bawang);

        setupCard(card5, "🧈 Nasi Ayam Katsu Mentega", 52000, "Rp 52.000",
                "Nasi dengan ayam katsu saus mentega, rasa manis gurih dan harum.",
                R.drawable.nasi_ayam_katsu_mentega);

        setupCard(card6, "🍚 Nasi Ayam Katsu Polos", 48000, "Rp 48.000",
                "Nasi putih dengan ayam katsu crispy original dan saus pelengkap.",
                R.drawable.nasi_ayam_goreng_katsu_polos);

        setupCard(card7, "🍳 Nasi Ayam Teriyaki + Telur", 38000, "Rp 38.000",
                "Nasi ayam teriyaki manis gurih lengkap dengan telur dan sayuran.",
                R.drawable.nasi_ayam_teriyaki_telur);

        setupCard(card8, "🍳 Nasi Telur Dadar", 42000, "Rp 42.000",
                "Nasi hangat dengan telur dadar tebal, saus, dan pelengkap sayuran.",
                R.drawable.nasi_telur_dadar);

        setupCard(card9, "🧀 Kimbab Goreng Keju", 31500, "Rp 31.500",
                "Kimbab goreng crispy dengan isian keju meleleh dan sayuran.",
                R.drawable.kimbab_sosis_keju);

        setupCard(card10, "🍗 Kimbab Goreng Ayam", 30000, "Rp 30.000",
                "Kimbab goreng renyah dengan isian ayam gurih dan saus spesial.",
                R.drawable.kimbab_goreng_ayam);

        setupCard(card11, "🥬 Kimbab Sayur", 32000, "Rp 32.000",
                "Kimbab sehat dengan isian sayuran segar, telur, dan saus khas.",
                R.drawable.kimbab_sayur);

        setupCard(card12, "🍽️ Side Dish", 25000, "Rp 25.000",
                "Paket makanan pendamping seperti kimchi, acar, dan lauk kecil.",
                R.drawable.side_dish);

        setupCard(card13, "🍧 Es Serut", 27000, "Rp 27.000",
                "Es serut segar dengan sirup, susu, jelly, dan topping manis.",
                R.drawable.es_serut);

        setupCard(card14, "🐟 Kimbab Goreng Tuna Spicy", 40000, "Rp 40.000",
                "Kimbab goreng dengan isian tuna pedas, crispy di luar dan gurih di dalam.",
                R.drawable.kimbab_goreng_tuna_spicy);

        setupCard(card15, "🦐 Kimbab Seafood", 44000, "Rp 44.000",
                "Kimbab dengan isian seafood seperti udang, cumi, dan sayuran segar.",
                R.drawable.kimbab_tuna_spicy);

        setupCard(card16, "🥩 Kimbab Bulgogi", 43000, "Rp 43.000",
                "Kimbab dengan isian daging bulgogi, telur, sayuran, dan saus khas Korea.",
                R.drawable.kimbab_sosis_keju);
    }

    private void setupCard(CardView card, String name, int price, String priceText, String description, int image) {
        if (card == null) return;

        card.setOnClickListener(v -> addToCart(name, price, image));

        card.setOnLongClickListener(v -> {
            openProductDetail(name, priceText, description, image);
            return true;
        });
    }

    private void addToCart(String name, int price, int image) {
        if (!db.isMenuAvailable(name)) {
            Toast.makeText(this, "Stok menu habis", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result = db.addCart(name, price, image, 1);

        if (result) {
            Toast.makeText(this, name + " masuk ke keranjang", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal masuk ke keranjang", Toast.LENGTH_SHORT).show();
        }
    }

    private void openProductDetail(String name, String price, String description, int imageRes) {
        if (!db.isMenuAvailable(name)) {
            Toast.makeText(this, "Stok menu habis", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Menu.this, detail_menu.class);
        intent.putExtra("product_name", name);
        intent.putExtra("product_price", price);
        intent.putExtra("product_description", description);
        intent.putExtra("product_image", imageRes);
        startActivity(intent);
    }

    private void showCategory(String category) {
        kategoriDipilih = category;
        updateCategorySelection();

        if (etSearch != null) {
            etSearch.setText("");
        }

        filterByCategory(category);
    }

    private void updateCategorySelection() {
        View[] categoryViews = {
                categoryKimbap, categorySideDish, categoryRiceBowl, categoryEsSerut
        };
        String[] categories = {"Kimbap", "Side Dish", "Rice Bowl", "Es Serut"};

        for (int i = 0; i < categoryViews.length; i++) {
            if (categoryViews[i] != null) {
                categoryViews[i].setAlpha(
                        kategoriDipilih.equalsIgnoreCase("Semua")
                                || categories[i].equalsIgnoreCase(kategoriDipilih)
                                ? 1f : 0.65f
                );
            }
        }
    }

    private void filterByCategory(String kategori) {
        kategoriDipilih = kategori;
        rebuildMenuGrid("");
    }

    private void setupSearch() {
        if (etSearch == null) return;

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterMenu(String keyword) {
        rebuildMenuGrid(keyword.toLowerCase().trim());
    }

    private void rebuildMenuGrid(String keyword) {
        if (menuGrid == null) return;

        CardView[] cards = getAllCards();
        menuGrid.removeAllViews();

        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) continue;

            boolean cocokKategori = kategoriDipilih.equalsIgnoreCase("Semua")
                    || menuCategories[i].equalsIgnoreCase(kategoriDipilih);

            boolean cocokSearch = keyword.isEmpty()
                    || menuNames[i].contains(keyword);

            if (cocokKategori && cocokSearch) {
                cards[i].setVisibility(View.VISIBLE);
                menuGrid.addView(cards[i]);
            } else {
                cards[i].setVisibility(View.GONE);
            }
        }
    }

    private CardView[] getAllCards() {
        return new CardView[]{
                card1, card2, card3, card4,
                card5, card6, card7, card8,
                card9, card10, card11, card12,
                card13, card14, card15, card16
        };
    }

    private void setupTopBar() {
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                kategoriDipilih = "Semua";
                updateCategorySelection();
                filterByCategory("Semua");
            });
        }

        if (ivProfile != null) {
            ivProfile.setOnClickListener(v ->
                    startActivity(new Intent(Menu.this, Profil.class))
            );
        }
    }

    private void setupBottomNav() {
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.navigation_menu);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                openRootScreen(MainActivity.class);
                finish();
                return true;

            } else if (itemId == R.id.navigation_menu) {
                return true;

            } else if (itemId == R.id.navigation_cart) {
                openRootScreen(checkout.class);
                return true;

            } else if (itemId == R.id.navigation_profile) {
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
            bottomNav.setSelectedItemId(R.id.navigation_menu);
        }
    }
}
