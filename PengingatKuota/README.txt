PENGINGAT KUOTA INTERNET (LENGKAP)
Package: com.example.pengingatkuota | Bahasa: Java

FITUR
1. Dashboard: sisa kuota, progress bar persen, status (aman / hampir habis / habis / masa aktif habis)
2. Masa aktif paket + saran pemakaian maksimal per hari
3. Catat pemakaian (GB + catatan), kuota otomatis berkurang
4. Riwayat pemakaian (database SQLite), tekan lama untuk hapus (kuota dikembalikan)
5. Aktifkan paket baru / isi ulang
6. Pengaturan: nama, batas peringatan, mode gelap
7. Reset semua data

PEMBAGIAN DATA (untuk poin 2 dan 3 tugas)
SharedPreferences : nama, total kuota, sisa kuota, batas peringatan, tanggal akhir masa aktif, mode gelap
Database SQLite   : riwayat pemakaian (banyak, bertambah, bisa dihapus)
Tidak disimpan    : teks status, persen, saran harian (dihitung ulang dari data)

CARA PASANG (project PengingatKuota yang sudah ada)
1. Folder java/com/example/pengingatkuota/ -> salin 4 file .java ke
   app/src/main/java/com/example/pengingatkuota/ (pilih Replace untuk MainActivity & PrefsManager)
2. Folder res/layout/ -> salin 3 file .xml ke app/src/main/res/layout/ (Replace activity_main.xml)
   TIPS: di Android Studio, drag-and-drop file dari File Explorer ke folder di panel Project.
3. Ikuti AndroidManifest_tambahan.txt
4. Menu File > Sync Project with Gradle Files, lalu Run.

ALUR UJI UNTUK VIDEO
1. Pengaturan: isi nama, batas 2, nyalakan mode gelap, Simpan.
2. Pengaturan: total 10 GB, masa aktif 30 hari, Aktifkan Paket.
3. Catat pemakaian 1.5 GB (YouTube), 3 GB (Download), dst. sampai sisa < batas -> status merah/oranye.
4. Force Stop aplikasi, buka lagi: sisa, riwayat, mode gelap tetap ada.
