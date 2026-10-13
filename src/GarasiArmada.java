package src;

import java.util.ArrayList;

/**
 * Class GarasiArmada
 * Merepresentasikan pool penyimpanan atau garasi cabang armada AutoRent System.
 * Menerapkan relasi Agregasi (Aggregation ◇) terhadap Class Kendaraan.
 * Objek GarasiArmada (whole) mengelola sekumpulan objek Kendaraan (part).
 * Keberadaan (lifecycle) Kendaraan bersifat independen dan tidak terikat mati pada GarasiArmada.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Agregasi)
 */
public class GarasiArmada {
    // 1. Enkapsulasi: Attribute Private
    private String kodeGarasi;
    private String namaCabang;
    private String lokasiKota;
    private int kapasitasMaksimal;

    // Relasi Agregasi (Aggregation ◇): Garasi memiliki daftar referensi ke objek Kendaraan
    private ArrayList<Kendaraan> daftarArmada;

    // 2. Parameterized Constructor
    public GarasiArmada(String kodeGarasi, String namaCabang, String lokasiKota, int kapasitasMaksimal) {
        if (kodeGarasi != null && !kodeGarasi.trim().isEmpty()) {
            this.kodeGarasi = kodeGarasi.trim().toUpperCase();
        } else {
            this.kodeGarasi = "GAR-DEF-01";
        }

        this.namaCabang = (namaCabang != null && !namaCabang.trim().isEmpty()) ? namaCabang.trim() : "Cabang Utama";
        this.lokasiKota = (lokasiKota != null && !lokasiKota.trim().isEmpty()) ? lokasiKota.trim() : "Surabaya";
        this.kapasitasMaksimal = (kapasitasMaksimal > 0) ? kapasitasMaksimal : 10;
        this.daftarArmada = new ArrayList<>();
    }

    // 3. Getter Methods
    public String getKodeGarasi() {
        return kodeGarasi;
    }

    public String getNamaCabang() {
        return namaCabang;
    }

    public String getLokasiKota() {
        return lokasiKota;
    }

    public int getKapasitasMaksimal() {
        return kapasitasMaksimal;
    }

    public int getTotalArmada() {
        return daftarArmada.size();
    }

    public ArrayList<Kendaraan> getDaftarArmada() {
        return daftarArmada;
    }

    // 4. Operasi Agregasi (Menambah, Menghapus, Mencari Part Kendaraan)
    public boolean tambahKendaraan(Kendaraan k) {
        if (k == null) {
            System.out.println("  [GARASI ERROR] Objek Kendaraan tidak boleh NULL!");
            return false;
        }

        if (daftarArmada.size() >= kapasitasMaksimal) {
            System.out.println("  [GARASI ERROR] Garasi " + namaCabang + " sudah PENUH! (Kapasitas Max: " + kapasitasMaksimal + ")");
            return false;
        }

        // Cek duplikasi plat nomor dalam garasi
        for (Kendaraan existing : daftarArmada) {
            if (existing.getPlatNomor().equalsIgnoreCase(k.getPlatNomor())) {
                System.out.println("  [GARASI ERROR] Unit dengan Plat " + k.getPlatNomor() + " sudah terdaftar di garasi ini!");
                return false;
            }
        }

        daftarArmada.add(k);
        System.out.println("  >> [AGREGASI: SUKSES] Unit " + k.getPlatNomor() + " (" + k.getMerk() + ") berhasil ditambahkan ke " + namaCabang + " [" + daftarArmada.size() + "/" + kapasitasMaksimal + "]");
        return true;
    }

    public boolean hapusKendaraan(String platNomor) {
        if (platNomor == null) return false;
        for (int i = 0; i < daftarArmada.size(); i++) {
            if (daftarArmada.get(i).getPlatNomor().equalsIgnoreCase(platNomor.trim())) {
                Kendaraan removed = daftarArmada.remove(i);
                System.out.println("  >> [AGREGASI: MUTASI] Unit " + removed.getPlatNomor() + " (" + removed.getMerk() + ") dikeluarkan dari " + namaCabang + " (Objek unit tetap eksis di memori).");
                return true;
            }
        }
        System.out.println("  [GARASI ERROR] Unit Plat " + platNomor + " tidak ditemukan di " + namaCabang);
        return false;
    }

    public Kendaraan cariKendaraan(String platNomor) {
        if (platNomor == null) return null;
        for (Kendaraan k : daftarArmada) {
            if (k.getPlatNomor().equalsIgnoreCase(platNomor.trim())) {
                return k;
            }
        }
        return null;
    }

    public int hitungArmadaReady() {
        int readyCount = 0;
        for (Kendaraan k : daftarArmada) {
            if (k.isUnitTersedia()) {
                readyCount++;
            }
        }
        return readyCount;
    }

    public void tampilkanDaftarArmada() {
        System.out.println("  =============================================================");
        System.out.println("   INVENTARIS GARASI ARMADA: " + namaCabang.toUpperCase() + " (" + kodeGarasi + ")");
        System.out.println("   Lokasi Cabang : " + lokasiKota + " | Kapasitas: " + daftarArmada.size() + "/" + kapasitasMaksimal + " Unit");
        System.out.println("   Status Unit   : " + hitungArmadaReady() + " Unit Ready | " + (daftarArmada.size() - hitungArmadaReady()) + " Unit Sedang Disewa");
        System.out.println("  =============================================================");
        if (daftarArmada.isEmpty()) {
            System.out.println("   [INFO] Belum ada armada kendaraan yang ditempatkan di garasi ini.");
        } else {
            int no = 1;
            for (Kendaraan k : daftarArmada) {
                System.out.printf("   %d. [%s] %-28s | Rp %,.2f/hari | %s\n",
                        no++, k.getPlatNomor(), k.getMerk(), k.getTarifPerHari(),
                        (k.isUnitTersedia() ? "READY (Tersedia)" : "BOOKED (Disewa)"));
            }
        }
        System.out.println("  =============================================================");
    }

    /**
     * Membuktikan Agregasi: Mengosongkan garasi tanpa menghancurkan objek kendaraan di dalamnya
     */
    public void kosongkanGarasi() {
        System.out.println("  >> [AGREGASI PROOF] Mengosongkan seluruh armada dari " + namaCabang + "...");
        daftarArmada.clear();
        System.out.println("  >> Garasi " + namaCabang + " kini kosong, namun objek-objek Kendaraan di luar garasi tetap aktif!");
    }
}
