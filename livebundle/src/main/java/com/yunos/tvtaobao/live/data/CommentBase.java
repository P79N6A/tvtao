package com.yunos.tvtaobao.live.data;

/**
 * Created by pan on 16/12/12.
 */

public class CommentBase {
    private String nick;
    private String comment;
    private int color;

    public int getColor() {
        return color;
    }

    public String getComment() {
        return comment;
    }

    public String getNick() {
        return nick;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
