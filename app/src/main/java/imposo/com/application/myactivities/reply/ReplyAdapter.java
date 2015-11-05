package imposo.com.application.myactivities.reply;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.CommentDTO;
import imposo.com.application.dto.ReplyDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myactivities.CommentDiscActivity;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 05/11/15.
 */
public class ReplyAdapter extends BaseAdapter implements NetworkConstants {
    private Activity activity;
    private LayoutInflater inflater;
    private ListView listView;
    private List<ReplyDTO> feedItems;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private EditText etMessage;
    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private CommentDTO commentDTO;

    public ReplyAdapter(Activity activity, CommentDTO commentDTO, ListView  listView) {
        this.activity = activity;
        this.listView = listView;
        this.commentDTO = commentDTO;
        this.feedItems = CommentDiscActivity.replies;
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
            convertView = inflater.inflate(R.layout.single_row_reply, null);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);

        final ReplyDTO item = feedItems.get(position);
        if (item.getIsAnonyomous() == 1)
            name.setText("Anonymous");
        else
            name.setText(item.getName());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(item.getReplyTime())));
        } catch (ParseException e) {
            timestamp.setVisibility(View.GONE);
        }
        profilePic.setDefaultImageResId(R.mipmap.logo);
        profilePic.setErrorImageResId(R.mipmap.logo);
        if (item.getIsAnonyomous() == 1)
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + "anon.png", imageLoader);
        else
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + item.getUserId() + ".png", imageLoader);
        statusMsg.setText(item.getComment());
        Linkify.addLinks(statusMsg, Linkify.ALL);
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

            }
        });

        TextView txtShare = (TextView) convertView.findViewById(R.id.txtShare);
        TextView txtLike = (TextView) convertView.findViewById(R.id.txtLike);
        if (item.isLiked()) {
            txtLike.setText(item.getLikes() + " Unlike");
            txtLike.setTextColor(Color.BLUE);
        } else {
            txtLike.setText(item.getLikes() + " Like");
            txtLike.setTextColor(Color.BLACK);
        }

        txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkCheck.isNetworkAvailable(activity)) {
                    if (feedItems.get(position).isLiked()) {
                        ReplyDTO commentDTO = feedItems.get(position);
                        int likes = commentDTO.getLikes() - 1;
                        DelikeAsynTask likeAsynTask = new DelikeAsynTask(activity, commentDTO, likes);
                        likeAsynTask.execute();

                        int index = CommentDiscActivity.replies.indexOf(commentDTO);
                        commentDTO.setLikes(likes);
                        commentDTO.setLiked(false);

                        CommentDiscActivity.replies.set(index, commentDTO);
                        notifyDataSetChanged();
                    } else {
                        ReplyDTO commentDTO = feedItems.get(position);
                        int likes = commentDTO.getLikes() + 1;
                        LikeAsynTask likeAsynTask = new LikeAsynTask(activity, commentDTO, likes);
                        likeAsynTask.execute();

                        int index = CommentDiscActivity.replies.indexOf(commentDTO);
                        commentDTO.setLikes(likes);
                        commentDTO.setLiked(true);

                        CommentDiscActivity.replies.set(index, commentDTO);
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