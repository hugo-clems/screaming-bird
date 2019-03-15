package hutoch.m2dl.screamingbird.jeu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import hutoch.m2dl.screamingbird.R;

/**
 * Ecran de jeu.
 */
public class JeuActivity extends Activity implements View.OnTouchListener {

    public AnimatedView animatedView = null;
    public static int screenWidth;
    public static int screenHeight;

    // Personnage
    public static Bitmap personnage;
    public static final int HAUTEUR_PERSONNAGE = 100;
    public static final int LARGEUR_PERSONNAGE = 200;
    public static final int VITESSE = 2;
    public static int personnagePositionLeft;
    public static int personnagePositionTop;

    // Element du décor
    public static Bitmap square;
    public static Obstacle obstacle;
    public static final int TAILLE_OBSTACLE = 154;

    // Le saut
    public Handler handler = new Handler();
    public boolean canTouch = true;
    public int compteurJump = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        // Récupération de la taille de l'écran
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        this.animatedView = findViewById(R.id.zoneDeJeu);
        this.animatedView.setOnTouchListener(this);
        this.animatedView.init();
    }

    /**
     * Quand on touche l'écran.
     * @param v la vue associé
     * @param event l'évenement de touché
     * @return boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (canTouch) {
            canTouch = false;
            handler.postDelayed(jump, VITESSE);
        }
        return false;
    }

    /**
     * Gestion du saut.
     */
    private Runnable jump = new Runnable() {
        public void run() {
            if (compteurJump < 35) {
                personnagePositionTop -= 10;
                compteurJump++;
                handler.postDelayed(jump, VITESSE);
            } else {
                compteurJump = 0;
                handler.postDelayed(fall, VITESSE);
            }
        }
    };

    /**
     * Gestion de la chutte.
     */
    private Runnable fall = new Runnable() {
        public void run() {
            if (!isOnObstacle()) {
                personnagePositionTop += 10;
                handler.postDelayed(fall, VITESSE);
            } else {
                canTouch = true;
            }
        }
    };

    /**
     * Quand on touche l'obstacle.
     * @return true si on touche l'obstacle, false sinon
     */
    private boolean isOnObstacle() {
        int posX = personnagePositionLeft + LARGEUR_PERSONNAGE / 2;
        int posY = personnagePositionTop + HAUTEUR_PERSONNAGE;

        if (posX > obstacle.getX() && posX < obstacle.getX() + TAILLE_OBSTACLE
                && posY > obstacle.getY() && posY < obstacle.getY() + TAILLE_OBSTACLE) {
            return true;
        }

        return false;
    }

    /**
     * Zone de jeu.
     */
    public static class AnimatedView extends android.support.v7.widget.AppCompatImageView {

        public AnimatedView(Context context) {
            super(context);
            init();
        }

        public AnimatedView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public AnimatedView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public void init() {
            personnage = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(personnage, LARGEUR_PERSONNAGE, HAUTEUR_PERSONNAGE, true));
            personnage = ((BitmapDrawable) d).getBitmap();
            personnagePositionLeft = screenWidth / 2;
            personnagePositionTop = screenHeight - HAUTEUR_PERSONNAGE;

            square = BitmapFactory.decodeResource(getResources(), R.drawable.square);
            Drawable d2 = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(square, TAILLE_OBSTACLE, TAILLE_OBSTACLE, true));
            square = ((BitmapDrawable) d2).getBitmap();
            obstacle = new Obstacle(personnagePositionLeft, personnagePositionTop - TAILLE_OBSTACLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(personnage, personnagePositionLeft, personnagePositionTop, null);
            canvas.drawBitmap(square, obstacle.getX(), obstacle.getY(), null);
            invalidate();
        }

    }

}
