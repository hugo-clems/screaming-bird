package hutoch.m2dl.screamingbird;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import hutoch.m2dl.screamingbird.datacontracts.LevelContract;
import hutoch.m2dl.screamingbird.datacontracts.ScoreContract;
import hutoch.m2dl.screamingbird.utils.Obstacle;

public class FinDeJeuActivity extends AppCompatActivity {

    private TextView tvScoreTemps;
    private TextView tvNbVies;
    public int score;
    public int nbVies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_de_jeu);

        score = (int) getIntent().getSerializableExtra("score");
        nbVies = (int) getIntent().getSerializableExtra("nbVies");

        tvScoreTemps = findViewById(R.id.tvScoreTemps);
        tvNbVies = findViewById(R.id.tvNbVies);

        tvNbVies.setText("Nb vies restantes : " + nbVies);
        tvScoreTemps.setText("Temps : " + score);
    }

    /**
     * Retour à l'accueil.
     * @param v
     */
    public void retourAccueil(View v) {
        AppDatabaseSQLite dbHelper = new AppDatabaseSQLite(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues scoreValues = new ContentValues();

        EditText pseudonyme = (EditText) findViewById(R.id.pseudo);
        String strPseudo = pseudonyme.getText().toString();
        if(strPseudo.isEmpty()) strPseudo = "?";
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_NAME_NICKNAME, strPseudo);
        scoreValues.put(ScoreContract.ScoreEntry.COLUMN_NAME_SCORE, score);

        db.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, scoreValues);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
