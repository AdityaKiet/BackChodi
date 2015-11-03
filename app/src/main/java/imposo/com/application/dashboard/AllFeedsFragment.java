package imposo.com.application.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.allfeeds.adapter.FeedListAdapter;
import imposo.com.application.allfeeds.data.FeedItem;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class AllFeedsFragment extends Fragment implements View.OnClickListener, NetworkConstants, AbsListView.OnScrollListener{
    private View view;
    private TextView txtError;
    private static final String TAG = AllFeedsFragment.class.getSimpleName();
    private ImageView imgError;
    private ProgressWheel progressWheel;
    private String URL_FEED = GET_NETWORK_IP + "/GetAllFeed?postid=POSTID&first=FIRST&id=ID";
    private ListView listView;
    private List<FeedItem> feedItems;
    private FeedListAdapter listAdapter;
    private int preLast;
    private boolean isDataLoaded = false;
    private int maxId = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =(RelativeLayout) inflater.inflate(R.layout.all_feeds_activity,container,false);
        populate();
        return view;
    }

    private void populate(){
        listView = (ListView) view.findViewById(R.id.listAllFeeds);
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        imgError = (ImageView) view.findViewById(R.id.imgError);
        txtError = (TextView) view.findViewById(R.id.txtErrorMessage);
        imgError.setOnClickListener(this);
        txtError.setOnClickListener(this);
        populateData();
    }

    private void populateData(){
        txtError.setVisibility(View.GONE);
        imgError.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        loadCache();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtErrorMessage:
            case R.id.imgError:

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
                    progressWheel.setVisibility(View.GONE);
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    if(!isDataLoaded){
                        txtError.setVisibility(View.VISIBLE);
                        imgError.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                if(!isDataLoaded){
                    txtError.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.VISIBLE);
                }
                e.printStackTrace();
            }
        } else {
            loadJSONFeed();
        }
    }

    private void loadJSONFeed(){

        String url = this.URL_FEED;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SessionDTO sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
        url = url.replaceFirst("ID", sessionDTO.getId()+"");
        url = url.replaceFirst("POSTID", String.valueOf(maxId));

        if(isDataLoaded)
            url = url.replaceFirst("FIRST", "1");
        else
            url = url.replaceFirst("FIRST", "0");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject response) {
                progressWheel.setVisibility(View.GONE);
                if (response != null) {
                    parseJsonFeed(response);
                    Log.d(TAG, response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!isDataLoaded){
                    txtError.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.VISIBLE);
                }else{
                    loadJSONFeed();
                }
                progressWheel.setVisibility(View.GONE);
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

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = new JSONArray(response.getString("feed"));

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = feedArray.getJSONObject(i);
                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("post_id"));
                maxId = feedObj.getInt("post_id");
                item.setName(feedObj.getString("name"));
                item.setImge(feedObj.getString("post_image"));
                item.setVideo(feedObj.getString("post_video"));
                item.setStatus(feedObj.getString("post_text"));
                item.setProfilePic(feedObj.getString("profile_pic"));
                item.setTimeStamp(feedObj.getString("post_date_time"));
                item.setUrl(feedObj.getString("post_url"));
                feedItems.add(item);
            }
            isDataLoaded = true;
            txtError.setVisibility(View.GONE);
            imgError.setVisibility(View.GONE);
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            if(!isDataLoaded){
                txtError.setVisibility(View.VISIBLE);
                imgError.setVisibility(View.VISIBLE);
            }
            e.printStackTrace();
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
                Snackbar.make(getView(), "Loading... Please wait..", Snackbar.LENGTH_LONG).show();
                preLast = lastItem;
            }
        }
    }
}
