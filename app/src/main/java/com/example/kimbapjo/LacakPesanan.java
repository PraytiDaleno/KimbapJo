package com.example.kimbapjo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LacakPesanan extends AppCompatActivity {

    private static final long ONE_MINUTE = 60 * 1000L;

    private ImageView btnMenu, btnProfile;
    private TextView tvEstimasiMenit, tvJamSelesai;
    private TextView tvStatusDibuat, tvStatusAdmin, tvStatusDibuatMenu;
    private TextView tvStatusSiap, tvStatusSelesai;
    private TextView tvWaktuDibuat, tvWaktuAdmin, tvWaktuDibuatMenu;
    private TextView tvWaktuSiap, tvWaktuSelesai;

    private CountDownTimer countDownTimer;
    private long waktuMulai;
    private long waktuSelesai;

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lacak_pesanan);

        initViews();

        int estimasiMenit = getIntent().getIntExtra("estimasi_menit", 20);
        waktuMulai = getIntent().getLongExtra(
                "waktu_mulai",
                System.currentTimeMillis()
        );
        waktuSelesai = getIntent().getLongExtra(
                "waktu_selesai",
                waktuMulai + (estimasiMenit * ONE_MINUTE)
        );

        tvJamSelesai.setText(formatTime(waktuSelesai));
        setupTimelineTimes();
        startCountdown();

        btnMenu.setOnClickListener(v ->
                startActivity(new Intent(LacakPesanan.this, MainActivity.class))
        );

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(LacakPesanan.this, Profil.class))
        );
    }

    private void initViews() {
        btnMenu = findViewById(R.id.btn_menu);
        btnProfile = findViewById(R.id.btn_profile);
        tvEstimasiMenit = findViewById(R.id.tvEstimasiMenit);
        tvJamSelesai = findViewById(R.id.tvJamSelesai);

        tvStatusDibuat = findViewById(R.id.tvStatusDibuat);
        tvStatusAdmin = findViewById(R.id.tvStatusAdmin);
        tvStatusDibuatMenu = findViewById(R.id.tvStatusDibuatMenu);
        tvStatusSiap = findViewById(R.id.tvStatusSiap);
        tvStatusSelesai = findViewById(R.id.tvStatusSelesai);

        tvWaktuDibuat = findViewById(R.id.tvWaktuDibuat);
        tvWaktuAdmin = findViewById(R.id.tvWaktuAdmin);
        tvWaktuDibuatMenu = findViewById(R.id.tvWaktuDibuatMenu);
        tvWaktuSiap = findViewById(R.id.tvWaktuSiap);
        tvWaktuSelesai = findViewById(R.id.tvWaktuSelesai);
    }

    private void setupTimelineTimes() {
        tvWaktuDibuat.setText(formatTime(waktuMulai));
        tvWaktuAdmin.setText(formatTime(waktuMulai + (2 * ONE_MINUTE)));
        tvWaktuDibuatMenu.setText(formatTime(waktuMulai + (5 * ONE_MINUTE)));
        tvWaktuSiap.setText(formatTime(waktuSelesai - ONE_MINUTE));
        tvWaktuSelesai.setText(formatTime(waktuSelesai));
    }

    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long sisaWaktu = Math.max(0L, waktuSelesai - System.currentTimeMillis());

        if (sisaWaktu == 0L) {
            showOrderFinished();
            return;
        }

        updateCountdown(sisaWaktu);

        countDownTimer = new CountDownTimer(sisaWaktu, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdown(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                showOrderFinished();
            }
        }.start();
    }

    private void updateCountdown(long millisUntilFinished) {
        long totalSeconds = (millisUntilFinished + 999L) / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;

        tvEstimasiMenit.setText(
                String.format(Locale.getDefault(), "Sisa Waktu %02d:%02d", minutes, seconds)
        );
        updateTimeline(System.currentTimeMillis());
    }

    private void showOrderFinished() {
        tvEstimasiMenit.setText("Sisa Waktu 00:00");
        updateTimeline(waktuSelesai);
    }

    private void updateTimeline(long currentTime) {
        long waktuAdmin = waktuMulai + (2 * ONE_MINUTE);
        long waktuMulaiDibuat = waktuMulai + (5 * ONE_MINUTE);

        int tahapAktif;
        if (currentTime >= waktuSelesai) {
            tahapAktif = 4;
        } else if (currentTime >= waktuSelesai - ONE_MINUTE) {
            tahapAktif = 3;
        } else if (currentTime >= waktuMulaiDibuat) {
            tahapAktif = 2;
        } else if (currentTime >= waktuAdmin) {
            tahapAktif = 1;
        } else {
            tahapAktif = 0;
        }

        updateStatusRow(tvStatusDibuat, tvWaktuDibuat, "Pesanan Dibuat", 0, tahapAktif);
        updateStatusRow(tvStatusAdmin, tvWaktuAdmin, "Diproses Admin", 1, tahapAktif);
        updateStatusRow(tvStatusDibuatMenu, tvWaktuDibuatMenu, "Sedang Dibuat", 2, tahapAktif);
        updateStatusRow(tvStatusSiap, tvWaktuSiap, "Siap Diambil", 3, tahapAktif);
        updateStatusRow(tvStatusSelesai, tvWaktuSelesai, "Selesai", 4, tahapAktif);
    }

    private void updateStatusRow(
            TextView statusView,
            TextView timeView,
            String label,
            int tahap,
            int tahapAktif
    ) {
        boolean completed = tahap < tahapAktif || tahapAktif == 4;
        boolean active = tahap == tahapAktif;
        int color = (completed || active)
                ? Color.rgb(46, 125, 50)
                : Color.rgb(158, 158, 158);
        String marker = completed ? "\u2713 " : active ? "\u25CF " : "\u25CB ";

        statusView.setText(marker + label);
        statusView.setTextColor(color);
        timeView.setTextColor(color);
    }

    private String formatTime(long timeMillis) {
        return timeFormat.format(new Date(timeMillis));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waktuSelesai > 0L) {
            startCountdown();
        }
    }

    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
    }
}
