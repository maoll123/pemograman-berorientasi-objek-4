package src;

import java.util.regex.Pattern;

/**
 * Class Kendaraan
 * Merepresentasikan armada kendaraan dalam AutoRent System.
 * Menerapkan prinsip OOP:
 * 1. Encapsulation: Seluruh attribute private dengan validasi ketat.
 * 2. Composition (◆): Memiliki objek Mesin yang dibuat dan dikendalikan bersamaan dengan Kendaraan.
 * 3. Aggregation (◇): Berperan sebagai elemen (part) yang dikelompokkan dalam GarasiArmada (whole).
 * 4. Association: Berasosiasi dengan TransaksiSewa saat disewa oleh Pelanggan.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek)
 */
public class Kendaraan {
    // 1. Enkapsulasi: Attribute Private
    private String platNomor;
    private String merk;
    private String jenis;
    private double tarifPerHari;
    private boolean isTersedia;

    // Relasi Komposisi (Composition ◆): Kendaraan "has-a" Mesin (Lifecycle Mesin terikat kuat pada Kendaraan)
    private Mesin mesin;

    // Konstanta Bisnis & Pola Regex
    private static final String REGEX_PLAT_NOMOR = "^[A-Z]{1,2}\\s[0-9]{1,4}\\s[A-Z]{1,3}$";
    private static final double MIN_TARIF_PER_HARI = 50000.0;

    // 2. Parameterized Constructor (Defensive + Composition Instantiation)
    public Kendaraan(String platNomor, String merk, String jenis, double tarifPerHari, boolean isTersedia,
                     String noRangkaMesin, int kapasitasCC, String transmisi) {
        if (!setPlatNomor(platNomor)) {
            this.platNomor = "N/A (Invalid Plat)";
        }
        if (!setMerk(merk)) {
            this.merk = "Unit Standar (Default)";
        }
        if (!setJenis(jenis)) {
            this.jenis = "MPV (Default)";
        }
        if (!setTarifPerHari(tarifPerHari)) {
            this.tarifPerHari = MIN_TARIF_PER_HARI;
        }
        this.isTersedia = isTersedia;

        // Instansiasi Eksklusif Objek Mesin (Composition Pattern)
        this.mesin = new Mesin(noRangkaMesin, kapasitasCC, transmisi);
    }

    // Overloaded Constructor sederhana
    public Kendaraan(String platNomor, String merk, String jenis, double tarifPerHari, boolean isTersedia) {
        this(platNomor, merk, jenis, tarifPerHari, isTersedia, "ENG-" + platNomor.replace(" ", ""), 1500, "Automatic");
    }

    // 3. Getter Methods (Controlled Read Access)
    public String getPlatNomor() {
        return platNomor;
    }

    public String getMerk() {
        return merk;
    }

    public String getJenis() {
        return jenis;
    }

    public double getTarifPerHari() {
        return tarifPerHari;
    }

    public boolean isUnitTersedia() {
        return isTersedia;
    }

    public Mesin getMesin() {
        return mesin;
    }

    // 4. Setter Methods dengan Validasi Ketat
    public boolean setPlatNomor(String platNomor) {
        if (platNomor != null && Pattern.matches(REGEX_PLAT_NOMOR, platNomor.trim().toUpperCase())) {
            this.platNomor = platNomor.trim().toUpperCase();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Plat nomor '" + platNomor + "' TIDAK VALID! Format baku harus sesuai: [Kode Wilayah] [Nomor] [Seri], contoh: 'L 1234 AB'");
            return false;
        }
    }

    public boolean setMerk(String merk) {
        if (merk != null && merk.trim().length() >= 3) {
            this.merk = merk.trim();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Merk kendaraan '" + merk + "' TIDAK VALID! Nama merk tidak boleh kosong dan minimal 3 karakter.");
            return false;
        }
    }

    public boolean setJenis(String jenis) {
        if (jenis != null && !jenis.trim().isEmpty()) {
            this.jenis = jenis.trim();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Jenis armada '" + jenis + "' TIDAK VALID! Kategori jenis kendaraan tidak boleh kosong.");
            return false;
        }
    }

    public boolean setTarifPerHari(double tarifPerHari) {
        if (tarifPerHari >= MIN_TARIF_PER_HARI) {
            this.tarifPerHari = tarifPerHari;
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Tarif per hari Rp " + String.format("%,.2f", tarifPerHari) + " TIDAK VALID! Tarif harus minimal Rp " + String.format("%,.2f", MIN_TARIF_PER_HARI));
            return false;
        }
    }

    public void setStatusKetersediaan(boolean isTersedia) {
        this.isTersedia = isTersedia;
        if (isTersedia) {
            System.out.println("  >> [Kendaraan " + this.platNomor + "] Status armada berhasil diubah: TERSEDIA (Ready)");
        } else {
            System.out.println("  >> [Kendaraan " + this.platNomor + "] Status armada berhasil diubah: SEDANG DISEWA (Booked)");
        }
    }

    // 5. Method Operasional
    public void tampilkanInformasi() {
        System.out.println("  -------------------------------------------------------------");
        System.out.println("  [Plat Nomor]     : " + platNomor);
        System.out.println("  [Merk / Tipe]    : " + merk);
        System.out.println("  [Jenis Armada]   : " + jenis);
        System.out.println("  [Tarif Sewa/Hari]: Rp " + String.format("%,.2f", tarifPerHari));
        System.out.println("  [Status Unit]    : " + (isTersedia ? "TERSEDIA (Ready)" : "SEDANG DISEWA (Booked)"));
        if (mesin != null) {
            System.out.println("  -- Spesifikasi Mesin (Komposisi Part) --");
            mesin.tampilkanSpesifikasiMesin();
        }
        System.out.println("  -------------------------------------------------------------");
    }

    public void updateTarif(double tarifBaru) {
        System.out.println("  >> Mengajukan pembaruan tarif armada " + platNomor + " menjadi Rp " + String.format("%,.2f", tarifBaru) + "...");
        if (setTarifPerHari(tarifBaru)) {
            System.out.println("  >> [SUKSES] Tarif armada " + platNomor + " berhasil diperbarui menjadi Rp " + String.format("%,.2f", this.tarifPerHari));
        } else {
            System.out.println("  >> [GAGAL] Pembaruan tarif ditolak. Nilai tarif armada tetap Rp " + String.format("%,.2f", this.tarifPerHari));
        }
    }

    public double hitungEstimasiSewa(int durasiHari) {
        if (durasiHari > 0) {
            return this.tarifPerHari * durasiHari;
        } else {
            System.out.println("  [VALIDATION ERROR] Durasi sewa (" + durasiHari + " hari) tidak valid untuk kalkulasi estimasi.");
            return 0.0;
        }
    }
}
