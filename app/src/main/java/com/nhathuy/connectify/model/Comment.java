package com.nhathuy.connectify.model;

public class Comment {

    private long id;
    private int postNum;
    private long postTime;
    private String commentAuth;
    private String commentAuthPic;

    private String commentString;
    private String commentPic;
    private boolean hasPic;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPostNum() {
        return postNum;
    }

    public void setPostNum(int postNum) {
        this.postNum = postNum;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getCommentAuth() {
        return commentAuth;
    }

    public void setCommentAuth(String commentAuth) {
        this.commentAuth = commentAuth;
    }

    public String getCommentAuthPic() {
        return commentAuthPic;
    }

    public void setCommentAuthPic(String commentAuthPic) {
        this.commentAuthPic = commentAuthPic;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getCommentPic() {
        return commentPic;
    }

    public void setCommentPic(String commentPic) {
        this.commentPic = commentPic;
    }

    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }
}
