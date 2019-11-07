package medantechno.com.catatankeuangan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import medantechno.com.catatankeuangan.R;
import medantechno.com.catatankeuangan.database.DbTransaksi;
import medantechno.com.catatankeuangan.model.Transaksi;

/**
 * Created by dinaskominfokab.pakpakbharat on 07/11/18.
 */

public class AdapterTrx extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Transaksi> transaksiList;
    String keyword;
    private AdapterTrx adapter;

    public AdapterTrx(Activity activity, List<Transaksi> transaksiList){
        this.activity=activity;
        this.transaksiList=transaksiList;

        this.adapter=this;
    }


    @Override
    public int getCount() {
        return transaksiList.size();
    }

    @Override
    public Object getItem(int location) {
        return transaksiList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) convertView = inflater.inflate(R.layout.adapter_trx, null);


        TextView jenis  = (TextView) convertView.findViewById(R.id.v_jenis);
        TextView jumlah = (TextView) convertView.findViewById(R.id.v_jumlah);
        TextView ket = (TextView) convertView.findViewById(R.id.v_ket);
        TextView tgl = (TextView)convertView.findViewById(R.id.v_tgl);



        Button hapus = (Button) convertView.findViewById(R.id.hapus);


        final Transaksi mTrx = transaksiList.get(position);



        jenis.setText(mTrx.getJenis());
        jumlah.setText(rupiah(mTrx.getJumlah()));
        tgl.setText(mTrx.getTanggal());

        ket.setText(mTrx.getKet());

        LinearLayout ln = (LinearLayout) convertView.findViewById(R.id.layoutAdapter);
        if(mTrx.getJenis().equals("Pengeluaran"))
        {
            ln.setBackgroundColor(Color.parseColor("#FAF5ED"));
        }else if(mTrx.getJenis().equals("Pemasukan")){
            ln.setBackgroundColor(Color.parseColor("#EDF8ED"));
        }else{
            ln.setBackgroundColor(Color.parseColor("#FADCDA"));
        }



         hapus.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 DbTransaksi dbTransaksi = new DbTransaksi(activity);
                 dbTransaksi.delete(String.valueOf(mTrx.getId()));

                 Snackbar.make(view, "Dihapus dari Database.", Snackbar.LENGTH_LONG).show();
                 transaksiList.remove(mTrx);
                 adapter.notifyDataSetChanged();
             }
         });
        return convertView;

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
