package medantechno.com.catatankeuangan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import medantechno.com.catatankeuangan.model.Transaksi;



public class DbTransaksi extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "transaksi.db";

    private static final String table = "tbl_transaksi";

    private static final String id = "id";
    private static final String jenis = "jenis";
    private static final String jumlah = "jumlah";
    private static final String tanggal = "tanggal";
    private static final String ket = "ket";

    public DbTransaksi(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // membuat Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE  "+table+" ("+id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+jenis+" TEXT,"+jumlah+" TEXT,"+tanggal+" TEXT,"+ket+" TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+table);

        // Create tables again
        onCreate(db);
    }



    public void insert(Transaksi transaksi)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(id,transaksi.getId());
        values.put(jenis,transaksi.getJenis());
        values.put(jumlah,transaksi.getJumlah());
        values.put(tanggal,transaksi.getTanggal());
        values.put(ket,transaksi.getKet());
        db.insertOrThrow(table, null, values);
        //db.insert(table,null,values);
        db.close();
    }



    public void update(Transaksi transaksi)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id,transaksi.getId());
        values.put(jenis,transaksi.getJenis());
        values.put(jumlah,transaksi.getJumlah());
        values.put(tanggal,transaksi.getTanggal());
        values.put(ket,transaksi.getKet());

        db.update(table, values, id+"=?",new String[]{String.valueOf(transaksi.getId())});
        //db.insert(table,null,values);
        db.close();
    }

    // Getting All
    public List<Transaksi> getAll() {
        List<Transaksi> semuanya = new ArrayList<Transaksi>();
        String selectQuery = "SELECT  * FROM " + table + " ORDER BY DATE("+tanggal+") DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();

                transaksi.setId(cursor.getInt(0));
                transaksi.setJenis(cursor.getString(1));
                transaksi.setJumlah(cursor.getDouble(2));
                transaksi.setTanggal(cursor.getString(3));
                transaksi.setKet(cursor.getString(4));
                semuanya.add(transaksi);
            } while (cursor.moveToNext());
        }

        return semuanya;
    }

    public List<Transaksi> getAntara(String awal,String akhir) {

        awal = awal.replaceAll("\\D+","");
        akhir = akhir.replaceAll("\\D+","");

        List<Transaksi> semuanya = new ArrayList<Transaksi>();
        String selectQuery = "SELECT  * FROM " + table + " WHERE tanggal>="+awal+" AND tanggal<="+akhir+" ORDER BY DATE("+tanggal+") DESC";
        System.out.println(selectQuery);
        //String selectQuery = "SELECT  * FROM " + table + " ORDER BY DATE("+tanggal+") DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();

                transaksi.setId(cursor.getInt(0));
                transaksi.setJenis(cursor.getString(1));
                transaksi.setJumlah(cursor.getDouble(2));
                transaksi.setTanggal(cursor.getString(3));
                transaksi.setKet(cursor.getString(4));
                semuanya.add(transaksi);
            } while (cursor.moveToNext());
        }

        return semuanya;
    }

    public Double saldo()
    {
        Double saldo =(double)0;

        String qPengeluaran = "SELECT SUM(jumlah) FROM " + table+" WHERE jenis='Pengeluaran'";
        SQLiteDatabase dbP = this.getWritableDatabase();
        Cursor cPengeluaran = dbP.rawQuery(qPengeluaran, null);

        Double saldoPengeluaran =(double)0;
        if (cPengeluaran.moveToFirst()) {
            do {
                //cursor.getInt(0);
                saldoPengeluaran+=cPengeluaran.getDouble(0);

            } while (cPengeluaran.moveToNext());
        }


        String qPemasukan = "SELECT SUM(jumlah) FROM " + table+" WHERE jenis='Pemasukan'";
        SQLiteDatabase dbPemasukan = this.getWritableDatabase();
        Cursor cPemasukan = dbPemasukan.rawQuery(qPemasukan, null);

        Double saldoPemasukan =(double)0;
        if (cPemasukan.moveToFirst()) {
            do {
                //cursor.getInt(0);
                saldoPemasukan+=cPemasukan.getDouble(0);

            } while (cPemasukan.moveToNext());
        }




        String qKoreksi = "SELECT SUM(jumlah) FROM " + table+" WHERE jenis='Koreksi'";
        SQLiteDatabase dbKoreksi = this.getWritableDatabase();
        Cursor cKoreksi = dbKoreksi.rawQuery(qKoreksi, null);

        Double saldoKoreksi =(double)0;
        if (cKoreksi.moveToFirst()) {
            do {
                //cursor.getInt(0);
                saldoKoreksi+=cKoreksi.getDouble(0);

            } while (cKoreksi.moveToNext());
        }



        saldo = saldoPemasukan-saldoPengeluaran+(saldoKoreksi);

        return saldo;
    }

    // Getting All
    public List<Transaksi> cari(String key) {
        List<Transaksi> semuanya = new ArrayList<Transaksi>();

        String selectQuery;

        if(isNumeric(key))
        {
            selectQuery = "SELECT  * FROM " + table + " WHERE "+jumlah+"="+key;
        }else{
            selectQuery = "SELECT  * FROM " + table + " WHERE "+ket+" LIKE '%"+key+"%' ORDER BY "+id+" DESC";
        }



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();

                transaksi.setId(cursor.getInt(0));
                transaksi.setJenis(cursor.getString(1));
                transaksi.setJumlah(cursor.getDouble(2));
                transaksi.setTanggal(cursor.getString(3));
                transaksi.setKet(cursor.getString(4));

                semuanya.add(transaksi);
            } while (cursor.moveToNext());
        }

        return semuanya;
    }




    public void delete(String idnya)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+table+" where "+id+"='"+idnya+"'");

    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }



}
