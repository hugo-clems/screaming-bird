package hutoch.m2dl.screamingbird.jeu;

public class Obstacle {

    private float posX;
    private float posY;
    private float rate;

    public Obstacle(float x, float y) {
        posX = x;
        posY = y;
        rate = 20;
    }

    public void tick() {
        posY += rate;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }
}
