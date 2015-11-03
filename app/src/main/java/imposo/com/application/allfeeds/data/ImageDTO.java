package imposo.com.application.allfeeds.data;

/**
 * Created by adityaagrawal on 03/11/15.
 */
public class ImageDTO {
    private String imageLink;
    private int imageId;

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public int hashCode() {
        return this.getImageId();
    }

    @Override
    public boolean equals(Object obj) {
        if (((ImageDTO) obj).getImageId() == this.getImageId())
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "ImageDTO [imageLink=" + imageLink + ", imageId=" + imageId
                + "]";
    }

}