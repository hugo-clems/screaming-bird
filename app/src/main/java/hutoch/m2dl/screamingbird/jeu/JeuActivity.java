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
    public static final int taillePersonnage = 120;
    public static int personnagePositionLeft;
    public static int personnagePositionTop;

    // Element du décor
    public static Bitmap square;
    public static Obstacle obstacle;
    public static final int tailleObstacle = 250;

    // Le saut
    public Handler handler = new Handler();
    public boolean canTouch = true;
    public int compteurJump = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        this.animatedView = findViewById(R.id.zoneDeJeu);
        this.animatedView.setOnTouchListener(this);

        // Récupération de la taille de l'écran
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
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
            handler.postDelayed(jump, 2);
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
                handler.postDelayed(jump, 2);
            } else {
                compteurJump = 0;
                handler.postDelayed(fall, 2);
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
                handler.postDelayed(fall, 2);
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
        int posX = personnagePositionLeft + taillePersonnage / 2;
        int posY = personnagePositionTop + taillePersonnage / 2;

        if (posX > obstacle.getX() && posX < obstacle.getX() + tailleObstacle
                && posY > obstacle.getY() && posY < obstacle.getY() + tailleObstacle) {
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

        private void init() {
            personnage = BitmapFactory.decodeResource(getResources(), R.drawable.note);
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(personnage, taillePersonnage, taillePersonnage, true));
            personnage = ((BitmapDrawable) d).getBitmap();
            personnagePositionLeft = screenWidth / 2;
            personnagePositionTop = screenHeight - taillePersonnage;

            square = BitmapFactory.decodeResource(getResources(), R.drawable.square);
            Drawable d2 = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(square, tailleObstacle, tailleObstacle, true));
            square = ((BitmapDrawable) d2).getBitmap();
            obstacle = new Obstacle(personnagePositionLeft, personnagePositionTop - tailleObstacle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(personnage, personnagePositionLeft, personnagePositionTop, null);
            canvas.drawBitmap(square, obstacle.getX(), obstacle.getY(), null);
            invalidate();
        }

    }

}
