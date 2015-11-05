package imposo.com.application.dto;

/**
 * Created by adityaagrawal on 04/11/15.
 */
public class CommentDTO {
    private int commentId;
    private int commenterId;
    private String comment;
    private int isAnonyomous;
    private String commentTime;
    private String imageLink;
    private String commenterName;
    private String option;
    private int likes;
    private int comments;
    private boolean isLiked;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIsAnonyomous() {
        return isAnonyomous;
    }

    public void setIsAnonyomous(int isAnonyomous) {
        this.isAnonyomous = isAnonyomous;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    @Override
    public boolean equals(Object o) {
        if(((CommentDTO)o).getCommentId() == this.getCommentId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.getCommentId();
    }

    @Override
    public String toString() {
        return "CommentDTO [commentId=" + commentId + ", commenterId="
                + commenterId + ", comment=" + comment + ", isAnonyomous="
                + isAnonyomous + ", commentTime=" + commentTime
                + ", imageLink=" + imageLink + ", commenterName="
                + commenterName + ", option=" + option + ", likes=" + likes
                + ", comments=" + comments + ", isLiked=" + isLiked + "]";
    }
}
