package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class checkout_online extends AppCompatActivity {

    private ImageView btnBack, ivCheckoutImage;
    private MaterialButton btnConfirmPayment;

    private TextView tvCheckoutName, tvCheckoutQuantity, tvCheckoutPrice;
    private TextView tvSubtotal, tvShipping, tvTotalAmount;

    private RadioButton rbTransferBank, rbQris, rbEwallet, rbCod;
    private LinearLayout llTransferBank, llQris, llEwallet, llCod;

    private String selectedPaymentMethod = "";

    private int subtotal = 0;
    private int totalAmount = 0;
    private final int shippingFee = 10000;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_online);

        db = new DatabaseHelper(this);

        initViews();
        loadCartData();
        setupPaymentMethods();

        btnBack.setOnClickListener(v -> finish());

        btnConfirmPayment.setOnClickListener(v -> {
            if (totalAmount == 0) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Silakan pilih metode pembayaran terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod.equals("Transfer Bank")) {
                showTransferBankDialog();
                return;
            }

            String orderCode = saveTransactionFromCart();

            if (selectedPaymentMethod.equals("QRIS")
                    || selectedPaymentMethod.equals("E-Wallet")) {

                Intent intent = new Intent(checkout_online.this, qr_offline.class);
                intent.putExtra("payment_method", selectedPaymentMethod);
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("checkout_type", "Online");
                intent.putExtra("order_code", orderCode);
                startActivity(intent);

            } else if (selectedPaymentMethod.equals("COD")) {
                createSuccessfulOrder(orderCode);

                Intent intent = new Intent(checkout_online.this, order_sukses.class);
                intent.putExtra("payment_method", selectedPaymentMethod);
                intent.putExtra("total_amount", totalAmount);
                startActivity(intent);
            }

            db.clearCart();
            finish();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnConfirmPayment = findViewById(R.id.btn_confirm_payment);

        ivCheckoutImage = findViewById(R.id.iv_checkout_image);
        tvCheckoutName = findViewById(R.id.tv_checkout_name);
        tvCheckoutQuantity = findViewById(R.id.tv_checkout_quantity);
        tvCheckoutPrice = findViewById(R.id.tv_checkout_price);

        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotalAmount = findViewById(R.id.tv_total_amount);

        rbTransferBank = findViewById(R.id.rb_transfer_bank);
        rbQris = findViewById(R.id.rb_qris);
        rbEwallet = findViewById(R.id.rb_ewallet);
        rbCod = findViewById(R.id.rb_cod);

        llTransferBank = findViewById(R.id.ll_transfer_bank);
        llQris = findViewById(R.id.ll_qris);
        llEwallet = findViewById(R.id.ll_ewallet);
        llCod = findViewById(R.id.ll_cod);
    }

    private void loadCartData() {
        Cursor cursor = db.getCart();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();

            tvCheckoutName.setText("Tidak ada pesanan");
            tvCheckoutQuantity.setText("0x");
            tvCheckoutPrice.setText(formatRupiah(0));
            tvSubtotal.setText(formatRupiah(0));
            tvShipping.setText(formatRupiah(0));
            tvTotalAmount.setText(formatRupiah(0));

            totalAmount = 0;

            if (cursor != null) cursor.close();
            return;
        }

        StringBuilder orderNames = new StringBuilder();
        int totalQty = 0;
        int firstImage = R.drawable.kimbab_ayam_katsu;

        subtotal = 0;
        boolean isFirstItem = true;

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            if (isFirstItem) {
                firstImage = image;
                isFirstItem = false;
            }

            subtotal += price * quantity;
            totalQty += quantity;

            orderNames.append(name)
                    .append(" x")
                    .append(quantity)
                    .append("\n");
        }

        cursor.close();

        totalAmount = subtotal + shippingFee;

        ivCheckoutImage.setImageResource(firstImage);
        tvCheckoutName.setText(orderNames.toString().trim());
        tvCheckoutQuantity.setText(totalQty + "x");
        tvCheckoutPrice.setText(formatRupiah(subtotal));

        tvSubtotal.setText(formatRupiah(subtotal));
        tvShipping.setText(formatRupiah(shippingFee));
        tvTotalAmount.setText(formatRupiah(totalAmount));
    }

    private String saveTransactionFromCart() {
        Cursor cursor = db.getCart();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return "";
        }

        String orderId = db.generateOrderCode();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            db.addTransaction(
                    orderId,
                    name,
                    price,
                    image,
                    quantity,
                    selectedPaymentMethod,
                    "Online",
                    totalAmount
            );
        }

        cursor.close();
        return orderId;
    }

    private void createSuccessfulOrder(String orderCode) {
        db.createOrderFromTransactions(
                orderCode,
                db.getUserNameById(new UserSession(this).getUserId()),
                selectedPaymentMethod,
                totalAmount,
                20
        );
    }

    private void setupPaymentMethods() {
        llTransferBank.setOnClickListener(v -> selectPayment(rbTransferBank, "Transfer Bank"));
        llQris.setOnClickListener(v -> selectPayment(rbQris, "QRIS"));
        llEwallet.setOnClickListener(v -> selectPayment(rbEwallet, "E-Wallet"));
        llCod.setOnClickListener(v -> selectPayment(rbCod, "COD"));

        rbTransferBank.setOnClickListener(v -> selectPayment(rbTransferBank, "Transfer Bank"));
        rbQris.setOnClickListener(v -> selectPayment(rbQris, "QRIS"));
        rbEwallet.setOnClickListener(v -> selectPayment(rbEwallet, "E-Wallet"));
        rbCod.setOnClickListener(v -> selectPayment(rbCod, "COD"));
    }

    private void selectPayment(RadioButton selectedRadioButton, String method) {
        rbTransferBank.setChecked(false);
        rbQris.setChecked(false);
        rbEwallet.setChecked(false);
        rbCod.setChecked(false);

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
            String orderCode = saveTransactionFromCart();
            createSuccessfulOrder(orderCode);

            Toast.makeText(
                    checkout_online.this,
                    "Pembayaran berhasil dikonfirmasi",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(checkout_online.this, order_sukses.class);
            intent.putExtra("payment_method", selectedPaymentMethod);
            intent.putExtra("total_amount", totalAmount);
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
