package fr.autruche.slurpsV2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreationPartie extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mDatabase;

    private TextView code1, code2, code3, code4;
    private String codePartie,createurID ;
    private NumberPicker timePickerHour, timePickerMinute;
    private Button buttonValider;
    private GridLayout gridUser;
    private ImageView waitUser;
    private ProgressBar progressBar;
    private int cote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_partie);

        mDatabase = FirebaseDatabase.getInstance();

        timePickerHour = (NumberPicker) findViewById(R.id.timePickerHour);
        timePickerHour.setMinValue(0);
        timePickerHour.setMaxValue(10);

        timePickerMinute = (NumberPicker) findViewById(R.id.timePickerMin);
        timePickerMinute.setMinValue(0);
        timePickerMinute.setMaxValue(59);

        buttonValider = (Button) findViewById(R.id.ButtonValider);
        buttonValider.setOnClickListener(this);



        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);


        codePartie();

        waitUserIcon();

        writePartieOnFirebase();



//        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)){
//            FirebaseDatabase.getInstance().getReference("parties").child(codePartie).removeValue();
//        }
//        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.DESTROYED)){
//            FirebaseDatabase.getInstance().getReference("parties").child(codePartie).removeValue();
//        }
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
            FirebaseDatabase.getInstance().getReference("parties/" + codePartie + "/accessible").setValue(false);
            Toast.makeText(CreationPartie.this,"⚠️ Partie lancée et injoignable !",Toast.LENGTH_LONG).show();
            return;
        }else{
            Toast.makeText(CreationPartie.this,"⚠️ Veuillez choisir la durée de la partie !",Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void writePartieOnFirebase() {
        createurID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Partie partie = new Partie(codePartie,createurID);
        mDatabase.getReference("parties/" + codePartie).setValue(partie);
    }

    public void setImageView() {

        //creation FrameLayout
        FrameLayout fm = new FrameLayout(gridUser.getContext());
        int p = 40;
        fm.setPadding(p, p, p, p);
        //FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) fm.getLayoutParams();
        //params.setMargins(15,15,15,15);
        //fm.setLayoutParams(params);

        //creation Cardview
        CardView cd = new CardView(fm.getContext());
        cd.setRadius(500);
        //cd.setPadding(15,15,15,15);

        // chemin image
        ImageView v = new ImageView(cd.getContext());
        //v.setImageResource(getResources().getDrawable());
        v.setAdjustViewBounds(true);
        v.setMaxHeight(cote);
        v.setMaxWidth(cote);

        //v.setPadding(15,15,15,15);
        cd.addView(v);
        fm.addView(cd);
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
}