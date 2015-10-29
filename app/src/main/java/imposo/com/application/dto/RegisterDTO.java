package imposo.com.application.dto;

public class RegisterDTO {
	private String name;
	private String password;
	private String email;
	private String countryCode;
	private String phoneNumber;
	private String otp;
	private Integer id;
	private String gender;
	private Boolean isPromoCodeSetup;
	private Boolean isNew;
	private String occupation;
	private String dob;
	private String gcmId;
	private String city;
	private String country;

	public Boolean getIsPromoCodeSetup() {
		return isPromoCodeSetup;
	}

	public void setIsPromoCodeSetup(Boolean isPromoCodeSetup) {
		this.isPromoCodeSetup = isPromoCodeSetup;
	}

	public String getGcmId() {
		return gcmId;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	@Override
	public String toString() {
		return "RegisterDTO [name=" + name + ", password=" + password
				+ ", email=" + email + ", countryCode=" + countryCode
				+ ", phoneNumber=" + phoneNumber + ", otp=" + otp + ", id="
				+ id + ", gender=" + gender + ", isNew=" + isNew
				+ ", occupation=" + occupation + ", dob=" + dob + ", city="
				+ city + ", country=" + country + "]";
	}

}
