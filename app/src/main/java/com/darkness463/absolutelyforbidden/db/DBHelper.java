package com.darkness463.absolutelyforbidden.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.darkness463.absolutelyforbidden.common.log.MyLog;

import java.util.List;

/**
 * Created by darkn on 2016/4/20.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "apps.db";
    private static final String TABLE_NAME = "disabledApp";
    private static final int DB_VERSION = 1;

    public static final String _ID = "_id";
    public static final String KEY_PACKAGENAME = "packageName";
//    public static final String APPNAME = "appName";

    private SQLiteDatabase mDb;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized SQLiteDatabase getDb() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    _ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_PACKAGENAME +" VARCHAR(30)" +
//                    APPNAME +" VARCHAR(20)," +
                    ")"
            );
        }catch (SQLException e) {
            MyLog.e("fail to create table: "+TABLE_NAME+":\n"+ Log.getStackTraceString(e));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor query(String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        try {
            c = getDb().query(TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return c;
    }

    public void insert(ContentValues values) {
        getDb().beginTransaction();
        try {
            getDb().insert(TABLE_NAME, null, values);
            getDb().setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            getDb().endTransaction();
        }
    }

    public void insertMulti(List<ContentValues> values) {
        getDb().beginTransaction();
        try {
            for (ContentValues value : values) {
                getDb().insert(TABLE_NAME, null, value);
            }
            getDb().setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            getDb().endTransaction();
        }
    }

    public int delete(String selection, String[] selectionArgs) {
        int result = 0;
        getDb().beginTransaction();
        try {
            result = getDb().delete(TABLE_NAME, selection, selectionArgs);
            getDb().setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            getDb().endTransaction();
        }
        return result;
    }
}
