package com.admin.gitframeapacas.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 29/1/2560.
 */

public class DBGrid extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Grid.db";
    public static final String CONTACTS_TABLE_NAME = "grid_lut";
    public static final String CONTACTS_COLUMN_SCODE = "scode";
    public static final String CONTACTS_COLUMN_SNAME = "sname";
    public static final String CONTACTS_COLUMN_DCODE = "dcode";
    public static final String CONTACTS_COLUMN_DNAME = "dname";
    public static final String CONTACTS_COLUMN_PCODE = "pcode";
    public static final String CONTACTS_COLUMN_PNAME = "pname";


    public DBGrid(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        "(" +
                        CONTACTS_COLUMN_SCODE + " integer," +
                        CONTACTS_COLUMN_SNAME + " text," +
                        CONTACTS_COLUMN_DCODE + " integer," +
                        CONTACTS_COLUMN_DNAME + " text," +
                        CONTACTS_COLUMN_PCODE + " integer," +
                        CONTACTS_COLUMN_PNAME + " text" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int scode, String sname, int dcode, String dname, int pcode, String pname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_SCODE, scode);
        contentValues.put(CONTACTS_COLUMN_SNAME, sname);
        contentValues.put(CONTACTS_COLUMN_DCODE, dcode);
        contentValues.put(CONTACTS_COLUMN_DNAME, dname);
        contentValues.put(CONTACTS_COLUMN_PCODE, pcode);
        contentValues.put(CONTACTS_COLUMN_PNAME, pname);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME, null);
        return res;
    }

}