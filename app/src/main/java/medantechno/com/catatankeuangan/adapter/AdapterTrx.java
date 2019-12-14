package medantechno.com.catatankeuangan.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.URI;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
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

        TextView v_bukti = (TextView) convertView.findViewById(R.id.v_bukti);


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
                /*
                 DbTransaksi dbTransaksi = new DbTransaksi(activity);
                 dbTransaksi.delete(String.valueOf(mTrx.getId()));

                 Snackbar.make(view, "Dihapus dari Database.", Snackbar.LENGTH_LONG).show();
                 transaksiList.remove(mTrx);
                 adapter.notifyDataSetChanged();

                 */

                showDialog(view,mTrx);
             }
         });


        v_bukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    /*
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("content://" + mTrx.getImg()), "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                     */
                    showImage(Uri.parse(mTrx.getImg()));
                }catch (Exception e)
                {
                    System.out.println(e);
                    Toast.makeText(activity,"Tidak ada struk",Toast.LENGTH_SHORT).show();
                }
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

    public void showImage(Uri imageUri) {
        Dialog builder = new Dialog(activity);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(activity);
        imageView.setImageURI(imageUri);

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
        builder.getWindow().setLayout(600,800);
    }


    private void showDialog(final View v, final Transaksi trx){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        // set title dialog
        alertDialogBuilder.setTitle("Anda akan menghapus Trx?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk hapus " +trx.getJenis()+" sebesar "+rupiah(trx.getJumlah())+ "!")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        //activity.finish();
                        /********* disini action menghapus*****/
                        DbTransaksi dbTransaksi = new DbTransaksi(activity);
                        dbTransaksi.delete(String.valueOf(trx.getId()));

                        Snackbar.make(v, trx.getJenis()+" sebesar "+trx.getJumlah()+" Dihapus dari Database.", Snackbar.LENGTH_LONG).show();
                        transaksiList.remove(trx);
                        adapter.notifyDataSetChanged();
                        /******* disini action menghapus*****/

                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }
}
