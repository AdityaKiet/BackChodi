package imposo.com.application.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.nispok.snackbar.SnackbarManager;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dashboard.account.ProfileUpdateActivity;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.myfeeds.adapter.FeedListAdapter;
import imposo.com.application.myfeeds.data.FeedDTO;
import imposo.com.application.newfeed.AddNewFeed;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class MyFeedFragment extends Fragment implements View.OnClickListener , NetworkConstants, AbsListView.OnScrollListener{
    private View view;
    private Toolbar toolbar;
    private FloatingActionButton btnAskNewQuestion;
    private TextView txtError;
    private static final String TAG = MyFeedFragment.class.getSimpleName();
    private ImageView imgError;
    private ProgressWheel progressWheel;
    private String URL_FEED = GET_NETWORK_IP + "/GetMyFeed?lastpost=POSTiD&first=FIRST&userid=ID&phonenumber=PHONENUMBER";
    private ListView listView;
    public static List<FeedDTO> feedItems;
    private FeedListAdapter listAdapter;
    private int preLast;
    private boolean isDataLoaded = false;
    private int maxId = 0;
    private View footerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =(RelativeLayout) inflater.inflate(R.layout.my_feed_fragment,container,false);
        setRetainInstance(true);
        populate();
        return view;
    }

    private void populate(){
        setHasOptionsMenu(true);
        ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        actionBarActivity.setSupportActionBar(toolbar);
        btnAskNewQuestion = (FloatingActionButton) view.findViewById(R.id.btnAskNewQuestion);
        btnAskNewQuestion.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.listAllFeeds);
        listView.setOnScrollListener(this);
        footerView =  ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progress_dialog, null, false);
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
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(getActivity());
        listView.setAdapter(listAdapter);
        loadCache();
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
        url = url.replaceFirst("POSTiD", String.valueOf(maxId));
        url = url.replaceFirst("PHONENUMBER", sessionDTO.getPhoneNumber());

        if(isDataLoaded)
            url = url.replaceFirst("FIRST", "0");
        else
            url = url.replaceFirst("FIRST", "1");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject response) {
                progressWheel.setVisibility(View.GONE);
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
        List<String> gsonString = new ArrayList<>();
        List<FeedDTO> feedDTOs = new ArrayList<>();

        try {
            if(response.getInt("success") == 0){

            }else{
                Gson gson = new Gson();
                if(!"false".equals(response.getString("data"))) {
                    gsonString = gson.fromJson(response.getString("data"), List.class);
                }else{
                    listView.removeFooterView(footerView);
                }
                for(String s : gsonString){
                    FeedDTO feedDTO = gson.fromJson(s, FeedDTO.class);
                    feedDTOs.add(feedDTO);
                    maxId = feedDTO.getPostId();

                }
                feedItems.addAll(feedDTOs);
                isDataLoaded = true;
            }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_feeds_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_profile){
            Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnAskNewQuestion:
                Intent intent = new Intent(getActivity(), AddNewFeed.class);
                startActivity(intent);
                break;
            case R.id.txtErrorMessage:
            case R.id.imgError:
                populateData();
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
                SnackbarManager.show(com.nispok.snackbar.Snackbar.with(getActivity())
                        .text("Loading... Please wait..!!!")
                        .textColor(Color.WHITE)
                        .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .color(getResources().getColor(R.color.ColorPrimary)), getActivity());
                preLast = lastItem;
            }
        }
    }

    @Override
    public void onResume() {
        try{
            if(listAdapter != null){
                listAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }
}
