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
    private String email, pseudo;
    private int score;
    private String id;
    private Bitmap imgProfil;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    public UserForScore(){
    }

    public UserForScore(String idUser,String idPartie){
        this.id = idUser;
        mDatabase.getReference("Users").child(idUser).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReference("Users").child(idUser).child("pseudo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pseudo = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReference("parties").child(idPartie).child("listJoueur").child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                score = snapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReference("Users").child(idUser).child("pdp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chemin = snapshot.getValue(String.class);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference pdpRef = storage.getReference().child(chemin);
                pdpRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imgProfil = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
