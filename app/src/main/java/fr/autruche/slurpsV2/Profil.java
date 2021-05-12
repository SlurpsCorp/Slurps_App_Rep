package fr.autruche.slurpsV2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Profil extends AppCompatActivity {

    private ImageView photoProfil;
    private EditText pseudoProfil;
    private Button SaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        this.pseudoProfil = (EditText) findViewById(R.id.pseudoEditText);
        this.photoProfil = (ImageView) findViewById(R.id.photoProfil);
        this.SaveBtn = (Button) findViewById(R.id.SaveBtn);


        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String newPseudo = (String) pseudoProfil.getText().toString().trim();

                SharedPreferences sp = getSharedPreferences("ProfilDico",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pseudo",newPseudo);
                editor.commit();
                finish();
            }
        });

        photoProfil.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent viewProfilGrid = new Intent(getApplicationContext(), ProfilsGrid.class);
                startActivity(viewProfilGrid);

            }
        });

        refresh();
    }

    protected void onResume()
    {
        super.onResume();
        refresh();
    }

    private void displayImageProfil()
    {
        java.io.File file = Environment.getExternalStorageDirectory();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),file.getAbsolutePath() + "/ASlurps2/ProfileImage.png");
        photoProfil.setImageDrawable(bitmapDrawable);
    }

    private void displayPseudoProfil()
    {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("ProfilDico", Context.MODE_PRIVATE);
        pseudoProfil.setText(sp.getString("pseudo",""));
    }

    private void refresh(){
        displayImageProfil();
        displayPseudoProfil();
    }


}