package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RejoindrePartie extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;

    private EditText code1, code2, code3, code4;
    private EditText[] codes;
    private String codePartie,joueurID ;
    private Button buttonAcceder;
    private GridLayout gridUser;
    private ImageView waitUser;
    private ProgressBar progressBar;
    private int cote;
    private boolean accessible;

    private TextView textaaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_partie);

        textaaa = findViewById(R.id.textaaa);

        mDatabase = FirebaseDatabase.getInstance();
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);

        buttonAcceder = findViewById(R.id.ButtonAcceder);
        buttonAcceder.setOnClickListener(this);

        codePartie();

        waitUserIcon();
    }

    @Override
    public void onClick(View v) {
        accederPartie();
    }




    private void accederPartie() {
        ArrayList<String> joueurIDlist = new ArrayList<>();
        joueurID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        codePartie = code1.getText().toString().trim() + code2.getText().toString().trim() + code3.getText().toString().trim() + code4.getText().toString().trim();


        if (isAccessible(codePartie) == true){

            joueurIDlist.add(1, joueurID);
            mDatabase.getReference("parties/" + codePartie + "joueurIDList").setValue(joueurIDlist);
            mDatabase.getReference("parties/" + codePartie).child("joueurIDList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        joueurIDlist.add(snapshot.getValue().toString());
                        textaaa.setText(textaaa + "\n" + snapshot.getValue().toString());

                        // trouver chemain photo avec user et afficher pdp storage
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Log.w("firebase", "loadPost:onCancelled", DatabaseError.toException());
                }
            });

        }else{
            Toast.makeText(RejoindrePartie.this,"😢 La partie a commencé sans vous! ",Toast.LENGTH_LONG).show();
        }
    }

    public void codePartie(){
        code1 =  findViewById(R.id.code1);
        code2 =  findViewById(R.id.code2);
        code3 =  findViewById(R.id.code3);
        code4 =  findViewById(R.id.code4);
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

    private void waitUserIcon() {
        gridUser = (GridLayout) findViewById(R.id.GridLayout);

        waitUser = (ImageView)  findViewById(R.id.waitUser);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int px = metrics.widthPixels;

        cote = px / 5;
        waitUser.setAdjustViewBounds(true);
        waitUser.setMaxHeight(cote);
        waitUser.setMaxWidth(cote);
    }

    private boolean isAccessible(String code){

        mDatabase.getReference().child("parties").child(code).child("isAccessible").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    accessible = false;
                    Toast.makeText(RejoindrePartie.this,"❌ Partie introuvable! ",Toast.LENGTH_LONG).show();
                }else {
                   if (task.getResult().getValue() == "true"){
                       accessible = true;
                   }else{accessible = false;}
                }
            }
        });
        return accessible;
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