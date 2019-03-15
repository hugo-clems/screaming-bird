package hutoch.m2dl.screamingbird;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import hutoch.m2dl.screamingbird.utils.FinishLine;
import hutoch.m2dl.screamingbird.datacontracts.LevelContract;
import hutoch.m2dl.screamingbird.utils.Obstacle;

public class CreateurNiveauActivity extends AppCompatActivity implements OnTouchListener, OnDragListener {

    private final String OBSTACLE_TYPE = "OBSTACLE";
    private final String FINISH_TYPE = "FINISH";

    public static Bitmap obstacle;
    public static Bitmap finishLineBitmap;
    private EditText editName;
    private Button buttonOK;
    public static ArrayList<Obstacle> listeObstacles;
    public AnimatedView animatedView = null;
    public int posX = 0;
    public int oldX = 0;
    public int obstacleSizeX = 300;
    public int obstacleSizeY = 1000;
    public int finishLineSizeX = 200;
    public int finishLineSizeY = 200;
    public static FinishLine finishLine;
    public int idObstacle;
    public int idFinishLine;
    public int idDragged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createur_niveau);

        findViewById(R.id.obstacle).setOnTouchListener(this);
        findViewById(R.id.finish).setOnTouchListener(this);
        findViewById(R.id.zoneDeJeu).setOnDragListener(this);
        //findViewById(R.id.bottomleft).setOnDragListener(this);

        editName = findViewById(R.id.editName);
        buttonOK = findViewById(R.id.buttonOK);
        buttonOK.setEnabled(false);
        buttonOK.setOnTouchListener(this);
        animatedView = findViewById(R.id.zoneDeJeu);
        animatedView.setOnTouchListener(this);
        finishLine = null;

        idObstacle = findViewById(R.id.obstacle).getId();
        idFinishLine = findViewById(R.id.finish).getId();

        listeObstacles = new ArrayList<Obstacle>();
        obstacle = BitmapFactory.decodeResource(getResources(), R.drawable.square);
        Drawable dO = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(obstacle, obstacleSizeX, obstacleSizeY, true));
        obstacle = ((BitmapDrawable) dO).getBitmap();

        finishLineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.finishline);
        Drawable dF = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(finishLineBitmap, finishLineSizeX, finishLineSizeY, true));
        finishLineBitmap = ((BitmapDrawable) dF).getBitmap();
    }


    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!view.equals(animatedView)) {
            if (view.equals(buttonOK)) {
                buttonOK.setEnabled(false);
                prepareObstaclesForSave();
                saveLevel();
                finish();
            } else {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    idDragged = view.getId();
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(null, shadowBuilder, view, 0);
                    return true;
                }
            }
        } else {
            if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                posX = (int) motionEvent.getRawX();

                int diffPosX = posX - oldX;
                oldX = posX;

                if(diffPosX > 0){
                    for (int i = 0; i < listeObstacles.size(); i++) {
                        listeObstacles.get(i).toRight();
                    }
                    if(finishLine != null) finishLine.toRight();
                } else {
                    for (int i = 0; i < listeObstacles.size(); i++) {
                        listeObstacles.get(i).toLeft();
                    }
                    if(finishLine != null) finishLine.toLeft();
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
                if(idDragged == idObstacle) {
                    listeObstacles.add(new Obstacle(dragevent.getX() - obstacleSizeX/2, dragevent.getY() - obstacleSizeY/10));
                } else if (idDragged == idFinishLine) {
                    finishLine = new FinishLine(dragevent.getX() - finishLineSizeX/2, dragevent.getY() - finishLineSizeY/2);
                    buttonOK.setEnabled(true);
                }
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
                canvas.drawBitmap(obstacle, listeObstacles.get(i).getX(), listeObstacles.get(i).getY(), null);
                //listeObstacles.get(i).toLeft();
            }
            if(finishLine != null) {
                canvas.drawBitmap(finishLineBitmap, finishLine.getX(), finishLine.getY(), null);
            }
            invalidate();
        }
    }

    private void prepareObstaclesForSave() {
        float xMin = listeObstacles.get(0).getX();

        for (Obstacle obstacle : listeObstacles) {
            if (obstacle.getX() < xMin) {
                xMin = obstacle.getX();
            }
        }

        if (xMin < 0) {
            xMin = Math.abs(xMin);
        } else {
            xMin = 0 - xMin;
        }

        for (Obstacle obstacle : listeObstacles) {
            obstacle.setPosX(obstacle.getX() + xMin);
        }

        finishLine.setPosX(finishLine.getX() + xMin);
    }

    private void saveLevel() {
        AppDatabaseSQLite dbHelper = new AppDatabaseSQLite(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String levelName = editName.getText().toString();

        ContentValues levelValues = new ContentValues();
        levelValues.put(LevelContract.LevelEntry.COLUMN_NAME_NAME, levelName);

        long levelId = db.insert(LevelContract.LevelEntry.TABLE_NAME, null, levelValues);

        ContentValues obstacleValues = new ContentValues();
        for (Obstacle obstacle : listeObstacles) {
            obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_X, obstacle.getX());
            obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_Y, obstacle.getY());
            obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_TYPE, OBSTACLE_TYPE);
            obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_LEVEL, levelId);

            db.insert(LevelContract.ObstacleEntry.TABLE_NAME, null, obstacleValues);
        }

        obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_X, finishLine.getX());
        obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_Y, finishLine.getY());
        obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_TYPE, FINISH_TYPE);
        obstacleValues.put(LevelContract.ObstacleEntry.COLUMN_NAME_LEVEL, levelId);

        db.insert(LevelContract.ObstacleEntry.TABLE_NAME, null, obstacleValues);
    }
}