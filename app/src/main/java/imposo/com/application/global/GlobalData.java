package imposo.com.application.global;

import android.app.Application;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class GlobalData extends Application {
    private String otp;
    private String gcmID;

    public String getGcmID() {
        return gcmID;
    }

    public void setGcmID(String gcmID) {
        this.gcmID = gcmID;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
