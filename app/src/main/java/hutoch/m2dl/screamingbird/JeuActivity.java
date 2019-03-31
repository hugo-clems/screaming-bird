package hutoch.m2dl.screamingbird;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hutoch.m2dl.screamingbird.datacontracts.LevelContract;
import hutoch.m2dl.screamingbird.utils.DetectNoise;
import hutoch.m2dl.screamingbird.utils.FinishLine;
import hutoch.m2dl.screamingbird.utils.Obstacle;

/**
 * Ecran de jeu.
 */
public class JeuActivity extends Activity implements View.OnTouchListener, SensorEventListener {

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
    public static Bitmap obstacle;
    public static FinishLine finishLine;
    public static final int LARGEUR_OBSTACLE = 300;
    public static final int HAUTEUR_OBSTACLE = 1000;
    public static ArrayList<Obstacle> listeObstacles;
    private final String OBSTACLE_TYPE = "OBSTACLE";
    public static Bitmap finishLineBitmap;
    public static final int LARGEUR_FINISH_LINE = 200;
    public static final int HAUTEUR_FINISH_LINE = 200;

    // Vies
    public static Bitmap coeur;
    public static final int TAILLE_COEUR = 150;
    public static int nbVies;
    public int score = 0;

    // Le saut
    public Handler handler = new Handler();
    public boolean canJump = true;
    public int compteurJump = 0;

    // Son
    private static final int MIN_NOISE = 80;
    private static final int NOISE_POLL_INTERVAL = 300;
    private DetectNoise noiseSensor;
    private Handler noiseHandler = new Handler();
    private PowerManager.WakeLock mWakeLock;
    private boolean permissionsOK = false;
    private boolean noiseRunning = false;
    private SensorManager sensorManager;
    private int noiseAct;

    // Accelero
    private Sensor accelerometer;

    private int levelId;

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

        levelId = (int) getIntent().getSerializableExtra("levelId");

        listeObstacles = new ArrayList<>();
        loadLevel();
        score = listeObstacles.size();

        this.animatedView = findViewById(R.id.zoneDeJeu);
        this.animatedView.setOnTouchListener(this);
        this.animatedView.init();

