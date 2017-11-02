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
    final private static String Insert = "insert into " + TABLENAME + "(Number, CompanyCode, " +
            "Date) values(?, ?, ?)";
    private SQLiteStatement InsertStmt;
    private OpenHelper oh;
    private SQLiteDatabase db;

    public SearchingHistoryDataHelper(final Context context){
        oh = new OpenHelper(context, TABLENAME);
        db = oh.getWritableDatabase();
        InsertStmt = db.compileStatement(Insert);
    }

    public boolean InsertHistory(String num, String comp_code){
        InsertStmt.bindString(1, num);
        InsertStmt.bindString(2, comp_code);
        InsertStmt.bindString(3, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        try {
            InsertStmt.executeInsert();
        }
        catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> DisplatHistory(){
        List<String> rtn = new ArrayList<>();
        Cursor cursor = db.query(TABLENAME, new String[]{"Number", "CompanyCode", "Date"}, null,
                null, null, null, "Date");
        if(cursor.moveToFirst()){
            rtn.add(cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";" +
                    ""+cursor.getString(3));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return rtn;
    }

    private class OpenHelper extends SQLiteOpenHelper {
        private String DBName;

        private OpenHelper(Context context, String DBName){
            super(context, DBName, null, 1, null);
            this.DBName = DBName;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+ DBName + "(Number text primary key, CompanyCode text, " +
                    "Date text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + DBName);
            onCreate(db);
        }
    }
}

