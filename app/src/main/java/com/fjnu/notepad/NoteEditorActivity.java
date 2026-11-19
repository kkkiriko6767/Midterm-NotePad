package com.fjnu.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 笔记编辑界面：新建或修改一条笔记。
 * 保存时自动更新时间戳；可设置分类颜色；可把笔记分享出去。
 */
public class NoteEditorActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etContent;
    private NoteDbHelper dbHelper;
    private Note note;
    private boolean isNew = true;
    private boolean dirty = false;
    private int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        dbHelper = new NoteDbHelper(this);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        long id = getIntent().getLongExtra(NoteListActivity.EXTRA_NOTE_ID, 0);
        if (id > 0) {
            note = dbHelper.findById(id);
        }
        if (note == null) {
            note = new Note();
        } else {
            isNew = false;
            etTitle.setText(note.title);
            etContent.setText(note.content);
            etContent.setSelection(etContent.getText().length());
            color = note.color;
        }
        setTitle(isNew ? "新建笔记" : "编辑笔记");
        applyColor();

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                dirty = true;
            }
        };
        etTitle.addTextChangedListener(watcher);
        etContent.addTextChangedListener(watcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 返回或切到后台时自动保存，避免内容丢失
        if (dirty) {
            save(false);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void applyColor() {
        int barColor;
        switch (color) {
            case 1:
                barColor = 0xFFE57373;
                break;
            case 2:
                barColor = 0xFFFFB74D;
                break;
            case 3:
                barColor = 0xFF81C784;
                break;
            case 4:
                barColor = 0xFF64B5F6;
                break;
            default:
                barColor = 0xFFBDBDBD;
                break;
        }
        findViewById(R.id.view_color).setBackgroundColor(barColor);
    }

    private boolean save(boolean showToast) {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString();
        if (title.isEmpty() && content.trim().isEmpty()) {
            if (showToast) {
                Toast.makeText(this, "内容为空，未保存", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        note.title = title;
        note.content = content;
        note.modified = System.currentTimeMillis();
        note.color = color;

        if (isNew) {
            dbHelper.insert(note);
            isNew = false;
        } else {
            dbHelper.update(note);
        }
        dirty = false;
        if (showToast) {
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void chooseColor() {
        final String[] colors = {"默认", "红色", "橙色", "绿色", "蓝色"};
        new AlertDialog.Builder(this)
                .setTitle("选择笔记分类颜色")
                .setSingleChoiceItems(colors, color, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        color = which;
                        applyColor();
                        dirty = true;
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void share() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title.isEmpty() ? "无标题笔记" : title);
        intent.putExtra(Intent.EXTRA_TEXT, (title.isEmpty() ? "" : title + "\n\n") + content);
        startActivity(Intent.createChooser(intent, "分享笔记"));
    }

    private void delete() {
        if (isNew) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("删除笔记")
                .setMessage("删除后不能恢复，确定删除吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delete(note.id);
                        dirty = false;
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            save(true);
            return true;
        } else if (id == R.id.action_color) {
            chooseColor();
            return true;
        } else if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_delete) {
            delete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
