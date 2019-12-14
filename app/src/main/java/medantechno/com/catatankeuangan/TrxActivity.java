package medantechno.com.catatankeuangan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import medantechno.com.catatankeuangan.adapter.AdapterTrx;
import medantechno.com.catatankeuangan.database.DbTransaksi;
import medantechno.com.catatankeuangan.model.Transaksi;

/**
 * Created by dinaskominfokab.pakpakbharat on 07/11/18.
 */

public class TrxActivity extends AppCompatActivity {

    private AdapterTrx adapterTrx;
    private List<Transaksi> transaksiList = new ArrayList<>();
    private ListView listView;
    private ProgressDialog pDialog;

    EditText editText;
    SwipeRefreshLayout swipeSuka;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        /******** membuat back button *******/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /******** membuat back button *******/


        listView = (ListView) findViewById(R.id.list);
        listTrx();

        swipeSuka = findViewById(R.id.swipeSuka);
        swipeSuka.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listTrx();
                swipeSuka.setRefreshing(false);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tambah) {

            Intent iii = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(iii);

            return true;
        }

        if (id == R.id.action_laporan) {

            Intent ii = new Intent(getApplicationContext(),LaporanActivity.class);
            startActivity(ii);

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
                    transaksi.setImg(trx.getImg());

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
