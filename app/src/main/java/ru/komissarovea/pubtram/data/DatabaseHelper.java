package ru.komissarovea.pubtram.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "PubTramDB";
    // Contacts table name
    private static final String TABLE_STOPS = "stops";
    // Contacts Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "Name";
    private static final String KEY_STREET = "Street";
    private static final String KEY_INFO = "Info";
    private static final String KEY_LONGITUDE = "Longitude";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_STOPS = "Stops";
    private static final String KEY_STOPNUMBER = "StopNumber";

    private Context _context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_STOPS_TABLE = "CREATE TABLE " + TABLE_STOPS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                    + KEY_STREET + " TEXT," + KEY_INFO + " TEXT,"
                    + KEY_LONGITUDE + " REAL," + KEY_LATITUDE + " REAL,"
                    + KEY_STOPS + " TEXT," + KEY_STOPNUMBER + " TEXT" + ")";
            db.execSQL(CREATE_STOPS_TABLE);

            ArrayList<Stop> stops = StopsHelper.getDefaultStops(_context);
            for (int i = 0; i < stops.size(); i++) {
                Stop stop = stops.get(i);
                ContentValues values = new ContentValues();
                values.put(KEY_ID, stop.getID());
                values.put(KEY_NAME, stop.getName());
                values.put(KEY_STREET, stop.getStreet());
                values.put(KEY_INFO, stop.getInfo());
                values.put(KEY_LONGITUDE, stop.getLongitude());
                values.put(KEY_LATITUDE, stop.getLatitude());
                values.put(KEY_STOPS, stop.getStopsString());
                values.put(KEY_STOPNUMBER, stop.getStopNumber());
                db.insert(TABLE_STOPS, null, values);
                // addStop(stop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
        // Create tables again
        onCreate(db);
    }

    public ArrayList<Stop> getAllStops() {
        ArrayList<Stop> stopList = new ArrayList<>();
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_STOPS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Stop stop = new Stop();
                    stop.setID(Integer.parseInt(cursor.getString(0)));
                    stop.setName(cursor.getString(1));
                    stop.setStreet(cursor.getString(2));
                    stop.setInfo(cursor.getString(3));
                    stop.setLongitude(cursor.getDouble(4));
                    stop.setLatitude(cursor.getDouble(5));
                    stop.setStopsString(cursor.getString(6));
                    stop.setStopNumber(cursor.getString(7));
                    if (!TextUtils.isEmpty(stop.getName()))
                        stopList.add(stop);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stopList;
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addStop(Stop stop) {
        try {
            if (stop != null) {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(KEY_ID, stop.getID());
                values.put(KEY_NAME, stop.getName());
                values.put(KEY_STREET, stop.getStreet());
                values.put(KEY_INFO, stop.getInfo());
                values.put(KEY_LONGITUDE, stop.getLongitude());
                values.put(KEY_LATITUDE, stop.getLatitude());
                values.put(KEY_STOPS, stop.getStopsString());
                values.put(KEY_STOPNUMBER, stop.getStopNumber());

                // Inserting Row
                db.insert(TABLE_STOPS, null, values);
                db.close(); // Closing database connection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stop getStop(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOPS,
                new String[] { KEY_ID, KEY_NAME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Stop stop = new Stop();
        stop.setID(Integer.parseInt(cursor.getString(0)));
        stop.setName(cursor.getString(1));
        stop.setStreet(cursor.getString(2));
        stop.setInfo(cursor.getString(3));
        stop.setLongitude(cursor.getDouble(4));
        stop.setLatitude(cursor.getDouble(5));
        stop.setStopsString(cursor.getString(6));
        stop.setStopNumber(cursor.getString(7));
        cursor.close();
        db.close();
        return stop;
    }

    public int updateStop(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, stop.getName());

        // updating row
        return db.update(TABLE_STOPS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(stop.getID()) });
    }

    public void deleteStop(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STOPS, KEY_ID + " = ?",
                new String[] { String.valueOf(stop.getID()) });
        db.close();
    }

    public int getStopsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STOPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
