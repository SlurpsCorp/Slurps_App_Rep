package fr.autruche.slurpsV2;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.PKIXRevocationChecker;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo extends AppCompatActivity  implements View.OnClickListener{

    private ImageView picture;
    private Button button_take, button_choose;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private String currentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        picture = (ImageView) findViewById(R.id.picture);

        button_choose = (Button) findViewById(R.id.button_choose);
        button_choose.setOnClickListener(this);

        button_take = (Button) findViewById(R.id.button_take);
        button_take.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.button_take:
                buttonTake();
                break;

            case R.id.button_choose:
                buttonChoose();
                break;
        }
    }

    private void buttonChoose() {

        // code alex
        
    }

    private void buttonTake(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            picture.setImageBitmap(imageBitmap);

            saveToGallery();
        }
    }

    private void saveToGallery(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) picture.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/ASlurps");
        dir.mkdirs();

        String filename = String.format("%d.png",System.currentTimeMillis());
        File outFile = new File(dir,filename);
        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        Toast.makeText(this, "Image saved to internal!", Toast.LENGTH_SHORT).show();
        try{
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}