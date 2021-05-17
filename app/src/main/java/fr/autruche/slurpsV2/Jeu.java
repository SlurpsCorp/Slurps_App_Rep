package fr.autruche.slurpsV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import static android.os.Build.VERSION_CODES.O;
import static android.view.ViewGroup.*;
import static fr.autruche.slurpsV2.R.font.roboto_condensed_bold;

public class Jeu extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    private String selfID;

    LinearLayout linear1, linear2;
    TextView description, timer;
    static Button valider;
    ImageView reload;
    private boolean defiDisplayed;
    ScrollView scroll;

    CardView i12,i32;
    LinearLayout i22, i52;
    ImageView i42;
    TextView i62,i72,i82;

    Intent timerDefi;

    Defi defiEnCours;

    public static long TIME;
    private String TAG = "TIMER-TAG";

    private ArrayList<UserForScore> joueursListe = new ArrayList<UserForScore>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        //initialisation des composants du layout
        init();
        initItemsForParams();

        //initialisation des onClickListeners
        setOnClickListenerReload();
        setOnClickListenerButton();

        //verif
        timerDefi  = new Intent(Jeu.this, BroadcastService.class);
        if(timerDefi != null)
            Log.i("LOOOOOOOOOOG","INITIALISATIOOOOOOOOOOOOOOOOOOOOON)");

        FirebaseDatabase.getInstance().getReference("parties/" + Menu.codePartie + "/acces").setValue(false);
        mDatabase.getReference("parties").child(Menu.codePartie).child("listJoueur").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idFound = snapshot.getKey();
                int index = Menu.arrayOfJoueur.indexOf(idFound);
                UserForScore userforscore = new UserForScore(idFound, Menu.arrayOfPseudo.get(index), Menu.arrayOfBitmap.get(index), snapshot.getValue(Integer.class));
                joueursListe.add(userforscore);
                refresh();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idChanged = snapshot.getKey();
                for(UserForScore u : joueursListe){
                    if(u.getId().equals(idChanged)){
                        u.setScore(snapshot.getValue(Integer.class));
                    }
                }
                refresh();
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

        Menu.arrayOfDefisActif.clear();
        mDatabase.getReference("parties").child(Menu.codePartie).child("defisActif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Defi defiRetrieve = snapshot.getValue(Defi.class);
                defiRetrieve.setKey(Integer.parseInt(snapshot.getKey()));
                Menu.arrayOfDefisActif.add(defiRetrieve);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Defi defi = snapshot.getValue(Defi.class);
                System.out.println("key: " + defi.getKey());

                for(Defi d :Menu.arrayOfDefisActif){
                    System.out.println("key: " + d.getKey()+ "    description: " + d.getDescription());}

                Iterator<Defi> itr = Menu.arrayOfDefisActif.iterator();
                while(itr.hasNext()){
                    int key = itr.next().getKey();
                    if (key == defi.getKey()){
                        itr.remove();
                        if(Menu.arrayOfDefisActif.size() == 0){
                            setPartieFinie();
                        }
                    }
                }
                for(Defi d :Menu.arrayOfDefisActif){
                    System.out.println("key: " + d.getKey()+ "    description: " + d.getDescription());}

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setPasDeDefi();
        //refresh affichage
        refresh();

    }

    private void init()
    {
        mDatabase = FirebaseDatabase.getInstance();
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        description = findViewById(R.id.description);
        timer = findViewById(R.id.timer);
        valider = findViewById(R.id.valider);
        reload = findViewById(R.id.reload);
        scroll= findViewById(R.id.scroll);
        selfID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        defiDisplayed = false;


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(TAG,"Registered broadcast receiver");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"Unregistered broadcast receiver");
    }

    @Override
    protected void onStop()
    {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already
        }
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        stopService(new Intent(this,BroadcastService.class));
        Log.i(TAG,"Stopped service");
        try{
            //mDatabase.getReference().child("parties").child(Menu.codePartie).child("listJoueur").child(selfID).setValue(null);
            finish();
        }catch (Exception e){
        }

        super.onDestroy();
    }

    private void initItemsForParams()
    {
        i12 = findViewById(R.id.i1);
        i22 = findViewById(R.id.i2);
        i32 = findViewById(R.id.i3);
        i42 = findViewById(R.id.i4);
        i52 = findViewById(R.id.i5);
        i62 = findViewById(R.id.i6);
        i72 = findViewById(R.id.i7);
        i82 = findViewById(R.id.i8);
    }

    private void setOnClickListenerReload()
    {
        reload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //(Menu.arrayOfDefisActif.size() > 0 &&
                if(defiDisplayed){
                    if (Menu.arrayOfDefisActif.size() > 0) {
                        setPasDeDefi();
                        stopTimer(timerDefi);
                    }else{
                        setPartieFinie();
                        stopTimer(timerDefi);
                    }
                    //}if(Menu.arrayOfDefisActif.size() == 0) {
                    //setPartieFinie();
                    //Menu.arrayOfDefisActif.size() > 0 &&
                }else if(!defiDisplayed)
                {
                    if (Menu.arrayOfDefisActif.size() > 0){
                        setDefi();
                    }else{
                        setPartieFinie();
                        stopTimer(timerDefi);
                    }
                }
                scroll.scrollTo( O,scroll.getTop());
                //Toast.makeText(Jeu.this, Boolean.toString(defiDisplayed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListenerButton()
    {
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.getReference("parties").child(Menu.codePartie).child("listJoueur").child(selfID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int ancienScore = snapshot.getValue(Integer.class);
                        int newScore = ancienScore + defiEnCours.getNbPoints();
                        mDatabase.getReference("parties").child(Menu.codePartie).child("listJoueur").child(selfID).setValue(newScore);
                        stopTimer(timerDefi);
                        setDefiReussi();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void setPasDeDefi()
    {
        description.setText("Pas de défi pour le moment! Rien ne t'empêche de boire en attendant ;)");
        timer.setVisibility(View.GONE);
        valider.setVisibility(View.GONE);
        defiDisplayed = false;

    }
    private void setDefiReussi()
    {
        description.setText("Défi réussi...Bravo! Rien ne t'empêche de boire en attendant le prochain !");
        timer.setVisibility(View.GONE);
        valider.setVisibility(View.GONE);
        defiDisplayed = false;

    }
    private void setPartieFinie()
    {
        description.setText("Partie terminé ! Bravo " + joueursListe.get(0).getPseudo() + " !");
        timer.setVisibility(View.GONE);
        valider.setVisibility(View.GONE);
        //defiDisplayed = 0;
    }

    private void setDefi()
    {
        getDefiRandom();
        valider.setEnabled(true);
        defiDisplayed = true;
        description.setText(defiEnCours.getDescription());
        timer.setVisibility(View.VISIBLE);
        valider.setVisibility(View.VISIBLE);
        startTimer(defiEnCours.getTemps()*60000);
    }

    private void getDefiRandom(){
        Random rand = new Random();
        int index = rand.nextInt(Menu.arrayOfDefisActif.size());
        defiEnCours = Menu.arrayOfDefisActif.get(index);
        int key = defiEnCours.getKey();
        mDatabase.getReference("parties").child(Menu.codePartie).child("defisActif").child(Integer.toString(key)).setValue(null);
    }

    private void refresh()
    {
        linear2.removeAllViews();
        joueursListe = this.sortUserPerScore(joueursListe);
        int classement = 1;
        for(UserForScore u: joueursListe)
        {
            addJoueurClassement(u,classement);
            classement++;
        }
    }

    private void addJoueurClassement(UserForScore player, int classement)
    {

        //-----initialisation variables de player------

        String pseudo = player.getPseudo();
        int score = player.getScore();
        Bitmap pdp = player.getImgProfil();

        //----------------CardView i1------------------

        CardView i1 = new CardView(this);
        LayoutParams i1Params = i12.getLayoutParams();
        i1.setLayoutParams(i1Params);
        i1.setRadius(60);
        i1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));


        //----------------LLH i2------------------
        LinearLayout i2 = new LinearLayout(this);
        i2.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams i2Params = i22.getLayoutParams();
        i2.setLayoutParams(i2Params);


        //-----------------CardView i3-----------------
        CardView i3 = new CardView(this);
        LayoutParams i3Params = i32.getLayoutParams();
        i3.setLayoutParams(i3Params);
        i3.setRadius(200);
        i1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));


        //---------------ImageView i4-------------------
        ImageView i4 = new ImageView(this);
        LayoutParams i4Params = i42.getLayoutParams();
        i4.setLayoutParams(i4Params);
        i4.setImageBitmap(pdp);

        //---------------LLH i5-------------------
        LinearLayout i5 = new LinearLayout(this);
        i5.setOrientation(LinearLayout.VERTICAL);
        LayoutParams i5Params = i52.getLayoutParams();
        i5.setLayoutParams(i5Params);
        i5.setGravity(Gravity.CENTER);


        //---------------TextView i6-------------------
        TextView i6 = new TextView(this);
        LayoutParams i6Params = i62.getLayoutParams();
        i6.setLayoutParams(i6Params);
        i6.setText(troncPseudo(pseudo));
        i6.setTextSize(30);
        i6.setTextColor(Color.parseColor("#000000"));
        Typeface tf = ResourcesCompat.getFont(i5.getContext(), roboto_condensed_bold);
        i6.setTypeface(tf);


        //---------------TextView i7-------------------
        TextView i7 = new TextView(this);
        LayoutParams i7Params = i72.getLayoutParams();
        i7.setLayoutParams(i7Params);
        i7.setText(score + " Pts");
        i7.setTextSize(20);
        i7.setTextColor(Color.parseColor("#000000"));


        //--------------TextView i8--------------------
        TextView i8 = new TextView(this);
        LayoutParams i8Params = i82.getLayoutParams();
        i8.setLayoutParams(i8Params);
        i8.setText("#"+  classement);
        i8.setGravity(Gravity.RIGHT | Gravity.CENTER_HORIZONTAL);
        i8.setTextColor(Color.parseColor("#A1DFCECE"));
        i8.setTextSize(60);

        if(classement <= 3)
        {
            switch (classement)
            {
                case 1: i8.setTextColor(Color.parseColor("#FFD700"));
                    break;
                case 2: i8.setTextColor(Color.parseColor("#C0C0C0"));
                    break;
                case 3: i8.setTextColor(Color.parseColor("#614e1a"));
                    break;

            }

        }
        //-----------------------------------------------------
        //-------------Emboitement des items---------------------
        //-----------------------------------------------------

        linear2.addView(i1);
        i1.addView(i2);
        i2.addView(i3);
        i3.addView(i4);

        i2.addView(i5);
        i5.addView(i6);
        i5.addView(i7);

        i2.addView(i8);
    }

    private String troncPseudo(String pseudo)
    {
        String p1 = pseudo.substring(0,1).toUpperCase();
        String p2 = pseudo.substring(1,pseudo.length()).toLowerCase();
        pseudo = p1+p2;
        if(pseudo.length()>10) return pseudo.substring(0,8)+"...";
        else return pseudo;
    }

    private ArrayList<UserForScore> sortUserPerScore(ArrayList<UserForScore> players)
    {
        if(players.isEmpty()) return players;
        else
        {

            int taille = players.size();
            ArrayList<UserForScore> sortedPlayers = players;

            boolean sorted = false;

            while(!sorted)
            {

                boolean done = true;

                for(int indice = 0; indice<taille-1;indice++)
                {

                    if(sortedPlayers.get(indice).getScore() < sortedPlayers.get(indice+1).getScore())
                    {
                        UserForScore save = sortedPlayers.get(indice);

                        sortedPlayers.set(indice, sortedPlayers.get(indice+1));
                        sortedPlayers.set(indice+1, save);
                        done = false;
                    }
                }

                if(done)
                    sorted = true;
            }
            return sortedPlayers;

        }

    }

    public static void setDefiFini()
    {
        try{
            valider.setEnabled(false);
            valider.setBackgroundColor(0xA1DFCECE);
            valider.setTextColor(Color.parseColor("#777777"));
        }catch (Exception e){}
    }


    //--------TIMER METHODS-----------------------

    private void startTimer(long time)
    {
        TIME = time;
        startService(timerDefi);
        Log.i(TAG,"Started Service");
    }

    private void stopTimer(Intent timer)
    {
        stopService(timer);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Update GUI
            updateGUI(intent);
        };
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown",30000);
            Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);


            int minutes = (int) (millisUntilFinished / 1000) / 60;
            int seconds = (int) (millisUntilFinished / 1000) % 60;
            String timeLeftFormatted = String.format(Locale.getDefault(), "TIMER: %02d:%02d", minutes, seconds);
            timer.setText(timeLeftFormatted);

            //txt.setText( Long.toString(millisUntilFinished / 1000));
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);

            sharedPreferences.edit().putLong("time",millisUntilFinished).apply();
        }
    }


}