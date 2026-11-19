package com.fjnu.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记列表界面。
 * 基本功能：每条笔记显示标题、内容摘要和修改时间戳；支持按标题或内容查询。
 * 扩展功能：置顶、分类颜色、分享（导出为文本）。
 */
public class NoteListActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "extra_note_id";

    private ListView listView;
    private TextView emptyView;
    private NoteAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private NoteDbHelper dbHelper;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        setTitle("我的记事本");

        dbHelper = new NoteDbHelper(this);
        listView = findViewById(R.id.list_view);
        emptyView = findViewById(R.id.tv_empty);

        adapter = new NoteAdapter(this, notes);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notes.get(position);
                Intent intent = new Intent(NoteListActivity.this, NoteEditorActivity.class);
                intent.putExtra(EXTRA_NOTE_ID, note.id);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showItemMenu(notes.get(position));
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void loadNotes() {
        notes.clear();
        notes.addAll(dbHelper.query(keyword));
        adapter.notifyDataSetChanged();
        if (keyword.isEmpty()) {
            emptyView.setText("还没有笔记，点右上角 + 新建一条");
        } else {
            emptyView.setText("没有找到包含“" + keyword + "”的笔记");
        }
    }

    private void showItemMenu(final Note note) {
        String[] items = {note.isPinned() ? "取消置顶" : "置顶", "删除", "分享"};
        new AlertDialog.Builder(this)
                .setTitle(note.displayTitle())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            note.pinned = note.isPinned() ? 0 : 1;
                            note.modified = System.currentTimeMillis();
                            dbHelper.update(note);
                            loadNotes();
                            Toast.makeText(NoteListActivity.this,
                                    note.isPinned() ? "已置顶" : "已取消置顶",
                                    Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            confirmDelete(note);
                        } else {
                            shareNote(note);
                        }
                    }
                })
                .show();
    }

    private void confirmDelete(final Note note) {
        new AlertDialog.Builder(this)
                .setTitle("删除笔记")
                .setMessage("确定删除“" + note.displayTitle() + "”吗？删除后不能恢复。")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delete(note.id);
                        loadNotes();
                        Toast.makeText(NoteListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void shareNote(Note note) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, note.displayTitle());
        intent.putExtra(Intent.EXTRA_TEXT,
                note.displayTitle() + "\n\n" + (note.content == null ? "" : note.content));
        startActivity(Intent.createChooser(intent, "分享笔记"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索标题或内容");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query == null ? "" : query;
                loadNotes();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                keyword = newText == null ? "" : newText;
                loadNotes();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new) {
            startActivity(new Intent(this, NoteEditorActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
