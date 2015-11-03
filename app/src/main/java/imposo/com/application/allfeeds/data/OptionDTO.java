package imposo.com.application.allfeeds.data;

/**
 * Created by adityaagrawal on 03/11/15.
 */
public class OptionDTO {
    private String option;
    private int optionId;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    @Override
    public int hashCode() {
        return this.getOptionId();
    }

    @Override
    public boolean equals(Object obj) {
        if (((OptionDTO) obj).getOptionId() == this.getOptionId())
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "OptionDTO [option=" + option + ", optionId=" + optionId + "]";
    }

}