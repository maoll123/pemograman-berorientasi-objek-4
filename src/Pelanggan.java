package src;

import java.util.regex.Pattern;

/**
 * Class Pelanggan
 * Merepresentasikan data penyewa/pelanggan dalam AutoRent System.
 * Menerapkan prinsip Encapsulation dengan attribute private, identifier read-only,
 * validasi NIK/KTP 16 digit, validasi format telepon, dan kontrol kuota sewa.
 * Berperan dalam relasi Asosiasi (Association) dengan TransaksiSewa.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Asosiasi)
 */
public class Pelanggan {
    // 1. Enkapsulasi: Attribute Private
    private String idPelanggan;
    private String namaLengkap;
    private String nomorKtp;
    private String nomorTelepon;
    private int totalSewaAktif;

    private static final int BATAS_MAX_SEWA = 2;
    private static final String REGEX_KTP = "^[0-9]{16}$";
    private static final String REGEX_TELEPON = "^08[0-9]{8,12}$";

    // 2. Parameterized Constructor
    public Pelanggan(String idPelanggan, String namaLengkap, String nomorKtp, String nomorTelepon, int totalSewaAktif) {
        if (idPelanggan != null && !idPelanggan.trim().isEmpty()) {
            this.idPelanggan = idPelanggan.trim().toUpperCase();
        } else {
            this.idPelanggan = "CUST-DEFAULT";
        }

        if (!setNamaLengkap(namaLengkap)) {
            this.namaLengkap = "Pelanggan Tanpa Nama";
        }

        if (!setNomorKtp(nomorKtp)) {
            this.nomorKtp = "0000000000000000";
        }

        if (!setNomorTelepon(nomorTelepon)) {
            this.nomorTelepon = "081200000000";
        }

        if (totalSewaAktif >= 0 && totalSewaAktif <= BATAS_MAX_SEWA) {
            this.totalSewaAktif = totalSewaAktif;
        } else {
            this.totalSewaAktif = 0;
        }
    }

    public Pelanggan(String idPelanggan, String namaLengkap, String nomorKtp, String nomorTelepon) {
        this(idPelanggan, namaLengkap, nomorKtp, nomorTelepon, 0);
    }

    // 3. Getter Methods
    public String getIdPelanggan() {
        return idPelanggan;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getNomorKtp() {
        return nomorKtp;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public int getTotalSewaAktif() {
        return totalSewaAktif;
    }

    public int getBatasMaxSewa() {
        return BATAS_MAX_SEWA;
    }

    public int getSisaKuotaSewa() {
        return BATAS_MAX_SEWA - totalSewaAktif;
    }

    public boolean bolehSewa() {
        return totalSewaAktif < BATAS_MAX_SEWA;
    }

    // 4. Setter Methods dengan Validasi
    public boolean setNamaLengkap(String namaLengkap) {
        if (namaLengkap != null && namaLengkap.trim().length() >= 3) {
            this.namaLengkap = namaLengkap.trim();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Nama pelanggan '" + namaLengkap + "' TIDAK VALID! Nama tidak boleh kosong dan minimal 3 karakter.");
            return false;
        }
    }

    public boolean setNomorKtp(String nomorKtp) {
        if (nomorKtp != null && Pattern.matches(REGEX_KTP, nomorKtp.trim())) {
            this.nomorKtp = nomorKtp.trim();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Nomor KTP '" + nomorKtp + "' TIDAK VALID! NIK/KTP harus tepat 16 digit angka numerik.");
            return false;
        }
    }

    public boolean setNomorTelepon(String nomorTelepon) {
        if (nomorTelepon != null && Pattern.matches(REGEX_TELEPON, nomorTelepon.trim())) {
            this.nomorTelepon = nomorTelepon.trim();
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Nomor telepon '" + nomorTelepon + "' TIDAK VALID! Nomor harus diawali '08' dan terdiri dari 10-14 digit angka.");
            return false;
        }
    }

    // 5. Method Bisnis & Mutasi State
    public void perbaruiKontak(String noTeleponBaru) {
        System.out.println("  >> Mengajukan pembaruan nomor kontak pelanggan " + idPelanggan + " menjadi '" + noTeleponBaru + "'...");
        if (setNomorTelepon(noTeleponBaru)) {
            System.out.println("  >> [SUKSES] Nomor kontak pelanggan " + idPelanggan + " (" + namaLengkap + ") berhasil diperbarui menjadi: " + this.nomorTelepon);
        } else {
            System.out.println("  >> [GAGAL] Pembaruan kontak ditolak. Nomor tetap: " + this.nomorTelepon);
        }
    }

    public boolean tambahSewaAktif(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("  [VALIDATION ERROR] Jumlah penambahan unit sewa harus lebih dari 0.");
            return false;
        }
        if ((this.totalSewaAktif + jumlah) <= BATAS_MAX_SEWA) {
            this.totalSewaAktif += jumlah;
            System.out.println("  >> [Pelanggan " + idPelanggan + "] Transaksi sewa disetujui +" + jumlah + " unit. Total sewa aktif: " + this.totalSewaAktif + "/" + BATAS_MAX_SEWA);
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Penambahan sewa DITOLAK! Kuota sewa terlampaui. Maksimal: " + BATAS_MAX_SEWA + ", Sewa saat ini: " + this.totalSewaAktif);
            return false;
        }
    }

    public boolean kurangiSewaAktif(int jumlah) {
        if (jumlah <= 0) {
            System.out.println("  [VALIDATION ERROR] Jumlah pengurangan unit sewa harus lebih dari 0.");
            return false;
        }
        if ((this.totalSewaAktif - jumlah) >= 0) {
            this.totalSewaAktif -= jumlah;
            System.out.println("  >> [Pelanggan " + idPelanggan + "] Pengembalian tercatat -" + jumlah + " unit. Sisa sewa aktif: " + this.totalSewaAktif + "/" + BATAS_MAX_SEWA);
            return true;
        } else {
            System.out.println("  [VALIDATION ERROR] Pengurangan sewa tidak valid! Jumlah sewa aktif tidak boleh kurang dari 0.");
            return false;
        }
    }

    public void tampilkanDataPelanggan() {
        System.out.println("  -------------------------------------------------------------");
        System.out.println("  [ID Pelanggan]      : " + idPelanggan);
        System.out.println("  [Nama Lengkap]      : " + namaLengkap);
        System.out.println("  [Nomor KTP / NIK]   : " + nomorKtp);
        System.out.println("  [Nomor Telepon]     : " + nomorTelepon);
        System.out.println("  [Unit Sedang Disewa]: " + totalSewaAktif + " unit");
        System.out.println("  [Sisa Kuota Sewa]   : " + getSisaKuotaSewa() + " unit (Max: " + BATAS_MAX_SEWA + ")");
        System.out.println("  [Status Hak Sewa]   : " + (bolehSewa() ? "DIIZINKAN SEWA" : "KUOTA PENUH"));
        System.out.println("  -------------------------------------------------------------");
    }
}
