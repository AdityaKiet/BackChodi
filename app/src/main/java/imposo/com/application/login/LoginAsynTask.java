package imposo.com.application.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

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
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class LoginAsynTask extends AsyncTask<Void, Void, Void> implements NetworkConstants{

    private Context context;
    private InputStream is;
    private HttpEntity entity;
    private String result = "";
    private MaterialDialog.Builder progressDialogBuilder;
    private MaterialDialog progressDialog;
    private RegisterDTO registerDTO;

    public LoginAsynTask(Context context, RegisterDTO registerDTO) {
        this.context = context;
        this.registerDTO = registerDTO;
    }

    @Override
    protected void onPreExecute() {
        progressDialogBuilder = new MaterialDialog.Builder(context);
        progressDialogBuilder.content("Please Wait.....");
        progressDialogBuilder.cancelable(false);
        progressDialogBuilder.progress(true, 0);
        progressDialog = progressDialogBuilder.show();

    }

    @Override
    protected Void doInBackground(Void... params) {

        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
        list.add(new BasicNameValuePair("phonenumber", registerDTO.getPhoneNumber()));
        list.add(new BasicNameValuePair("otp", registerDTO.getOtp()));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_NETWORK_IP + LOGIN_SERVLET);
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
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            LoginAsynTask.this.cancel(true);
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle("Network Error");
            messageCustomDialogDTO.setButton("OK");
            messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
            messageCustomDialogDTO.setContext(context);
            MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
            messageCustomDialog.show();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("log", result);
        progressDialog.dismiss();
        try{
            JSONDecode jsonDecode = new JSONDecode();
            jsonDecode.decode(context, result, registerDTO);
        }catch (Exception e){
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle("Network Error");
            messageCustomDialogDTO.setButton("OK");
            messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
            messageCustomDialogDTO.setContext(context);
            MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
            messageCustomDialog.show();
        }
        super.onPostExecute(aVoid);
    }
}
