package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CreationPartie extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private TextView code1, code2, code3, code4;
    private String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private NumberPicker timePickerHour, timePickerMinute;
    private Button buttonValider;
    private GridLayout gridUser;
    private FrameLayout frameImg;
    private ImageView waitUser;
    private ProgressBar progressBar;
    private int px;
    private int coteImg;
    private int interImg;
    private ConstraintLayout constraintL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_partie);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        timePickerHour = (NumberPicker) findViewById(R.id.timePickerHour);
        timePickerHour.setMinValue(0);
        timePickerHour.setMaxValue(4);

        timePickerMinute = (NumberPicker) findViewById(R.id.timePickerMin);
        timePickerMinute.setMinValue(0);
        timePickerMinute.setMaxValue(59);

        buttonValider = (Button) findViewById(R.id.ButtonValider);
        buttonValider.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        frameImg = findViewById(R.id.frameLayout1);
        gridUser = findViewById(R.id.GridLayout);
        constraintL = findViewById(R.id.consti);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        px = metrics.widthPixels;
        //Toast.makeText(this, Integer.toString(px), Toast.LENGTH_SHORT).show();
        coteImg = px/5;
        interImg = coteImg /4;
        //Toast.makeText(this, Integer.toString(coteImg), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, Integer.toString(interImg), Toast.LENGTH_SHORT).show();


        codePartie();

        waitUserIcon();

        writePartieOnFirebase();

    }
    protected void onDestroy(){
        super.onDestroy();
        try{
            mDatabase.getReference().child("parties").child(Menu.codePartie).child("acces").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue(boolean.class) == true){
                        mDatabase.getReference().child("parties").child(Menu.codePartie).child("listJoueur").child(selfID).setValue(null);
                        Menu.arrayOfJoueur.clear();
                        Menu.arrayOfPseudo.clear();
                        Menu.arrayOfBitmap.clear();
                        finish();
                    }
                    mDatabase.getReference("Defis").child("DefisRoot").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            //Toast.makeText(getApplicationContext(), snapshot.getValue(Defi.class), Toast.LENGTH_SHORT).show();

                            Defi defiRetrieve = snapshot.getValue(Defi.class);
                            defiRetrieve.setKey(Integer.parseInt(snapshot.getKey()));
                            Menu.fullArrayOfDefis.add(defiRetrieve);
                            Menu.arrayOfDefisActif = Menu.fullArrayOfDefis;
                            writeDefisActifOnFireBase();

                            /* Pour un max de défis
                            if(Menu.fullArrayOfDefis.size() < 10*Menu.arrayOfJoueur.size()){
                                Menu.arrayOfDefisActif = Menu.fullArrayOfDefis;
                                writeDefisActifOnFireBase();
                            }else{
                                Random rand = new Random();
                                int indexRand = rand.nextInt(Menu.fullArrayOfDefis.size());
                                for(int i = indexRand; i <= Menu.fullArrayOfDefis.size() ; i++){
                                    Menu.arrayOfDefisActif.add(Menu.fullArrayOfDefis.get(i));
                                }
                                for(int i=0; i < 10*Menu.arrayOfJoueur.size()-indexRand; i++){
                                    Menu.arrayOfDefisActif.add(Menu.fullArrayOfDefis.get(i));
                                }
                            }*/
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.ButtonValider:
                validerPartie();
                break;
        }
    }

    private void writeDefisActifOnFireBase(){
        mDatabase.getReference("parties/"+ Menu.codePartie).child("defisActif").setValue(Menu.arrayOfDefisActif);
    }

    private void validerPartie() {

        Integer TimePickerHour = timePickerHour.getValue();
        Integer TimePickerMinute = timePickerMinute.getValue();

        if(TimePickerMinute != 0 || TimePickerHour != 0 ){
            mDatabase.getReference("parties/"+ Menu.codePartie + "/heurePartie").setValue(TimePickerHour);
            mDatabase.getReference("parties/" + Menu.codePartie + "/minutePartie").setValue(TimePickerMinute);

            //Toast.makeText(CreationPartie.this,"⚠️ Partie lancée et maintenant injoignable !",Toast.LENGTH_LONG).show();
            Intent openJeu = new Intent(getApplicationContext(), Jeu.class);
            startActivity(openJeu);
            finish();
        }else{
            Toast.makeText(CreationPartie.this,"⚠️ Veuillez choisir la durée de la partie !",Toast.LENGTH_LONG).show();
        }
    }

    private void waitUserIcon(){

        FrameLayout fm = new FrameLayout(gridUser.getContext());
        fm.setPadding(interImg,interImg,interImg,interImg);

        //creation Cardview
        CardView cd = new CardView(fm.getContext());
        cd.setRadius(500);

        // chemin image
        ImageView v = new ImageView(cd.getContext());


        v.setImageDrawable(Drawable.createFromPath("@drawable/ic_userwaiting"));
        v.setAdjustViewBounds(true);
        v.setMinimumWidth(coteImg);
        v.setMinimumHeight(coteImg);

        cd.addView(v);
        fm.addView(cd);

        constraintL.addView(fm,0);

    }

    public void codePartie(){
        code1 = (TextView) findViewById(R.id.code1);
        code2 = (TextView) findViewById(R.id.code2);
        code3 = (TextView) findViewById(R.id.code3);
        code4 = (TextView) findViewById(R.id.code4);

        String Code1 = randomChar();
        String Code2 = randomChar();
        String Code3 = randomChar();
        String Code4 = randomChar();

        Menu.codePartie = Code1 + Code2 + Code3 + Code4;

        code1.setText(Code1);
        code2.setText(Code2);
        code3.setText(Code3);
        code4.setText(Code4);

    }

    public String randomChar(){
        Random rand = new Random();
        char c = (char)(rand.nextInt(26) + 65);
        String myStr = Character.toString(c);
        return myStr;
    }

    private void writePartieOnFirebase() {
        Partie partie = new Partie(Menu.codePartie,selfID);
        mDatabase.getReference("parties").child(Menu.codePartie).setValue(partie);
        ajout_de_joueur();
    }

    private void ajout_de_joueur() {
        mDatabase.getReference("parties").child(Menu.codePartie).child("listJoueur").child(selfID).setValue(0);
        mDatabase.getReference("parties").child(Menu.codePartie).child("listJoueur").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Menu.arrayOfJoueur.add(snapshot.getKey());
                retrieveBitmapInArray(snapshot.getKey());
                retrievePseudoInArray(snapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {


                try{

                    int position = Menu.arrayOfJoueur.indexOf(snapshot.getKey());
                   //System.out.println(arrayOfJoueur);
                    Menu.arrayOfBitmap.remove(position);
                    Menu.arrayOfJoueur.remove(position);
                    Menu.arrayOfPseudo.remove(position);
                    //System.out.println(arrayOfJoueur);
                    refreshImageGrid();

                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void retrievePseudoInArray(String userId){
        mDatabase.getReference("Users").child(userId).child("pseudo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Menu.arrayOfPseudo.add(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void retrieveBitmapInArray(String userId){
        mDatabase.getReference("Users").child(userId).child("pdp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference pdpRef = storage.getReference().child(snapshot.getValue(String.class));
                pdpRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        Menu.arrayOfBitmap.add(Bitmap.createScaledBitmap(bitmap, coteImg, coteImg, false));
                        refreshImageGrid();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void refreshImageGrid() {

        try{
            gridUser.removeViews(1, gridUser.getChildCount()-1);
        }catch(Exception e){}


        for (Bitmap bitmap : Menu.arrayOfBitmap){
            //creation FrameLayout
            FrameLayout fm = new FrameLayout(gridUser.getContext());
            fm.setPadding(interImg,interImg,interImg,interImg);

            //creation Cardview
            CardView cd = new CardView(fm.getContext());
            cd.setRadius(500);

            // chemin image
            ImageView v = new ImageView(cd.getContext());


            v.setImageBitmap(bitmap);
            v.setAdjustViewBounds(true);

            v.setMinimumWidth(coteImg);
            v.setMinimumHeight(coteImg);

            cd.addView(v);
            fm.addView(cd);

            gridUser.addView(fm);
        }
    }

}