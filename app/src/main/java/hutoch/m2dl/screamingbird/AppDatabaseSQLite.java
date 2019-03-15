package hutoch.m2dl.screamingbird;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hutoch.m2dl.screamingbird.datacontracts.ScoreContract.ScoreEntry;
import hutoch.m2dl.screamingbird.datacontracts.LevelContract.LevelEntry;
import hutoch.m2dl.screamingbird.datacontracts.LevelContract.ObstacleEntry;

public class AppDatabaseSQLite extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "screaming_bird.db";

    /**
     * CREATE TABLES
     */
    private static final String CREATE_SCORE =
            "CREATE TABLE " + ScoreEntry.TABLE_NAME + " (" +
                    ScoreEntry._ID + " INTEGER PRIMARY KEY," +
                    ScoreEntry.COLUMN_NAME_NICKNAME + " TEXT," +
                    ScoreEntry.COLUMN_NAME_SCORE + " NUMBER);";

    private static final String CREATE_LEVEL =
            "CREATE TABLE " + LevelEntry.TABLE_NAME + " (" +
                    LevelEntry._ID + " INTEGER PRIMARY KEY," +
                    LevelEntry.COLUMN_NAME_NAME + " TEXT);";

    private static final String CREATE_OBSTACLE =
            "CREATE TABLE " + ObstacleEntry.TABLE_NAME + " (" +
                    ObstacleEntry._ID + " INTEGER PRIMARY KEY," +
                    ObstacleEntry.COLUMN_NAME_X + " NUMBER," +
                    ObstacleEntry.COLUMN_NAME_Y + " NUMBER," +
                    ObstacleEntry.COLUMN_NAME_TYPE + " TEXT," +
                    ObstacleEntry.COLUMN_NAME_LEVEL + " INTEGER REFERENCES " + LevelEntry.TABLE_NAME + ");";

    /**
     * DROP TABLES
     */
    private static final String SQL_DELETE_SCORE =
            "DROP TABLE IF EXISTS " + ScoreEntry.TABLE_NAME + ";";

    private static final String SQL_DELETE_LEVEL =
            "DROP TABLE IF EXISTS " + LevelEntry.TABLE_NAME + ";";

    private static final String SQL_DELETE_OBSTABLE =
            "DROP TABLE IF EXISTS " + LevelEntry.TABLE_NAME + ";";

    public AppDatabaseSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCORE);
        db.execSQL(CREATE_LEVEL);
        db.execSQL(CREATE_OBSTACLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SCORE);
        db.execSQL(SQL_DELETE_OBSTABLE);
        db.execSQL(SQL_DELETE_LEVEL);
        onCreate(db);
    }
}
