package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;
    private String selfID;
    private EditText editTextEmail, editTextPassword;
    private Button buttonConnexion;
    private TextView textPasswdForgot;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private SharedPreferences sp;
    private String email, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPasswords);

        buttonConnexion = (Button) findViewById(R.id.buttonConnexion);
        buttonConnexion.setOnClickListener(this);

        textPasswdForgot = (TextView) findViewById(R.id.textPasswdForgot);
        textPasswdForgot.setOnClickListener(this);

        checkBox = findViewById(R.id.checkBox);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        autoConnect();

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonConnexion:
                buttonConnexion();
                break;

            case R.id.textPasswdForgot:
                textPasswdForgot();
                break;
        }
    }


    private void buttonConnexion() {
        email = editTextEmail.getText().toString().trim();
        passwd = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("⚠️ Fournir un email !");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("⚠️ Email invalide!");
            editTextEmail.requestFocus();
            return;
        }

        if(passwd.isEmpty()){
            editTextPassword.setError("⚠️ Fournir mot de passe !");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    selfID = mAuth.getCurrentUser().getUid();
                    //////////////////save preferences file protected//////////

                    sp = getSharedPreferences("emailSaved", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    if(checkBox.isChecked()){
                        editor.putString("email", email);
                        editor.putString("password", passwd);
                        editor.putBoolean("isChecked", true);
                        editor.commit();
                    }else{
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.putBoolean("isChecked", false);
                        editor.commit();
                    }

                    mDatabase.getReference("Users").child(selfID).child("pdp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference pdpRef = storage.getReference().child(snapshot.getValue(String.class));
                            pdpRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                    saveToGalleryBitmap(bitmap);
                                    mDatabase.getReference("Users").child(selfID).child("pseudo").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String pseudoRetrieve = snapshot.getValue(String.class);

                                            SharedPreferences sp = getSharedPreferences("ProfilDico",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("pseudo",pseudoRetrieve);
                                            editor.commit();

                                            Intent openPlay = new Intent(getApplicationContext(), Menu.class);
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(openPlay);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Intent openPlay = new Intent(getApplicationContext(), Menu.class);
                            progressBar.setVisibility(View.GONE);
                            startActivity(openPlay);
                            finish();
                        }
                    });

                }else{
                    Toast.makeText(SignIn.this,"❌ Une erreur est survenue! Veuillez vérifier vos identifients!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void autoConnect() {

        sp = getApplicationContext().getSharedPreferences("emailSaved", Context.MODE_PRIVATE);
        editTextEmail.setText(sp.getString("email",""));
        editTextPassword.setText(sp.getString("password", ""));
        if(sp.getBoolean("isChecked", true)){
            checkBox.setChecked(true);
        }
    }

    private void textPasswdForgot() {

        Intent openPasswdForgot = new Intent(getApplicationContext(), PasswdForgot.class);
        startActivity(openPasswdForgot);

    }

    private void saveToGalleryBitmap(Bitmap bitmap){

        FileOutputStream outputStream=null;
        java.io.File file = Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath() + "/ASlurps2";
        java.io.File dir = new java.io.File(path);
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

        try {
            write(path+"\n"+read());
        }catch(IOException e){
            e.printStackTrace();
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

}