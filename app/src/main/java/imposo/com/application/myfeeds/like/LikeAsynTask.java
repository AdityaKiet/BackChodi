package imposo.com.application.myfeeds.like;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.nispok.snackbar.SnackbarManager;

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

import imposo.com.application.R;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dashboard.MyFeedFragment;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.myfeeds.data.FeedDTO;

/**
 * Created by adityaagrawal on 04/11/15.
 */

public class LikeAsynTask extends AsyncTask<Void, Void, Void> implements NetworkConstants{
    private Context context;
    private int  likes;
    private FeedDTO feedDTO;
    private InputStream is;
    private HttpEntity entity;
    private String result = "";

    public LikeAsynTask(Context context, FeedDTO feedDTO, int likes){
        this.context = context;
        this.feedDTO = feedDTO;
        this.likes = likes;
    }

    @Override
    protected Void doInBackground(Void... params) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SessionDTO sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);

        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
        list.add(new BasicNameValuePair("userid", sessionDTO.getId()+""));
        list.add(new BasicNameValuePair("postid", feedDTO.getPostId()+""));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_NETWORK_IP + LIKE_SERVLET);
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
            LikeAsynTask.this.cancel(true);
            e.printStackTrace();
            int index = MyFeedFragment.feedItems.indexOf(feedDTO);
            feedDTO.setLikes(likes - 1);
            feedDTO.setLiked(false);
            MyFeedFragment.feedItems.set(index, feedDTO);
            Snackbar.make(((ActionBarActivity) context).getCurrentFocus(), "Some network error has occured.", Snackbar.LENGTH_SHORT).show();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("log", result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("success") == 0){
                LikeAsynTask.this.cancel(true);
                int index = MyFeedFragment.feedItems.indexOf(feedDTO);
                feedDTO.setLikes(likes - 1);
                feedDTO.setLiked(false);
                MyFeedFragment.feedItems.set(index, feedDTO);
                Snackbar.make(((ActionBarActivity) context).getCurrentFocus(), "Some network error has occured.", Snackbar.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            LikeAsynTask.this.cancel(true);
            int index = MyFeedFragment.feedItems.indexOf(feedDTO);
            feedDTO.setLikes(likes - 1);
            feedDTO.setLiked(false);
            MyFeedFragment.feedItems.set(index, feedDTO);
            SnackbarManager.show(com.nispok.snackbar.Snackbar.with(context.getApplicationContext()).text("Some network error has occured")
                    .textColor(Color.WHITE)
                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                    .color(context.getResources().getColor(R.color.black)));

        }
        super.onPostExecute(aVoid);
    }
}
