package imposo.com.application.dashboard.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.adapter.SimpleRecyclerAdapter;
import imposo.com.application.dto.ProfileListDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.util.RequestHandler;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class ProfileUpdateActivity extends ActionBarActivity{
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SessionDTO sessionDTO;
    String imgPath, fileName;
    Bitmap bitmap;
    int mutedColor = R.attr.colorPrimary;
    FloatingActionButton btnUploadPic;
    ImageView header;
    SimpleRecyclerAdapter simpleRecyclerAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    public static final String UPLOAD_URL = "http://simplifiedcoding.16mb.com/ImageUpload/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";

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
                Intent intent = new Intent(ProfileUpdateActivity.this, ProfileActivity.class);
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
        SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFmt = new SimpleDateFormat("dd-MMMM-yyyy");
        try {
           profileListDTO.setValue(outFmt.format(inFmt.parse(sessionDTO.getDob())));
        } catch (Exception e) {
            profileListDTO.setValue("");
            e.printStackTrace();
        }
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
                Intent intent = new Intent(ProfileUpdateActivity.this, ProfileActivity.class);
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
        btnUploadPic = (FloatingActionButton) findViewById(R.id.btnUploadPic);
        btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });

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

        header = (ImageView) findViewById(R.id.header);

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
                Intent intent = new Intent(ProfileUpdateActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loadImagefromGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Snackbar.make(getCurrentFocus(), "Some unexpected error has occured.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                header.setImageBitmap(bitmap);
              //  uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {
            private MaterialDialog.Builder progressDialogBuilder;
            private MaterialDialog progressDialog;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialogBuilder = new MaterialDialog.Builder(ProfileUpdateActivity.this);
                progressDialogBuilder.content("Please Wait.....");
                progressDialogBuilder.cancelable(false);
                progressDialogBuilder.progress(true, 0);
                progressDialog = progressDialogBuilder.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                String result = rh.sendPostRequest(UPLOAD_URL,data);
                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }
}
