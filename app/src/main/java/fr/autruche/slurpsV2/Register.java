package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private EditText editTextEmail, editTextPassword, editTextPasswords2;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private String pseudoDefault;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPasswords);
        editTextPasswords2 = (EditText) findViewById(R.id.editTextPasswords2);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


    }

    @Override
    public void onClick(View v) {
        buttonRegister();

    }

    private void buttonRegister() {

        String email = editTextEmail.getText().toString().trim();
        String passwd = editTextPassword.getText().toString().trim();
        String passwd2 = editTextPasswords2.getText().toString().trim();
        retrievePseudo();

        if(email.isEmpty()){
            editTextEmail.setError("⚠️ Email obligatoire!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("⚠️ Email invalide!");
            editTextEmail.requestFocus();
            return;
        }

        if(passwd.isEmpty()){
            editTextPassword.setError("⚠️ Mot de passe obligatoire!");
            editTextPassword.requestFocus();
            return;
        }

        if(passwd.length() < 6){
            Toast.makeText(Register.this,"⚠️ 6 caractères minimum pour le mot de passe!",Toast.LENGTH_LONG).show();
            editTextPassword.requestFocus();
            return;
        }
        if(!passwd2.equals(passwd)){
            Toast.makeText(Register.this,"⚠️ Les mots de passe doivent être identiques!",Toast.LENGTH_LONG).show();
            editTextPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    sp = getSharedPreferences("emailSaved", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("email", email);
                    editor.putString("password", passwd);
                    editor.putBoolean("isChecked", true);
                    editor.commit();

                    User user = new User(pseudoDefault, email);
                    mDatabase.getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                //////////////////redirect play view /////////////////////
                                Intent signIn = new Intent(getApplicationContext(), SignIn.class);
                                progressBar.setVisibility(View.GONE);
                                startActivity(signIn);
                                finish();


                            }else{
                                Toast.makeText(Register.this,"❌ Une erreur est survenue! Veuillez réessayer!ecrire database",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }else{
                    Toast.makeText(Register.this,"❌ Une erreur est survenue! Veuillez réessayer!register",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    public void retrievePseudo(){

        Random rand = new Random();
        String pseudoNum = String.valueOf(rand.nextInt(478) + 1);

        DatabaseReference ref = mDatabase.getReference().child("pseudoDefault");
        ref.child(pseudoNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    pseudoDefault = "Crasseux";
                    //Toast.makeText(Register.this,"Pseudo: " + pseudoDefault,Toast.LENGTH_LONG).show();
                }else {
                    pseudoDefault = String.valueOf(task.getResult().getValue());
                    Toast.makeText(Register.this,"Ton pseudo: " + pseudoDefault,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}