package imposo.com.application.newfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.ui.MessageDialog;
import imposo.com.application.ui.NestedListView;
import imposo.com.application.util.RealPathUtil;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class AddNewFeed extends ActionBarActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private EditText etTitle, etQuestion, etOption;
    private ImageButton btnAddOption;
    public static List<String> options = new ArrayList<>();
    public static List<Bitmap> bitmaps = new ArrayList<>();
    public static List<String> imageNames = new ArrayList<>();
    private NestedListView listOptions, listImagesSelected;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_feed_activity);
        populate();
    }

    private void populate(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        getSupportActionBar().setTitle("Ask Question");
        etTitle = (EditText) findViewById(R.id.etTitle);
        etQuestion = (EditText) findViewById(R.id.etQuestion);
        etOption = (EditText) findViewById(R.id.etOption);
        btnAddOption = (ImageButton) findViewById(R.id.btnAddOption);
        btnAddOption.setOnClickListener(this);
        listOptions = (NestedListView) findViewById(R.id.listOptionsSelected);
        listImagesSelected = (NestedListView) findViewById(R.id.listImagesSelected);
        OptionListAdapter adapter = new OptionListAdapter(this);
        listOptions.setAdapter(adapter);
        ImageListAdapter imageListAdapter = new ImageListAdapter(this);
        listImagesSelected.setAdapter(imageListAdapter);
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
            String title = etTitle.getText().toString().trim();
            String questions = etQuestion.getText().toString().trim();
            if("".equals(title)){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Error");
                messageCustomDialogDTO.setMessage("Title can't be blank.");
                messageCustomDialogDTO.setContext(AddNewFeed.this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else if("".equals(questions)){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Error");
                messageCustomDialogDTO.setMessage("Question can't be blank.");
                messageCustomDialogDTO.setContext(AddNewFeed.this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else{
                Intent intent = new Intent(this, FeedShareWithActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("question", questions);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        }else if(id == R.id.action_attach){
            loadImagefromGallery();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddOption:
                String option = etOption.getText().toString().trim();
                if(!"".equals(option)){
                    options.add(option);
                    etOption.setText("");
                    OptionListAdapter adapter = new OptionListAdapter(this);
                    listOptions.setAdapter(adapter);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack(){
        AddNewFeed.options = new ArrayList<>();
        AddNewFeed.bitmaps = new ArrayList<>();
        AddNewFeed.imageNames = new ArrayList<>();
        finish();
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);

                String path = null;
                if (Build.VERSION.SDK_INT < 11) {
                    path = RealPathUtil.getRealPathFromURI_BelowAPI11(this, filePath);
                } else if (Build.VERSION.SDK_INT < 19) {
                    path = RealPathUtil.getRealPathFromURI_API11to18(this, filePath);
                } else {
                    path = RealPathUtil.getRealPathFromURI_API19(this, filePath);
                }
                File file = new File(path);
                imageNames.add(file.getName());
                bitmaps.add(bitmap);
                ImageListAdapter imageListAdapter = new ImageListAdapter(this);
                listImagesSelected.setAdapter(imageListAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}