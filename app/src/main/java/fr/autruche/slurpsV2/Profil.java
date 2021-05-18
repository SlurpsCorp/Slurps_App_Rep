package fr.autruche.slurpsV2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;

public class Profil extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private ImageView photoProfil;
    private EditText pseudoProfil;
    private Button SaveBtn;
    private BitmapDrawable bitmapDrawable;
    private String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        this.pseudoProfil = (EditText) findViewById(R.id.pseudoEditText);
        this.photoProfil = (ImageView) findViewById(R.id.photoProfil);
        this.SaveBtn = (Button) findViewById(R.id.SaveBtn);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("emailSaved", Context.MODE_PRIVATE);

        pseudoProfil.setText(sp.getString("pseudo",""));


        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String newPseudo = (String) pseudoProfil.getText().toString().trim();

                SharedPreferences sp = getSharedPreferences("ProfilDico",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pseudo",newPseudo);
                editor.commit();
                mDatabase.getReference().child("Users").child(selfID).child("pseudo").setValue(newPseudo);
                uploadImageToFirebase();

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

    private void uploadImageToFirebase(){
        java.io.File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath() + "/ASlurps2/ProfileImage.png";
        Uri uri = Uri.fromFile(new File(path));
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

        mStorage.getReference().child("image_profil/" + selfID + ".png").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Profil.this, "PsahhhrTek", Toast.LENGTH_SHORT).show();
                mDatabase.getReference().child("Users").child(selfID).child("pdp").setValue("image_profil/"+ selfID + ".png");
                finish();
            }
        });
    }

    protected void onResume()
    {
        super.onResume();
        refresh();
    }

    private void displayImageProfil()
    {
        java.io.File file = Environment.getExternalStorageDirectory();
        bitmapDrawable = new BitmapDrawable(getResources(),file.getAbsolutePath() + "/ASlurps2/ProfileImage.png");
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