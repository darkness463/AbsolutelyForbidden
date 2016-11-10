package com.darkness463.absolutelyforbidden.common.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.darkness463.absolutelyforbidden.common.context.ApplicationContext;
import com.darkness463.absolutelyforbidden.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkn on 2016/4/26.
 */
public class DbUtil {

    public static List<String> getAllApps() {
        DBHelper dbHelper = ApplicationContext.getDBHelper();
        Cursor cursor = dbHelper.query(null, null, null, null);
        List<String> apps = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            String packageName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PACKAGENAME));
            apps.add(packageName);
        }
        return apps;
    }

    public static void insert(String packageName) {
        DBHelper dbHelper = ApplicationContext.getDBHelper();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_PACKAGENAME, packageName);
        dbHelper.insert(values);
    }

    public static void insertMulti(List<String> pkgs) {
        DBHelper dbHelper = ApplicationContext.getDBHelper();
        List<ContentValues> valuesList = new ArrayList<>();
        for (String pkg : pkgs) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.KEY_PACKAGENAME, pkg);
            valuesList.add(values);
        }
        dbHelper.insertMulti(valuesList);
    }

    public static void delete(String packageName) {
        DBHelper dbHelper = ApplicationContext.getDBHelper();
        dbHelper.delete(DBHelper.KEY_PACKAGENAME + " = ?", new String[] {packageName});
    }

    public static void deleteAll() {
        DBHelper dbHelper = ApplicationContext.getDBHelper();
        dbHelper.delete(null, null);
    }

}
