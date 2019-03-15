package hutoch.m2dl.screamingbird.utils;

public abstract class ElementMap {
    private float posX;
    private float posY;

    public ElementMap(float x, float y) {
        posX = x;
        posY = y;
    }

    public void toLeft() {
        posX -= 15;
    }

    public void toRight() {
        posX += 15;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public void setPosX(float posX) { this.posX = posX; }
}
