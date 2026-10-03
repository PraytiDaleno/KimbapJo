package com.example.kimbapjo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class kuitansi extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnDone;

    private LinearLayout containerReceiptItems;
    private TextView tvPaymentMethod, tvTotalAmount, tvDate;

    private DatabaseHelper db;

    private String paymentMethod;
    private int totalAmount;
    private String orderCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuitansi);

        db = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btn_back);
        btnDone = findViewById(R.id.btn_done);

        containerReceiptItems = findViewById(R.id.container_receipt_items);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTotalAmount = findViewById(R.id.tv_total_price);
        tvDate = findViewById(R.id.tv_date);

        paymentMethod = getIntent().getStringExtra("payment_method");
        totalAmount = getIntent().getIntExtra("total_amount", 0);
        orderCode = getIntent().getStringExtra("order_code");

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = "N/A";
        }

        tvPaymentMethod.setText("Metode: " + paymentMethod);
        tvTotalAmount.setText(formatRupiah(totalAmount));

        String dateStr = new SimpleDateFormat(
                "dd MMMM yyyy, HH:mm",
                new Locale("id", "ID")
        ).format(new Date());

        tvDate.setText(dateStr);

        loadReceiptItems();

        btnBack.setOnClickListener(v -> finish());

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(kuitansi.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadReceiptItems() {
        containerReceiptItems.removeAllViews();

        Cursor cursor = db.getTransactions();

        if (cursor == null || cursor.getCount() == 0) {
            showEmpty("Belum ada transaksi");
            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String dbOrderCode = "";

            try {
                dbOrderCode = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
            } catch (Exception ignored) {}

            if (orderCode != null && !orderCode.isEmpty()) {
                if (!orderCode.equals(dbOrderCode)) {
                    continue;
                }
            }

            String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("product_price"));
            int image = cursor.getInt(cursor.getColumnIndexOrThrow("product_image"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            addItemView(name, price, image, quantity);
        }

        cursor.close();

        if (containerReceiptItems.getChildCount() == 0) {
            showEmpty("Data kuitansi tidak ditemukan");
        }
    }

    private void showEmpty(String message) {
        TextView empty = new TextView(this);
        empty.setText(message);
        empty.setTextSize(14);
        empty.setTextColor(0xFF666666);
        empty.setPadding(0, 12, 0, 12);
        containerReceiptItems.addView(empty);
    }

    private void addItemView(String name, int price, int image, int quantity) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 0, 0, 12);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(55, 55);
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageResource(image);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        textParams.setMargins(12, 0, 0, 0);
        textContainer.setLayoutParams(textParams);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(14);
        tvName.setTextColor(0xFF333333);

        TextView tvDetail = new TextView(this);
        tvDetail.setText(quantity + "x   " + formatRupiah(price));
        tvDetail.setTextSize(12);
        tvDetail.setTextColor(0xFF2E7D32);

        textContainer.addView(tvName);
        textContainer.addView(tvDetail);

        row.addView(img);
        row.addView(textContainer);

        containerReceiptItems.addView(row);
    }

    private String formatRupiah(int amount) {
        return String.format("Rp %,d", amount).replace(",", ".");
    }
}
