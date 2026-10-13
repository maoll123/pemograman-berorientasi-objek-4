package src;

/**
 * Class DetailItemBiaya
 * Merepresentasikan rincian item biaya dalam satu transaksi rental (Invoice Line Item).
 * Menjadi bagian integral (part) dalam relasi Komposisi (Composition ◆) dengan TransaksiSewa.
 * Diciptakan, dihitung, dan dimusnahkan bersamaan dengan siklus hidup (lifecycle) TransaksiSewa.
 *
 * @author Ahmad Yasar Maulana Dais (NRP: 3125522018)
 * @version 4.0 (Modul 4 - Relasi Antarobjek: Komposisi)
 */
public class DetailItemBiaya {
    // 1. Enkapsulasi: Attribute Private
    private String namaItem;
    private double hargaSatuan;
    private int kuantitas;
    private double subtotal;

    // 2. Parameterized Constructor
    public DetailItemBiaya(String namaItem, double hargaSatuan, int kuantitas) {
        this.namaItem = (namaItem != null && !namaItem.trim().isEmpty()) ? namaItem.trim() : "Item Tambahan";
        this.hargaSatuan = (hargaSatuan >= 0) ? hargaSatuan : 0.0;
        this.kuantitas = (kuantitas > 0) ? kuantitas : 1;
        this.subtotal = this.hargaSatuan * this.kuantitas;
    }

    // 3. Getter Methods
    public String getNamaItem() {
        return namaItem;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // 4. Method Tampilan Item
    public void tampilkanItem(int nomorUrut) {
        System.out.printf("     %2d. %-32s | %2d unit/hari x Rp %,10.2f = Rp %,12.2f\n",
                nomorUrut, namaItem, kuantitas, hargaSatuan, subtotal);
    }
}
