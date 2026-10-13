package src;

/**
 * Class Mesin
 * Merepresentasikan spesifikasi mesin internal dari kendaraan.
 * Menjadi bagian integral (part) dalam relasi Komposisi (Composition) dengan Class Kendaraan.
 * Lifecycle objek Mesin dikendalikan sepenuhnya oleh objek Kendaraan.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Komposisi)
 */
public class Mesin {
    // Enkapsulasi: Seluruh attribute berstatus private
    private String nomorRangkaMesin;
    private int kapasitasCC;
    private String tipeTransmisi; // "Manual" atau "Automatic"
    private String kondisiMesin;  // "Prima", "Perlu Servis", "Kritis"

    // Default & Parameterized Constructor (Defensive)
    public Mesin(String nomorRangkaMesin, int kapasitasCC, String tipeTransmisi) {
        if (nomorRangkaMesin != null && !nomorRangkaMesin.trim().isEmpty()) {
            this.nomorRangkaMesin = nomorRangkaMesin.trim().toUpperCase();
        } else {
            this.nomorRangkaMesin = "ENG-DEFAULT-001";
        }

        if (kapasitasCC >= 500 && kapasitasCC <= 6000) {
            this.kapasitasCC = kapasitasCC;
        } else {
            this.kapasitasCC = 1500; // Standar fallback
        }

        if (tipeTransmisi != null && (tipeTransmisi.equalsIgnoreCase("Automatic") || tipeTransmisi.equalsIgnoreCase("Manual") || tipeTransmisi.equalsIgnoreCase("CVT") || tipeTransmisi.equalsIgnoreCase("AT"))) {
            this.tipeTransmisi = tipeTransmisi.trim();
        } else {
            this.tipeTransmisi = "Automatic (Default)";
        }

        this.kondisiMesin = "Prima (100%)";
    }

    // Getter Methods (Controlled Read Access)
    public String getNomorRangkaMesin() {
        return nomorRangkaMesin;
    }

    public int getKapasitasCC() {
        return kapasitasCC;
    }

    public String getTipeTransmisi() {
        return tipeTransmisi;
    }

    public String getKondisiMesin() {
        return kondisiMesin;
    }

    // Mutator Methods
    public void setKondisiMesin(String kondisiMesin) {
        if (kondisiMesin != null && !kondisiMesin.trim().isEmpty()) {
            this.kondisiMesin = kondisiMesin.trim();
        }
    }

    public void lakukanServisBerkala() {
        this.kondisiMesin = "Prima (Baru Diservis)";
        System.out.println("     >> [Mesin " + nomorRangkaMesin + "] Servis berkala selesai. Kondisi mesin kembali PRIMA.");
    }

    public void tampilkanSpesifikasiMesin() {
        System.out.println("     [No. Rangka Mesin] : " + nomorRangkaMesin);
        System.out.println("     [Kapasitas Silinder]: " + kapasitasCC + " cc");
        System.out.println("     [Tipe Transmisi]   : " + tipeTransmisi);
        System.out.println("     [Kesehatan Mesin]  : " + kondisiMesin);
    }
}
