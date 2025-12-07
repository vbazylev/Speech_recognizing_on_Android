package com.example.kurs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "transcriptions.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "transcriptions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertTranscription(Transcription transcription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, transcription.getText());
        values.put(COLUMN_TIMESTAMP, transcription.getTimestamp());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Transcription> getAllTranscriptions() {
        List<Transcription> transcriptions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_TEXT, COLUMN_TIMESTAMP},
                null, null, null, null,
                COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transcription transcription = new Transcription();
                transcription.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transcription.setText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)));
                transcription.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                transcriptions.add(transcription);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transcriptions;
    }

    public void deleteTranscription(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllTranscriptions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}