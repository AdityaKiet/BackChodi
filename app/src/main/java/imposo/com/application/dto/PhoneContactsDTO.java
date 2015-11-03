package imposo.com.application.dto;

/**
 * Created by adityaagrawal on 02/11/15.
 */
public class PhoneContactsDTO {
    private String phoneNumber;
    private String name;

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

    @Override
    public boolean equals(Object obj) {
      /*  if(((ContactDTO)o).getPhoneNumber().equals(this.getPhoneNumber()))
            return true;
        else
            return false;*/

        PhoneContactsDTO other = (PhoneContactsDTO) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }
}