        if (!hasMicroPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            permissionsOK = true;
            noiseSensor = new DetectNoise();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Noise:Alert");
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * Quand on touche l'écran.
     * @param v la vue associé
     * @param event l'évenement de touché
     * @return boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (canJump) {
            canJump = false;
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
                isOnFinishLine();
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
                if (isOnDeathZone()) {
                    killPersonnage();
                } else {
                    personnagePositionTop += 10;
                    handler.postDelayed(fall, VITESSE);
                    isOnFinishLine();
                }
            } else {
                canJump = true;
            }
        }
    };

    /**
     * Quand on touche l'obstacle.
     * @return true si on touche l'obstacle, false sinon
     */
    private static boolean isOnObstacle() {
        int posX = personnagePositionLeft + LARGEUR_PERSONNAGE / 2;
        int posY = personnagePositionTop + HAUTEUR_PERSONNAGE;

        for (Obstacle square : listeObstacles) {
            if (posX > square.getX() && posX < square.getX() + LARGEUR_OBSTACLE
                    && posY > square.getY() && posY < square.getY() + 11) {
                return true;
            }
        }

        return false;
    }

    /**
     * Quand on touche le bas de l'écran.
     * @return true si on touche le bas de l'écran, false sinon
     */
    private boolean isOnDeathZone() {
        int posY = personnagePositionTop + HAUTEUR_PERSONNAGE;

        if (posY > screenHeight && posY < screenHeight + LARGEUR_OBSTACLE) {
            return true;
        }

        return false;
    }

    /**
     * Quand on touche la finish line.
     */
    private void isOnFinishLine() {
        int posX = personnagePositionLeft + LARGEUR_PERSONNAGE / 2;
        int posY = personnagePositionTop + HAUTEUR_PERSONNAGE;

        if (posX > finishLine.getX() && posX < finishLine.getX() + LARGEUR_FINISH_LINE
                && posY > finishLine.getY() && posY < finishLine.getY() + HAUTEUR_FINISH_LINE) {
            Intent intent = new Intent(this, FinDeJeuActivity.class);
            intent.putExtra("score", score - (3 - nbVies));
            intent.putExtra("nbVies", nbVies);
            startActivity(intent);
        }
    }

    private void killPersonnage() {
        if (nbVies > 1) {
            nbVies--;
            personnagePositionLeft = 0;
            personnagePositionTop = 0;
            canJump = true;
        } else {
            // Redirection vers l'écran de GameOver
            Intent intent = new Intent(this, GameOverActivity.class);
            startActivity(intent);
        }
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
            nbVies = 3;

            // Personnage
            personnage = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(personnage, LARGEUR_PERSONNAGE, HAUTEUR_PERSONNAGE, true));
            personnage = ((BitmapDrawable) d).getBitmap();
            personnagePositionLeft = 0;
            personnagePositionTop = 0;

            // Obstacle
            obstacle = BitmapFactory.decodeResource(getResources(), R.drawable.square);
            Drawable d2 = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(obstacle, LARGEUR_OBSTACLE, HAUTEUR_OBSTACLE, true));
            obstacle = ((BitmapDrawable) d2).getBitmap();

            // Finishline
            finishLineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.finishline);
            Drawable dF = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(finishLineBitmap, LARGEUR_FINISH_LINE, HAUTEUR_FINISH_LINE, true));
            finishLineBitmap = ((BitmapDrawable) dF).getBitmap();

            // Coeur
            coeur = BitmapFactory.decodeResource(getResources(), R.drawable.coeur);
            Drawable d3 = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(coeur, TAILLE_COEUR, TAILLE_COEUR, true));
            coeur = ((BitmapDrawable) d3).getBitmap();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(personnage, personnagePositionLeft, personnagePositionTop, null);

            for (int i = 1; i <= nbVies; i++) {
                canvas.drawBitmap(coeur, screenWidth - TAILLE_COEUR*i, 0, null);
            }

            for (Obstacle square : listeObstacles) {
                canvas.drawBitmap(obstacle, square.getX(), square.getY(), null);
            }

            canvas.drawBitmap(finishLineBitmap, finishLine.getX(), finishLine.getY(), null);

            if(!isOnObstacle()) {
                personnagePositionTop += 1;
            }

            invalidate();
        }

    }

    /**
     * Charge le niveau depuis la bdd
     */
    private void loadLevel() {
        AppDatabaseSQLite dbHelper = new AppDatabaseSQLite(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LevelContract.ObstacleEntry.COLUMN_NAME_X,
                LevelContract.ObstacleEntry.COLUMN_NAME_Y,
                LevelContract.ObstacleEntry.COLUMN_NAME_TYPE
        };

        String selection = LevelContract.ObstacleEntry.COLUMN_NAME_LEVEL + " = ?";
        String[] selectionArgs = { "" + levelId };

        Cursor cursor = db.query(
                LevelContract.ObstacleEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while (cursor.moveToNext()) {
            float x = cursor.getLong(cursor.getColumnIndexOrThrow(LevelContract.ObstacleEntry.COLUMN_NAME_X));
            float y = cursor.getLong(cursor.getColumnIndexOrThrow(LevelContract.ObstacleEntry.COLUMN_NAME_Y));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(LevelContract.ObstacleEntry.COLUMN_NAME_TYPE));

            if (OBSTACLE_TYPE.equals(type)) {
                listeObstacles.add(new Obstacle(x, y));
            } else {
                finishLine = new FinishLine(x, y);
            }

        }
        cursor.close();
    }

    /* ***** Capteur Sonore ***** */

    private void updateNoise(int signalEMA) {
        if (Math.abs(signalEMA) > MIN_NOISE) {
            noiseAct = signalEMA;
            if (canJump) {
                canJump = false;
                handler.postDelayed(jump, VITESSE);
            }
        }
    }

    public boolean hasMicroPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private Runnable noiseSleepTask = new Runnable() {
        public void run() {
            startNoiseSensor();
        }
    };

    private Runnable noisePollTask = new Runnable() {
        public void run() {
            updateNoise(noiseSensor.getAmplitude());
            noiseHandler.postDelayed(noisePollTask, NOISE_POLL_INTERVAL);
        }
    };

    /**
     * Démarre le détecteur de bruits.
     */
    private void startNoiseSensor() {
        noiseSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

        noiseHandler.postDelayed(noisePollTask, NOISE_POLL_INTERVAL);
    }

    /**
     * Arrête le détecteur de bruits.
     */
    private void stopNoiseSensor() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        noiseHandler.removeCallbacks(noiseSleepTask);
        noiseHandler.removeCallbacks(noisePollTask);
        noiseSensor.stop();
        updateNoise(0);
        noiseRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int xValue = (int) event.values[1];
            for (Obstacle square : listeObstacles) {
                square.setRate(xValue);
                square.tick();
            }
            finishLine.setRate(xValue);
            finishLine.tick();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }

    /* ***** Gestion Appli ***** */

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionsOK) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionsOK) {
            if (!noiseRunning) {
                noiseRunning = true;
                startNoiseSensor();
            }
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopNoiseSensor();
        sensorManager.unregisterListener(this);
    }

}
