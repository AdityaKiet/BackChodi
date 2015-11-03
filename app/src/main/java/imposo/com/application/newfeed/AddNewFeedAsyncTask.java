package imposo.com.application.newfeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dashboard.DashboardActivity;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 01/11/15.
 */
public class AddNewFeedAsyncTask extends AsyncTask<Void, Void, Void> implements NetworkConstants {

    private Context context;
    private InputStream is;
    private HttpEntity entity;
    private String result = "";
    private String title, question;
    private MaterialDialog.Builder progressDialogBuilder;
    private MaterialDialog progressDialog;
    private RegisterDTO registerDTO;
    private SessionDTO sessionDTO;
    private List<String> contacts;
    private boolean isAnonmyous, isPublic;
    private List<String> options;
    private List<Bitmap> bitmaps;
    private List<String> bitmapString = new ArrayList<>();

    public AddNewFeedAsyncTask(Context context, String title, String question, List<String> contacts, boolean isAnonmyous, boolean isPublic) {
        this.context = context;
        this.title  = title;
        this.question = question;
        this.contacts = contacts;
        this.isAnonmyous =  isAnonmyous;
        this.isPublic = isPublic;
        options = AddNewFeed.options;
        bitmaps = AddNewFeed.bitmaps;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
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
        for(Bitmap bitmap : bitmaps){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            String encodedString = Base64.encodeToString(byte_arr, 0);
            bitmapString.add(encodedString);
        }

        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
        list.add(new BasicNameValuePair("title", title));
        list.add(new BasicNameValuePair("bitmaps", new Gson().toJson(bitmapString)));
        list.add(new BasicNameValuePair("options", new Gson().toJson(options)));
        list.add(new BasicNameValuePair("question", question));
        list.add(new BasicNameValuePair("id", sessionDTO.getId() + ""));
        list.add(new BasicNameValuePair("isPublic" ,isPublic+""));
        list.add(new BasicNameValuePair("contacts", new Gson().toJson(contacts)));
        list.add(new BasicNameValuePair("isAnonmyous", isAnonmyous + ""));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_NETWORK_IP + INSERT_NEW_FEED);
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
            AddNewFeedAsyncTask.this.cancel(true);
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
            if(jsonObject.getInt("success") == 1 && jsonObject.getString("data").equals("true")){
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle bundle = new Bundle();
                bundle.putInt("success", 1);
                intent.putExtras(bundle);
                context.startActivity(intent);
                AddNewFeed.options = new ArrayList<>();
                AddNewFeed.bitmaps = new ArrayList<>();
                AddNewFeed.imageNames = new ArrayList<>();

            }else{
                AddNewFeedAsyncTask.this.cancel(true);
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Network Error");
                messageCustomDialogDTO.setButton("OK");
                messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                messageCustomDialogDTO.setContext(context);
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
            AddNewFeedAsyncTask.this.cancel(true);
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
