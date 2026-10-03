package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class checkout_offline extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnOrder;
    private TextView tvTotalAmount, tvSubtotal;
    private LinearLayout containerItems;

    private RadioButton rbTransferBank, rbQris, rbEwallet, rbTunai;
    private LinearLayout llTransferBank, llQris, llEwallet, llTunai;

    private String selectedPaymentMethod = "";
    private int totalAmount = 0;
    private int tableNumber = 1;
    private String orderCode = "KBP001";

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_offline);

        db = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btn_back);
        btnOrder = findViewById(R.id.btn_order);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        containerItems = findViewById(R.id.container_items);

        rbTransferBank = findViewById(R.id.rb_transfer_bank);
        rbQris = findViewById(R.id.rb_qris);
        rbEwallet = findViewById(R.id.rb_ewallet);
        rbTunai = findViewById(R.id.rb_tunai);

        llTransferBank = findViewById(R.id.ll_transfer_bank);
        llQris = findViewById(R.id.ll_qris);
        llEwallet = findViewById(R.id.ll_ewallet);
        llTunai = findViewById(R.id.ll_tunai);

        tableNumber = db.getTransactionCount() + 1;
        orderCode = db.generateOrderCode();

        loadCartItems();
        setupPaymentMethods();

        btnBack.setOnClickListener(v -> finish());

        btnOrder.setOnClickListener(v -> {
            if (totalAmount == 0) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Pilih metode pembayaran dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod.equals("Transfer Bank")) {
                showTransferBankDialog();
                return;
            }

            if (selectedPaymentMethod.equals("QRIS") || selectedPaymentMethod.equals("E-Wallet")) {
                saveTransactionFromCart();

                Intent intent = new Intent(checkout_offline.this, qr_offline.class);
                intent.putExtra("payment_method", selectedPaymentMethod);
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("checkout_type", "Offline");
                intent.putExtra("table_number", tableNumber);
                intent.putExtra("order_code", orderCode);
                startActivity(intent);

                db.clearCart();
                finish();
                return;
            }

            if (selectedPaymentMethod.equals("Tunai")) {
                saveTransactionFromCart();
                createSuccessfulOrder();

                Intent intent = new Intent(checkout_offline.this, OrderSukses_Offline.class);
                intent.putExtra("payment_method", selectedPaymentMethod);
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("table_number", tableNumber);
                intent.putExtra("order_code", orderCode);
                startActivity(intent);

                db.clearCart();
                finish();
            }
        });
    }

    private void loadCartItems() {
        containerItems.removeAllViews();
        totalAmount = 0;

        Cursor cursor = db.getCart();

        if (cursor == null || cursor.getCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("Keranjang masih kosong");
            empty.setTextSize(14);
            empty.setTextColor(0xFF2E7D32);
            empty.setPadding(0, 20, 0, 20);
            containerItems.addView(empty);

            tvSubtotal.setText("Rp 0");
            tvTotalAmount.setText("Rp 0");

            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            totalAmount += price * quantity;

            View itemView = getLayoutInflater().inflate(
                    R.layout.item_checkout_row,
                    containerItems,
                    false
            );

            ImageView ivImage = itemView.findViewById(R.id.iv_item_image);
            TextView tvName = itemView.findViewById(R.id.tv_item_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_item_price);
            TextView tvQty = itemView.findViewById(R.id.tv_item_qty);

            ivImage.setImageResource(image);
            tvName.setText(name);
            tvPrice.setText(formatRupiah(price));
            tvQty.setText("x" + quantity);

            containerItems.addView(itemView);
        }

        cursor.close();

        tvSubtotal.setText(formatRupiah(totalAmount));
        tvTotalAmount.setText(formatRupiah(totalAmount));
    }

    private void saveTransactionFromCart() {
        Cursor cursor = db.getCart();

        if (cursor == null || cursor.getCount() == 0) {
            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            db.addTransaction(
                    orderCode,
                    name,
                    price,
                    image,
                    quantity,
                    selectedPaymentMethod,
                    "Offline",
                    totalAmount
            );
        }

        cursor.close();
    }

    private void createSuccessfulOrder() {
        db.createOrderFromTransactions(
                orderCode,
                db.getUserNameById(new UserSession(this).getUserId()),
                selectedPaymentMethod,
                totalAmount,
                20
        );
    }

    private void setupPaymentMethods() {
        llTransferBank.setOnClickListener(v ->
                selectPayment(rbTransferBank, "Transfer Bank")
        );

        llQris.setOnClickListener(v ->
                selectPayment(rbQris, "QRIS")
        );

        llEwallet.setOnClickListener(v ->
                selectPayment(rbEwallet, "E-Wallet")
        );

        llTunai.setOnClickListener(v ->
                selectPayment(rbTunai, "Tunai")
        );

        rbTransferBank.setOnClickListener(v ->
                selectPayment(rbTransferBank, "Transfer Bank")
        );

        rbQris.setOnClickListener(v ->
                selectPayment(rbQris, "QRIS")
        );

        rbEwallet.setOnClickListener(v ->
                selectPayment(rbEwallet, "E-Wallet")
        );

        rbTunai.setOnClickListener(v ->
                selectPayment(rbTunai, "Tunai")
        );
    }

    private void selectPayment(RadioButton selectedRadioButton, String method) {
        rbTransferBank.setChecked(false);
        rbQris.setChecked(false);
        rbEwallet.setChecked(false);
        rbTunai.setChecked(false);

        selectedRadioButton.setChecked(true);
        selectedPaymentMethod = method;
    }

    private void showTransferBankDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Transfer Bank");
        builder.setMessage(
                "Lakukan transfer ke rekening berikut:\n\n" +
                        "Bank BCA\n" +
                        "No. Rekening: 1234567890\n" +
                        "Atas Nama: HUBUP KIMBAP JO\n\n" +
                        "Total Transfer: " + formatRupiah(totalAmount) + "\n\n" +
                        "Setelah transfer, konfirmasi pesanan Anda."
        );

        builder.setNegativeButton("BATAL", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("SAYA SUDAH TRANSFER", (dialog, which) -> {
            saveTransactionFromCart();
            createSuccessfulOrder();

            Intent intent = new Intent(checkout_offline.this, OrderSukses_Offline.class);
            intent.putExtra("payment_method", selectedPaymentMethod);
            intent.putExtra("total_amount", totalAmount);
            intent.putExtra("table_number", tableNumber);
            intent.putExtra("order_code", orderCode);
            startActivity(intent);

            db.clearCart();
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}
