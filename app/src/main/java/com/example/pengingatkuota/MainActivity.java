package com.example.pengingatkuota;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    PrefsManager prefs;
    DbHelper db;

    ListView listRiwayat;
    TextView tvSapaan, tvSisa, tvPersen, tvStatus, tvMasaAktif, tvSaran, tvTotalPakai, tvKosong;
    ProgressBar progress;
    EditText etJumlah, etCatatan;
    Button btnCatat, btnPengaturan;

    final List<DbHelper.Riwayat> data = new ArrayList<>();
    ArrayAdapter<DbHelper.Riwayat> adapter;
    final SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new PrefsManager(this);
        prefs.terapkanTema();
        setContentView(R.layout.activity_main);
        db = new DbHelper(this);

        listRiwayat = findViewById(R.id.listRiwayat);
        View header = getLayoutInflater().inflate(R.layout.header_dashboard, listRiwayat, false);
        listRiwayat.addHeaderView(header, null, false);

        tvSapaan = header.findViewById(R.id.tvSapaan);
        tvSisa = header.findViewById(R.id.tvSisa);
        tvPersen = header.findViewById(R.id.tvPersen);
        tvStatus = header.findViewById(R.id.tvStatus);
        tvMasaAktif = header.findViewById(R.id.tvMasaAktif);
        tvSaran = header.findViewById(R.id.tvSaran);
        tvTotalPakai = header.findViewById(R.id.tvTotalPakai);
        tvKosong = header.findViewById(R.id.tvKosong);
        progress = header.findViewById(R.id.progress);
        etJumlah = header.findViewById(R.id.etJumlah);
        etCatatan = header.findViewById(R.id.etCatatan);
        btnCatat = header.findViewById(R.id.btnCatat);
        btnPengaturan = header.findViewById(R.id.btnPengaturan);

        adapter = new ArrayAdapter<DbHelper.Riwayat>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                DbHelper.Riwayat r = getItem(position);
                TextView t1 = v.findViewById(android.R.id.text1);
                TextView t2 = v.findViewById(android.R.id.text2);
                t1.setText(String.format(Locale.getDefault(), "- %.2f GB", r.jumlah));
                String cat = (r.catatan == null || r.catatan.isEmpty()) ? "" : "  |  " + r.catatan;
                t2.setText(fmt.format(new Date(r.waktu)) + cat);
                return v;
            }
        };
        listRiwayat.setAdapter(adapter);

        btnCatat.setOnClickListener(v -> catatPemakaian());
        btnPengaturan.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        listRiwayat.setOnItemLongClickListener((parent, view, position, id) -> {
            int idx = position - listRiwayat.getHeaderViewsCount();
            if (idx >= 0 && idx < data.size()) konfirmasiHapus(data.get(idx));
            return true;
        });
    }

    // Pengaturan dan status paket dibaca di sini
    @Override
    protected void onResume() {
        super.onResume();
        tampilkan();
    }

    private void tampilkan() {
        tvSapaan.setText("Halo, " + prefs.getNama() + "!");

        if (!prefs.adaPaket()) {
            tvSisa.setText("- GB");
            progress.setProgress(0);
            tvPersen.setText("");
            tvStatus.setText("Belum ada paket kuota");
            tvStatus.setTextColor(Color.GRAY);
            tvMasaAktif.setText("Buka Pengaturan, lalu aktifkan paket kuota.");
            tvSaran.setText("");
        } else {
            float total = prefs.getTotal();
            float sisa = prefs.getSisa();
            float batas = prefs.getBatas();
            int hari = prefs.getHariTersisa();
            int persen = Math.round(sisa / total * 100f);

            tvSisa.setText(String.format(Locale.getDefault(), "%.2f GB", sisa));
            progress.setProgress(Math.max(0, Math.min(100, persen)));
            tvPersen.setText(String.format(Locale.getDefault(),
                    "%d%% dari paket %.2f GB", persen, total));

            if (hari == 0) {
                tvStatus.setText("Masa aktif paket habis");
                tvStatus.setTextColor(Color.RED);
            } else if (sisa <= 0f) {
                tvStatus.setText("Kuota habis!");
                tvStatus.setTextColor(Color.RED);
            } else if (sisa <= batas) {
                tvStatus.setText("Hampir habis! Segera isi ulang");
                tvStatus.setTextColor(Color.parseColor("#E65100"));
            } else {
                tvStatus.setText("Kuota masih aman");
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            }

            tvMasaAktif.setText("Masa aktif: " + hari + " hari lagi");
            if (hari > 0) {
                tvSaran.setText(String.format(Locale.getDefault(),
                        "Saran pemakaian: maks. %.2f GB per hari", sisa / hari));
            } else {
                tvSaran.setText("");
            }
        }

        data.clear();
        data.addAll(db.getSemua());
        adapter.notifyDataSetChanged();
        tvKosong.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        tvTotalPakai.setText(String.format(Locale.getDefault(),
                "Total tercatat: %.2f GB (tekan lama untuk menghapus)", db.totalPemakaian()));
    }

    private void catatPemakaian() {
        if (!prefs.adaPaket()) {
            Toast.makeText(this, "Aktifkan paket dulu di Pengaturan", Toast.LENGTH_SHORT).show();
            return;
        }
        String s = etJumlah.getText().toString().trim().replace(',', '.');
        if (s.isEmpty()) {
            Toast.makeText(this, "Isi jumlah pemakaian dulu", Toast.LENGTH_SHORT).show();
            return;
        }
        float jumlah;
        try {
            jumlah = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Angka tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jumlah <= 0f) {
            Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jumlah > prefs.getSisa()) {
            Toast.makeText(this, "Melebihi sisa kuota", Toast.LENGTH_SHORT).show();
            return;
        }
        db.tambah(jumlah, etCatatan.getText().toString().trim());
        prefs.setSisa(prefs.getSisa() - jumlah);
        etJumlah.setText("");
        etCatatan.setText("");
        tampilkan();
        Toast.makeText(this, "Pemakaian dicatat", Toast.LENGTH_SHORT).show();
    }

    private void konfirmasiHapus(DbHelper.Riwayat r) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus catatan?")
                .setMessage(String.format(Locale.getDefault(),
                        "Pemakaian %.2f GB akan dihapus dan kuota dikembalikan.", r.jumlah))
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus", (d, w) -> {
                    db.hapus(r.id);
                    float baru = Math.min(prefs.getTotal(), prefs.getSisa() + r.jumlah);
                    prefs.setSisa(baru);
                    tampilkan();
                })
                .show();
    }
}