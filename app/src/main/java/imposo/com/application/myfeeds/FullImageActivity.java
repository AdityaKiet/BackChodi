package imposo.com.application.myfeeds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import imposo.com.application.R;
import imposo.com.application.ui.ZoomableImageView;

/**
 * Created by adityaagrawal on 03/11/15.
 */
public class FullImageActivity extends ActionBarActivity{
    private Bitmap bitmap;
    private TextView tvNoImage;
    private ZoomableImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);
        populate();
    }

    private void populate() {
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        Bitmap b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);

        tvNoImage = (TextView) findViewById(R.id.txtNoImage);
        image = (ZoomableImageView) findViewById(R.id.imgProfilePicFullScreen);
        try {
            image.setImageBitmap(b);
            tvNoImage.setVisibility(View.GONE);
        }catch (Exception e){
            image.setVisibility(View.GONE);
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
