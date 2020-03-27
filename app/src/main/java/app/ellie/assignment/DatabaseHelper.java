package app.ellie.assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "cohort5xx";
    public static final int VERSION = 1;
    public static final String TABLENAME = "Student";
    public static final String COL0 = "ID";
    public static final String COL1 = "Name";
    public static final String COL2 = "password";
    public static final String COL3 = "email";
    public static final String COL4 = "student_id";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME +
                "(" + COL0 + " integer primary key autoincrement, " + COL4 + " text unique, " + COL1 + " text, " + COL2 + " text, " + COL3 + " text)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("  drop table if exists TABLENAME");
        onCreate(db);
    }

    public long insertData(String id, String name, String pass, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues records = new ContentValues();
        records.put(COL1, name);
        records.put(COL2, pass);
        records.put(COL3, email);
        records.put(COL4, id);

        return db.insert(TABLENAME, null, records);
    }

    public long updateData(String id, String name, String pass, String email) {
        if (getStudentById(id) != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues records = new ContentValues();
            records.put(COL1, name);
            records.put(COL2, pass);
            records.put(COL3, email);
            return db.update(TABLENAME, records, COL4 + " =?", new String[]{String.valueOf(id)});
        } else {
            return insertData(id, name, pass, email);
        }
    }

    public Cursor ReadData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dataToRead = db.rawQuery("SELECT * FROM " + TABLENAME, null);
        return dataToRead;

    }

    public Map<String, String> getStudentById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dataToRead = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE " + COL4 + "='" + id + "'", null);
        Map<String, String> map = new HashMap<>();
        if (dataToRead.moveToFirst()) {
            map.put(COL1, dataToRead.getString(dataToRead.getColumnIndex(COL1)));
            map.put(COL2, dataToRead.getString(dataToRead.getColumnIndex(COL2)));
            map.put(COL3, dataToRead.getString(dataToRead.getColumnIndex(COL3)));
            return map;
        }
        return null;
    }

    public Cursor deleteRow(String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLENAME + " WHERE " + COL4 + "='" + studentId + "'");
        db.close();
        return null;
    }


    public void updateData(int Id, String name, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues records = new ContentValues();

        records.put(COL1, name);
        records.put(COL2, password);
        records.put(COL3, email);

        String whereClause = "id=?";
        String whereArgs[] = new String[]{String.valueOf(Id)};

        db.update(TABLENAME, records, whereClause, whereArgs);
    }
}
