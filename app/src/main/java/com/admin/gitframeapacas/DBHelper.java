package com.admin.gitframeapacas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Aparcas.db";
    public static final String CONTACTS_TABLE_NAME = "checklogin";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_STATUS = "status";
    public static final String CONTACTS_COLUMN_NAME = "name";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table checklogin " +
                        "(id integer primary key, name text,status integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertAccount(String name, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("status", status);

        db.insert("checklogin", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from checklogin where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateAccount(Integer id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);

        db.update("checklogin", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("checklogin",
                "status = ? ",
                new String[]{Integer.toString(status)});
    }


    public int getStatus() {
        int status = 0;
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select status from checklogin", null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_STATUS));
            res.moveToNext();
        }

        return Integer.parseInt(place);
    }

    public String getName() {
        String place = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select name from checklogin ", null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            place = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME));
            res.moveToNext();
        }

        return place;
    }
}
