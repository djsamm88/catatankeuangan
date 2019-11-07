package medantechno.com.catatankeuangan.model;

public class Transaksi {
    String jenis,tanggal,ket;
    int id;
    double jumlah;
    public Transaksi()
    {

    }

    public Transaksi (int id,String jenis,double jumlah,String tanggal,String ket)
    {
        this.id = id;
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.ket = ket;

    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getId() {
        return id;
    }

    public String getJenis() {
        return jenis;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }




}
