package fr.autruche.slurpsV2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ProfilsGrid extends AppCompatActivity {

    private GridLayout gridLayout1;
    private ImageView addCircle;
    private int c;
    private static final int REQUEST_IMAGE_CAPTURE = 101;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profils_grid);



        gridLayout1 = findViewById(R.id.GridLayout1);
        FrameLayout flCercle = findViewById(R.id.flCercle);
        flCercle.setPadding(60, 60, 60, 60);




        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int px = metrics.widthPixels;
        c = (px / 3);




        addCircle = findViewById(R.id.addCircle);
        addCircle.setAdjustViewBounds(true);
        addCircle.setMaxWidth(c);
        addCircle.setMaxHeight(c);


        //debug = findViewById(R.id.DEBUG);


        if(Files.exists(Paths.get("historyImage")))
        {
            try {
                write("");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createOnClickPhotoBtn();
    }

    public void setImageViewFromPath(String path) {
        //creation FrameLayout
        FrameLayout fm = new FrameLayout(gridLayout1.getContext());
        int p = 60;
        fm.setPadding(p, p, p, p);

        //creation Cardview
        CardView cd = new CardView(fm.getContext());
        cd.setRadius(500);

        // chemin image
        ImageView v = new ImageView(cd.getContext());

        //image en bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        //ragnage bitmap
        int value = 0;
        Bitmap finalBitmap;
        if (bitmap.getHeight() <= bitmap.getWidth()) {
            value = bitmap.getHeight();
            finalBitmap = Bitmap.createBitmap(bitmap,(bitmap.getWidth()-value)/2 , 0, value, value);
        } else {
            value = bitmap.getWidth();
            finalBitmap = Bitmap.createBitmap(bitmap,0 , (bitmap.getHeight()-value)/2, value, value);
        }



        v.setImageBitmap(finalBitmap);
        v.setAdjustViewBounds(true);
        v.setMaxHeight(c);
        v.setMaxWidth(c);

        chooseImageProfilOnclick(v);

        cd.addView(v);
        fm.addView(cd);
        gridLayout1.addView(fm);
    }

    public void chooseImageProfilOnclick(final ImageView photoIV)
    {

        photoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGalleryImageView(photoIV);
                finish();

            }
        });



    }

    private void saveToGalleryImageView(ImageView picture){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) picture.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream=null;
        java.io.File file = Environment.getExternalStorageDirectory();
        java.io.File dir = new java.io.File(file.getAbsolutePath() + "/ASlurps2");
        dir.mkdirs();

        String filename = "ProfileImage.png";

        java.io.File outFile = new java.io.File(dir, filename);

        if(outFile == null)
            Log.i("TAG1","outfile error");

        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            Log.e("TAG1","outPutStream error");
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        //Toast.makeText(this, "Image saved to internal!", Toast.LENGTH_SHORT).show();
        try{
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveToGalleryBitmap(Bitmap bitmap){

        FileOutputStream outputStream=null;
        java.io.File file = Environment.getExternalStorageDirectory();
        java.io.File dir = new java.io.File(file.getAbsolutePath() + "/ASlurps2");
        dir.mkdirs();

        String filename = "ProfileImage.png";

        java.io.File outFile = new java.io.File(dir, filename);

        if(outFile == null)
            Log.i("TAG1","outfile error");

        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            Log.e("TAG1","outPutStream error");
            //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        //Toast.makeText(this, "Image saved to internal!", Toast.LENGTH_SHORT).show();
        try{
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //-------------------ECRITURE ET LECTURE FICHIER------------------------------------------------

    private void refresh() {
        gridLayout1.removeViews(1,gridLayout1.getChildCount()-1);
        int n = 0;
        while (!getLine(n).equals("empty")) {
            try {
                setImageViewFromPath(getLine(n));
            }catch(Exception e)
            {}

            n++;
        }

    }

    public void write(String mot) throws IOException {
        mot = mot + "\n";
        FileOutputStream out = openFileOutput("historyImage", MODE_PRIVATE);
        out.write(mot.getBytes());
        out.close();


    }

    public String read() throws IOException {
        String value = null;

        FileInputStream inputStream = openFileInput("historyImage");
        StringBuilder stringb = new StringBuilder();
        int content;
        while ((content = inputStream.read()) != -1) {
            value = String.valueOf(stringb.append((char) content));
        }

        return value;
    }

    public String getLine(int n) {
        String text = "";
        try {
            text = read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] textList;

        textList = text.split("\n");

        try {
            return textList[n];
        }
        catch (Exception e) {
            return "empty";
        }

    }

    //-------------------IMAGE---------------------------------------

    // evnt click sur btn
    private void createOnClickPhotoBtn() {
        addCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog(ProfilsGrid.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_dialog,(RelativeLayout)findViewById(R.id.relativeDialog));
                bottomSheetView.findViewById(R.id.PhotoBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAppareilPhoto();
                    }
                });

                bottomSheetView.findViewById(R.id.BibliothequeBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLibrairie();
                    }
                });
            }
        });
    }

    public void openAppareilPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void openLibrairie(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //verifie si une image est récupérée
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            //accès à l'image à partir de data
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // curseur d'accès au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            //position sur la premiere ligne (normalement une seule)
            cursor.moveToFirst();


            //récupération chemin precis de l'image
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            //Toast.makeText(getApplicationContext(), imgPath, Toast.LENGTH_SHORT).show();
            cursor.close();

            //Ecrire path dans fichier
            try {
                write(imgPath+"\n"+read());
            }catch(IOException e){
                e.printStackTrace();
            }

        }
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveToGalleryBitmap(imageBitmap);
        }
        refresh();
    }


}
