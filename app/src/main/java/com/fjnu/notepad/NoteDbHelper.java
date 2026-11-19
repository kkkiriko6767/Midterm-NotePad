package com.fjnu.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 用 SQLite 保存笔记。数据表 notes 中除了标题和正文，还保存创建时间、修改时间、
 * 分类颜色和置顶标记，为时间戳显示、查询和扩展功能提供数据基础。
 */
public class NoteDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notepad.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "notes";

    public NoteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "content TEXT, "
                + "created INTEGER, "
                + "modified INTEGER, "
                + "color INTEGER DEFAULT 0, "
                + "pinned INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insert(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.title);
        values.put("content", note.content);
        values.put("created", note.created);
        values.put("modified", note.modified);
        values.put("color", note.color);
        values.put("pinned", note.pinned);
        long id = getWritableDatabase().insert(TABLE, null, values);
        note.id = id;
        return id;
    }

    public void update(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.title);
        values.put("content", note.content);
        values.put("modified", note.modified);
        values.put("color", note.color);
        values.put("pinned", note.pinned);
        getWritableDatabase().update(TABLE, values, "_id = ?",
                new String[]{String.valueOf(note.id)});
    }

    public void delete(long id) {
        getWritableDatabase().delete(TABLE, "_id = ?", new String[]{String.valueOf(id)});
    }

    public Note findById(long id) {
        Cursor cursor = getReadableDatabase().query(TABLE, null, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Note note = null;
        if (cursor.moveToFirst()) {
            note = readNote(cursor);
        }
        cursor.close();
        return note;
    }

    /**
     * 查询笔记列表：keyword 为空时返回全部；否则按标题或正文模糊匹配。
     * 排序规则：置顶优先，其次按修改时间倒序。
     */
    public List<Note> query(String keyword) {
        List<Note> result = new ArrayList<>();
        String selection = null;
        String[] args = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            selection = "title LIKE ? OR content LIKE ?";
            String like = "%" + keyword.trim() + "%";
            args = new String[]{like, like};
        }
        Cursor cursor = getReadableDatabase().query(TABLE, null, selection, args,
                null, null, "pinned DESC, modified DESC");
        while (cursor.moveToNext()) {
            result.add(readNote(cursor));
        }
        cursor.close();
        return result;
    }

    private Note readNote(Cursor cursor) {
        Note note = new Note();
        note.id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        note.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        note.content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
        note.created = cursor.getLong(cursor.getColumnIndexOrThrow("created"));
        note.modified = cursor.getLong(cursor.getColumnIndexOrThrow("modified"));
        note.color = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
        note.pinned = cursor.getInt(cursor.getColumnIndexOrThrow("pinned"));
        return note;
    }
}
