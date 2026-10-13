package src;

import java.util.ArrayList;

/**
 * Class TransaksiSewa
 * Mengelola seluruh siklus pemesanan, perpanjangan, dan pengembalian rental armada.
 * Merupakan titik integrasi utama yang mendemonstrasikan seluruh 4 relasi OOP Modul 4:
 * 1. Association: Berasosiasi secara stabil dengan Objek Pelanggan dan Objek Kendaraan.
 * 2. Composition (◆): Mengendalikan penuh koleksi DetailItemBiaya (Invoice line items).
 * 3. Dependency (- - - ->): Menggunakan service transient KuitansiPrinter dan LayananNotifikasi.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Asosiasi, Komposisi, Dependensi)
 */
public class TransaksiSewa {
    // 1. Enkapsulasi: Attribute Private
    private String kodeTransaksi;

    // Relasi Asosiasi (Association): Mengetahui Pelanggan dan Kendaraan
    private Kendaraan kendaraan;
    private Pelanggan pelanggan;

    private String tanggalSewa;
    private int durasiHari;
    private String statusSewa;

    // Relasi Komposisi (Composition ◆): DetailItemBiaya dibuat & dikelola eksklusif oleh TransaksiSewa
    private ArrayList<DetailItemBiaya> rincianBiaya;
    private double totalBiaya;

    private static final double TARIF_DENDA_PER_HARI = 50000.0;

    // 2. Parameterized Constructor (Defensive + Composition Initializer)
    public TransaksiSewa(String kodeTransaksi, Kendaraan kendaraan, Pelanggan pelanggan, String tanggalSewa, int durasiHari) {
        if (kodeTransaksi != null && !kodeTransaksi.trim().isEmpty()) {
            this.kodeTransaksi = kodeTransaksi.trim().toUpperCase();
        } else {
            this.kodeTransaksi = "TRX-UNKNOWN";
        }

        this.tanggalSewa = (tanggalSewa != null && !tanggalSewa.trim().isEmpty()) ? tanggalSewa.trim() : "18-09-2026";
        this.rincianBiaya = new ArrayList<>();

        // Validasi Relasi Objek Asosiasi
        if (kendaraan == null) {
            System.out.println("  [TRANSACTION ERROR] Objek Kendaraan tidak boleh NULL!");
            this.statusSewa = "Dibatalkan (Kendaraan Null)";
            this.durasiHari = 0;
            this.totalBiaya = 0.0;
            return;
        }

        if (pelanggan == null) {
            System.out.println("  [TRANSACTION ERROR] Objek Pelanggan tidak boleh NULL!");
            this.statusSewa = "Dibatalkan (Pelanggan Null)";
            this.durasiHari = 0;
            this.totalBiaya = 0.0;
            return;
        }

        // Validasi Ketersediaan Armada
        if (!kendaraan.isUnitTersedia()) {
            System.out.println("  [TRANSACTION ERROR] Unit '" + kendaraan.getPlatNomor() + " (" + kendaraan.getMerk() + ")' SEDANG DISEWA / Tidak Tersedia!");
            this.kendaraan = kendaraan;
            this.pelanggan = pelanggan;
            this.statusSewa = "Ditolak (Armada Tidak Tersedia)";
            this.durasiHari = 0;
            this.totalBiaya = 0.0;
            return;
        }

        // Validasi Kuota Sewa Pelanggan
        if (!pelanggan.bolehSewa()) {
            System.out.println("  [TRANSACTION ERROR] Pelanggan '" + pelanggan.getNamaLengkap() + "' telah mencapai batas kuota maksimal sewa (2 unit)!");
            this.kendaraan = kendaraan;
            this.pelanggan = pelanggan;
            this.statusSewa = "Ditolak (Kuota Pelanggan Penuh)";
            this.durasiHari = 0;
            this.totalBiaya = 0.0;
            return;
        }

        // Validasi Durasi Sewa
        if (durasiHari <= 0) {
            System.out.println("  [VALIDATION ERROR] Durasi sewa (" + durasiHari + " hari) TIDAK VALID! Minimal durasi adalah 1 hari.");
            this.durasiHari = 1;
        } else {
            this.durasiHari = durasiHari;
        }

        // Inisialisasi Objek Asosiasi yang Sah
        this.kendaraan = kendaraan;
        this.pelanggan = pelanggan;
        this.statusSewa = "Sedang Disewa";

        // Instansiasi Komposisi Bagian Pokok (Composition Part 1)
        double tarifPokok = this.kendaraan.getTarifPerHari();
        DetailItemBiaya itemPokok = new DetailItemBiaya("Sewa Armada Pokok (" + kendaraan.getMerk() + ")", tarifPokok, this.durasiHari);
        this.rincianBiaya.add(itemPokok);

        hitungUlangTotalBiaya();

        // Mutasi State Objek Asosiasi
        this.kendaraan.setStatusKetersediaan(false);
        this.pelanggan.tambahSewaAktif(1);
        System.out.println("  >> [SUKSES] Transaksi " + this.kodeTransaksi + " berhasil dibuat. Unit " + kendaraan.getPlatNomor() + " dipinjam oleh " + pelanggan.getNamaLengkap());
    }

    // 3. Getter Methods
    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public String getTanggalSewa() {
        return tanggalSewa;
    }

