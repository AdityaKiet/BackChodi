package imposo.com.application.dashboard;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.ui.MessageDialog;
import imposo.com.application.ui.slidingtabs.SlidingTabLayout;


public class DashboardActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[]={"All Feeds","My Feeds", "My Answers"};
    private int Numboftabs =3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        populate();
    }

    private void populate(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getInt("success") == 1){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Success");
                messageCustomDialogDTO.setMessage("Congratulations !!\nYour question has been posted.");
                messageCustomDialogDTO.setContext(this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else if(bundle.getInt("success") == 2){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Success");
                messageCustomDialogDTO.setMessage("Congratulations !!\nYour comment has been posted.");
                messageCustomDialogDTO.setContext(this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(3);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        tabs.setViewPager(pager);

    }

}