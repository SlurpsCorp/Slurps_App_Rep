package fr.autruche.slurpsV2;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.FirebaseDatabase;

public class BroadcastService extends Service {
    final private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.backgoundtimercount";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("my notif", "my notif", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Log.i(TAG,"Starting timer...");
        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        long millis = Jeu.TIME;

        countDownTimer = new CountDownTimer(millis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                intent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                Notif notif = new Notif(Menu.arrayOfPseudo.get(Menu.arrayOfJoueur.indexOf(Jeu.selfID)),Jeu.defiEnCours.getDescription(),Jeu.defiEnCours.getNbPoints()*3);
                sendNotif("TEMPS ÉCOULÉ ⏳","Le temps t'a rattrapé connard ! 🖕");
                mDatabase.getReference("parties").child(Menu.codePartie).child("notif").setValue(notif);
                mDatabase.getReference("parties").child(Menu.codePartie).child("receiveNotif").setValue(true);
                Jeu.setDefiFini();
                stopSelf();

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotif(String titre, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BroadcastService.this, "my notif")
                .setSmallIcon(R.drawable.ic_glass) //https://stackoverflow.com/questions/30795431/android-push-notifications-icon-not-displaying-in-notification-white-square-sh
                .setContentTitle(titre)
                .setContentText(message).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BroadcastService.this);
        notificationManager.notify(1,builder.build());
    }
}