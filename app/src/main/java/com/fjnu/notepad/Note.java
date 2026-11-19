package com.fjnu.notepad;

/**
 * 一条笔记的数据模型。
 * color 表示分类颜色（0 默认、1 红、2 橙、3 绿、4 蓝），pinned 表示是否置顶。
 */
public class Note {

    public long id;
    public String title;
    public String content;
    public long created;
    public long modified;
    public int color;
    public int pinned;

    public Note() {
        this.created = System.currentTimeMillis();
        this.modified = this.created;
    }

    public boolean isPinned() {
        return pinned == 1;
    }

    public String displayTitle() {
        if (title == null || title.trim().isEmpty()) {
            return "无标题笔记";
        }
        return title.trim();
    }
}
