package imposo.com.application.dto;

import java.util.List;

/**
 * Created by adityaagrawal on 09/10/15.
 */
public class ContactDTO {

    private Integer id;
    private String phoneNumber;
    private String name;
    private String email;
    private String gender;
    private String dob;
    private String occupation;
    private List<String> interests;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    @Override
    public String toString() {
        return "ContactProfileDTO{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", occupation='" + occupation + '\'' +
                ", interests=" + interests +
                '}';
    }
}
