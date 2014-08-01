package com.tv.xeeng.game.data;

public class PostEntity {
    public int postID;
    public int avatarID;
    public String title;
    public String content;
    public long postDate;
    public int commentID;
    public int isNewComment;

    public PostEntity() {
        postID = 0;
        avatarID = 0;
        title = "undefined";
        content = "undefined";
        isNewComment = 0;
    }
}
