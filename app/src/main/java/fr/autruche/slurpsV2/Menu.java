package fr.autruche.slurpsV2;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {
    private ImageView profilImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        this.profilImage = (ImageView) findViewById(R.id.ProfilImage);
        profilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewProfil = new Intent(getApplicationContext(),Profil.class);
                startActivity(viewProfil);

            }
        });



        displayImageProfil();

    }

    protected void onResume()
    {
        super.onResume();
        displayImageProfil();
    }

    private void displayImageProfil()
    {
        java.io.File file = Environment.getExternalStorageDirectory();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),file.getAbsolutePath() + "/ASlurps2/ProfileImage.png");
        profilImage.setImageDrawable(bitmapDrawable);
    }




}