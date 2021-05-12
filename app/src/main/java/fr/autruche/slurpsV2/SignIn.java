package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
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

                    //////////////////redirect play view /////////////////////
                    Intent openPlay = new Intent(getApplicationContext(), JoinOrCreate.class);
                    progressBar.setVisibility(View.GONE);
                    startActivity(openPlay);
                    finish();


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

}