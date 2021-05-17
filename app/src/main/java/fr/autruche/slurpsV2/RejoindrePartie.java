package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RejoindrePartie extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;

    private EditText code1, code2, code3, code4;
    private EditText[] codes;
    private String createurID;
    private String selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Button buttonAcceder;
    private GridLayout gridUser;
    private ImageView waitUser;
    private FrameLayout frameImg;
    private ProgressBar progressBar;
    private CardView cardViewButton;
    private int px;
    private int coteImg;
    private int interImg;
    private ConstraintLayout constraintL;
    private boolean acces;
    private boolean onPartie = false;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

        mDatabase = FirebaseDatabase.getInstance();

        code1 = findViewById(R.id.codo1);
        code2 = findViewById(R.id.codo2);
        code3 = findViewById(R.id.codo3);
        code4 = findViewById(R.id.codo4);

        buttonAcceder = findViewById(R.id.ButtonAcceder);
        buttonAcceder.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        cardViewButton = findViewById(R.id.cardView);
        frameImg = findViewById(R.id.frameLayout2);
        gridUser = findViewById(R.id.GridLayout);
        gridUser.setVisibility(View.INVISIBLE);
        constraintL = findViewById(R.id.consti);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        px = metrics.widthPixels;
        coteImg = px/5;
        interImg = coteImg /4;

        codePartie();
        waitUserIcon();

    }

    protected void onDestroy(){
        super.onDestroy();
        try{
            mDatabase.getReference().child("parties").child(Menu.codePartie).child("listJoueur").child(selfID).setValue(null);
        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View v) {
        accederPartie();
    }

    private void accederPartie() {
        Menu.codePartie = code1.getText().toString().trim() + code2.getText().toString().trim() + code3.getText().toString().trim() + code4.getText().toString().trim();


        mDatabase.getReference().child("parties").child(Menu.codePartie).child("acces").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                acces = snapshot.getValue(boolean.class);
                if(acces == true || acces == false){
                    if (acces == true && onPartie == false){
                        ajout_de_joueur();
                        gridUser.setVisibility(View.VISIBLE);
                        buttonAcceder.setVisibility(View.GONE);
                        cardViewButton.setVisibility(View.GONE);
                        onPartie = true;
                    }
                    if (acces == false && onPartie == false){
                        Toast.makeText(RejoindrePartie.this,"😢 💩La partie a commencé sans vous! ",Toast.LENGTH_LONG).show();
                    }
                    if (acces == false && onPartie == true){
                        Intent openJeu = new Intent(getApplicationContext(), Jeu.class);
                        startActivity(openJeu);
                        finish();
                    }
                }else{
                    Toast.makeText(RejoindrePartie.this, "Cette partie n'existe pas !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void codePartie(){
        codes = new EditText[]{code1, code2, code3, code4};

        code1.addTextChangedListener(new PinTextWatcher(0));
        code2.addTextChangedListener(new PinTextWatcher(1));
        code3.addTextChangedListener(new PinTextWatcher(2));
        code4.addTextChangedListener(new PinTextWatcher(3));

        code1.setOnKeyListener(new PinOnKeyListener(0));
        code2.setOnKeyListener(new PinOnKeyListener(1));
        code3.setOnKeyListener(new PinOnKeyListener(2));
        code4.setOnKeyListener(new PinOnKeyListener(3));
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


    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == codes.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            codes[currentIndex].removeTextChangedListener(this);
            codes[currentIndex].setText(text);
            codes[currentIndex].setSelection(text.length());
            codes[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                codes[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                codes[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                codes[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : codes)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (codes[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    codes[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }
}