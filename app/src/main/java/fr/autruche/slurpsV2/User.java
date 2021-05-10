package fr.autruche.slurpsV2;

import android.util.Log;

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
    private String email, pseudo;
    private boolean sexe = false; // 0 homme   1 femme

    Random rand = new Random();

    String avatarNum = String.valueOf(rand.nextInt(8) + 1);

    private String pdp = "image_profil/avatar/avatar" + avatarNum + ".png";



    public User(){
    }

    public User(String pseudo, String email){
        this.email = email;
        this.pseudo = pseudo;
    }


    public String getPdp() {
        return pdp;
    }

    public String getEmail(){
        return email;
    }

    public String getPseudo(){
        return pseudo;
    }

    public boolean getSexe(){
        return sexe;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

    public void setPdp(String pdp) {
        this.pdp = pdp;
    }
}
