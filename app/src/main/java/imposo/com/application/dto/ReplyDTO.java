package imposo.com.application.dto;

/**
 * Created by adityaagrawal on 05/11/15.
 */
public class ReplyDTO {
    private int replyId;
    private int userId;
    private String comment;
    private int isAnonyomous;
    private String replyTime;
    private String imageLink;
    private String name;
    private int likes;
    private boolean isLiked;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsAnonyomous() {
        return isAnonyomous;
    }

    public void setIsAnonyomous(int isAnonyomous) {
        this.isAnonyomous = isAnonyomous;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    @Override
    public boolean equals(Object o) {
        if(((ReplyDTO)o).getReplyId() == this.getReplyId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.getReplyId();
    }

    @Override
    public String toString() {
        return "ReplyDTO [replyId=" + replyId + ", userId=" + userId
                + ", comment=" + comment + ", isAnonyomous=" + isAnonyomous
                + ", replyTime=" + replyTime + ", imageLink=" + imageLink
                + ", name=" + name + ", likes=" + likes + ", isLiked="
                + isLiked + "]";
    }

}