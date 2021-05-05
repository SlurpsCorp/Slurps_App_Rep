package fr.autruche.slurpsV2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Partie {

    private String codePartie;

    private int heurePartie = 0;
    private int minutePartie = 0;
    private String createurID;
    private ArrayList joueurIDlist;
    private boolean accessible= true;;

    public Partie(){}

    public Partie(String codePartie, String createurID){
        this.codePartie = codePartie;
        this.createurID = createurID;
        ArrayList<String> myArrayList = new ArrayList();
        myArrayList.add(createurID);
        this.joueurIDlist = myArrayList;

    }

    public Partie(String codePartie, String createurID, int heurePartie, int minutePartie){
        this(codePartie, createurID);
        this.heurePartie = heurePartie;
        this.minutePartie = minutePartie;
    }

    public boolean isAccessible() {
        return accessible;
    }
    public String getCodePartie(){
        return codePartie;
    }
    public int getHeurePartie(){
        return heurePartie;
    }
    public int getMinutePartie(){
        return minutePartie;
    }
    public String getCreateurID(){
        return createurID;
    }
    public ArrayList getJoueurIDlist() {
        return joueurIDlist;
    }
}