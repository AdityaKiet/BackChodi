package imposo.com.application.otp;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.RegisterDTO;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class SendRegistrationOTPAsynTask extends AsyncTask<Void, Void, Void> implements NetworkConstants{
    private RegisterDTO registerDTO;
    private InputStream is;
    private HttpEntity entity;
    public SendRegistrationOTPAsynTask(RegisterDTO registerDTO){
        this.registerDTO = registerDTO;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
        list.add(new BasicNameValuePair("otp", registerDTO.getOtp()));
        list.add(new BasicNameValuePair("phonenumber", registerDTO.getPhoneNumber()));
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost;
            httpPost = new HttpPost(GET_NETWORK_IP + SEND_OTP_SERVLET);
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            entity = httpResponse.getEntity();
            is = entity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            SendRegistrationOTPAsynTask.this.cancel(true);
        }
        return null;

    }
}
