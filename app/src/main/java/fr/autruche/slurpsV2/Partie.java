package fr.autruche.slurpsV2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Partie {

    private String codePartie;

    private int heurePartie = 0;
    private int minutePartie = 0;
    private String createurID;
    private ArrayList joueurIDlist;
    private boolean acces = true;


    Date d = new Date();
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
    private String date = f.format(d);
    

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

    public boolean isAcces() {
        return acces;
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
    public String getDate() {
        return date;
    }
}