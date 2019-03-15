package hutoch.m2dl.screamingbird;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hutoch.m2dl.screamingbird.datacontracts.LevelContract;

public class SelecteurNiveauActivity extends AppCompatActivity {

    ListView lvLevels;

    private List<String> levelList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecteur_niveau);

        lvLevels = findViewById(R.id.lvLevels);
        levelList = new ArrayList<>();
        loadLevels();

        lvLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String levelInfos = lvLevels.getItemAtPosition(position).toString();
                int levelId = Integer.parseInt(levelInfos.split(" ")[0]);

                Intent intent = new Intent(parent.getContext(), JeuActivity.class);
                intent.putExtra("levelId", levelId);
                startActivity(intent);
            }
        });
    }

    private void loadLevels() {
        AppDatabaseSQLite dbHelper = new AppDatabaseSQLite(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                LevelContract.LevelEntry._ID,
                LevelContract.LevelEntry.COLUMN_NAME_NAME
        };

        Cursor cursor = db.query(
                LevelContract.LevelEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(LevelContract.LevelEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(LevelContract.LevelEntry.COLUMN_NAME_NAME));

            levelList.add(id + " " + name);
        }
        cursor.close();

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                levelList);

        lvLevels.setAdapter(adapter);
    }

}
