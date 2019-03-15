package hutoch.m2dl.screamingbird;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SelecteurNiveauActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecteur_niveau);
    }

    /**
     * Affiche le jeu.
     * @param v non utilisé
     */
    public void clicJouer(View v) {
        Intent intent = new Intent(this, JeuActivity.class);
        startActivity(intent);
    }

}
