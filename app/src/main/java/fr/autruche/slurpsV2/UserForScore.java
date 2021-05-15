package fr.autruche.slurpsV2;

public class UserForScore {

    private int pdp;
    private String pseudo;
    private int points;

    public UserForScore(int s) {
        pdp = R.drawable.fatima;
        this.pseudo = "aLEX";
        this.points = s;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPdp() {
        return pdp;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getPoints() {
        return points;
    }
}
