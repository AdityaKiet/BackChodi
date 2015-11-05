package imposo.com.application.myactivities.adapter;

import android.app.Activity;
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
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import imposo.com.application.allfeeds.data.FeedDTO;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dashboard.AllFeedsFragment;
import imposo.com.application.dashboard.MyAnswersFragment;
import imposo.com.application.dashboard.MyFeedFragment;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myactivities.PostDiscActivity;
import imposo.com.application.myactivities.comment.PostCommentActivity;
import imposo.com.application.myactivities.like.LikeAsynTask;
import imposo.com.application.myactivities.like.UnLikeAsynTask;
import imposo.com.application.util.NetworkCheck;

public class FeedListAdapter extends BaseAdapter implements NetworkConstants {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedDTO> feedItems;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private SessionDTO sessionDTO;

	public FeedListAdapter(Activity activity) {
        this.activity = activity;
        this.feedItems = MyAnswersFragment.feedItems;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
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
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);
        if (imageLoader == null)
            imageLoader = GlobalData.getInstance().getImageLoader();
 
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView.findViewById(R.id.feedImage1);
 
        FeedDTO item = feedItems.get(position);
        if(item.getIsAnonyomous() == 1)
            name.setText("Anonymous");
        else
            name.setText(item.getPostCreaterName());
        
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(item.getPostTime())));
		} catch (ParseException e) {
			timestamp.setVisibility(View.GONE);
		}
        profilePic.setDefaultImageResId(R.mipmap.logo);
        profilePic.setErrorImageResId(R.mipmap.logo);
        if(item.getIsAnonyomous() == 1)
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + "anon.png", imageLoader);
        else
            profilePic.setImageUrl(GET_IMAGE_NETWORK_IP + item.getPostCreaterId() +".png", imageLoader);
        statusMsg.setText(item.getPostText());
        url.setText(item.getPostTitle());
        Linkify.addLinks(statusMsg, Linkify.ALL);
 
        if (!(item.getImages().size() == 0)) {
            feedImageView.setImageUrl(item.getImages().get(0).getImageLink(), imageLoader);
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
        convertView.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
                Intent intent = new Intent(activity, PostDiscActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("gson", new Gson().toJson(feedItems.get(position)));
                intent.putExtras(bundle);
                activity.startActivity(intent);
        	}
        });
        
        TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);
        TextView txtShare = (TextView) convertView.findViewById(R.id.txtShare);
        TextView txtLike = (TextView) convertView.findViewById(R.id.txtLike);
        if(item.isLiked()){
            txtLike.setText(item.getLikes() + " Unlike");
            txtLike.setTextColor(Color.BLUE);
        }
        else {
            txtLike.setText(item.getLikes() + " Like");
            txtLike.setTextColor(Color.BLACK);
        }
        txtComment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("gson", new Gson().toJson(feedItems.get(position)));
                Intent intent = new Intent(activity, PostCommentActivity.class);
                intent.putExtras(bundle);
               activity.startActivity(intent);
			}
		});

        txtLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(NetworkCheck.isNetworkAvailable(activity)) {
                    if (feedItems.get(position).isLiked()) {
                        FeedDTO feedDTO = feedItems.get(position);
                        int likes = feedDTO.getLikes() - 1;
                        UnLikeAsynTask likeAsynTask = new UnLikeAsynTask(activity, feedDTO, likes);
                        likeAsynTask.execute();
                        int index = MyAnswersFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(false);
                        MyAnswersFragment.feedItems.set(index, feedDTO);
                        notifyDataSetChanged();

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
                        FeedDTO feedDTO = feedItems.get(position);
                        int likes = feedDTO.getLikes() + 1;
                        LikeAsynTask likeAsynTask = new LikeAsynTask(activity, feedDTO, likes);
                        likeAsynTask.execute();
                        int index = MyAnswersFragment.feedItems.indexOf(feedDTO);
                        feedDTO.setLikes(likes);
                        feedDTO.setLiked(true);
                        MyAnswersFragment.feedItems.set(index, feedDTO);
                        notifyDataSetChanged();

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

                }

			}
		});
        
        txtShare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                if(feedItems.get(position).getImages().size() == 0){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getPostTitle() + "\n" + feedItems.get(position).getPostText());
                    activity.startActivity(Intent.createChooser(share, "Share Using"));
                }else{
                    GlobalData.getInstance().getImageLoader().get(feedItems.get(position).getImages().get(0).getImageLink(), new ImageLoader.ImageListener() {

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
                                    share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getPostTitle() + "\n" + feedItems.get(position).getPostText());
                                    activity.startActivity(Intent.createChooser(share, "Share Using"));
                                } else {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/jpeg");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getPostTitle() + "\n" + feedItems.get(position).getPostText());
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