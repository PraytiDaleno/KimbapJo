package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class checkout extends AppCompatActivity {

    private LinearLayout containerItems;
    private MaterialButton btnCheckout;
    private ImageView btnBack;
    private TextView tvTotalAmount;
    private RadioGroup rgPayment;
    private RadioButton rbOnline, rbOffline;

    private DatabaseHelper db;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = new DatabaseHelper(this);

        containerItems = findViewById(R.id.container_items);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnBack = findViewById(R.id.btn_back);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rgPayment = findViewById(R.id.rg_payment);
        rbOnline = findViewById(R.id.rb_online);
        rbOffline = findViewById(R.id.rb_offline);

        displayCartItems();

        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            if (totalAmount == 0) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = rgPayment.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Pilih metode pembayaran dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == R.id.rb_online) {
                Intent intent = new Intent(checkout.this, checkout_online.class);
                intent.putExtra("total_amount", totalAmount);
                startActivity(intent);

            } else if (selectedId == R.id.rb_offline) {
                Intent intent = new Intent(checkout.this, checkout_offline.class);
                intent.putExtra("total_amount", totalAmount);
                startActivity(intent);
            }
        });
    }

    private void displayCartItems() {
        containerItems.removeAllViews();
        totalAmount = 0;

        Cursor cursor = db.getCart();

        if (cursor == null || cursor.getCount() == 0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Keranjang masih kosong");
            emptyText.setTextSize(16);
            emptyText.setTextColor(0xFF2E7D32);
            emptyText.setGravity(android.view.Gravity.CENTER);
            emptyText.setPadding(0, 60, 0, 60);

            containerItems.addView(emptyText);
            tvTotalAmount.setText("Total: Rp 0");

            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int cartId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            totalAmount += price * quantity;

            View itemView = getLayoutInflater().inflate(R.layout.item_checkout_row, containerItems, false);

            ImageView ivImage = itemView.findViewById(R.id.iv_item_image);
            TextView tvName = itemView.findViewById(R.id.tv_item_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_item_price);
            TextView tvQty = itemView.findViewById(R.id.tv_item_qty);
            Button btnMinus = itemView.findViewById(R.id.btn_minus);
            Button btnPlus = itemView.findViewById(R.id.btn_plus);

            ivImage.setImageResource(image);
            tvName.setText(name);
            tvPrice.setText(formatRupiah(price));
            tvQty.setText(String.valueOf(quantity));

            btnPlus.setOnClickListener(v -> {
                db.updateCartQuantity(cartId, quantity + 1);
                displayCartItems();
            });

            btnMinus.setOnClickListener(v -> {
                if (quantity > 1) {
                    db.updateCartQuantity(cartId, quantity - 1);
                } else {
                    db.deleteCartItem(cartId);
                }

                displayCartItems();
            });

            containerItems.addView(itemView);
        }

        cursor.close();
        tvTotalAmount.setText("Total: " + formatRupiah(totalAmount));
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}