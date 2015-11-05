package imposo.com.application.allfeeds.comment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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

import imposo.com.application.allfeeds.PostDiscActivity;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.CommentDTO;
import imposo.com.application.dto.SessionDTO;

/**
 * Created by adityaagrawal on 04/11/15.
 */
public class UnlikeAsynTask extends AsyncTask<Void, Void, Void> implements NetworkConstants {
    private Context context;
    private int  likes;
    private CommentDTO commentDTO;
    private InputStream is;
    private HttpEntity entity;
    private String result = "";

    public UnlikeAsynTask(Context context, CommentDTO commentDTO, int likes){
        this.context = context;
        this.commentDTO = commentDTO;
        this.likes = likes;
    }

    @Override
    protected Void doInBackground(Void... params) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SessionDTO sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);

        List<NameValuePair> list = new ArrayList<NameValuePair>(1);
        list.add(new BasicNameValuePair("userid", sessionDTO.getId()+""));
        list.add(new BasicNameValuePair("commentid", commentDTO.getCommentId() + ""));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_NETWORK_IP + UNLIKE_COMMENT);
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
            UnlikeAsynTask.this.cancel(true);
            e.printStackTrace();
            int index = PostDiscActivity.comments.indexOf(commentDTO);
            commentDTO.setLikes(likes + 1);
            commentDTO.setLiked(true);
            PostDiscActivity.comments.set(index, commentDTO);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("log", result);
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("success") == 0){
                UnlikeAsynTask.this.cancel(true);
                int index = PostDiscActivity.comments.indexOf(commentDTO);
                commentDTO.setLikes(likes + 1);
                commentDTO.setLiked(true);
                PostDiscActivity.comments.set(index, commentDTO);
            }
        }catch (Exception e){
            UnlikeAsynTask.this.cancel(true);
            int index = PostDiscActivity.comments.indexOf(commentDTO);
            commentDTO.setLikes(likes + 1);
            commentDTO.setLiked(true);
            PostDiscActivity.comments.set(index, commentDTO);

        }
        super.onPostExecute(aVoid);
    }
}
