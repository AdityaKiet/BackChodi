package imposo.com.application.constants;

public interface NetworkConstants {
	
	public final String GET_NETWORK_IP = "http://192.168.51.1:8080/DiscussionApp";

	public final String LOGIN_SERVLET = "/Login";
	public final String SEND_OTP_SERVLET = "/SendOTP";
	public final String CONFIRM_OTP_SERVLET ="/ConfirmOTP";
	public final String UPDATE_GCM_ID_SERVLET = "/UpdateGCMID";
    public final String UPDATE_USER_INFO_SERVLET = "/UpdateUserInfo";

	public final String GOOGLE_PROJ_ID = "561135762715";
	public final String APP_STRING_URL = "/gcm/gcm.php?shareRegId=true";
	public final String MSG_KEY = "m";
}
