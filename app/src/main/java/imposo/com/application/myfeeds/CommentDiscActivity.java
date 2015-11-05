package imposo.com.application.myfeeds;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.CommentDTO;
import imposo.com.application.dto.ReplyDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myfeeds.comment.LikeAsynTask;
import imposo.com.application.myfeeds.comment.UnlikeAsynTask;
import imposo.com.application.myfeeds.reply.ReplyActivity;
import imposo.com.application.myfeeds.reply.ReplyAdapter;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 05/11/15.
 */
public class CommentDiscActivity extends ActionBarActivity implements NetworkConstants, View.OnClickListener, AbsListView.OnScrollListener{
    private CommentDTO commentDTO;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private Toolbar toolbar;
    private TextView name, timestamp, statusMsg, url, txtComment, txtShare, txtLike;
    private LinearLayout llFeedImages;
    private static final String TAG = PostDiscActivity.class.getSimpleName();
    private ListView listView;
    public static List<ReplyDTO> replies;
    public String URL_FEED = GET_NETWORK_IP + "/GetAllCommentReply?lastreplyid=LASTREPLYiD&first=FIRST&userid=ID&commentid=COMMENTiD";
    private int preLast;
    private ReplyAdapter replyAdapter;
    private boolean isDataLoaded = false;
    private int maxId = 0;
    private View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        commentDTO = new Gson().fromJson(bundle.getString("commentDTO"), CommentDTO.class);
    }

    private void populate() {
        setContentView(R.layout.comment_disc_activity);
        replies = new ArrayList<>();
        maxId = 0;
        isDataLoaded = false;
        footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comment_list_header_view, null, false);
        listView = (ListView) findViewById(R.id.listComments);
        listView.addHeaderView(footerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (commentDTO.getIsAnonyomous() == 1)
            getSupportActionBar().setTitle("Anonymous");
        else
            getSupportActionBar().setTitle(commentDTO.getCommenterName());
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llFeedImages = (LinearLayout) footerView.findViewById(R.id.llFeedImages);
        name = (TextView) footerView.findViewById(R.id.name);
        timestamp = (TextView) footerView.findViewById(R.id.timestamp);
        statusMsg = (TextView) footerView.findViewById(R.id.txtStatusMsg);
        url = (TextView) footerView.findViewById(R.id.txtUrl);
        txtComment = (TextView) footerView.findViewById(R.id.txtComment);
        txtShare = (TextView) footerView.findViewById(R.id.txtShare);
        txtLike = (TextView) footerView.findViewById(R.id.txtLike);
        loadData();
        if(!commentDTO.getImageLink().equals(""))
            loadImages();
        listView.setOnScrollListener(this);
        txtComment.setOnClickListener(this);
        txtLike.setOnClickListener(this);
        txtShare.setOnClickListener(this);

    }
    private void loadData() {
        NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.profilePic);
        if (commentDTO.getIsAnonyomous() == 1)
            name.setText("Anonymous");
        else
            name.setText(commentDTO.getCommenterName());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(commentDTO.getCommentTime())));
        } catch (ParseException e) {
            timestamp.setVisibility(View.GONE);
        }
        profilePic.setDefaultImageResId(R.mipmap.logo);
        profilePic.setErrorImageResId(R.mipmap.logo);
        if (commentDTO.getIsAnonyomous() == 1)
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + "anon.png", imageLoader);
        else
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + commentDTO.getCommenterId() +".png", imageLoader);
        statusMsg.setText(commentDTO.getComment());
        url.setText(commentDTO.getOption());
        Linkify.addLinks(statusMsg, Linkify.ALL);


        if (commentDTO.isLiked()) {
            txtLike.setText(commentDTO.getLikes() + " Unlike");
            txtLike.setTextColor(Color.BLUE);
        } else {
            txtLike.setText(commentDTO.getLikes() + " Like");
            txtLike.setTextColor(Color.BLACK);
        }
        replies = new ArrayList<>();
        replyAdapter = new ReplyAdapter(this, commentDTO, listView);
        listView.setAdapter(replyAdapter);
        loadCache();
    }

    private void loadImages() {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            FeedImageView feedImageView = (FeedImageView) layoutInflater.inflate(R.layout.feed_image_view, null, false);
            feedImageView.setImageUrl(commentDTO.getImageLink(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            llFeedImages.addView(feedImageView);
            feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                @Override
                public void onError() {
                }

                @Override
                public void onSuccess() {
                }
            });
            feedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalData.getInstance().getImageLoader().get(commentDTO.getImageLink(), new ImageLoader.ImageListener() {

                        public void onErrorResponse(VolleyError arg0) {
                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                            Bitmap mBitmap = response.getBitmap();
                            ContentValues image = new ContentValues();
                            image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
                            try {
                                OutputStream out = getContentResolver().openOutputStream(uri);
                                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.close();
                                Intent intent = new Intent(CommentDiscActivity.this, FullImageActivity.class);
                                Bundle bundle = new Bundle();
                                if (commentDTO.getIsAnonyomous() == 1)
                                    bundle.putString("name", "Anonymous");
                                else
                                    bundle.putString("name", commentDTO.getCommenterName());
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                intent.putExtra("image", bs.toByteArray());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });

                }
            });

    }

    private void loadCache(){
        Cache cache = GlobalData.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    if(!isDataLoaded){
                    }
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                if(!isDataLoaded){
                }
                e.printStackTrace();
            }
        } else {
            loadJSONFeed();
        }
    }


    private void parseJsonFeed(JSONObject response) {
        List<String> gsonString = new ArrayList<>();
        List<ReplyDTO> replyDTOs = new ArrayList<>();

        try {
            if(response.getInt("success") == 0){

            }else{
                Gson gson = new Gson();
                if(!"false".equals(response.getString("data"))) {
                    gsonString = gson.fromJson(response.getString("data"), List.class);
                }
                for(String s : gsonString){
                    ReplyDTO feedDTO = gson.fromJson(s, ReplyDTO.class);
                    if(!replies.contains(feedDTO)){
                        replyDTOs.add(feedDTO);
                        maxId = feedDTO.getReplyId();
                    }

                }

                replies.addAll(replyDTOs);
                isDataLoaded = true;

            }
            replyAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadJSONFeed(){
        String url = this.URL_FEED;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SessionDTO sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
        url = url.replaceFirst("ID", sessionDTO.getId()+"");
        url = url.replaceFirst("COMMENTiD", commentDTO.getCommentId()+"");
        url = url.replaceFirst("LASTREPLYiD", maxId+"");

        if(isDataLoaded)
            url = url.replaceFirst("FIRST", "0");
        else
            url = url.replaceFirst("FIRST", "1");

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject response) {
                listView.setVisibility(View.VISIBLE);

                if (response != null) {
                    parseJsonFeed(response);
                    Log.d(TAG, response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!isDataLoaded){
                    Log.e(TAG, error.toString());
                    loadJSONFeed();
                }
            }
        }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        GlobalData.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtLike:
                if (NetworkCheck.isNetworkAvailable(this)) {
                    if (commentDTO.isLiked()) {
                        int likes = commentDTO.getLikes() - 1;
                        UnlikeAsynTask likeAsynTask = new UnlikeAsynTask(this, commentDTO, likes);
                        likeAsynTask.execute();
                        txtLike.setText(likes + " Like");
                        txtLike.setTextColor(Color.BLACK);
                        int index = PostDiscActivity.comments.indexOf(commentDTO);
                        commentDTO.setLikes(likes);
                        commentDTO.setLiked(false);

                        PostDiscActivity.comments.set(index, commentDTO);
                    } else {
                        int likes = commentDTO.getLikes() + 1;
                        LikeAsynTask likeAsynTask = new LikeAsynTask(this, commentDTO, likes);
                        likeAsynTask.execute();
                        txtLike.setText(likes + " Unlike");
                        txtLike.setTextColor(Color.BLUE);
                        int index = PostDiscActivity.comments.indexOf(commentDTO);
                        commentDTO.setLikes(likes);
                        commentDTO.setLiked(true);

                        PostDiscActivity.comments.set(index, commentDTO);
                    }
                }
                break;
            case R.id.txtComment:

                Intent intent = new Intent(this, ReplyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("commentDTO", new Gson().toJson(commentDTO));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.txtShare:
                if (commentDTO.getImageLink().equals("")) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, commentDTO.getComment());
                    startActivity(Intent.createChooser(share, "Share Using"));
                } else {
                    GlobalData.getInstance().getImageLoader().get(commentDTO.getImageLink(), new ImageLoader.ImageListener(){
                        public void onErrorResponse(VolleyError arg0) {
                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                            Bitmap mBitmap = response.getBitmap();
                            ContentValues image = new ContentValues();
                            image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
                            try {
                                OutputStream out = getContentResolver().openOutputStream(uri);
                                boolean success = mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.close();
                                if (!success) {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, commentDTO.getComment());
                                    startActivity(Intent.createChooser(share, "Share Using"));
                                } else {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/jpeg");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    share.putExtra(Intent.EXTRA_TEXT, commentDTO.getComment());
                                    startActivity(Intent.createChooser(share, "Share Using"));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                }
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(lastItem == totalItemCount) {
            if(preLast!=lastItem){
                loadJSONFeed();
                SnackbarManager.show(Snackbar.with(getApplicationContext())
                        .text("Loading... Please wait..")
                        .textColor(Color.WHITE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .color(getResources().getColor(R.color.ColorPrimary)), this);
                preLast = lastItem;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }
}
