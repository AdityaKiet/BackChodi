package imposo.com.application.allfeeds;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import imposo.com.application.R;
import imposo.com.application.allfeeds.comment.PostCommentActivity;
import imposo.com.application.allfeeds.data.FeedDTO;
import imposo.com.application.allfeeds.data.ImageDTO;
import imposo.com.application.allfeeds.like.LikeAsynTask;
import imposo.com.application.allfeeds.like.UnLikeAsynTask;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.dashboard.AllFeedsFragment;
import imposo.com.application.global.GlobalData;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 03/11/15.
 */
public class PostDiscActivity extends ActionBarActivity implements View.OnClickListener {
    private FeedDTO feedDTO;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private Toolbar toolbar;
    private TextView name, timestamp, statusMsg, url, txtComment, txtShare, txtLike;
    private LinearLayout llFeedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_disc_activity);
        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        feedDTO = gson.fromJson(bundle.getString("gson"), FeedDTO.class);
        populate();
    }

    private void populate() {
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
        llFeedImages = (LinearLayout) findViewById(R.id.llFeedImages);
        name = (TextView) findViewById(R.id.name);
        timestamp = (TextView) findViewById(R.id.timestamp);
        statusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        url = (TextView) findViewById(R.id.txtUrl);
        txtComment = (TextView) findViewById(R.id.txtComment);
        txtShare = (TextView) findViewById(R.id.txtShare);
        txtLike = (TextView) findViewById(R.id.txtLike);
        loadData();
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
        if (feedDTO.getIsAnonyomous() == 1)
            profilePic.setImageUrl("http://techstory.in/wp-content/uploads/2015/02/dhoni1.jpg", imageLoader);
        else
            profilePic.setImageUrl("http://www.hdwallpaper4u.com/wp-content/uploads/2015/06/virat_kohli_is_most_loved_boy_of_indian_team-203x126.jpg", imageLoader);
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
        loadImages();
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
                        int index = AllFeedsFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(false);
                        AllFeedsFragment.feedItems.set(index, feedDTO);
                    } else {
                        int likes = feedDTO.getLikes() +1;
                        LikeAsynTask likeAsynTask = new LikeAsynTask(this, feedDTO, likes);
                        likeAsynTask.execute();
                        txtLike.setText(likes + " Unlike");
                        txtLike.setTextColor(Color.BLUE);
                        int index = AllFeedsFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(true);
                        AllFeedsFragment.feedItems.set(index, feedDTO);
                    }
                }else{
                    SnackbarManager.show(com.nispok.snackbar.Snackbar.with(getApplicationContext())
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

}
