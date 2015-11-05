package imposo.com.application.myfeeds.data;

import java.util.List;


public class FeedDTO {
    private int postCreaterId;
    private String postCreaterName;
    private String postText;
    private int postId;
    private List<OptionDTO> options;
    private List<ImageDTO> images;
    private int isAnonyomous;
    private int likes;
    private String postTitle;
    private String postTime;
    private boolean isLiked;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public int getPostCreaterId() {
        return postCreaterId;
    }

    public void setPostCreaterId(int postCreaterId) {
        this.postCreaterId = postCreaterId;
    }

    public String getPostCreaterName() {
        return postCreaterName;
    }

    public void setPostCreaterName(String postCreaterName) {
        this.postCreaterName = postCreaterName;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }

    public List<ImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    public int getIsAnonyomous() {
        return isAnonyomous;
    }

    public void setIsAnonyomous(int isAnonyomous) {
        this.isAnonyomous = isAnonyomous;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (((FeedDTO) obj).getPostId() == this.getPostId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.getPostId();
    }

    @Override
    public String toString() {
        return "FeedDTO [postCreaterId=" + postCreaterId + ", postCreaterName="
                + postCreaterName + ", postText=" + postText + ", postId="
                + postId + ", options=" + options + ", images=" + images
                + ", isAnonyomous=" + isAnonyomous + ", likes=" + likes
                + ", postTitle=" + postTitle + ", postTime=" + postTime
                + ", isLiked=" + isLiked + "]";
    }
}


