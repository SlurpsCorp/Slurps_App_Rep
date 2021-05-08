package fr.autruche.slurpsV2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class Accueil extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewInscription, textViewConnexion;
    static Accueil AccueilActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccueilActivity = this;

        setContentView(R.layout.activity_accueil);
        textViewInscription = (TextView) findViewById(R.id.textRegisterAccueil);
        textViewInscription.setOnClickListener(this);

        textViewConnexion= (TextView) findViewById(R.id.textConnexionAccueil);
        textViewConnexion.setOnClickListener(this);

        ActivityCompat.requestPermissions(Accueil.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(Accueil.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(Accueil.this,new String[]{"Manifest.permission.CAMERA2"},1);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textConnexionAccueil:
                Intent openConnexion = new Intent(getApplicationContext(), SignIn.class); //signin.class
                startActivity(openConnexion);
                break;

            case R.id.textRegisterAccueil:
                Intent openRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(openRegister);
                break;
        }

    }



}