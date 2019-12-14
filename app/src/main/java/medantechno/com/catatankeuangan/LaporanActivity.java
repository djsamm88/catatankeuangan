package medantechno.com.catatankeuangan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import medantechno.com.catatankeuangan.adapter.AdapterTrx;
import medantechno.com.catatankeuangan.database.DbTransaksi;
import medantechno.com.catatankeuangan.model.Transaksi;

/**
 * Created by dinaskominfokab.pakpakbharat on 07/11/18.
 */

public class LaporanActivity extends AppCompatActivity {

     AdapterTrx adapterTrx;
     List<Transaksi> transaksiList = new ArrayList<>();

     ProgressDialog pDialog;

    ListView listView;
    EditText v_awal,v_akhir;
    TextView t_pemasukan,t_pengeluaran,t_koreksi,t_total;
    SwipeRefreshLayout swipeSuka;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateTanggal,dateTanggal2;

    Double pemasukan,pengeuaran,koreksi,total;

    Button btnTampil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        /******** membuat back button *******/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /******** membuat back button *******/

        listView = (ListView)findViewById(R.id.list);

        t_pemasukan = (TextView)findViewById(R.id.t_pemasukan);
        t_pengeluaran = (TextView)findViewById(R.id.t_pengeluaran);
        t_total = (TextView)findViewById(R.id.total_semua);
        t_koreksi = (TextView)findViewById(R.id.t_koreksi);

        /****datepicker awal****/
        v_awal = (EditText)findViewById(R.id.spinnerAwal);
        myCalendar = Calendar.getInstance();
        dateTanggal = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                Locale localeID = new Locale("in", "ID");
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, localeID);
                v_awal.setText(sdf.format(myCalendar.getTime()));

            }

        };
        v_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LaporanActivity.this, dateTanggal, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /****datepicker awal*****/

        /****datepicker akhir****/
        v_akhir = (EditText)findViewById(R.id.spinnerAkhir);
        myCalendar = Calendar.getInstance();
        dateTanggal2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                Locale localeID = new Locale("in", "ID");
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, localeID);
                v_akhir.setText(sdf.format(myCalendar.getTime()));



                try {
                    SimpleDateFormat xxx = new SimpleDateFormat("yyyy-MM-dd");
                    Date tglAntara = xxx.parse(sdf.format(myCalendar.getTime()));
                    Date tglMulai = xxx.parse(v_awal.getText().toString());
                    if (tglMulai.getTime() > tglAntara.getTime()) {
                        //catalog_outdated = 1;
                        System.out.println("ada masalah");
                        Toast.makeText(getApplicationContext(), "Peringatan!! Tanggal akhir harus lebih besar.", Toast.LENGTH_LONG).show();
                        v_akhir.setText("");
                    }

                }catch (Exception e)
                {

                }

            }

        };
        v_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LaporanActivity.this, dateTanggal2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /****datepicker akhir*****/

        Calendar c = Calendar.getInstance();
        int tahun = c.get(Calendar.YEAR);
        int bulan = c.get(Calendar.MONTH);
        Locale localeID = new Locale("in", "ID");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", localeID);

        String awal_bulan = String.valueOf(tahun)+"-"+String.valueOf(bulan+1)+"-01";
        String sekarang = sdf.format(c.getTime());
        listTrx(awal_bulan,sekarang);


        swipeSuka = findViewById(R.id.swipeSuka);
        swipeSuka.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar c = Calendar.getInstance();
                int tahun = c.get(Calendar.YEAR);
                int bulan = c.get(Calendar.MONTH);
                Locale localeID = new Locale("in", "ID");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", localeID);

                String awal_bulan = String.valueOf(tahun)+"-"+String.valueOf(bulan+1)+"-01";
                String sekarang = sdf.format(c.getTime());
                listTrx(awal_bulan,sekarang);
                swipeSuka.setRefreshing(false);
            }
        });

        btnTampil = (Button)findViewById(R.id.btnTampil);
        btnTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String awal = v_awal.getText().toString();
                String akhir = v_akhir.getText().toString();
                listTrx(awal,akhir);

                swipeSuka = findViewById(R.id.swipeSuka);
                swipeSuka.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        String awal = v_awal.getText().toString();
                        String akhir = v_akhir.getText().toString();
                        listTrx(awal,akhir);
                        swipeSuka.setRefreshing(false);
                    }
                });

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_tambah) {
            Intent iii = new Intent(LaporanActivity.this,MainActivity.class);
            startActivity(iii);
            return true;
        }

        if (id == R.id.action_history) {
            Intent ii = new Intent(LaporanActivity.this,TrxActivity.class);
            startActivity(ii);
            return true;
        }

        if (id == R.id.action_laporan) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void listTrx(String awal,String akhir)
    {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        adapterTrx = new AdapterTrx(this,transaksiList);
        listView.setAdapter(adapterTrx);
        adapterTrx.notifyDataSetChanged();

                hidePDialog();

                DbTransaksi dbTransaksi = new DbTransaksi(getApplicationContext());
                List<Transaksi> transaksis = dbTransaksi.getAntara(awal,akhir);
                transaksiList.clear();

                pengeuaran=(double)0;
                pemasukan=(double)0;
                total=(double)0;
                koreksi=(double)0;

                for(Transaksi trx:transaksis)
                {
                    Transaksi transaksi = new Transaksi();
                    //int id,String jenis,double jumlah,String tanggal,String ket
                    transaksi.setJenis(trx.getJenis());
                    transaksi.setJumlah(trx.getJumlah());
                    transaksi.setTanggal(trx.getTanggal());
                    transaksi.setKet(trx.getKet());
                    transaksi.setId(trx.getId());
                    transaksi.setImg(trx.getImg());


                    int cAwal = Integer.parseInt(awal.replaceAll("\\D+",""));
                    int cAkhir = Integer.parseInt(akhir.replaceAll("\\D+",""));

                    int hTgl = Integer.parseInt(trx.getTanggal().replaceAll("\\D+",""));

                    if(hTgl>=cAwal && hTgl<=cAkhir) {

                        transaksiList.add(transaksi);

                        if (trx.getJenis().equals("Pengeluaran")) {
                            pengeuaran += trx.getJumlah();
                        } else if (trx.getJenis().equals("Pemasukan")) {
                            pemasukan += trx.getJumlah();
                        } else {
                            koreksi += trx.getJumlah();
                        }
                    }




                }

                total+=pemasukan-pengeuaran+(koreksi);

                t_pengeluaran.setText(rupiah(pengeuaran));
                t_pemasukan.setText(rupiah(pemasukan));
                t_koreksi.setText(rupiah(koreksi));
                t_total.setText(rupiah(total));

                v_awal.setText(awal);
                v_akhir.setText(akhir);

                if(transaksis.size()==0)
                {
                    Toast.makeText(getApplicationContext(),"Belum ada transaksi",Toast.LENGTH_LONG).show();
                }

                adapterTrx.notifyDataSetChanged();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    public String rupiah(double uang)
    {

        /**** format rupiah ***/
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        //penggunaan formatRupiah.format();
        /**** format rupiah ***/
        return formatRupiah.format(uang);

    }


}
