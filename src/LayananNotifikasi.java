package src;

/**
 * Class LayananNotifikasi
 * Merepresentasikan service pengiriman pesan instan (WhatsApp / SMS / Gateway).
 * Berperan dalam relasi Ketergantungan (Dependency - - - ->) dengan TransaksiSewa.
 * Objek ini digunakan sementara saat pemrosesan booking dan pengembalian unit.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Dependency)
 */
public class LayananNotifikasi {
    private String providerGateway;

    public LayananNotifikasi(String providerGateway) {
        this.providerGateway = (providerGateway != null) ? providerGateway : "AutoRent Cloud Notifier Gateway";
    }

    public LayananNotifikasi() {
        this("AutoRent WhatsApp & SMS Gateway");
    }

    public void kirimNotifikasiPemesanan(Pelanggan pelanggan, TransaksiSewa transaksi) {
        if (pelanggan == null || transaksi == null) return;
        System.out.println("  >> [NOTIFIKASI GATEWAY (" + providerGateway + ")]");
        System.out.println("     Tujuan: " + pelanggan.getNomorTelepon() + " (" + pelanggan.getNamaLengkap() + ")");
        System.out.println("     Pesan : Halo " + pelanggan.getNamaLengkap() + "! Booking rental Anda (" + transaksi.getKodeTransaksi() + ") untuk unit " +
                transaksi.getKendaraan().getMerk() + " [" + transaksi.getKendaraan().getPlatNomor() + "] selama " +
                transaksi.getDurasiHari() + " hari BERHASIL dikonfirmasi. Total: Rp " + String.format("%,.2f", transaksi.getTotalBiaya()));
    }

    public void kirimNotifikasiPengembalian(Pelanggan pelanggan, TransaksiSewa transaksi, int hariTelat, double denda) {
        if (pelanggan == null || transaksi == null) return;
        System.out.println("  >> [NOTIFIKASI GATEWAY (" + providerGateway + ")]");
        System.out.println("     Tujuan: " + pelanggan.getNomorTelepon() + " (" + pelanggan.getNamaLengkap() + ")");
        System.out.println("     Pesan : Terima kasih " + pelanggan.getNamaLengkap() + "! Unit " +
                transaksi.getKendaraan().getPlatNomor() + " telah berhasil dikembalikan." +
                (hariTelat > 0 ? " Tercatat keterlambatan " + hariTelat + " hari dengan denda Rp " + String.format("%,.2f", denda) + "." : " Unit dikembalikan tepat waktu.") +
                " Transaksi " + transaksi.getKodeTransaksi() + " resmi SELESAI.");
    }
}
