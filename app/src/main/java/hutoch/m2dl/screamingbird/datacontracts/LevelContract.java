package hutoch.m2dl.screamingbird.datacontracts;

import android.provider.BaseColumns;

public final class LevelContract {

    private LevelContract() {}

    public static class LevelEntry implements BaseColumns {
        public static final String TABLE_NAME = "level";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static class ObstacleEntry implements BaseColumns {
        public static final String TABLE_NAME = "obstable";
        public static final String COLUMN_NAME_X = "x";
        public static final String COLUMN_NAME_Y = "y";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LEVEL = "level";
    }
}
