package fr.autruche.slurpsV2;
public class Defi
{
    private String description="";
    private int nbPoints=0;
    private int nbPersonnes=0;
    private int tempsMin = 0;

    public Defi(String description, int nbPoints, int nbPersonnes, int tempsMin) {
        this.description = description;
        this.nbPoints = nbPoints;
        this.nbPersonnes = nbPersonnes;
        this.tempsMin = tempsMin;
    }
    public String getDescription() {
        return description;
    }
    public int getNbPoints() {
        return nbPoints;
    }
    public int getNbPersonnes() {
        return nbPersonnes;
    }
    public int getTempsMin() {
        return tempsMin;
    }
}