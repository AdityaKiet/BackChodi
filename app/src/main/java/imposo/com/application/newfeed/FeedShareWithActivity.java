package imposo.com.application.newfeed;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.PhoneContactsDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 02/11/15.
 */
public class FeedShareWithActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private Switch aSwitch;
    private String title, question;
    private PhoneContactsDTO phoneContactsDTO = null;
    private ListView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_share_with_activity);
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        question = bundle.getString("question");
        populate();
    }

    private void populate(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Share With");
        aSwitch = (Switch) findViewById(R.id.switchAnonyomous);
        listContacts = (ListView) findViewById(R.id.listSelectContacts);
        listContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listContacts.setItemsCanFocus(false);
        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phoneContactsDTO = ((GlobalData)getApplicationContext()).getContactsDTOList().get(position);
            }
        });
        GetAllContactsAsynTask getAllContactsAsynTask  = new GetAllContactsAsynTask(this, listContacts);
        getAllContactsAsynTask.execute();

        aSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                if(b){
                    phoneContactsDTO = null;
                    listContacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listContacts.setItemChecked(-1, true);
                }
                else
                    listContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_feeds_share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send){
            final List<String> selectedPhones = new ArrayList<>();
            if(aSwitch.isChecked()){
                if(null != phoneContactsDTO){
                    selectedPhones.add(phoneContactsDTO.getPhoneNumber());
                    AddNewFeedAsyncTask newFeedAsyncTask = new AddNewFeedAsyncTask(this, title, question, selectedPhones, true, false);
                    newFeedAsyncTask.execute();
                }else{
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Error");
                    messageCustomDialogDTO.setMessage("Select a contact for your anonmoyous post.");
                    messageCustomDialogDTO.setContext(FeedShareWithActivity.this);
                    messageCustomDialogDTO.setButton("OK");
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
            }else{
                SparseBooleanArray checked = listContacts.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++)
                    if (checked.get(i)) {
                        selectedPhones.add(((GlobalData) getApplicationContext()).getContactsDTOList().get(i).getPhoneNumber());
                    }
                if(selectedPhones.size() == 0){
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
                    builder.title("Warning");
                    builder.content("You have not selected any contact, so your question will be public. Do you want to make your question public ?");
                    builder.positiveText("Submit");
                    builder.negativeText("Cancel");
                    builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            AddNewFeedAsyncTask newFeedAsyncTask = new AddNewFeedAsyncTask(FeedShareWithActivity.this, title, question, selectedPhones, false, true);
                            newFeedAsyncTask.execute();
                        }
                    });
                    builder.show();

                }else{
                    AddNewFeedAsyncTask newFeedAsyncTask = new AddNewFeedAsyncTask(this, title, question, selectedPhones, false, false);
                    newFeedAsyncTask.execute();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
