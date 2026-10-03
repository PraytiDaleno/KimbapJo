package com.example.kimbapjo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;

public class detail_menu extends AppCompatActivity {

    private ImageView btnBack, ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvQuantity;
    private MaterialButton btnOrder;
    private CardView btnMinus, btnPlus;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_menu);

        // Inisialisasi View
        btnBack = findViewById(R.id.btn_back);
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductDescription = findViewById(R.id.tv_product_description);
        btnOrder = findViewById(R.id.btn_order);
        tvQuantity = findViewById(R.id.tv_quantity);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);

        // Ambil data dari Intent
        String productName = getIntent().getStringExtra("product_name");
        String productPrice = getIntent().getStringExtra("product_price");
        String productDescription = getIntent().getStringExtra("product_description");
        int productImage = getIntent().getIntExtra("product_image", R.drawable.kimbab_ayam_katsu);

        // Set data ke View
        tvProductName.setText(productName);
        tvProductPrice.setText(productPrice);
        tvProductDescription.setText(productDescription);
        ivProductImage.setImageResource(productImage);

        // Setup Quantity Buttons
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // Tombol PESAN - TAMPILKAN PILIHAN ONLINE/OFFLINE
        btnOrder.setOnClickListener(v -> {
            showCheckoutOptions(productName, productPrice, productDescription, productImage);
        });
    }

    private void showCheckoutOptions(String productName, String productPrice, String productDescription, int productImage) {
        // Buat dialog pilihan
        String[] options = {"🚚 Online (Delivery)", "🏪 Offline (Ambil di Toko)"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Metode Checkout");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Pilih ONLINE
                    goToCheckoutOnline(productName, productPrice, productDescription, productImage);
                } else {
                    // Pilih OFFLINE
                    goToCheckoutOffline(productName, productPrice, productDescription, productImage);
                }
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void goToCheckoutOnline(String productName, String productPrice, String productDescription, int productImage) {
        Intent intent = new Intent(detail_menu.this, checkout_online.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_price", productPrice);
        intent.putExtra("product_description", productDescription);
        intent.putExtra("product_image", productImage);
        intent.putExtra("product_quantity", quantity);
        startActivity(intent);
    }

    private void goToCheckoutOffline(String productName, String productPrice, String productDescription, int productImage) {
        Intent intent = new Intent(detail_menu.this, checkout_offline.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_price", productPrice);
        intent.putExtra("product_description", productDescription);
        intent.putExtra("product_image", productImage);
        intent.putExtra("product_quantity", quantity);
        startActivity(intent);
    }
}