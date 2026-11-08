package com.example.pengingatkuota;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    PrefsManager prefs;
    DbHelper db;
    EditText etNama, etBatas, etTotal, etHari;
    Switch swDark;
    Button btnSimpan, btnAktifkan, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = new PrefsManager(this);
        db = new DbHelper(this);

        etNama = findViewById(R.id.etNama);
        etBatas = findViewById(R.id.etBatas);
        etTotal = findViewById(R.id.etTotal);
        etHari = findViewById(R.id.etHari);
        swDark = findViewById(R.id.swDark);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnAktifkan = findViewById(R.id.btnAktifkan);
        btnReset = findViewById(R.id.btnReset);

        btnSimpan.setOnClickListener(v -> simpanPengaturan());
        btnAktifkan.setOnClickListener(v -> aktifkanPaket());
        btnReset.setOnClickListener(v -> konfirmasiReset());
    }

    @Override
    protected void onResume() {
        super.onResume();
        etNama.setText(prefs.getNama());
        etBatas.setText(String.format(Locale.US, "%.2f", prefs.getBatas()));
        swDark.setChecked(prefs.isDark());
        if (prefs.adaPaket()) {
            etTotal.setText(String.format(Locale.US, "%.2f", prefs.getTotal()));
        }
    }

    private Float parseAngka(EditText et) {
        String s = et.getText().toString().trim().replace(',', '.');
        if (s.isEmpty()) return null;
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void simpanPengaturan() {
        String nama = etNama.getText().toString().trim();
        Float batas = parseAngka(etBatas);
        if (nama.isEmpty() || batas == null || batas < 0f) {
            Toast.makeText(this, "Isi nama dan batas peringatan dengan benar", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.simpanPengaturan(nama, batas, swDark.isChecked());
        prefs.terapkanTema();
        Toast.makeText(this, "Pengaturan tersimpan", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void aktifkanPaket() {
        Float total = parseAngka(etTotal);
        Float hari = parseAngka(etHari);
        if (total == null || total <= 0f || hari == null || hari < 1f) {
            Toast.makeText(this, "Isi total kuota (GB) dan masa aktif (hari)", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.aktifkanPaket(total, Math.round(hari));
        db.hapusSemua();
        Toast.makeText(this, "Paket diaktifkan", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void konfirmasiReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reset semua data?")
                .setMessage("Pengaturan, paket, dan riwayat akan dihapus.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Reset", (d, w) -> {
                    prefs.reset();
                    db.hapusSemua();
                    prefs.terapkanTema();
                    Toast.makeText(this, "Data direset", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}