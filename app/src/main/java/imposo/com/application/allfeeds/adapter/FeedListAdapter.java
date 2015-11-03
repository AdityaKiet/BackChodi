package imposo.com.application.allfeeds.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.data.FeedItem;
import imposo.com.application.allfeeds.volley.FeedImageView;
import imposo.com.application.global.GlobalData;

public class FeedListAdapter extends BaseAdapter {  
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private ImageLoader imageLoader = GlobalData.getInstance().getImageLoader();
    private EditText etMessage;

	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        feedItems =  new ArrayList<FeedItem>(new LinkedHashSet<FeedItem>(feedItems));
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
 
        FeedItem item = feedItems.get(position);
        name.setText(item.getName());
        
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			timestamp.setText(new SimpleDateFormat("dd MMM, yyyy").format(dateFormat.parse(item.getTimeStamp())));
		} catch (ParseException e) {
			timestamp.setVisibility(View.GONE);
		}

        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            statusMsg.setVisibility(View.GONE);
        }

        if (!item.getUrl().equals("")) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">" + item.getUrl() + "</a> "));
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            url.setVisibility(View.GONE);
        }
 
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);
 
        if (!item.getImge().equals("")) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
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

        	}
        });
        
        TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);
        TextView txtShare = (TextView) convertView.findViewById(R.id.txtShare);
        TextView txtMore = (TextView) convertView.findViewById(R.id.txtMore);
        txtComment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
        
        txtMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
        
        txtShare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(feedItems.get(position).getImge().equals("")){
					Intent share = new Intent(Intent.ACTION_SEND);
            		share.setType("text/plain");
            		share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getStatus());
            		activity.startActivity(Intent.createChooser(share, "Share Using"));
				}else{
				GlobalData.getInstance().getImageLoader().get(feedItems.get(position).getImge(), new ImageListener() {
					
					public void onErrorResponse(VolleyError arg0) {
					}
					
					@Override
					public void onResponse(ImageContainer response, boolean arg1) {
						 Bitmap mBitmap = response.getBitmap();
				            ContentValues image = new ContentValues();
				                image.put(Media.MIME_TYPE, "image/jpg");
				                Uri uri = activity.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, image);
				                try {
				                    OutputStream out = activity.getContentResolver().openOutputStream(uri);
				                    boolean success = mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				                    out.close();
				                    if (!success) {
				                    	Intent share = new Intent(Intent.ACTION_SEND);
				                		share.setType("text/plain");
				                		share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getStatus());
				                		activity.startActivity(Intent.createChooser(share, "Share Using"));
				                    } else {
				                    	Intent share = new Intent(Intent.ACTION_SEND);
				                		share.setType("image/jpeg");
				                		share.putExtra(Intent.EXTRA_STREAM, uri);
				                		share.putExtra(Intent.EXTRA_TEXT, feedItems.get(position).getStatus());
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