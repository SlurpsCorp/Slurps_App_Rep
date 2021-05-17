package fr.autruche.slurpsV2;
public class Defi
{
    private String description;
    private int nbPoints;
    private int nbParticipant;
    private int temps;
    private int key = 0;

    public Defi(){}

    public Defi(String description, int nbPoints, int nbParticipant, int temps) {
        this.description = description;
        this.nbPoints = nbPoints;
        this.nbParticipant = nbParticipant;
        this.temps = temps;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNbPoints(int nbPoints) {
        this.nbPoints = nbPoints;
    }

    public void setNbParticipant(int nbParticipant) {
        this.nbParticipant = nbParticipant;
    }

    public void setTemps(int temps) {
        this.temps = temps;
    }

    public int getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
    public int getNbPoints() {
        return nbPoints;
    }
    public int getNbParticipant() {
        return nbParticipant;
    }
    public int getTemps() {
        return temps;
    }

    public void setKey(int key) {
        this.key = key;
    }
}