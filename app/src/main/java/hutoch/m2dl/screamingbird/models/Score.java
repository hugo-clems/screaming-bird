package hutoch.m2dl.screamingbird.models;

public class Score {

    private int id;

    private String pseudo;

    private int score;

    public Score(String pseudo, int score) {
        this.pseudo = pseudo;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
