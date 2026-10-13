package src;

/**
 * Class KuitansiPrinter
 * Merepresentasikan service utilitas pencetakan bukti transaksi / invoice.
 * Berperan dalam relasi Ketergantungan (Dependency - - - ->) dengan TransaksiSewa.
 * Class ini tidak disimpan sebagai attribute permanen di TransaksiSewa,
 * melainkan disuplai sementara sebagai parameter method saat operasi cetak dipanggil.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Dependency)
 */
public class KuitansiPrinter {
    private String tipePrinter;
    private String modelPerangkat;

    public KuitansiPrinter(String tipePrinter, String modelPerangkat) {
        this.tipePrinter = (tipePrinter != null) ? tipePrinter : "Thermal POS";
        this.modelPerangkat = (modelPerangkat != null) ? modelPerangkat : "Epson TM-T82X";
    }

    public KuitansiPrinter() {
        this("Thermal POS 80mm", "Epson TM-T82X");
    }

    public String getModelPerangkat() {
        return modelPerangkat;
    }

    public void cetakKuitansiResmi(TransaksiSewa transaksi, String namaPetugasKasir) {
        if (transaksi == null) {
            System.out.println("  [PRINTER ERROR] Objek Transaksi NULL, tidak dapat mencetak kuitansi!");
            return;
        }

        System.out.println("\n  =======================================================================");
        System.out.println("                     AUTORENT SYSTEM - OFFICIAL RECEIPT                  ");
        System.out.println("               Pusat Rental & Manajemen Armada Kendaraan Modern           ");
        System.out.println("  =======================================================================");
        System.out.println("   Device Printer : " + modelPerangkat + " (" + tipePrinter + ")");
        System.out.println("   Petugas Kasir  : " + (namaPetugasKasir != null ? namaPetugasKasir : "Admin Rental"));
        System.out.println("   No. Transaksi  : " + transaksi.getKodeTransaksi());
        System.out.println("   Tanggal Sewa   : " + transaksi.getTanggalSewa());
        System.out.println("   Status Transaksi: " + transaksi.getStatusSewa());
        System.out.println("  -----------------------------------------------------------------------");
        
        Pelanggan p = transaksi.getPelanggan();
        if (p != null) {
            System.out.println("   [DATA PENYEWA]");
            System.out.println("   Nama Lengkap   : " + p.getNamaLengkap() + " (ID: " + p.getIdPelanggan() + ")");
            System.out.println("   No. Kontak / WA: " + p.getNomorTelepon());
            System.out.println("   NIK KTP        : " + p.getNomorKtp());
        }

        Kendaraan k = transaksi.getKendaraan();
        if (k != null) {
            System.out.println("   [DATA ARMADA]");
            System.out.println("   Unit Kendaraan : " + k.getMerk() + " [" + k.getJenis() + "]");
            System.out.println("   Plat Nomor     : " + k.getPlatNomor());
            System.out.println("   Tarif Harian   : Rp " + String.format("%,.2f", k.getTarifPerHari()));
            if (k.getMesin() != null) {
                System.out.println("   Mesin / Transm.: " + k.getMesin().getNomorRangkaMesin() + " (" + k.getMesin().getTipeTransmisi() + ")");
            }
        }

        System.out.println("  -----------------------------------------------------------------------");
        System.out.println("   [RINCIAN ITEM BIAYA (KOMPOSISI PART)]");
        transaksi.tampilkanRincianItem();

        System.out.println("  -----------------------------------------------------------------------");
        System.out.printf("   TOTAL PEMBAYARAN: Rp %,12.2f\n", transaksi.getTotalBiaya());
        System.out.println("  =======================================================================");
        System.out.println("          Terima kasih telah mempercayakan perjalanan Anda bersama kami!  ");
        System.out.println("  =======================================================================\n");
    }
}
