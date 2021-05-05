package fr.autruche.slurpsV2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class JoinOrCreate extends AppCompatActivity implements View.OnClickListener {

    private TextView playTextView, creerTextView;
    private ImageView profilImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_or_create);

        playTextView = (TextView) findViewById(R.id.RejoindreTextView);
        playTextView.setOnClickListener(this);
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
        creerTextView.setOnClickListener(this);
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

        profilImage = (ImageView) findViewById(R.id.ProfilImage);
        profilImage.setOnClickListener(this);
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

//
//    private void ouvrirProfil(){
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
//                JoinOrCreate.this, R.style.BottomSheetDialogTheme
//        );
//        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_creation_partie);
//        bottomSheetDialog.show();
//    }
}