package com.example.pengingatkuota;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** Database SQLite untuk RIWAYAT pemakaian (data banyak, bertambah, bisa dihapus). */
public class DbHelper extends SQLiteOpenHelper {

    public static class Riwayat {
        public int id;
        public long waktu;
        public float jumlah;
        public String catatan;

        public Riwayat(int id, long waktu, float jumlah, String catatan) {
            this.id = id;
            this.waktu = waktu;
            this.jumlah = jumlah;
            this.catatan = catatan;
        }
    }

    public DbHelper(Context context) {
        super(context, "kuota.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE riwayat (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "waktu INTEGER, jumlah REAL, catatan TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS riwayat");
        onCreate(db);
    }

    public void tambah(float jumlah, String catatan) {
        ContentValues cv = new ContentValues();
        cv.put("waktu", System.currentTimeMillis());
        cv.put("jumlah", jumlah);
        cv.put("catatan", catatan);
        getWritableDatabase().insert("riwayat", null, cv);
    }

    public void hapus(int id) {
        getWritableDatabase().delete("riwayat", "id=?", new String[]{String.valueOf(id)});
    }

    public void hapusSemua() {
        getWritableDatabase().delete("riwayat", null, null);
    }

    public List<Riwayat> getSemua() {
        List<Riwayat> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("riwayat", null, null, null, null, null, "id DESC");
        while (c.moveToNext()) {
            list.add(new Riwayat(c.getInt(0), c.getLong(1), c.getFloat(2), c.getString(3)));
        }
        c.close();
        return list;
    }

    public float totalPemakaian() {
        Cursor c = getReadableDatabase().rawQuery("SELECT SUM(jumlah) FROM riwayat", null);
        float total = 0f;
        if (c.moveToFirst()) total = c.getFloat(0);
        c.close();
        return total;
    }
}
