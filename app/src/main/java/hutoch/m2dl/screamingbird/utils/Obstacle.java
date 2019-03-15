package hutoch.m2dl.screamingbird.utils;

public class Obstacle {
	private float posX;
	private float posY;
	
	public Obstacle(float x, float y) {
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
}
