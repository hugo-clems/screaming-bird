package hutoch.m2dl.screamingbird;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Menu principal de l'application.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Affiche les paramètres.
     * @param v non utilisé
     */
    public void clicParam(View v) {
        // TODO
    }

    /**
     * Affiche le sélecteur de niveau.
     * @param v non utilisé
     */
    public void clicJouer(View v) {
        Intent intent = new Intent(this, SelecteurNiveauActivity.class);
        startActivity(intent);
    }

    /**
     * Affiche le créateur de niveau.
     * @param v non utilisé
     */
    public void clicCreerNiveau(View v) {
        Intent intent = new Intent(this, CreateurNiveauActivity.class);
        startActivity(intent);
    }

    /**
     * Affiche le tableau des scores.
     * @param v non utilisé
     */
    public void clicScoreboard(View v) {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    /**
     * Affiche la page A Propos.
     * @param v non utilisé
     */
    public void clicAbout(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

}
