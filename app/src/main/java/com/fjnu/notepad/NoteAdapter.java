package com.fjnu.notepad;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 笔记列表适配器：左侧色条表示分类，中间是标题、内容摘要和时间戳，右侧是置顶标记。
 */
public class NoteAdapter extends BaseAdapter {

    private static final int[] COLORS = {
            Color.parseColor("#BDBDBD"),
            Color.parseColor("#E57373"),
            Color.parseColor("#FFB74D"),
            Color.parseColor("#81C784"),
            Color.parseColor("#64B5F6")
    };

    private final Context context;
    private final List<Note> notes;
    private final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.colorBar = convertView.findViewById(R.id.view_color);
            holder.title = convertView.findViewById(R.id.tv_title);
            holder.snippet = convertView.findViewById(R.id.tv_snippet);
            holder.time = convertView.findViewById(R.id.tv_time);
            holder.pin = convertView.findViewById(R.id.iv_pin);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);
        holder.title.setText(note.displayTitle());
        holder.snippet.setText(oneLine(note.content));
        holder.time.setText("修改于 " + format.format(new Date(note.modified)));

        int color = (note.color >= 0 && note.color < COLORS.length)
                ? COLORS[note.color] : COLORS[0];
        holder.colorBar.setBackgroundColor(color);
        holder.pin.setVisibility(note.isPinned() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private String oneLine(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "（空白笔记）";
        }
        return content.trim().replace("\n", " ");
    }

    static class ViewHolder {
        View colorBar;
        TextView title;
        TextView snippet;
        TextView time;
        ImageView pin;
    }
}
