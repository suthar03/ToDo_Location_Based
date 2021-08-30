package com.suthar.todo_location;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class EventDataBase {
    public static final String KEY_ROW_ID = "_id";
    public static final String EVENT_TYPE = "event_type";
    public static final String EVENT_TITLE = "event_name";
    public static final String EVENT_DESC = "event_desc";
    public static final String EVENT_TIME = "event_time";
    public static final String EVENT_DATE = "event_date";
    public static final String EVENT_ADDRESS = "event_address";
    public static final String EVENT_LATITUDE = "event_latitude";
    public static final String EVENT_LONGITUDE = "event_longitude";
    public static final String EVENT_ACCURACY = "event_accuracy";
    public static final String EVENT_DONE = "event_done";

    private final String DATABASE_NAME = "EventsDB";
    private final String DATABASE_TABLE = "EventsTable";
    private final int DATABASE_VERSION = 1;
    private final Context ourContext;
    private DBHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public EventDataBase(Context context) {
        ourContext = context;
    }

    public DBHelper open() throws SQLException {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return ourHelper;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ROW_ID, event.getId());
        cv.put(EVENT_TYPE, event.getType());
        cv.put(EVENT_TITLE, event.getTitle());
        cv.put(EVENT_DESC, event.getDescription());
        cv.put(EVENT_DATE, event.getDate());
        cv.put(EVENT_TIME, event.getTime());
        cv.put(EVENT_ADDRESS, event.getAddress());
        cv.put(EVENT_LATITUDE, event.getLatitude());
        cv.put(EVENT_LONGITUDE, event.getLongitude());
        cv.put(EVENT_ACCURACY, event.getAccuracy());
        cv.put(EVENT_DONE, event.getisDone());
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<Event> getData() {
        String[] columns = new String[]{KEY_ROW_ID, EVENT_TYPE, EVENT_TITLE, EVENT_DESC, EVENT_DATE, EVENT_TIME, EVENT_ADDRESS, EVENT_LATITUDE, EVENT_LONGITUDE, EVENT_ACCURACY, EVENT_DONE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, EVENT_DONE+" desc");
        ArrayList<Event> res = new ArrayList<>();
        int iROW_ID = c.getColumnIndex(KEY_ROW_ID);
        int iTYPE = c.getColumnIndex(EVENT_TYPE);
        int iTITLE = c.getColumnIndex(EVENT_TITLE);
        int iDESC = c.getColumnIndex(EVENT_DESC);
        int iDATE = c.getColumnIndex(EVENT_DATE);
        int iTIME = c.getColumnIndex(EVENT_TIME);
        int iADDRESS = c.getColumnIndex(EVENT_ADDRESS);
        int iLATITUDE = c.getColumnIndex(EVENT_LATITUDE);
        int iLONGITUDE = c.getColumnIndex(EVENT_LONGITUDE);
        int iACCURACY = c.getColumnIndex(EVENT_ACCURACY);
        int iDONE = c.getColumnIndex(EVENT_DONE);


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Event eve = new Event(c.getString(iROW_ID), c.getString(iTYPE), c.getString(iTITLE), c.getString(iDESC), c.getString(iDATE), c.getString(iTIME)
                    , c.getString(iADDRESS), c.getDouble(iLATITUDE), c.getDouble(iLONGITUDE), c.getInt(iACCURACY));
            eve.setisDone(c.getInt(iDONE));
            res.add(eve);
        }
        c.close();
        return res;
    }

    public long deleteEntry(String rowId) {
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROW_ID + "=?", new String[]{rowId});
    }

    public long updateEntry(Event event) {
        String rowId = event.getId();
        ContentValues cv = new ContentValues();
        cv.put(EVENT_TYPE, event.getType());
        cv.put(EVENT_TITLE, event.getTitle());
        cv.put(EVENT_DESC, event.getDescription());
        cv.put(EVENT_DATE, event.getDate());
        cv.put(EVENT_TIME, event.getTime());
        cv.put(EVENT_ADDRESS, event.getAddress());
        cv.put(EVENT_LATITUDE, event.getLatitude());
        cv.put(EVENT_LONGITUDE, event.getLongitude());
        cv.put(EVENT_ACCURACY, event.getAccuracy());
        cv.put(EVENT_DONE, event.getisDone());
        return ourDatabase.update(DATABASE_TABLE, cv, KEY_ROW_ID + "=?", new String[]{rowId});
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROW_ID + " TEXT PRIMARY KEY, " +
                    EVENT_TYPE + " TEXT NOT NULL, " +
                    EVENT_TITLE + " TEXT NOT NULL, " +
                    EVENT_DESC + " TEXT, " +
                    EVENT_DATE + " TEXT, " +
                    EVENT_TIME + " TEXT, " +
                    EVENT_ADDRESS + " TEXT, " +
                    EVENT_LATITUDE + " REAL, " +
                    EVENT_LONGITUDE + " REAL, " +
                    EVENT_ACCURACY + " INTEGER, " +
                    EVENT_DONE + " INTEGER );";

            db.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            //You Can take or use existing database just here after this that will get destroyed
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
