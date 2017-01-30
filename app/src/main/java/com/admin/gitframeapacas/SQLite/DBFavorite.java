package com.admin.gitframeapacas.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBFavorite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Favorite.db";
    public static final String CONTACTS_TABLE_NAME = "favorite";
    public static final String CONTACTS_COLUMN_SCODE = "scode";
    public static final String CONTACTS_COLUMN_AQI = "aqi";


    public DBFavorite(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        "(" +
                        CONTACTS_COLUMN_SCODE + " integer," +
                        CONTACTS_COLUMN_AQI + " float" +

                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAccount(String name, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_SCODE, name);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME, null);
        return res;
        /*
            how to use
          Cursor res = dbGrid.getAllData();
                if (res.getCount() == 0) {
                    Log.i("griddata", "Nothing found");
                } else {

                    while (res.moveToNext()) {
                        String sName = res.getString(0);
                    }

         */
    }


}