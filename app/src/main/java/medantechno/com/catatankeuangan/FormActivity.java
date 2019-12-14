package medantechno.com.catatankeuangan;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import medantechno.com.catatankeuangan.database.DbTransaksi;
import medantechno.com.catatankeuangan.model.Transaksi;

public class FormActivity extends AppCompatActivity {

    Calendar myCalendar;
    EditText tanggal,v_ket,v_jumlah,t4Img;
    Spinner spinJenis;
    DatePickerDialog.OnDateSetListener dateTanggal;
    Button simpan;
    ImageView refresh,vImg;

    private static final int STORAGE_PERMISSION_CODE = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_trx);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        requestStoragePermission();

        /**spinner jenis**/
        spinJenis = (Spinner)findViewById(R.id.spinnerJenis);
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
        tanggal = (EditText)findViewById(R.id.v_tanggal);
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
                new DatePickerDialog(FormActivity.this, dateTanggal, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /****datepicker*****/

        v_jumlah = (EditText)findViewById(R.id.eJumlah);
        v_ket = (EditText)findViewById(R.id.eKet);
        simpan = (Button)findViewById(R.id.btnSimpan);
        vImg = (ImageView)findViewById(R.id.gbr);
        t4Img = (EditText)findViewById(R.id.t4Img);

        vImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disini nanti ambil gambar
                /*
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                */
                dispatchTakePictureIntent();
            }
        });


        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(v_jumlah.getText());
                String jen = spinJenis.getSelectedItem().toString();
                String ket = v_ket.getText().toString();
                String tgl = tanggal.getText().toString();
                String img = t4Img.getText().toString();
                Double jum;
                String strJum = v_jumlah.getText().toString();
                if(!strJum.equals(""))
                {
                    jum = Double.parseDouble(strJum);
                }else{
                    jum = (double)0;

                }

                System.out.println("1:"+img);



                Transaksi transaksi = new Transaksi();
                transaksi.setJenis(jen);
                transaksi.setKet(ket);
                transaksi.setTanggal(tgl);
                transaksi.setJumlah(jum);
                transaksi.setImg(img);

                if(jum!=0)
                {

                    try {
                        new DbTransaksi(getApplicationContext()).insert(transaksi);
                    }catch (Exception e)
                    {
                        System.out.println(e.toString());
                    }

                    Toast.makeText(getApplicationContext(),"Berhasil disimpan:"+img,Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);

                    v_jumlah.setText("");
                    tanggal.setText("");
                    v_ket.setText("");
                    t4Img.setText("");
                }else {
                    Toast.makeText(getApplicationContext(),"Jumlah transaksi 0",Toast.LENGTH_SHORT).show();
                    v_jumlah.requestFocus();
                }

            }
        });

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
            //DialogWebview();
            return true;
        }

        if (id == R.id.action_history) {
            Intent i = new Intent(FormActivity.this,TrxActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_laporan) {
            Intent i = new Intent(FormActivity.this,LaporanActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "medantechno.com.catatankeuangan.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);

            }
        }
    }



    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {

            /*
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            vImg.setImageBitmap(imageBitmap);
            */

            try {


                File imgFile = new File(currentPhotoPath);

                if (imgFile.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    /** mengecilkan Bitmap **/
                    Bitmap converetdImage = getResizedBitmap(myBitmap, 1200);
                    /** mengecilkan Bitmap **/

                    /** membuat watermark **/
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                    Bitmap mark = mark(converetdImage, timeStamp);
                    /** membuat watermark **/

                    /** menyimpan Bitmap jadi file **/
                    File file = new File(imgFile.getAbsolutePath());
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                    mark.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    /** menyimpan Bitmap jadi file **/

                    //vImg.setImageBitmap(mark);
                    vImg.setImageBitmap(mark);

                    System.out.println(currentPhotoPath);
                    t4Img.setText(currentPhotoPath);

                    galleryAddPic();


                    //mInputPhotonya1.setText(mCurrentPhotoPath);

                    /** image to base64 ***/
                    Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();
                    /** image to base64 ***/

                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    //alamat.setText(encodedImage);
                    //System.out.println(encodedImage);


                } else {
                    Log.d("xxx", "tidaak ada");
                }

            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Ada kesalahan. Ulangi lagi.",Toast.LENGTH_LONG).show();



            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    //calling the method:
    //Bitmap converetdImage = getResizedBitmap(photo, 500);


    public static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        canvas.drawText(watermark, 20, 25, paint);

        return result;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }




}
