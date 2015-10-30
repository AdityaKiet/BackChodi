package imposo.com.application.newfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.adapter.SimpleRecyclerAdapter;
import imposo.com.application.dashboard.account.ProfileActivity;
import imposo.com.application.dto.ProfileListDTO;
import imposo.com.application.dto.SessionDTO;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class AddNewFeed extends ActionBarActivity{
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SessionDTO sessionDTO;
    int mutedColor = R.attr.colorPrimary;
    SimpleRecyclerAdapter simpleRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_profile:
                Intent intent = new Intent(AddNewFeed.this, ProfileActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resume(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sessionDTO =  new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);
        if(sessionDTO.getName().equals(""))
            collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
        else
            collapsingToolbar.setTitle(sessionDTO.getName());
        List<ProfileListDTO> listData = new ArrayList<ProfileListDTO>();
        ProfileListDTO profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Name");
        profileListDTO.setValue(sessionDTO.getName());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Phone Number");
        profileListDTO.setValue(sessionDTO.getPhoneNumber());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Email");
        profileListDTO.setValue(sessionDTO.getEmail());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Occupation");
        profileListDTO.setValue(sessionDTO.getOccupation());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Date of Birth");
        profileListDTO.setValue(sessionDTO.getDob());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Gender");
        if(sessionDTO.getGender().equals(""))
            profileListDTO.setValue("");
        else if(sessionDTO.getGender().equals("M"))
            profileListDTO.setValue("Male");
        if(sessionDTO.getGender().equals("F"))
            profileListDTO.setValue("Female");
        listData.add(profileListDTO);

            simpleRecyclerAdapter = new SimpleRecyclerAdapter(listData);
            recyclerView.setAdapter(simpleRecyclerAdapter);
        simpleRecyclerAdapter.SetOnItemClickListener(new SimpleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AddNewFeed.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populate(){
        setContentView(R.layout.profile_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.showOverflowMenu();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sessionDTO =  new Gson().fromJson(sharedPreferences.getString("session", null), SessionDTO.class);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if(sessionDTO.getName().equals(""))
            collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
        else
            collapsingToolbar.setTitle(sessionDTO.getName());
        collapsingToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ImageView header = (ImageView) findViewById(R.id.header);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                mutedColor = palette.getMutedColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<ProfileListDTO> listData = new ArrayList<ProfileListDTO>();
        ProfileListDTO profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Name");
        profileListDTO.setValue(sessionDTO.getName());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Phone Number");
        profileListDTO.setValue(sessionDTO.getPhoneNumber());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Email");
        profileListDTO.setValue(sessionDTO.getEmail());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Occupation");
        profileListDTO.setValue(sessionDTO.getOccupation());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Date of Birth");
        profileListDTO.setValue(sessionDTO.getDob());
        listData.add(profileListDTO);
        profileListDTO =  new ProfileListDTO();
        profileListDTO.setHeading("Gender");
        if(sessionDTO.getGender().equals(""))
            profileListDTO.setValue("");
        else if(sessionDTO.getGender().equals("M"))
            profileListDTO.setValue("Male");
        if(sessionDTO.getGender().equals("F"))
            profileListDTO.setValue("Female");
        listData.add(profileListDTO);

        if (simpleRecyclerAdapter == null) {
            simpleRecyclerAdapter = new SimpleRecyclerAdapter(listData);
            recyclerView.setAdapter(simpleRecyclerAdapter);
        }

        simpleRecyclerAdapter.SetOnItemClickListener(new SimpleRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AddNewFeed.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
