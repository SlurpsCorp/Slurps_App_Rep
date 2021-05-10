package fr.autruche.slurpsV2;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class User {
    private String email;
    private boolean sexe = true; // 0 homme   1 femme
    private String pseudoDefault;

    // ta mere


    //String psd = retrievePseudo();
    //private String pseudo;

    Random rand = new Random();
    String avatarNum = String.valueOf(rand.nextInt(8) + 1);
    private String pdp = "image_profil/avatar/avatar" + avatarNum + ".png";



    public User(){
    }

    public User(String email){
        this.email = email;
    }


    public String getPdp() {
        return pdp;
    }

    public String getEmail(){
        return email;
    }

    /*public String getPseudo(){
        return pseudo;
    }
*/
    public boolean getSexe(){
        return sexe;
    }
/*
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }*/

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

    public void setPdp(String pdp) {
        this.pdp = pdp;
    }

    public String retrievePseudo(){

        Random rand = new Random();
        String pseudoNum = String.valueOf(rand.nextInt(478) + 1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("pseudoDefault");
        ref.child(pseudoNum).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    //Toast.makeText(Register.this,"❌AIE PAS DE PSEUDO",Toast.LENGTH_LONG).show();
                    pseudoDefault = "Crasseux";
                    //Toast.makeText( User.this,"Pseudo: " + pseudoDefault,Toast.LENGTH_LONG).show();
                }else {
                    pseudoDefault = String.valueOf(task.getResult().getValue());
                    //Toast.makeText(Register.this,"Pseudo: " + pseudoDefault,Toast.LENGTH_LONG).show();
                }
            }
        });
        return pseudoDefault;
    }
}
