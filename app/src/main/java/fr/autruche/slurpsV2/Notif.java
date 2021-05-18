package fr.autruche.slurpsV2;

public class Notif {
    private String idJoueur;
    private String description;
    private int nbGorge;
    private boolean done = false;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Notif(){}

    public Notif(String idJoueur, String description, int nbGorge) {
        this.idJoueur = idJoueur;
        this.description = description;
        this.nbGorge = nbGorge;
    }

    public void setIdJoueur(String idJoueur) {
        this.idJoueur = idJoueur;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNbGorge(int nbGorge) {
        this.nbGorge = nbGorge;
    }

    public String getIdJoueur() {
        return idJoueur;
    }

    public String getDescription() {
        return description;
    }

    public int getNbGorge() {
        return nbGorge;
    }
}
