package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswdForgot extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private Button buttonSendMail;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwd_forgot);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        buttonSendMail = (Button) findViewById(R.id.buttonSendMail);
        buttonSendMail.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        resetPasswd();
    }

    private void resetPasswd() {
        String email = editTextEmail.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(PasswdForgot.this,"📩 Un email vous a été envoyé! Réinitialiser votre mot de passe!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    Intent openSignIn = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(openSignIn);
                    finish();
                }else{
                    Toast.makeText(PasswdForgot.this,"❌ Une erreur est survenue! Veuillez réessayer!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
    }
}