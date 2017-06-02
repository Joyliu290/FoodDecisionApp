package com.example.joyli.fooddecisionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joyli on 2017-05-31.
 */

    public class SQLdatabaseActivity extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "mylist.db";
        public static final String TABLE_NAME = "mylist_data";
        public static final String COL_1 = "ID";
        public static final String COL_2 = "ITEM1";

        public SQLdatabaseActivity (Context context) {
            super(context, DATABASE_NAME, null, 1);}

        @Override
        public void onCreate(SQLiteDatabase db) { //called when the database is created for the first time. This iswehre the creation of tables and the initial population of the tables should happen

            String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " ITEM1 TEXT)";
            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public boolean addData (String item1) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, item1);


            long result = db.insert(TABLE_NAME, null, contentValues);

            //if data is inserted incorrectly it will return a -1

            if (result == -1) {
                return false;
            }
            else {
                return true;
            }

        }

        public Cursor getListContents () {
            SQLiteDatabase db = this.getWritableDatabase(); //create and/ore open a database that will be used for reading and writing
            Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            return data;
        }


    /*
    public static String formatDateTime (Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyy-MM-DD HH:mm:ss");

        Date date = null;
        if (timeToFormat !=null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;

            }

            if (date !=null){
                long when = date.getTime();
                int flags = 0;
                flags |= DateUtils.FORMAT_SHOW_TIME;
                flags |= DateUtils.FORMAT_SHOW_DATE;
                flags |= DateUtils.FORMAT_ABBREV_MONTH;
                flags |= DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context, when + TimeZone.getDefault().getOffset(when), flags);

            }

        }

        return finalDateTime;
    }


*/

    }

