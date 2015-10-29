package imposo.com.application.dto;

/**
 * Created by adityaagrawal on 25/10/15.
 */

public class SessionDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private Integer id;
    private String gender;
    private String occupation;
    private String gcm;
    private String dob;
    private String city;
    private Boolean isContactsSetUp;
    private String country;
    private Boolean idDbSetup;

    public Boolean getIsContactsSetUp() {
        return isContactsSetUp;
    }

    public void setIsContactsSetUp(Boolean isContactsSetUp) {
        this.isContactsSetUp = isContactsSetUp;
    }


    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public Boolean getIdDbSetup() {
        return idDbSetup;
    }

    public void setIdDbSetup(Boolean idDbSetup) {
        this.idDbSetup = idDbSetup;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id=" + id +
                ", gender='" + gender + '\'' +
                ", occupation='" + occupation + '\'' +
                ", gcm='" + gcm + '\'' +
                ", dob='" + dob + '\'' +
                ", city='" + city + '\'' +
                ", isContactsSetUp=" + isContactsSetUp +
                ", country='" + country + '\'' +
                ", idDbSetup=" + idDbSetup +
                '}';
    }
}
