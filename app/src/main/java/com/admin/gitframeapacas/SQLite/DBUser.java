package com.admin.gitframeapacas.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBUser extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";
    public static final String CONTACTS_TABLE_NAME = "user";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_STATUS = "statusLogin";
    public static final String CONTACTS_COLUMN_TYPE = "usertype";
    public static final String CONTACTS_COLUMN_GRID = "grid";
    public static final String CONTACTS_COLUMN_CHECK_SENSOR = "checksensor";
    public static final String CONTACTS_COLUMN_HAVE_SENSOR = "havesensor";

    public static final String CONTACTS_COLUMN_USERID = "user_id";

    public DBUser(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        "(" +
                        CONTACTS_COLUMN_NAME + " text," +
                        CONTACTS_COLUMN_STATUS + " integer," +
                        CONTACTS_COLUMN_GRID + " integer," +
                        CONTACTS_COLUMN_TYPE + " text," +
                        CONTACTS_COLUMN_CHECK_SENSOR + " integer," +
                        CONTACTS_COLUMN_USERID + " integer," +
                        CONTACTS_COLUMN_HAVE_SENSOR + " integer" +
                        ")"
        );

        db.execSQL(
                "INSERT INTO " + CONTACTS_TABLE_NAME + " VALUES ('x',0,0,'',0,0,3)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public void drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
    }
    public boolean insertAccount(String name, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_STATUS, status);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }


    public boolean updateStatus(int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_STATUS, status);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public boolean updateName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public boolean updateGrid(int grid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_GRID, grid);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public boolean updateUserType(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_TYPE, name);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);

        return true;
    }

    public boolean updateCheckSensor(int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_CHECK_SENSOR, status);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public boolean updateHaveSensor(int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_HAVE_SENSOR, status);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public boolean updateUserID(Long name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_USERID, name);
        db.update(CONTACTS_TABLE_NAME, contentValues, null, null);
        return true;
    }

    public int getStatus() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_STATUS + " from " + CONTACTS_TABLE_NAME, null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_STATUS));
            res.moveToNext();
        }

        return Integer.parseInt(place);
    }

    public long getUserID() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_USERID + " from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_USERID));
            res.moveToNext();
        }
        return Long.parseLong(place);
    }
    public int getGrid() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_GRID + " from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_GRID));
            res.moveToNext();
        }
        return Integer.parseInt(place);
    }
    public String getName() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_NAME + " from " + CONTACTS_TABLE_NAME, null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
            res.moveToNext();
        }

        return place;
    }

    public String getUserType() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_TYPE + " from " + CONTACTS_TABLE_NAME, null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_TYPE));
            res.moveToNext();
        }

        return place;
    }

    public int getCheckSensor() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_CHECK_SENSOR + " from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_CHECK_SENSOR));
            res.moveToNext();
        }
        return Integer.parseInt(place);
    }

    public int getHaveSensor() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + CONTACTS_COLUMN_HAVE_SENSOR + " from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_HAVE_SENSOR));
            res.moveToNext();
        }
        return Integer.parseInt(place);
    }
}
