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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_or_create);

        mDatabase = FirebaseDatabase.getInstance();


        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        date = f.format(d);

        DatabaseReference ref = mDatabase.getReference().child("parties");
/*
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String dateSnap = snapshot.child("date").getValue(String.class);
                String id = snapshot.child("codePartie").getValue(String.class);

                try {
                    if ( Integer.parseInt(dateSnap.substring(0,4)) <  Integer.parseInt(date.substring(0, 4))){
                        mDatabase.getReference().child("parties").child(id).setValue(null);
                        //delete partie
                    }else{
                        if ( Integer.parseInt(dateSnap.substring(4, 6)) <  Integer.parseInt(date.substring(4, 6))){
                            mDatabase.getReference().child("parties").child(id).setValue(null);
                            //delete partie
                        }else{
                            if ( Integer.parseInt(dateSnap.substring(6, 8)) <  Integer.parseInt(date.substring(6, 8))){
                                if ( Integer.parseInt(date.substring(10, 11)) - Integer.parseInt(dateSnap.substring(9, 11))  > 5 ){
                                    mDatabase.getReference().child("parties").child(id).setValue(null);
                                    //delete partie
                                }
                                mDatabase.getReference().child("parties").child(id).setValue(null);
                                //delete partie
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
        });*/

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
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        date = f.format(d);

        DatabaseReference ref = mDatabase.getReference().child("parties");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String okey = snapshot.child("date").getValue(String.class);


                mDatabase.getReference().child("mamout").setValue(okey);

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
