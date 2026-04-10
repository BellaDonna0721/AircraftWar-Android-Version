package edu.hitsz.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 使用 SQLite 保存排行榜数据
 */
public class RankDatabase {

    private static final String DB_NAME = "rank.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "rank";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_SCORE = "score";
    private static final String COL_TIME = "time";
    private static final String COL_DIFFICULTY = "difficulty";

    private final SQLiteOpenHelper helper;

    public RankDatabase(Context context) {
        helper = new SQLiteOpenHelper(context.getApplicationContext(), DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_SCORE + " INTEGER NOT NULL, " +
                        COL_TIME + " TEXT NOT NULL, " +
                        COL_DIFFICULTY + " TEXT NOT NULL" +
                        ");";
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // 简单实现：版本升级时清空表
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        }; 
    }

    /**
     * 新增一条记录
     */
    public void addRecord(String playerName, int score, String difficulty) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, playerName);
        values.put(COL_SCORE, score);
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        values.put(COL_TIME, time);
        values.put(COL_DIFFICULTY, difficulty);
        db.insert(TABLE_NAME, null, values);
    }

    /**
     * 按难度获取排行榜（按分数从高到低排序）
     */
    public List<Record> getRecords(String difficulty) {
        List<Record> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COL_ID, COL_NAME, COL_SCORE, COL_TIME, COL_DIFFICULTY},
                COL_DIFFICULTY + "=?",
                new String[]{difficulty},
                null,
                null,
                COL_SCORE + " DESC, " + COL_ID + " ASC"
        );
        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCORE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
                String diff = cursor.getString(cursor.getColumnIndexOrThrow(COL_DIFFICULTY));
                list.add(new Record(id, name, score, time, diff));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 根据 id 删除一条记录
     */
    public void deleteRecord(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * 记录实体
     */
    public static class Record {
        public final long id;
        public final String playerName;
        public final int score;
        public final String time;
        public final String difficulty;

        public Record(long id, String playerName, int score, String time, String difficulty) {
            this.id = id;
            this.playerName = playerName;
            this.score = score;
            this.time = time;
            this.difficulty = difficulty;
        }
    }
}
