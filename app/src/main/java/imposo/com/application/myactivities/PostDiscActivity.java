package imposo.com.application.myactivities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.data.FeedDTO;
import imposo.com.application.allfeeds.data.ImageDTO;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dashboard.AllFeedsFragment;
import imposo.com.application.dashboard.MyAnswersFragment;
import imposo.com.application.dashboard.MyFeedFragment;
import imposo.com.application.dto.CommentDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myactivities.comment.CommentAdapter;
import imposo.com.application.myactivities.comment.PostCommentActivity;
import imposo.com.application.myactivities.like.LikeAsynTask;
import imposo.com.application.myactivities.like.UnLikeAsynTask;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 03/11/15.
 */
public class PostDiscActivity extends ActionBarActivity implements View.OnClickListener , NetworkConstants, AbsListView.OnScrollListener{
    private FeedDTO feedDTO;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private Toolbar toolbar;
    private TextView name, timestamp, statusMsg, url, txtComment, txtShare, txtLike;
    private LinearLayout llFeedImages;
    private static final String TAG = PostDiscActivity.class.getSimpleName();
    private ListView listView;
    public static String URL_FEED = GET_NETWORK_IP + "/GetAllComment?postid=POSTiD&first=FIRST&userid=ID&lastcommentid=LASTCOMMENTID";
    private int preLast;
    public static List<CommentDTO> comments;
    private boolean isDataLoaded = false;
    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private int maxId = 0;
    private View footerView;
    private SessionDTO sessionDTO;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_disc_activity);
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        feedDTO = gson.fromJson(bundle.getString("gson"), FeedDTO.class);
        populate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        invokeFragmentManagerNoteStateNotSaved();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void invokeFragmentManagerNoteStateNotSaved() {
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }
        try {
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!"Activity".equals(cls.getSimpleName()));
            Field fragmentMgrField = cls.getDeclaredField("mFragments");
            fragmentMgrField.setAccessible(true);

            Object fragmentMgr = fragmentMgrField.get(this);
            cls = fragmentMgr.getClass();

            Method noteStateNotSavedMethod = cls.getDeclaredMethod("noteStateNotSaved", new Class[] {});
            noteStateNotSavedMethod.invoke(fragmentMgr, new Object[] {});
            Log.d("DLOutState", "Successful call for noteStateNotSaved!!!");
        } catch (Exception ex) {
            Log.e("DLOutState", "Exception on worka FM.noteStateNotSaved", ex);
        }
    }
    private void populate() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sessionDTO = new Gson().fromJson(sharedPreferences.getString("session",null), SessionDTO.class);
        footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header_view, null, false);
        listView = (ListView) findViewById(R.id.listComments);

        listView.addHeaderView(footerView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (feedDTO.getIsAnonyomous() == 1)
            getSupportActionBar().setTitle("Anonymous");
        else
            getSupportActionBar().setTitle(feedDTO.getPostCreaterName());
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

        listView.setOnScrollListener(this);
        txtComment.setOnClickListener(this);
        txtLike.setOnClickListener(this);
        txtShare.setOnClickListener(this);

    }

    private void loadData() {
        NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.profilePic);
        if (feedDTO.getIsAnonyomous() == 1)
            name.setText("Anonymous");
        else
            name.setText(feedDTO.getPostCreaterName());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(feedDTO.getPostTime())));
        } catch (ParseException e) {
            timestamp.setVisibility(View.GONE);
        }
        profilePic.setDefaultImageResId(R.mipmap.logo);
        profilePic.setErrorImageResId(R.mipmap.logo);
        if (feedDTO.getIsAnonyomous() == 1)
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + "anon.png", imageLoader);
        else
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + feedDTO.getPostCreaterId() +".png", imageLoader);
        statusMsg.setText(feedDTO.getPostText());
        url.setText(feedDTO.getPostTitle());
        Linkify.addLinks(statusMsg, Linkify.ALL);


        if (feedDTO.isLiked()) {
            txtLike.setText(feedDTO.getLikes() + " Unlike");
            txtLike.setTextColor(Color.BLUE);
        } else {
            txtLike.setText(feedDTO.getLikes() + " Like");
            txtLike.setTextColor(Color.BLACK);
        }
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(PostDiscActivity.this, feedDTO, listView);
        listView.setAdapter(commentAdapter);
        loadImages();
        loadCache();
    }

    private void loadImages() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final ImageDTO imageDTO : feedDTO.getImages()) {
            FeedImageView feedImageView = (FeedImageView) layoutInflater.inflate(R.layout.feed_image_view, null, false);
            feedImageView.setImageUrl(imageDTO.getImageLink(), imageLoader);
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
                    GlobalData.getInstance().getImageLoader().get(imageDTO.getImageLink(), new ImageLoader.ImageListener() {

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
                                Intent intent = new Intent(PostDiscActivity.this, FullImageActivity.class);
                                Bundle bundle = new Bundle();
                                if (feedDTO.getIsAnonyomous() == 1)
                                    bundle.putString("name", "Anonymous");
                                else
                                    bundle.putString("name", feedDTO.getPostCreaterName());
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtShare:
                if(feedDTO.getImages().size() == 0){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, feedDTO.getPostTitle() + "\n" + feedDTO.getPostText());
                    startActivity(Intent.createChooser(share, "Share Using"));
                }else{
                    GlobalData.getInstance().getImageLoader().get(feedDTO.getImages().get(0).getImageLink(), new ImageLoader.ImageListener() {

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
                                    share.putExtra(Intent.EXTRA_TEXT, feedDTO.getPostTitle() + "\n" + feedDTO.getPostText());
                                    startActivity(Intent.createChooser(share, "Share Using"));
                                } else {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/jpeg");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    share.putExtra(Intent.EXTRA_TEXT, feedDTO.getPostTitle() + "\n" + feedDTO.getPostText());
                                    startActivity(Intent.createChooser(share, "Share Using"));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                }

                break;
            case R.id.txtLike:
                if(NetworkCheck.isNetworkAvailable(this)) {
                    if (feedDTO.isLiked()) {
                        int likes = feedDTO.getLikes() - 1;
                        UnLikeAsynTask likeAsynTask = new UnLikeAsynTask(this, feedDTO, likes);
                        likeAsynTask.execute();
                        txtLike.setText(likes + " Like");
                        txtLike.setTextColor(Color.BLACK);
                        int index = MyAnswersFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(false);
                        MyAnswersFragment.feedItems.set(index, feedDTO);

                        if(AllFeedsFragment.feedItems.contains(feedDTO)){
                            int index1 = AllFeedsFragment.feedItems.indexOf(feedDTO);
                            if(index1 != -1) {
                                AllFeedsFragment.feedItems.set(index1, feedDTO);
                                AllFeedsFragment.listAdapter.notifyDataSetChanged();
                            }
                        }
                        if(MyFeedFragment.feedItems.contains(feedDTO)){
                            int index1 = MyFeedFragment.feedItems.indexOf(feedDTO);
                            if(index1 != -1) {
                                MyFeedFragment.feedItems.set(index1, feedDTO);
                                MyFeedFragment.listAdapter.notifyDataSetChanged();
                            }
                        }

                    } else {
                        int likes = feedDTO.getLikes() +1;
                        LikeAsynTask likeAsynTask = new LikeAsynTask(this, feedDTO, likes);
                        likeAsynTask.execute();
                        txtLike.setText(likes + " Unlike");
                        txtLike.setTextColor(Color.BLUE);
                        int index = MyAnswersFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(true);
                        MyAnswersFragment.feedItems.set(index, feedDTO);

                        if(AllFeedsFragment.feedItems.contains(feedDTO)){
                            int index1 = AllFeedsFragment.feedItems.indexOf(feedDTO);
                            if(index1 != -1) {
                                AllFeedsFragment.feedItems.set(index1, feedDTO);
                                AllFeedsFragment.listAdapter.notifyDataSetChanged();
                            }
                        }
                        if(MyFeedFragment.feedItems.contains(feedDTO)){
                            int index1 = MyFeedFragment.feedItems.indexOf(feedDTO);
                            if(index1 != -1) {
                                MyFeedFragment.feedItems.set(index1, feedDTO);
                                MyFeedFragment.listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }else{
                    SnackbarManager.show(Snackbar.with(getApplicationContext())
                            .text("Network not available.")
                            .textColor(Color.WHITE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                            .color(getResources().getColor(R.color.black)), this);
                }
                break;
            case R.id.txtComment:
                Bundle bundle = new Bundle();
                bundle.putString("gson", new Gson().toJson(feedDTO));
                Intent intent = new Intent(this, PostCommentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
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
        List<CommentDTO> feedDTOs = new ArrayList<>();

        try {
            if(response.getInt("success") == 0){

            }else{
                Gson gson = new Gson();
                if(!"false".equals(response.getString("data"))) {
                    gsonString = gson.fromJson(response.getString("data"), List.class);
                }
                for(String s : gsonString){
                    CommentDTO feedDTO = gson.fromJson(s, CommentDTO.class);
                    if(!comments.contains(feedDTO)){
                        feedDTOs.add(feedDTO);
                        maxId = feedDTO.getCommentId();
                    }

                }

                comments.addAll(feedDTOs);
                isDataLoaded = true;

            }
            commentAdapter.notifyDataSetChanged();
        //    Helper.setListViewHeightBasedOnChildren(listView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadJSONFeed(){
        String url = this.URL_FEED;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SessionDTO sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
        url = url.replaceFirst("ID", sessionDTO.getId()+"");
        url = url.replaceFirst("POSTiD", feedDTO.getPostId()+"");
        url = url.replaceFirst("LASTCOMMENTID", maxId+"");

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == Activity.RESULT_OK) {
            int success = data.getIntExtra("success", 0);
            if(success == 1){
                SnackbarManager.show(Snackbar.with(getApplicationContext())
                        .text("Reply submitted !!!!")
                        .textColor(Color.WHITE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .color(getResources().getColor(R.color.ColorPrimary)), this);
            }
        }
    }

    @Override
    public void onResume() {
        try{
            if(commentAdapter != null){
                commentAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }
}
