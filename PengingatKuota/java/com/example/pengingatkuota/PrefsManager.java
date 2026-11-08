package com.example.pengingatkuota;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Pembungkus SharedPreferences.
 * Menyimpan PENGATURAN dan STATUS PAKET yang kecil (key-value).
 * Riwayat pemakaian (data banyak) disimpan di database SQLite (DbHelper).
 */
public class PrefsManager {
    private static final String PREF_NAME = "pengaturan_kuota";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_TOTAL = "total_kuota";
    private static final String KEY_SISA = "sisa_kuota";
    private static final String KEY_BATAS = "batas_peringatan";
    private static final String KEY_AKHIR = "masa_aktif_akhir";
    private static final String KEY_DARK = "dark_mode";
    private static final long SEHARI = 24L * 60L * 60L * 1000L;

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ---------- Tulis (semua memakai apply()) ----------
    public void simpanPengaturan(String nama, float batas, boolean dark) {
        prefs.edit()
                .putString(KEY_NAMA, nama)
                .putFloat(KEY_BATAS, batas)
                .putBoolean(KEY_DARK, dark)
                .apply();
    }

    public void aktifkanPaket(float total, int hari) {
        long akhir = System.currentTimeMillis() + hari * SEHARI;
        prefs.edit()
                .putFloat(KEY_TOTAL, total)
                .putFloat(KEY_SISA, total)
                .putLong(KEY_AKHIR, akhir)
                .apply();
    }

    public void setSisa(float sisa) {
        prefs.edit().putFloat(KEY_SISA, sisa).apply();
    }

    public void reset() {
        prefs.edit().clear().apply();
    }

    // ---------- Baca ----------
    public String getNama()    { return prefs.getString(KEY_NAMA, "Pengguna"); }
    public float getTotal()    { return prefs.getFloat(KEY_TOTAL, 0f); }
    public float getSisa()     { return prefs.getFloat(KEY_SISA, 0f); }
    public float getBatas()    { return prefs.getFloat(KEY_BATAS, 1f); }
    public long getAkhir()     { return prefs.getLong(KEY_AKHIR, 0L); }
    public boolean isDark()    { return prefs.getBoolean(KEY_DARK, false); }
    public boolean adaPaket()  { return prefs.contains(KEY_TOTAL) && getTotal() > 0f; }

    /** Sisa hari masa aktif (dibulatkan ke atas, minimal 0). */
    public int getHariTersisa() {
        long selisih = getAkhir() - System.currentTimeMillis();
        if (selisih <= 0) return 0;
        return (int) Math.ceil(selisih / (double) SEHARI);
    }

    /** Menerapkan mode gelap/terang sesuai pilihan tersimpan. */
    public void terapkanTema() {
        AppCompatDelegate.setDefaultNightMode(
                isDark() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
