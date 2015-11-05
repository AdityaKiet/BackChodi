package imposo.com.application.myfeeds.comment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.CommentDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myfeeds.CommentDiscActivity;
import imposo.com.application.myfeeds.PostDiscActivity;
import imposo.com.application.myfeeds.data.FeedDTO;
import imposo.com.application.myfeeds.reply.ReplyActivity;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 04/11/15.
 */
public class CommentAdapter extends BaseAdapter implements NetworkConstants {
    private Activity activity;
    private LayoutInflater inflater;
    private ListView listView;
    private List<CommentDTO> feedItems;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private EditText etMessage;
    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private FeedDTO feedDTO;

    public CommentAdapter(Activity activity, FeedDTO feedDTO, ListView  listView) {
        this.activity = activity;
        this.listView = listView;
        this.feedDTO = feedDTO;
        this.feedItems = PostDiscActivity.comments;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (imageLoader == null)
            imageLoader = GlobalData.getInstance().getImageLoader();

            if (convertView == null)
                convertView = inflater.inflate(R.layout.single_row_comment, null);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
            TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
            NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
            FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);

            final CommentDTO item = feedItems.get(position);
            if (item.getIsAnonyomous() == 1)
                name.setText("Anonymous");
            else
                name.setText(item.getCommenterName());

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(item.getCommentTime())));
            } catch (ParseException e) {
                timestamp.setVisibility(View.GONE);
            }
            profilePic.setDefaultImageResId(R.mipmap.logo);
            profilePic.setErrorImageResId(R.mipmap.logo);
            if (item.getIsAnonyomous() == 1)
                profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + "anon.png", imageLoader);
            else
                profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + item.getCommenterId() + ".png", imageLoader);
            statusMsg.setText(item.getComment());
            Linkify.addLinks(statusMsg, Linkify.ALL);
            if(!item.getOption().equals("")){
                url.setVisibility(View.VISIBLE);
                url.setText(item.getOption());
            }else
                url.setVisibility(View.GONE);

            if (!(item.getImageLink().equals(""))) {
                feedImageView.setImageUrl(item.getImageLink(), imageLoader);
                feedImageView.setVisibility(View.VISIBLE);
                feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
            } else {
                feedImageView.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, CommentDiscActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("commentDTO", new Gson().toJson(feedItems.get(position)));
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });

            TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);
            TextView txtShare = (TextView) convertView.findViewById(R.id.txtShare);
            TextView txtLike = (TextView) convertView.findViewById(R.id.txtLike);
            if (item.isLiked()) {
                txtLike.setText(item.getLikes() + " Unlike");
                txtLike.setTextColor(Color.BLUE);
            } else {
                txtLike.setText(item.getLikes() + " Like");
                txtLike.setTextColor(Color.BLACK);
            }
            txtComment.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ReplyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("commentDTO", new Gson().toJson(feedItems.get(position)));
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, REQUEST_GET_MAP_LOCATION);
                }
            });

            txtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkCheck.isNetworkAvailable(activity)) {
                        if (feedItems.get(position).isLiked()) {
                            CommentDTO commentDTO = feedItems.get(position);
                            int likes = commentDTO.getLikes() - 1;
                            UnlikeAsynTask likeAsynTask = new UnlikeAsynTask(activity, commentDTO, likes);
                            likeAsynTask.execute();

                            int index = PostDiscActivity.comments.indexOf(commentDTO);
                            commentDTO.setLikes(likes);
                            commentDTO.setLiked(false);

                            PostDiscActivity.comments.set(index, commentDTO);
                            notifyDataSetChanged();
                        } else {
                            CommentDTO commentDTO = feedItems.get(position);
                            int likes = commentDTO.getLikes() + 1;
                            LikeAsynTask likeAsynTask = new LikeAsynTask(activity, commentDTO, likes);
                            likeAsynTask.execute();

                            int index = PostDiscActivity.comments.indexOf(commentDTO);
                            commentDTO.setLikes(likes);
                            commentDTO.setLiked(true);

                            PostDiscActivity.comments.set(index, commentDTO);
                            notifyDataSetChanged();
                        }
                    }
                }
            });

            txtShare.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (feedItems.get(position).getImageLink().equals("")) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getComment());
                        activity.startActivity(Intent.createChooser(share, "Share Using"));
                    } else {
                            GlobalData.getInstance().getImageLoader().get(item.getImageLink(), new ImageLoader.ImageListener(){
                            public void onErrorResponse(VolleyError arg0) {
                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                                Bitmap mBitmap = response.getBitmap();
                                ContentValues image = new ContentValues();
                                image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                                Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
                                try {
                                    OutputStream out = activity.getContentResolver().openOutputStream(uri);
                                    boolean success = mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.close();
                                    if (!success) {
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("text/plain");
                                        share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getComment());
                                        activity.startActivity(Intent.createChooser(share, "Share Using"));
                                    } else {
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        share.putExtra(Intent.EXTRA_STREAM, uri);
                                        share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getComment());
                                        activity.startActivity(Intent.createChooser(share, "Share Using"));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                    }
                }
            });

        return convertView;
    }
}