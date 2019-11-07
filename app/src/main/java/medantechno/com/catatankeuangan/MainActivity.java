package medantechno.com.catatankeuangan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import medantechno.com.catatankeuangan.database.DbTransaksi;
import medantechno.com.catatankeuangan.model.Transaksi;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Calendar myCalendar;
    EditText tanggal,v_ket,v_jumlah;
    Spinner spinJenis;
    DatePickerDialog.OnDateSetListener dateTanggal;
    Button simpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final Button btnTrx = (Button)findViewById(R.id.btnTransaksi);
        btnTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogWebview();
            }
        });

        Button btnHistory = (Button)findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,TrxActivity.class);
                startActivity(i);
            }
        });


        tampilSaldo();


        Button btnLap = (Button)findViewById(R.id.btnLaporan);
        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,LaporanActivity.class);
                startActivity(i);
            }
        });
    }

    public void tampilSaldo()
    {
        TextView v_saldo = (TextView)findViewById(R.id.saldo);
        DbTransaksi db = new DbTransaksi(getApplicationContext());
        v_saldo.setText(rupiah(db.saldo()));
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void DialogWebview()
    {

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View tampung = inflater.inflate(R.layout.form_trx,null);

        /**spinner jenis**/
        spinJenis = (Spinner)tampung.findViewById(R.id.spinnerJenis);
        List<String> jenis = new ArrayList<String>();
        jenis.add("Pemasukan");
        jenis.add("Pengeluaran");
        jenis.add("Koreksi");
        ArrayAdapter arrayAdapterJenis= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jenis);
        arrayAdapterJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinJenis.setAdapter(arrayAdapterJenis);
        int posisiJenis = arrayAdapterJenis.getPosition(jenis);
        spinJenis.setSelection(posisiJenis);
        /**spinner jenis**/

        /****datepicker****/
        tanggal = (EditText)tampung.findViewById(R.id.v_tanggal);
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
                tanggal.setText(sdf.format(myCalendar.getTime()));

            }

        };
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, dateTanggal, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /****datepicker*****/

        v_jumlah = (EditText)tampung.findViewById(R.id.eJumlah);
        v_ket = (EditText)tampung.findViewById(R.id.eKet);
        simpan = (Button)tampung.findViewById(R.id.btnSimpan);



        alertDialog.setView(tampung);
        alertDialog.setTitle("Form Transaksi");
        alertDialog.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                tampilSaldo();
            }
        });
        alertDialog.show();

        //alertDialog.getWindow().setLayout(600, 400); //Controlling width and height.


        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(v_jumlah.getText());
                String jen = spinJenis.getSelectedItem().toString();
                String ket = v_ket.getText().toString();
                String tgl = tanggal.getText().toString();
                Double jum;
                String strJum = v_jumlah.getText().toString();
                if(!strJum.equals(""))
                {
                    jum = Double.parseDouble(strJum);
                }else{
                    jum = (double)0;

                }


                Transaksi transaksi = new Transaksi();
                transaksi.setJenis(jen);
                transaksi.setKet(ket);
                transaksi.setTanggal(tgl);
                transaksi.setJumlah(jum);

                if(jum!=0)
                {

                    try {
                        new DbTransaksi(getApplicationContext()).insert(transaksi);
                    }catch (Exception e)
                    {
                        System.out.println(e.toString());
                    }

                    Toast.makeText(getApplicationContext(),"Berhasil disimpan",Toast.LENGTH_SHORT).show();

                    v_jumlah.setText("");
                    tanggal.setText("");
                    v_ket.setText("");
                }else {
                    Toast.makeText(getApplicationContext(),"Jumlah transaksi 0",Toast.LENGTH_SHORT).show();
                    v_jumlah.requestFocus();
                }

            }
        });

    }


}
