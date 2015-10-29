package imposo.com.application.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import imposo.com.application.R;
import imposo.com.application.dashboard.account.ProfileActivity;
import imposo.com.application.newfeed.AddNewFeed;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class MyFeedFragment extends Fragment{
    private View view;
    private Toolbar toolbar;
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

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_feeds_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_question) {
            Intent intent = new Intent(getActivity(), AddNewFeed.class);
            startActivity(intent);
        }else if(id == R.id.action_profile){
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
