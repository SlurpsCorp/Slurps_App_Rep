package fr.autruche.slurpsV2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Menu extends AppCompatActivity implements View.OnClickListener {
    private ImageView profilImage;
    private TextView playTextView, creerTextView;
    public static ArrayList<String> arrayOfPseudo  = new ArrayList();
    public static ArrayList<String> arrayOfJoueur = new ArrayList();
    public static ArrayList<Bitmap> arrayOfBitmap  = new ArrayList();
    public static String codePartie;

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


        playTextView = (TextView) findViewById(R.id.RejoindreTextView);
        playTextView.setOnClickListener((View.OnClickListener) this);
        playTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    playTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){

                    playTextView.setTextColor(Color.parseColor("#000000"));
                    rejoindrePartie();
                    return true;
                }
                return false;
            }
        });

        creerTextView = (TextView) findViewById(R.id.CreerTextView);
        creerTextView.setOnClickListener((View.OnClickListener) this);

        creerTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    creerTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){

                    creerTextView.setTextColor(Color.parseColor("#000000"));
                    creerPartie();
                    return true;
                }
                return false;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.ProfilImage:
                profilImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
//               ouvrirProfil();
                break;
        }

    }
    private void creerPartie() {

        Intent creerPartie = new Intent(getApplicationContext(), CreationPartie.class);
        startActivity(creerPartie);
    }

    private void rejoindrePartie() {

        Intent rejoindrePartie = new Intent(getApplicationContext(), RejoindrePartie.class);
        startActivity(rejoindrePartie);

    }




}