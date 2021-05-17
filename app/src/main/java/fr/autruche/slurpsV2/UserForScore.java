/*package fr.autruche.slurpsV2;

public class UserForScore {

    private int pdp;
    private String pseudo;
    private int points;

    public UserForScore(int s) {
        pdp = R.drawable.fatima;
        this.pseudo = "aLEX";
        this.points = s;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPdp() {
        return pdp;
    }



    public int getPoints() {
        return points;
    }
}

*/
package fr.autruche.slurpsV2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserForScore {
    private String pseudo;
    private int score;
    private String id;
    private Bitmap imgProfil;



    public UserForScore(){
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getScore() {
        return score;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImgProfil() {
        return imgProfil;
    }

    public UserForScore(String id, String pseudo, Bitmap imgProfil, int score) {
        this.pseudo = pseudo;
        this.score = score;
        this.id = id;
        this.imgProfil = imgProfil;
    }
}

