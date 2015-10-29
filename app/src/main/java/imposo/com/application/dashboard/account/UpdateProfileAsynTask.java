package imposo.com.application.dashboard.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class UpdateProfileAsynTask extends AsyncTask<Void, Void, Void> implements NetworkConstants {

    private Context context;
    private InputStream is;
    private HttpEntity entity;
    private String result = "";
    private String sql1,sql2;
    private MaterialDialog.Builder progressDialogBuilder;
    private MaterialDialog progressDialog;
    private SessionDTO sessionDTO;

    public UpdateProfileAsynTask(Context context, SessionDTO sessionDTO) {
        this.context = context;
        this.sessionDTO = sessionDTO;
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
        list.add(new BasicNameValuePair("name", sessionDTO.getName()));
        list.add(new BasicNameValuePair("email", sessionDTO.getEmail()));
        list.add(new BasicNameValuePair("occupation", sessionDTO.getOccupation()));
        list.add(new BasicNameValuePair("dob", sessionDTO.getDob()));
        list.add(new BasicNameValuePair("gender", sessionDTO.getGender()));
        list.add(new BasicNameValuePair("id",sessionDTO.getId() + ""));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_NETWORK_IP + UPDATE_USER_INFO_SERVLET);
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
            UpdateProfileAsynTask.this.cancel(true);
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
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("success") == 0){
                UpdateProfileAsynTask.this.cancel(true);
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Network Error");
                messageCustomDialogDTO.setButton("OK");
                messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                messageCustomDialogDTO.setContext(context);
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else{
                if(jsonObject.getString("data").equals("true")){
                    UpdateProfileAsynTask.this.cancel(true);
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Success");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage("Profile Updated !!!");
                    messageCustomDialogDTO.setContext(context);
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                    SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                    sharedEditor.putString("session", new Gson().toJson(sessionDTO));
                    sharedEditor.commit();
                }else{
                    UpdateProfileAsynTask.this.cancel(true);
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Error");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage("Information has not been updated. Please try again.");
                    messageCustomDialogDTO.setContext(context);
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
            }
        }catch (Exception e){
            UpdateProfileAsynTask.this.cancel(true);
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle("Network Error");
            messageCustomDialogDTO.setButton("OK");
            messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
            messageCustomDialogDTO.setContext(context);
            MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
            messageCustomDialog.show();
        }
    }
}
