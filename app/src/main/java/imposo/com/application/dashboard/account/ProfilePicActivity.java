package imposo.com.application.dashboard.account;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;

import imposo.com.application.R;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.ZoomableImageView;

/**
 * Created by adityaagrawal on 31/10/15.
 */
public class ProfilePicActivity extends ActionBarActivity{
    private Bitmap bitmap;
    private SessionDTO sessionDTO;
    private TextView tvNoImage;
    private ZoomableImageView image;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);
        populate();

        try {
            String extr = Environment.getExternalStorageDirectory().toString();
            File f= new File(extr + "/.Application/Application-Images/" + sessionDTO.getId() + ".png");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            image.setImageBitmap(bitmap);
            tvNoImage.setVisibility(View.GONE);
        }
        catch(Exception e){
            image.setVisibility(View.GONE);
        }

    }

    private void populate() {
        tvNoImage = (TextView) findViewById(R.id.txtNoImage);
        image = (ZoomableImageView) findViewById(R.id.imgProfilePicFullScreen);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Gson gson = new Gson();
        String json = sharedPref.getString("session", null);
        sessionDTO = gson.fromJson(json, SessionDTO.class);
        getSupportActionBar().setTitle(sessionDTO.getName());

    }

}
