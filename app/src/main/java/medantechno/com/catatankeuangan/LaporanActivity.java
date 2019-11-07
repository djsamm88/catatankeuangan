package medantechno.com.catatankeuangan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
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

    private AdapterTrx adapterTrx;
    private List<Transaksi> transaksiList = new ArrayList<>();
    private ListView listView;
    private ProgressDialog pDialog;

    EditText v_awal,v_akhir;
    SwipeRefreshLayout swipeSuka;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateTanggal,dateTanggal2;
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



                try{
                    SimpleDateFormat xxx = new SimpleDateFormat("yyyy-MM-dd");
                    Date tglAntara = xxx.parse(sdf.format(myCalendar.getTime()));
                    Date tglMulai = xxx.parse(v_awal.getText().toString());
                    if (tglMulai.getTime() > tglAntara.getTime()) {
                        //catalog_outdated = 1;
                        System.out.println("ada masalah");
                        Toast.makeText(getApplicationContext(),"Peringatan!! Tanggal akhir harus lebih besar.",Toast.LENGTH_LONG).show();
                        v_akhir.setText("");
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




    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(getApplicationContext(), LaporanActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void listTrx()
    {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        adapterTrx = new AdapterTrx(this,transaksiList);
        listView.setAdapter(adapterTrx);
        adapterTrx.notifyDataSetChanged();

                hidePDialog();

                DbTransaksi dbTransaksi = new DbTransaksi(getApplicationContext());
                List<Transaksi> transaksis = dbTransaksi.getAll();
                transaksiList.clear();

                for(Transaksi trx:transaksis)
                {
                    Transaksi transaksi = new Transaksi();
                    //int id,String jenis,double jumlah,String tanggal,String ket
                    transaksi.setJenis(trx.getJenis());
                    transaksi.setJumlah(trx.getJumlah());
                    transaksi.setTanggal(trx.getTanggal());
                    transaksi.setKet(trx.getKet());
                    transaksi.setId(trx.getId());

                    transaksiList.add(transaksi);

                }

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


}