    public int getDurasiHari() {
        return durasiHari;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public String getStatusSewa() {
        return statusSewa;
    }

    public ArrayList<DetailItemBiaya> getRincianBiaya() {
        return rincianBiaya;
    }

    // 4. Operasi Komposisi (Menambahkan Item Layanan Ekstra ke Transaksi)
    public void tambahLayananTambahan(String namaLayanan, double tarifSatuan, int kuantitas) {
        if (!"Sedang Disewa".equalsIgnoreCase(this.statusSewa)) {
            System.out.println("  [KOMPOSISI ERROR] Tidak dapat menambahkan layanan. Status transaksi: " + statusSewa);
            return;
        }
        DetailItemBiaya itemBaru = new DetailItemBiaya(namaLayanan, tarifSatuan, kuantitas);
        this.rincianBiaya.add(itemBaru);
        hitungUlangTotalBiaya();
        System.out.println("  >> [KOMPOSISI: PART ADDED] Layanan '" + namaLayanan + "' ditambahkan ke Transaksi " + kodeTransaksi + " (Total Baru: Rp " + String.format("%,.2f", totalBiaya) + ")");
    }

    private void hitungUlangTotalBiaya() {
        double sum = 0.0;
        for (DetailItemBiaya item : rincianBiaya) {
            sum += item.getSubtotal();
        }
        this.totalBiaya = sum;
    }

    public void tampilkanRincianItem() {
        int no = 1;
        for (DetailItemBiaya item : rincianBiaya) {
            item.tampilkanItem(no++);
        }
    }

    // 5. Operasi Mutasi & Bisnis
    public boolean perpanjangDurasi(int tambahanHari) {
        System.out.println("  >> Mengajukan perpanjangan durasi transaksi " + kodeTransaksi + " sebesar +" + tambahanHari + " hari...");
        if (!"Sedang Disewa".equalsIgnoreCase(this.statusSewa)) {
            System.out.println("  [VALIDATION ERROR] Perpanjangan ditolak! Transaksi sudah berstatus '" + this.statusSewa + "'.");
            return false;
        }
        if (tambahanHari <= 0) {
            System.out.println("  [VALIDATION ERROR] Tambahan hari (" + tambahanHari + " hari) TIDAK VALID! Nilai harus lebih dari 0.");
            return false;
        }

        this.durasiHari += tambahanHari;
        // Tambahkan item perpanjangan ke dalam rincian biaya (Komposisi)
        DetailItemBiaya itemPerpanjangan = new DetailItemBiaya("Perpanjangan Sewa Armada", this.kendaraan.getTarifPerHari(), tambahanHari);
        this.rincianBiaya.add(itemPerpanjangan);
        hitungUlangTotalBiaya();

        System.out.println("  >> [SUKSES] Perpanjangan sewa disetujui. Total durasi: " + this.durasiHari + " hari | Total biaya akhir: Rp " + String.format("%,.2f", this.totalBiaya));
        return true;
    }

    // 6. Relasi Dependency: Menggunakan LayananNotifikasi dan KuitansiPrinter via Parameter Method
    public boolean prosesPengembalian(int hariKeterlambatan, LayananNotifikasi notifGateway) {
        System.out.println("  >> Memproses pengembalian kendaraan untuk Transaksi " + kodeTransaksi + "...");
        if (!"Sedang Disewa".equalsIgnoreCase(this.statusSewa)) {
            System.out.println("  [VALIDATION ERROR] Pengembalian ditolak! Transaksi tidak dalam status aktif ('" + this.statusSewa + "').");
            return false;
        }

        if (hariKeterlambatan < 0) {
            System.out.println("  [VALIDATION ERROR] Hari keterlambatan (" + hariKeterlambatan + ") tidak valid! Menggunakan 0 hari keterlambatan.");
            hariKeterlambatan = 0;
        }

        this.statusSewa = "Selesai";
        this.kendaraan.setStatusKetersediaan(true);
        this.pelanggan.kurangiSewaAktif(1);

        double denda = 0.0;
        if (hariKeterlambatan > 0) {
            denda = hariKeterlambatan * TARIF_DENDA_PER_HARI;
            // Masukkan denda ke rincian item biaya (Komposisi)
            DetailItemBiaya itemDenda = new DetailItemBiaya("Denda Keterlambatan (" + hariKeterlambatan + " Hari)", TARIF_DENDA_PER_HARI, hariKeterlambatan);
            this.rincianBiaya.add(itemDenda);
            hitungUlangTotalBiaya();
        }

        System.out.println("  >> [SUKSES] Unit kendaraan " + kendaraan.getPlatNomor() + " (" + kendaraan.getMerk() + ") berhasil dikembalikan.");
        if (hariKeterlambatan > 0) {
            System.out.println("     [Status Keterlambatan] : Terlambat " + hariKeterlambatan + " hari");
            System.out.println("     [Denda Dikenakan]      : Rp " + String.format("%,.2f", denda) + " (Rp 50.000 / hari)");
            System.out.println("     [Total Tagihan Akhir]  : Rp " + String.format("%,.2f", totalBiaya));
        } else {
            System.out.println("     [Status Keterlambatan] : Tepat Waktu (Bebas Denda)");
            System.out.println("     [Total Tagihan Akhir]  : Rp " + String.format("%,.2f", totalBiaya));
        }

        // Memanggil Service Transient (Dependency Injection via Method Parameter)
        if (notifGateway != null) {
            System.out.println("  >> [DEPENDENCY EXECUTION] Memanggil LayananNotifikasi untuk notifikasi pengembalian...");
            notifGateway.kirimNotifikasiPengembalian(this.pelanggan, this, hariKeterlambatan, denda);
        }

        return true;
    }

    public void cetakStrukDenganPrinter(KuitansiPrinter printer, String namaKasir) {
        if (printer != null) {
            System.out.println("  >> [DEPENDENCY EXECUTION] Mengoper objek TransaksiSewa ke KuitansiPrinter...");
            printer.cetakKuitansiResmi(this, namaKasir);
        } else {
            System.out.println("  [ERROR] KuitansiPrinter tidak tersedia.");
        }
    }
}
