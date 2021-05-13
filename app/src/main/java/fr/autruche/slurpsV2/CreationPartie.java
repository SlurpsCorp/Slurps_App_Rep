package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private ArrayList<String> arrayOfJoueur = new ArrayList();
    private ArrayList<Bitmap> arrayOfBitmap  = new ArrayList();
    private TextView code1, code2, code3, code4;
    private String codePartie;
    private String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private NumberPicker timePickerHour, timePickerMinute;
    private Button buttonValider;
    private GridLayout gridUser;
    private FrameLayout frameImg;
    private ImageView waitUser;
    private ProgressBar progressBar;
    private int cote;
    private int increment = 0;



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

        frameImg = findViewById(R.id.frameLayout2);
        gridUser = findViewById(R.id.GridLayout);
        waitUser = findViewById(R.id.waitUser);

        codePartie();

        waitUserIcon();

        writePartieOnFirebase();

       // getUserPlaying();

    }
    protected void onStop(){
        super.onStop();
        try{
            mDatabase.getReference().child("parties").child(codePartie).setValue(null);
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

    private void validerPartie() {

        Integer TimePickerHour = timePickerHour.getValue();
        Integer TimePickerMinute = timePickerMinute.getValue();

        if(TimePickerMinute != 0 || TimePickerHour != 0 ){
            FirebaseDatabase.getInstance().getReference("parties/"+ codePartie + "/heurePartie").setValue(TimePickerHour);
            FirebaseDatabase.getInstance().getReference("parties/" + codePartie + "/minutePartie").setValue(TimePickerMinute);
            FirebaseDatabase.getInstance().getReference("parties/" + codePartie + "/acces").setValue(false);
            Toast.makeText(CreationPartie.this,"⚠️ Partie lancée et injoignable !",Toast.LENGTH_LONG).show();
            return;
        }else{
            Toast.makeText(CreationPartie.this,"⚠️ Veuillez choisir la durée de la partie !",Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void waitUserIcon() {


        frameImg.setPadding(60, 60, 60, 60);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int px = metrics.widthPixels;
        cote = px / 8;

        waitUser.setAdjustViewBounds(true);
        waitUser.setMaxHeight(cote);
        waitUser.setMaxWidth(cote);

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

        codePartie = Code1 + Code2 + Code3 + Code4;

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
        Partie partie = new Partie(codePartie,selfID);
        mDatabase.getReference("parties").child(codePartie).setValue(partie);
        ajout_de_joueur();
    }

    private void ajout_de_joueur() {
        mDatabase.getReference("parties").child(codePartie).child("listJoueur").child(selfID).setValue(0);
        mDatabase.getReference("parties").child(codePartie).child("listJoueur").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                arrayOfJoueur.add(snapshot.getKey());
                retrieveBitmapInArray(snapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{

                    int position = arrayOfJoueur.indexOf(snapshot.getKey());
                    arrayOfBitmap.remove(position);
                    arrayOfJoueur.remove(position);
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
                        arrayOfBitmap.add(bitmap);
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

        int cote2;

        gridUser.removeViews(1, arrayOfBitmap.size()-1);

        for (Bitmap bitmap : arrayOfBitmap){
            //creation FrameLayout
            FrameLayout fm = new FrameLayout(gridUser.getContext());
            int p = 10;
            fm.setPadding(p, p, p, p);

            //creation Cardview
            CardView cd = new CardView(fm.getContext());
            cd.setRadius(500);

            // chemin image
            ImageView v = new ImageView(cd.getContext());

            //rognage bitmap
            int value = 0;
            Bitmap finalBitmap=null;
            try{
                if (bitmap.getHeight() <= bitmap.getWidth()) {
                    value = bitmap.getHeight();
                    finalBitmap = Bitmap.createBitmap(bitmap,(bitmap.getWidth()-value)/2 , 0, value, value);
                } else {
                    value = bitmap.getWidth();
                    finalBitmap = Bitmap.createBitmap(bitmap,0 , (bitmap.getHeight()-value)/2, value, value);
                }
            }catch (Exception e){}


            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int px = metrics.widthPixels;
            cote2 = px / 8;

            v.setImageBitmap(finalBitmap);
            v.setAdjustViewBounds(true);
            v.setMaxHeight(cote2);
            v.setMaxWidth(cote2);
            //v.setForegroundGravity(Gravity.CENTER_VERTICAL);

            cd.addView(v);
            fm.addView(cd);
            gridUser.addView(fm);
        }
    }

}