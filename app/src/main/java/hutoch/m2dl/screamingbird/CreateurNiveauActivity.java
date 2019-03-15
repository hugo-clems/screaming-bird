package hutoch.m2dl.screamingbird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import hutoch.m2dl.screamingbird.utils.Obstacle;

public class CreateurNiveauActivity extends AppCompatActivity implements OnTouchListener, OnDragListener {

    public static Bitmap square;
    public static ArrayList<Obstacle> listeObstacles;
    public AnimatedView animatedView = null;
    public int posX = 0;
    public int oldX = 0;
    public int obstacleSizeX = 500;
    public int obstacleSizeY = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createur_niveau);

        findViewById(R.id.myimage3).setOnTouchListener(this);
        findViewById(R.id.zoneDeJeu).setOnDragListener(this);
        //findViewById(R.id.bottomleft).setOnDragListener(this);

        animatedView = findViewById(R.id.zoneDeJeu);
        animatedView.setOnTouchListener(this);

        listeObstacles = new ArrayList<Obstacle>();
        square = BitmapFactory.decodeResource(getResources(), R.drawable.square);
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(square, obstacleSizeX, obstacleSizeY, true));
        square = ((BitmapDrawable) d).getBitmap();
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!view.equals(animatedView)) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(null, shadowBuilder, view, 0);
                return true;
            }
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                posX = (int) motionEvent.getRawX();

                int diffPosX = posX - oldX;
                oldX = posX;

                if (diffPosX > 0){
                    for (int i = 0; i < listeObstacles.size(); i++) {
                        listeObstacles.get(i).toRight();
                    }
                } else {
                    for (int i = 0; i < listeObstacles.size(); i++) {
                        listeObstacles.get(i).toLeft();
                    }
                }
            }
        }
            return true;
    }

    public boolean onDrag(View layoutview, DragEvent dragevent) {
        int action = dragevent.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                listeObstacles.add(new Obstacle(dragevent.getX() - obstacleSizeX/2, dragevent.getY() - obstacleSizeY/3));
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Affichage.
     */
    public static class AnimatedView extends android.support.v7.widget.AppCompatImageView {

        public AnimatedView(Context context) {
            super(context);
        }

        public AnimatedView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AnimatedView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //Draw
            for (int i = 0; i < listeObstacles.size(); i++) {
                canvas.drawBitmap(square, listeObstacles.get(i).getX(), listeObstacles.get(i).getY(), null);
                //listeObstacles.get(i).toLeft();
            }
            invalidate();
        }
    }
}