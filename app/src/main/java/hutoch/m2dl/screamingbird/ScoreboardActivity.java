package hutoch.m2dl.screamingbird;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hutoch.m2dl.screamingbird.datacontracts.ScoreContract;

public class ScoreboardActivity extends AppCompatActivity {

    private ListView scoreListView;
    private ArrayAdapter<String> adapter;

    private List<String> scoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        scoreListView = findViewById(R.id.scoreList);

        scoreList = new ArrayList<>();

        AppDatabaseSQLite dbHelper = new AppDatabaseSQLite(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME,
                ScoreContract.ScoreEntry.COLUMN_NAME_SCORE
        };

        Cursor cursor = db.query(
                ScoreContract.ScoreEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                ScoreContract.ScoreEntry.COLUMN_NAME_SCORE + " DESC"
        );

        while(cursor.moveToNext()) {
            String nick = cursor.getString(cursor.getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_NAME_SCORE));

            scoreList.add(nick + " - " + score);
        }


        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                scoreList);

        scoreListView.setAdapter(adapter);
    }

}
