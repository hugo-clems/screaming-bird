package hutoch.m2dl.screamingbird.datacontracts;

import android.provider.BaseColumns;

public final class ScoreContract {
    private ScoreContract() {}

    public static class ScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "score";
        public static final String COLUMN_NAME_NICKNAME = "pseudo";
        public static final String COLUMN_NAME_SCORE = "score";
    }
}
