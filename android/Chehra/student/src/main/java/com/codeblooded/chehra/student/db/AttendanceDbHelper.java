package com.codeblooded.chehra.student.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codeblooded.chehra.student.models.BiMap;

import java.util.Map;

public class AttendanceDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Attendance.db";
    private static final String SQL_CREATE_DEPARTMENT_TABLE =
            "CREATE TABLE " + AttendanceDb.Department.TABLE_NAME + " (" +
                    AttendanceDb.Department.COLUMN_NAME_DEPT_ID + " INTEGER PRIMARY KEY," +
                    AttendanceDb.Department.COLUMN_NAME_DEPT_NAME + " TEXT ) ";
    private static final String SQL_DROP_DEPARTMENT_TABLE =
            "DROP TABLE IF EXISTS " + AttendanceDb.Department.TABLE_NAME;

    public AttendanceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DEPARTMENT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_DEPARTMENT_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addDepartment(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AttendanceDb.Department.COLUMN_NAME_DEPT_ID, id);
        values.put(AttendanceDb.Department.COLUMN_NAME_DEPT_NAME, name);

        db.insertWithOnConflict(AttendanceDb.Department.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("DB", "Added");

        db.close();
    }

    public void deleteDepartment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AttendanceDb.Department.TABLE_NAME,
                AttendanceDb.Department.COLUMN_NAME_DEPT_ID + " = " + id,
                null);
        Log.d("DB", "Deleted");
        db.close();
    }

    public void deleteAllDepartments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DROP_DEPARTMENT_TABLE);
        db.close();
    }

    public void putDepartments(BiMap<Integer, String> map) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (Map.Entry m : map.getMap().entrySet()) {
            if (m.getKey() != null) {
                values.put(AttendanceDb.Department.COLUMN_NAME_DEPT_ID, (Integer) m.getKey());
                values.put(AttendanceDb.Department.COLUMN_NAME_DEPT_NAME, (String) m.getValue());

                db.insertWithOnConflict(AttendanceDb.Department.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d("DB", "Added");
                System.out.println(m.getKey() + " " + m.getValue());
            }
        }

        db.close();
    }

    public BiMap<Integer, String> getDepartments(boolean inverseMap) {
        BiMap<Integer, String> map = new BiMap<>();
        String[] projection = {
                AttendanceDb.Department.COLUMN_NAME_DEPT_ID,
                AttendanceDb.Department.COLUMN_NAME_DEPT_NAME
        };

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AttendanceDb.Department.TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return map;
    }

}