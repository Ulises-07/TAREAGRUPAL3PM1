package com.example.tareagrupal3pm1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Photos.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "photographs";
    public static final String COL_ID = "id";
    public static final String COL_IMAGEN = "imagen";
    public static final String COL_DESCRIPCION = "descripcion";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_IMAGEN + " BLOB, " +
                    COL_DESCRIPCION + " TEXT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addPhotograph(Photograph photograph) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IMAGEN, photograph.getImagen());
        contentValues.put(COL_DESCRIPCION, photograph.getDescripcion());

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        return result != -1;
    }

    public ArrayList<Photograph> getAllPhotographs() {
        ArrayList<Photograph> photoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                byte[] imagen = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGEN));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION));

                Photograph photo = new Photograph(id, imagen, descripcion);
                photoList.add(photo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return photoList;
    }
}
