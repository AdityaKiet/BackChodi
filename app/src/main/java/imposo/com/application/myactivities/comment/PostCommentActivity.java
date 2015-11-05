package imposo.com.application.myactivities.comment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.widget.Switch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import imposo.com.application.R;
import imposo.com.application.allfeeds.data.FeedDTO;
import imposo.com.application.allfeeds.data.OptionDTO;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.ui.MessageDialog;
import imposo.com.application.util.RealPathUtil;

/**
 * Created by adityaagrawal on 04/11/15.
 */
public class PostCommentActivity extends ActionBarActivity implements View.OnClickListener{
    private FeedDTO feedDTO;
    private Toolbar toolbar;
    private RelativeLayout rlImage;
    private TextView txtOptions;
    private View viewOptions;
    private RadioGroup radioGroup;
    private ImageView imgSelectedImage;
    private EditText etComment;
    private TextView txtName;
    private Bitmap bitmap;
    private ImageButton btnDeleteOption;
    private Switch switchAnonyomous;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_comment_activity);
        Bundle bundle = getIntent().getExtras();
        feedDTO = new Gson().fromJson(bundle.getString("gson"), FeedDTO.class);
        populate();
    }

    private void populate(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        if (feedDTO.getIsAnonyomous() == 1)
            getSupportActionBar().setTitle("Anonymous");
        else
            getSupportActionBar().setTitle(feedDTO.getPostCreaterName());
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.rgOptions);
        txtOptions = (TextView) findViewById(R.id.txtOptions);
        viewOptions = (View) findViewById(R.id.viewOptions);
        etComment = (EditText) findViewById(R.id.etComment);
        rlImage = (RelativeLayout) findViewById(R.id.rlImage);
        imgSelectedImage = (ImageView) findViewById(R.id.imgSelectedImage);
        txtName = (TextView) findViewById(R.id.txtName);
        btnDeleteOption = (ImageButton) findViewById(R.id.btnDeleteOption);
        switchAnonyomous = (Switch) findViewById(R.id.switchAnonyomous);
        btnDeleteOption.setOnClickListener(this);
        setData();

    }

    private void setData(){
        if(feedDTO.getOptions().size() == 0){
            txtOptions.setVisibility(View.GONE);
            viewOptions.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        }else{
            RadioButton radioButton;
            for(OptionDTO s : feedDTO.getOptions()){
                radioButton = new RadioButton(this);
                radioButton.setText(s.getOption());
                radioGroup.addView(radioButton);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_question_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_question){
            String comment = etComment.getText().toString().trim();
            if("".equals(comment)){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Error");
                messageCustomDialogDTO.setMessage("Comment can't be blank.");
                messageCustomDialogDTO.setContext(PostCommentActivity.this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else{
                if(feedDTO.getOptions().size() == 0){
                    int checked;
                    boolean isAnon = switchAnonyomous.isChecked();
                    if(isAnon)
                        checked = 1;
                    else
                        checked = 0;
                    String encodedString;

                    if(bitmap == null)
                        encodedString = "";
                    else{
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                        byte[] byte_arr = stream.toByteArray();
                        encodedString = Base64.encodeToString(byte_arr, 0);
                    }

                    PostCommentAsynTask postCommentAsynTask = new PostCommentAsynTask(checked, 0, comment, encodedString, feedDTO.getPostId(), this);
                    postCommentAsynTask.execute();
                }else{
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    if( radioButtonID == -1){
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle("Error");
                        messageCustomDialogDTO.setMessage("Please select an option.");
                        messageCustomDialogDTO.setContext(PostCommentActivity.this);
                        messageCustomDialogDTO.setButton("OK");
                        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                        messageCustomDialog.show();
                    }else {
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        int idx = radioGroup.indexOfChild(radioButton);
                        int checked;
                        boolean isAnon = switchAnonyomous.isChecked();
                        if(isAnon)
                            checked = 1;
                        else
                            checked = 0;
                        String encodedString;

                        if(bitmap == null)
                            encodedString = "";
                        else{
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            byte[] byte_arr = stream.toByteArray();
                            encodedString = Base64.encodeToString(byte_arr, 0);
                        }

                        PostCommentAsynTask postCommentAsynTask = new PostCommentAsynTask(checked, feedDTO.getOptions().get(idx).getOptionId(), comment, encodedString, feedDTO.getPostId(), this);
                        postCommentAsynTask.execute();
                    }
                }

            }
        }else if(id == R.id.action_attach){
                loadImagefromGallery();
        }
        return true;
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

                String path = null;
                if (Build.VERSION.SDK_INT < 11) {
                    path = RealPathUtil.getRealPathFromURI_BelowAPI11(this, filePath);
                } else if (Build.VERSION.SDK_INT < 19) {
                    path = RealPathUtil.getRealPathFromURI_API11to18(this, filePath);
                } else {
                    path = RealPathUtil.getRealPathFromURI_API19(this, filePath);
                }
                rlImage.setVisibility(View.VISIBLE);
                File file = new File(path);
                txtName.setText(file.getName());
                imgSelectedImage.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnDeleteOption:
                bitmap = null;
                rlImage.setVisibility(View.GONE);
                break;
        }
    }
}
