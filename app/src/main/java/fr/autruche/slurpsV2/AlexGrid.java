package fr.autruche.slurpsV2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AlexGrid extends AppCompatActivity {

    private GridLayout gridLayout1;
    private ImageView addCircle;
    private int c;
    private ImageView testChoix;
    //private TextView debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alex_grid);



       // gridLayout1 = findViewById(R.id.GridLayout1);
        FrameLayout flCercle = findViewById(R.id.flCercle);
        flCercle.setPadding(60, 60, 60, 60);

        //testChoix = findViewById(R.id.testImageChoice);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int px = metrics.widthPixels;
        c = (px / 3);




        //addCircle = findViewById(R.id.addCircle);
        addCircle.setAdjustViewBounds(true);
        addCircle.setMaxWidth(c);
        addCircle.setMaxHeight(c);


        //debug = findViewById(R.id.DEBUG);

        /*
        try {
        write("");
        }
        catch (Exception e){
            e.printStackTrace();
        }


         */


        refresh();
        createOnClickPhotoBtn();
    }

    public void setImageView(int i) {

        //creation FrameLayout
        FrameLayout fm = new FrameLayout(gridLayout1.getContext());
        int p = 60;
        fm.setPadding(p, p, p, p);
        //FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) fm.getLayoutParams();
        //params.setMargins(15,15,15,15);
        //fm.setLayoutParams(params);

        //creation Cardview
        CardView cd = new CardView(fm.getContext());
        cd.setRadius(500);
        //cd.setPadding(15,15,15,15);

        // chemin image
        ImageView v = new ImageView(cd.getContext());
        v.setImageResource(i);
        v.setAdjustViewBounds(true);
        v.setMaxHeight(c);
        v.setMaxWidth(c);

        //v.setPadding(15,15,15,15);

        cd.addView(v);
        fm.addView(cd);
        gridLayout1.addView(fm);
    }


    public void setImageViewFromPath(String path) {
        //creation FrameLayout
        FrameLayout fm = new FrameLayout(gridLayout1.getContext());
        int p = 60;
        fm.setPadding(p, p, p, p);
        //FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) fm.getLayoutParams();
        //params.setMargins(15,15,15,15);
        //fm.setLayoutParams(params);

        //creation Cardview
        CardView cd = new CardView(fm.getContext());
        cd.setRadius(500);
        //cd.setPadding(15,15,15,15);

        // chemin image
        ImageView v = new ImageView(cd.getContext());
        //v.setImageResource(Integer.parseInt(path));

        Bitmap bitmap = BitmapFactory.decodeFile(path);


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

        //v.setPadding(15,15,15,15);
        cd.addView(v);
        fm.addView(cd);
        gridLayout1.addView(fm);
    }

    public void chooseImageProfilOnclick(ImageView photoIV)
    {

        photoIV.setOnClickListener(v -> {
            testChoix.setImageDrawable(photoIV.getDrawable());
        });



    }
    //-------------------ECRITURE ET LECTURE FICHIER------------------------------------------------

    private void refresh() {
        gridLayout1.removeViews(1,gridLayout1.getChildCount()-1);
        int n = 0;
        while (!getLine(n).equals("empty")) {
            setImageViewFromPath(getLine(n));
            n++;
        }

        /*
        try {
            debug.setText("text: "+read());
        }
        catch (IOException e)
        {
            debug.setText("ERRROR: "+ e.toString());
            e.printStackTrace();
        }

         */
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

        String[] textList = text.split("\n");
        try {
            return textList[n];
        } catch (Exception e) {
            return "empty";
        }

    }

    //-------------------IMAGE---------------------------------------

    // evnt click sur btn
    private void createOnClickPhotoBtn() {
        addCircle.setOnClickListener(v -> {
            //acces a la galerie du telehphone
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);


        });
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

            /*
            //récupération image

            Bitmap image = BitmapFactory.decodeFile(imgPath);
            //afficher
            imgPhoto.setImageBitmap(image);

             */
        }
        /*
        else
        {
            Toast.makeText(this,"Aucune image selectionné", Toast.LENGTH_LONG).show();
        }

         */
        refresh();
    }
}