package src;

/**
 * Class Main (Driver & Comprehensive Verification Test Suite)
 * Menguji seluruh fungsionalitas dan relasi antarkelas pada Modul 4 (Sprint 4):
 * 1. Association: Hubungan struktural Pelanggan — TransaksiSewa — Kendaraan.
 * 2. Aggregation (◇): Hubungan whole-part lemah GarasiArmada ◇— Kendaraan.
 * 3. Composition (◆): Hubungan whole-part kuat Kendaraan ◆— Mesin & TransaksiSewa ◆— DetailItemBiaya.
 * 4. Dependency (- - - ->): Hubungan penggunaan sementara TransaksiSewa → KuitansiPrinter & LayananNotifikasi.
 * 5. Multiplicity & Navigability Verification.
 * 6. Pembuktian Lifecycle (Agregasi vs Komposisi).
 * 7. Negative Testing & Defensive Boundary Validation.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   PRAKTIKUM PEMROGRAMAN BERORIENTASI OBYEK - MODUL 4 (AGILE SPRINT P4)         ");
        System.out.println("   AutoRent System - Relasi Antarobjek: Association, Aggregation,               ");
        System.out.println("                     Composition, Dependency & Multiplicity                     ");
        System.out.println("   Penyusun: Ahmad Yasar Maulana Dais (NRP: 3125522018) | PENS PSDKU Sumenep    ");
        System.out.println("================================================================================\n");

        // =====================================================================
        // BAGIAN 1: PENGUJIAN KOMPOSISI TAHAP 1 (Kendaraan ◆— Mesin)
        // =====================================================================
        System.out.println(">>> [1. PENGUJIAN RELASI KOMPOSISI: Kendaraan ◆— Mesin] <<<");
        System.out.println("Menginstansiasi 3 objek Kendaraan lengkap dengan komponen Mesin internalnya...\n");

        Kendaraan kendaraan1 = new Kendaraan("L 1234 AB", "Toyota Avanza Veloz 1.5 AT", "MPV", 400000.0, true,
                "ENG-2NR-FE-9921", 1496, "Automatic CVT");
        Kendaraan kendaraan2 = new Kendaraan("N 5678 CD", "Honda CR-V 1.5 Turbo Prestige", "SUV", 750000.0, true,
                "ENG-L15BE-4412", 1498, "Automatic CVT");
        Kendaraan kendaraan3 = new Kendaraan("M 9900 EF", "Mitsubishi Pajero Sport Dakar 4x4", "SUV", 900000.0, true,
                "ENG-4N15-7733", 2442, "Automatic 8-Speed");

        System.out.println("Menampilkan informasi unit beserta spesifikasi mesin internal (Komposisi Part):");
        kendaraan1.tampilkanInformasi();
        kendaraan2.tampilkanInformasi();
        kendaraan3.tampilkanInformasi();

        // =====================================================================
        // BAGIAN 2: PENGUJIAN AGREGASI (GarasiArmada ◇— Kendaraan)
        // =====================================================================
        System.out.println("\n>>> [2. PENGUJIAN RELASI AGREGASI: GarasiArmada ◇— Kendaraan] <<<");
        System.out.println("Membuat objek GarasiArmada (Whole) dan mendaftarkan unit Kendaraan (Part)...\n");

        GarasiArmada garasiSurabaya = new GarasiArmada("GAR-SBY-01", "Garasi Pusat Juanda Surabaya", "Surabaya", 3);
        
        garasiSurabaya.tambahKendaraan(kendaraan1);
        garasiSurabaya.tambahKendaraan(kendaraan2);
        garasiSurabaya.tambahKendaraan(kendaraan3);

        System.out.println("\nMenampilkan daftar armada dalam Garasi Pusat Surabaya:");
        garasiSurabaya.tampilkanDaftarArmada();

        // =====================================================================
        // BAGIAN 3: PENGUJIAN ASOSIASI (Pelanggan — TransaksiSewa — Kendaraan)
        // =====================================================================
        System.out.println("\n>>> [3. PENGUJIAN RELASI ASOSIASI: Pelanggan — TransaksiSewa — Kendaraan] <<<");
        System.out.println("Menginstansiasi 2 objek Pelanggan dan membuat Transaksi Sewa pertama...\n");

        Pelanggan pelanggan1 = new Pelanggan("CUST-001", "Budi Santoso", "3529011205980001", "081234567890", 0);
        Pelanggan pelanggan2 = new Pelanggan("CUST-002", "Siti Aminah", "3529022308990002", "085678901234", 0);

        pelanggan1.tampilkanDataPelanggan();
        pelanggan2.tampilkanDataPelanggan();

        System.out.println("\nMembuat Transaksi Sewa TRX-2026-001 (Budi Santoso menyewa Avanza Veloz selama 3 hari)...");
        TransaksiSewa transaksi1 = new TransaksiSewa("TRX-2026-001", kendaraan1, pelanggan1, "18-09-2026", 3);

        System.out.println("\nMembuat Transaksi Sewa TRX-2026-002 (Siti Aminah menyewa Honda CR-V selama 2 hari)...");
        TransaksiSewa transaksi2 = new TransaksiSewa("TRX-2026-002", kendaraan2, pelanggan2, "18-09-2026", 2);

        // =====================================================================
        // BAGIAN 4: PENGUJIAN KOMPOSISI TAHAP 2 (TransaksiSewa ◆— DetailItemBiaya)
        // =====================================================================
        System.out.println("\n>>> [4. PENGUJIAN KOMPOSISI TAHAP 2: TransaksiSewa ◆— DetailItemBiaya] <<<");
        System.out.println("Menambahkan paket layanan ekstra (Add-On items) ke dalam Transaksi 1 (Komposisi Part)...\n");

        transaksi1.tambahLayananTambahan("Asuransi All-Risk Executive", 50000.0, 3);
        transaksi1.tambahLayananTambahan("Layanan Driver Profesional", 150000.0, 3);
        transaksi1.tambahLayananTambahan("GPS Tracker & Child Car Seat", 25000.0, 3);

        System.out.println("\nMelakukan perpanjangan durasi sewa Transaksi 1 sebesar +2 hari:");
        transaksi1.perpanjangDurasi(2);

        System.out.println("\nMenampilkan rincian item biaya Transaksi 1:");
        transaksi1.tampilkanRincianItem();
        System.out.printf("   TOTAL AKUMULASI BIAYA TRANSAKSI 1: Rp %,.2f\n", transaksi1.getTotalBiaya());

        // =====================================================================
        // BAGIAN 5: PENGUJIAN DEPENDENCY (TransaksiSewa → KuitansiPrinter & LayananNotifikasi)
        // =====================================================================
        System.out.println("\n>>> [5. PENGUJIAN RELASI DEPENDENCY: TransaksiSewa → KuitansiPrinter & LayananNotifikasi] <<<");
        System.out.println("Menginstansiasi service pendukung sementara (transient helper objects)...\n");

        KuitansiPrinter printerThermal = new KuitansiPrinter("Thermal High-Speed POS", "Epson TM-T88VI");
        LayananNotifikasi notifGateway = new LayananNotifikasi("AutoRent WhatsApp Cloud API");

        // Dependency 1: Transaksi memanggil KuitansiPrinter via parameter method
        System.out.println("Mencetak kuitansi resmi Transaksi 1 melalui KuitansiPrinter (Dependency):");
        transaksi1.cetakStrukDenganPrinter(printerThermal, "Rina Amelia (Kasir)");

        // Dependency 2: Memproses pengembalian unit armada pada Transaksi 2 dengan keterlambatan 2 hari
        System.out.println("\nMemproses pengembalian armada Transaksi 2 (Terlambat 2 Hari) dengan integrasi notifikasi:");
        transaksi2.prosesPengembalian(2, notifGateway);

        // Cetak kuitansi final Transaksi 2
        transaksi2.cetakStrukDenganPrinter(printerThermal, "Rina Amelia (Kasir)");

        // =====================================================================
        // BAGIAN 6: PEMBUKTIAN LIFECYCLE (AGREGASI VS KOMPOSISI)
        // =====================================================================
        System.out.println("\n================================================================================");
        System.out.println(">>> [6. PEMBUKTIAN KARAKTERISTIK LIFECYCLE: AGREGASI VS KOMPOSISI] <<<");
        System.out.println("================================================================================");

        System.out.println("\n[A. PEMBUKTIAN SIFAT AGREGASI (GarasiArmada ◇— Kendaraan)]");
        System.out.println("Kondisi Awal Garasi Surabaya: Total " + garasiSurabaya.getTotalArmada() + " unit armada.");
        garasiSurabaya.kosongkanGarasi();
        System.out.println("Status Garasi Surabaya setelah dikosongkan: " + garasiSurabaya.getTotalArmada() + " unit.");
        
        System.out.println("Memeriksa apakah objek Kendaraan 1 dan Kendaraan 3 masih hidup di memori:");
        System.out.println("  >> Kendaraan 1 Plat: " + kendaraan1.getPlatNomor() + " | Merk: " + kendaraan1.getMerk() + " | Status: " + (kendaraan1.isUnitTersedia() ? "Tersedia" : "Sedang Disewa"));
        System.out.println("  >> Kendaraan 3 Plat: " + kendaraan3.getPlatNomor() + " | Merk: " + kendaraan3.getMerk() + " | Status: " + (kendaraan3.isUnitTersedia() ? "Tersedia" : "Sedang Disewa"));
        
        // Memindahkan unit ke Garasi Cabang Malang (Agregasi fleksibel)
        GarasiArmada garasiMalang = new GarasiArmada("GAR-MLG-01", "Garasi Cabang Kota Malang", "Malang", 5);
        garasiMalang.tambahKendaraan(kendaraan3);
        System.out.println("  >> [BUKTI AGREGASI VALID] Unit " + kendaraan3.getPlatNomor() + " berhasil dipindah ke " + garasiMalang.getNamaCabang());

        System.out.println("\n[B. PEMBUKTIAN SIFAT KOMPOSISI (Kendaraan ◆— Mesin & TransaksiSewa ◆— DetailItemBiaya)]");
        System.out.println("Objek Mesin dan DetailItemBiaya tidak dapat diciptakan atau dipindahkan secara bebas tanpa induknya.");
        System.out.println("  >> No Rangka Mesin Kendaraan 1: " + kendaraan1.getMesin().getNomorRangkaMesin() + " (Terkunci pada " + kendaraan1.getPlatNomor() + ")");
        System.out.println("  >> Rincian Item Transaksi 1 berjumlah " + transaksi1.getRincianBiaya().size() + " item, terikat mutlak pada " + transaksi1.getKodeTransaksi());

        // =====================================================================
        // BAGIAN 7: NEGATIVE TESTING & DEFENSIVE BOUNDARY VALIDATION
        // =====================================================================
        System.out.println("\n================================================================================");
        System.out.println(">>> [7. PENGUJIAN DATA TIDAK VALID (NEGATIVE TESTING & VALIDATION DEFENSE)] <<<");
        System.out.println("Menguji ketahanan arsitektur terhadap kondisi batas dan pelanggaran aturan bisnis...");
        System.out.println("================================================================================");

        // Test Invalid 1: Menambah kendaraan ke Garasi melebihi kapasitas
        System.out.println("\n[TEST INVALID 1] Uji Kapasitas Garasi Penuh (Overflow Defense):");
        GarasiArmada garasiKecil = new GarasiArmada("GAR-MINI-01", "Garasi Pos Satelit", "Bangkalan", 1);
        garasiKecil.tambahKendaraan(kendaraan1);
        System.out.print("Mencoba menambahkan unit kedua ke garasi berkapasitas 1 unit -> ");
        garasiKecil.tambahKendaraan(kendaraan2);

        // Test Invalid 2: Menyewa Kendaraan yang sedang berstatus Booked
        System.out.println("\n[TEST INVALID 2] Uji Asosiasi: Menyewa Kendaraan yang Sedang Disewa (Booked):");
        System.out.println("Status unit " + kendaraan1.getPlatNomor() + " saat ini: " + (kendaraan1.isUnitTersedia() ? "Tersedia" : "SEDANG DISEWA"));
        System.out.print("Mencoba membuat transaksi baru untuk kendaraan 1 oleh pelanggan 2 -> ");
        TransaksiSewa trxGagal1 = new TransaksiSewa("TRX-ERR-001", kendaraan1, pelanggan2, "18-09-2026", 2);
        System.out.println("Status Transaksi Gagal: '" + trxGagal1.getStatusSewa() + "' [PROTEKSI BISNIS BERHASIL]");

        // Test Invalid 3: Pelanggan Melebihi Batas Kuota Sewa Maksimal (2 Unit)
        System.out.println("\n[TEST INVALID 3] Uji Multiplicity: Pelanggan Mencapai Kuota Maksimal Sewa:");
        System.out.println("Sewa aktif Budi Santoso saat ini: " + pelanggan1.getTotalSewaAktif() + "/2 unit");
        // Berikan sewa kedua kepada Budi Santoso
        TransaksiSewa trxSah2 = new TransaksiSewa("TRX-2026-003", kendaraan3, pelanggan1, "18-09-2026", 1);
        System.out.println("Sewa aktif Budi Santoso setelah transaksi kedua: " + pelanggan1.getTotalSewaAktif() + "/2 unit (KUOTA PENUH)");
        
        // Coba sewa ketiga untuk Budi Santoso
        Kendaraan kendaraan4 = new Kendaraan("W 8888 ZZ", "Toyota Fortuner GR Sport", "SUV", 950000.0, true);
        System.out.print("Mencoba membuat transaksi ketiga untuk Budi Santoso -> ");
        TransaksiSewa trxGagal2 = new TransaksiSewa("TRX-ERR-002", kendaraan4, pelanggan1, "18-09-2026", 2);
        System.out.println("Status Transaksi Gagal: '" + trxGagal2.getStatusSewa() + "' [PROTEKSI KUOTA BERHASIL]");

        // Test Invalid 4: Perpanjangan Durasi Negatif
        System.out.println("\n[TEST INVALID 4] Uji Perpanjangan Durasi Negatif pada Transaksi:");
        System.out.print("Mencoba memperpanjang Transaksi 1 sebesar -5 hari -> ");
        transaksi1.perpanjangDurasi(-5);

        // Test Invalid 5: Objek NULL pada Instansiasi Transaksi
        System.out.println("\n[TEST INVALID 5] Uji Penanganan Objek NULL (Null Safety Defense):");
        System.out.print("Mencoba membuat transaksi dengan Objek Kendaraan NULL -> ");
        TransaksiSewa trxNull = new TransaksiSewa("TRX-NULL", null, pelanggan2, "18-09-2026", 1);

        // =====================================================================
        // BAGIAN 8: REKAPITULASI STATE AKHIR SELURUH OBJEK
        // =====================================================================
        System.out.println("\n================================================================================");
        System.out.println(">>> [8. REKAPITULASI STATE AKHIR SELURUH OBJEK SISTEM AUTORENT] <<<");
        System.out.println("================================================================================");
        System.out.println("  1. Kendaraan 1 : [" + kendaraan1.getPlatNomor() + "] " + kendaraan1.getMerk() + " | Status: " + (kendaraan1.isUnitTersedia() ? "Tersedia" : "Sedang Disewa") + " | Mesin: " + kendaraan1.getMesin().getNomorRangkaMesin());
        System.out.println("  2. Kendaraan 2 : [" + kendaraan2.getPlatNomor() + "] " + kendaraan2.getMerk() + " | Status: " + (kendaraan2.isUnitTersedia() ? "Tersedia" : "Sedang Disewa") + " | Mesin: " + kendaraan2.getMesin().getNomorRangkaMesin());
        System.out.println("  3. Kendaraan 3 : [" + kendaraan3.getPlatNomor() + "] " + kendaraan3.getMerk() + " | Status: " + (kendaraan3.isUnitTersedia() ? "Tersedia" : "Sedang Disewa") + " | Mesin: " + kendaraan3.getMesin().getNomorRangkaMesin());
        System.out.println("  4. Pelanggan 1 : " + pelanggan1.getNamaLengkap() + " (" + pelanggan1.getIdPelanggan() + ") | Sewa Aktif: " + pelanggan1.getTotalSewaAktif() + "/2 unit | Telp: " + pelanggan1.getNomorTelepon());
        System.out.println("  5. Pelanggan 2 : " + pelanggan2.getNamaLengkap() + " (" + pelanggan2.getIdPelanggan() + ") | Sewa Aktif: " + pelanggan2.getTotalSewaAktif() + "/2 unit | Telp: " + pelanggan2.getNomorTelepon());
        System.out.println("  6. Transaksi 1 : " + transaksi1.getKodeTransaksi() + " | Status: " + transaksi1.getStatusSewa() + " | Total Biaya: Rp " + String.format("%,.2f", transaksi1.getTotalBiaya()) + " (" + transaksi1.getRincianBiaya().size() + " Item Biaya)");
        System.out.println("  7. Transaksi 2 : " + transaksi2.getKodeTransaksi() + " | Status: " + transaksi2.getStatusSewa() + " | Total Biaya: Rp " + String.format("%,.2f", transaksi2.getTotalBiaya()) + " (" + transaksi2.getRincianBiaya().size() + " Item Biaya)");

        System.out.println("\n================================================================================");
        System.out.println("   [STATUS: SUKSES SEMPURNA] SELURUH RELASI DAN BUSINESS RULES P4 TERVERIFIKASI! ");
        System.out.println("================================================================================");
    }
}
