package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinOrCreate extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;
    private TextView playTextView, creerTextView;
    private ImageView profilImage;
    private String date;
    Date calendar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_or_create);

        mDatabase = FirebaseDatabase.getInstance();

        deleteOldPartie();

        playTextView = (TextView) findViewById(R.id.RejoindreTextView);
        playTextView.setOnClickListener(this);
        playTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    playTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){

                    playTextView.setTextColor(Color.parseColor("#000000"));
                    rejoindrePartie();
                    return true;
                }
                return false;
            }
        });

        creerTextView = (TextView) findViewById(R.id.CreerTextView);
        creerTextView.setOnClickListener(this);
        creerTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    creerTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){

                    creerTextView.setTextColor(Color.parseColor("#000000"));
                    creerPartie();
                    return true;
                }
                return false;
            }
        });

        profilImage = (ImageView) findViewById(R.id.ProfilImage);
        profilImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.ProfilImage:
                profilImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
//               ouvrirProfil();
                break;
        }

    }
    private void creerPartie() {

        Intent creerPartie = new Intent(getApplicationContext(), CreationPartie.class);
        startActivity(creerPartie);
    }

    private void rejoindrePartie() {

        Intent rejoindrePartie = new Intent(getApplicationContext(), RejoindrePartie.class);
        startActivity(rejoindrePartie);

    }

//
//    private void ouvrirProfil(){
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
//                JoinOrCreate.this, R.style.BottomSheetDialogTheme
//        );
//        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_creation_partie);
//        bottomSheetDialog.show();
//    }

    public boolean deleteOldPartie(){

        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        date = f.format(d);

        int annee_actu = Integer.parseInt(date.substring(0,4));
        int mois_actu = Integer.parseInt(date.substring(4, 6));
        int jour_actu = Integer.parseInt(date.substring(6, 8));
        int heure_actu = Integer.parseInt(date.substring(8, 10));


        DatabaseReference ref = mDatabase.getReference().child("parties");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String date_creation = snapshot.child("date").getValue(String.class);
                String id = snapshot.child("codePartie").getValue(String.class);

                int annee_crea = Integer.parseInt(date_creation.substring(0,4));
                int mois_crea = Integer.parseInt(date_creation.substring(4, 6));
                int jour_crea = Integer.parseInt(date_creation.substring(6, 8));
                int heure_crea = Integer.parseInt(date_creation.substring(8, 10));
                int min_crea = Integer.parseInt(date_creation.substring(10, 12));
                int sec_crea = Integer.parseInt(date_creation.substring(12, 14));
                try {
                    if (annee_crea != annee_actu){
                        mDatabase.getReference().child("parties").child(id).setValue(null);
                    }else{
                        if (mois_crea != mois_actu){
                            mDatabase.getReference().child("parties").child(id).setValue(null);
                        }else{
                            if ( jour_actu - jour_crea > 1 ){
                                mDatabase.getReference().child("parties").child(id).setValue(null);

                            }else{
                                if((jour_actu - jour_crea == 1) && ( heure_crea < heure_actu )) {
                                    mDatabase.getReference().child("parties").child(id).setValue(null);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }
}
