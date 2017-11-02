package com.njupt.a4081.expresstracking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.style.TtsSpan;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.sql.SQLData;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hsu on 2017/10/31.
 */

public class SearchingHistoryDataHelper {
    final private static String TABLENAME = "SearchingHistory";
    final private static String DATABASENAME = "ExpressTracking";
    private OpenHelper oh;
    private SQLiteDatabase db;

    public SearchingHistoryDataHelper(final Context context){
        oh = new OpenHelper(context, DATABASENAME);
    }

    public boolean InsertHistory(String num, String comp_code){
        db = oh.getWritableDatabase();
        String InsertString = "insert into " + TABLENAME + "(Number, CompanyCode, " +
                "Date) values('" + num + "', '" + comp_code + "', '" + new SimpleDateFormat
                ("yyyy-MM-dd " +
                "hh:mm:ss").format(new Date()) + "')";
        try {
            db.execSQL(InsertString);
        }
        catch (Exception exception){
            Log.e("test", exception.toString());
            return false;
        }
        finally {
            db.close();
        }
        return true;
    }

    public List<String> DisplayHistory(){
        db = oh.getWritableDatabase();
        List<String> rtn = new ArrayList<>();
        Cursor cursor = db.query(TABLENAME, new String[]{"Number", "CompanyCode", "Date"}, null,
                null, null, null, "Date");
        if(cursor.moveToFirst()){
            do{
                rtn.add(cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2));
            }while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return rtn;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        private OpenHelper(Context context, String DBName){
            super(context, DBName, null, 1, null);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+ TABLENAME + "(Number text primary key, CompanyCode text, " +
                    "Date text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLENAME);
            onCreate(db);
        }
    }
}

