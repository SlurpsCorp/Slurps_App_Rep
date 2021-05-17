package fr.autruche.slurpsV2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import static android.os.Build.VERSION_CODES.O;
import static android.view.ViewGroup.*;
import static fr.autruche.slurpsV2.R.font.roboto_condensed_bold;

public class Jeu extends AppCompatActivity {

    LinearLayout linear1, linear2;
    TextView description, timer;
    static Button valider;
    ImageView reload;
    Boolean defiDisplayed;
    ScrollView scroll;

    CardView i12,i32;
    LinearLayout i22, i52;
    ImageView i42;
    TextView i62,i72,i82;

    Intent timerDefi;

    ArrayList<UserForScore> ParticpantsDejaAppelees = new ArrayList<>();

    public static long TIME;
    private String TAG = "TIMER-TAG";

    private ArrayList<UserForScore> joueursListe= new ArrayList();

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

        //Création de la fausse lite de joueurs

        for(int i=0; i< 10; i++)
        {
            joueursListe.add(new UserForScore("Alex"+i, i));
        }


        //verif
        timerDefi  = new Intent(this, BroadcastService.class);
        if(timerDefi != null)
            Log.i("LOOOOOOOOOOG","INITIALISATIOOOOOOOOOOOOOOOOOOOOON)");


        //refresh affichage
        refresh();

    }

    private void init()
    {
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        description = findViewById(R.id.description);
        timer = findViewById(R.id.timer);
        valider = findViewById(R.id.valider);
        reload = findViewById(R.id.reload);
        scroll= findViewById(R.id.scroll);

        //setPasDeDefi();
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
                if(defiDisplayed) {
                    //setPasDeDefi();
                    stopTimer(timerDefi);
                }
                else
                    setDefi(new Defi("test @0",5,1,1));

            scroll.scrollTo( O,scroll.getTop());
            }
        });
    }

    private void setOnClickListenerButton()
    {
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Jeu.this, "CE BOUTON FONCTIONNE NOM DE DIEU!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    private void setPasDeDefi()
        description.setText("Pas de défi pour le moment! Rien ne t'empêche de boire en attendant ;)");
        timer.setVisibility(View.GONE);
        valider.setVisibility(View.GONE);
        defiDisplayed = false;

    }*/

    private void setDefi(Defi defi)
    {
        valider.setEnabled(true);


        description.setText(formaterDefi(defi));


        timer.setVisibility(View.VISIBLE);
        valider.setVisibility(View.VISIBLE);
        startTimer(defi.getTempsMin()*60000);
        defiDisplayed=true;

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
        int score = player.getPoints();
        int pdp = player.getPdp();

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
        i4.setImageResource(pdp);

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

                    if(sortedPlayers.get(indice).getPoints() > sortedPlayers.get(indice+1).getPoints())
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
        valider.setEnabled(false);
        valider.setBackgroundColor(0xA1DFCECE);
        valider.setTextColor(Color.parseColor("#777777"));

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


    /*
    ----------------------------------------------------------------------------------------------
    CETTE PARTIE EST DEDIEE AUX DEFI (sauf pour set setDefi qui est au debut de la classe)
    ----------------------------------------------------------------------------------------------
     */
    // A modifier en allant chercher sur Firebase
    public Defi getRandomDefi()
    {

        Defi test ;

        //si test n'est pas dans les defi déja fais
            test = new Defi("Niquer des mères.",3,0,1);

        return test;
    }


    private String formaterDefi(Defi defi)
    {
        String description = defi.getDescription();
        int nbParticipants = defi.getNbPersonnes();



        for(int i=0; i < nbParticipants; i++)
        {
            boolean isAlreadyChosen = true;
            UserForScore JoueurAppele;
            while(isAlreadyChosen && nbParticipants <= joueursListe.size()) {
                Random rand = new Random();
                int indice = rand.nextInt(joueursListe.size());
                JoueurAppele = joueursListe.get(indice);


                if (!ParticpantsDejaAppelees.contains(JoueurAppele))
                    ParticpantsDejaAppelees.add(JoueurAppele);
                    isAlreadyChosen = false;
            }

            String aChanger = "@".concat(String.valueOf(i));
            description = description.replaceAll(aChanger,joueursListe.get(i).getPseudo());
        }
        //Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
        return description;
    }







}