package imposo.com.application.dashboard.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.adapter.SimpleRecyclerAdapter;
import imposo.com.application.constants.NetworkConstants;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.ProfileListDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class ProfileUpdateActivity extends ActionBarActivity implements NetworkConstants{
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SessionDTO sessionDTO;
    Bitmap bitmap;
    int mutedColor = R.attr.colorPrimary;
    FloatingActionButton btnUploadPic;
    ImageView header;
    SimpleRecyclerAdapter simpleRecyclerAdapter;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

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
        String extr = Environment.getExternalStorageDirectory().toString();
        File f= new File(extr + "/.Application/Application-Images/" + sessionDTO.getId() + ".png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            header.setImageBitmap(bitmap);
        }
        catch(Exception e){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
            header.setImageBitmap(bitmap);
        }

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                mutedColor = palette.getMutedColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUpdateActivity.this, ProfilePicActivity.class);
                startActivity(intent);
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
                bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
                uploadImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(final Bitmap bitmap){
        class UploadImage extends AsyncTask<Void,Void,Void> {
            private MaterialDialog.Builder progressDialogBuilder;
            private MaterialDialog progressDialog;
            private InputStream is;
            private HttpEntity entity;
            private String result = "";

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
            protected void onPostExecute(Void s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Log.e("image", result);
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("success") == 1){
                        header.setImageBitmap(bitmap);
                        Snackbar.make(getCurrentFocus(), "Profile pic updated !!!", Snackbar.LENGTH_LONG).show();
                        String extr = Environment.getExternalStorageDirectory().toString();
                        File mFolder = new File(extr + "/.Application");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        String strF = mFolder.getAbsolutePath();
                        File mSubFolder = new File(strF + "/Application-Images");
                        if (!mSubFolder.exists()) {
                            mSubFolder.mkdir();
                        }
                        String fileName = sessionDTO.getId() + ".png";
                        File f = new File(mSubFolder.getAbsolutePath(),fileName);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.PNG,70, fos);
                            fos.flush();
                            fos.close();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else{
                        UploadImage.this.cancel(true);
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle("Network Error");
                        messageCustomDialogDTO.setButton("OK");
                        messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                        messageCustomDialogDTO.setContext(ProfileUpdateActivity.this);
                        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                        messageCustomDialog.show();
                    }
                }catch (Exception e){
                    UploadImage.this.cancel(true);
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Network Error");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                    messageCustomDialogDTO.setContext(ProfileUpdateActivity.this);
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                String encodedString = Base64.encodeToString(byte_arr, 0);
                List<NameValuePair> list = new ArrayList<NameValuePair>(1);
                list.add(new BasicNameValuePair("id", sessionDTO.getId() + ""));
                list.add(new BasicNameValuePair("imagestring", encodedString));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(GET_NETWORK_IP + UPLOAD_IMAGE_SERVLET);
                    httpPost.setEntity(new UrlEncodedFormEntity(list));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    entity = httpResponse.getEntity();
                    is = entity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        stringBuilder.append(line);
                    is.close();
                    result = stringBuilder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    UploadImage.this.cancel(true);
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Network Error");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                    messageCustomDialogDTO.setContext(ProfileUpdateActivity.this);
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();

                }
                return null;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }

}
