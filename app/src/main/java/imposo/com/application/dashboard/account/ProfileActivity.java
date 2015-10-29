package imposo.com.application.dashboard.account;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.ganfra.materialspinner.MaterialSpinner;
import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.MessageDialog;
import imposo.com.application.util.NetworkCheck;
import imposo.com.application.util.Validation;

/**
 * Created by adityaagrawal on 26/10/15.
 */
public class ProfileActivity extends ActionBarActivity{
    private Toolbar toolbar;
    private MaterialEditText etName, etPhone, etEmail;
    private ToggleButton toggleGender;
    private ButtonRectangle btnAddProfile;
    private SessionDTO sessionDTO;
    private MaterialSpinner spinner;
    private int pos = -1;
    private TextView txtDOB;
    private String dob = "", occupation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_activity);
        populate();
    }

    private void populate(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Create Profile");
        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sessionDTO = new Gson().fromJson(sharedPreferences.getString("session", null),SessionDTO.class);
        etName = (MaterialEditText) findViewById(R.id.etName);
        etPhone = (MaterialEditText)findViewById(R.id.etPhone);
        etEmail = (MaterialEditText) findViewById(R.id.etEmail);
        toggleGender = (ToggleButton) findViewById(R.id.toggleGender);
        btnAddProfile = (ButtonRectangle) findViewById(R.id.btnAddProfile);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        etName.setText(sessionDTO.getName());
        etPhone.setText(sessionDTO.getPhoneNumber());
        etEmail.setText(sessionDTO.getEmail());
        SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFmt = new SimpleDateFormat("dd-MMMM-yyyy");
        try {
            dob = sessionDTO.getDob();
            txtDOB.setText(outFmt.format(inFmt.parse(dob)));
        } catch (Exception e) {
            txtDOB.setText(sessionDTO.getDob());
            e.printStackTrace();
        }

        if(sessionDTO.getGender().equals("M"))
            toggleGender.setChecked(false);
        else if(sessionDTO.getGender().equals("F"))
            toggleGender.setChecked(true);
        for( int i = 0 ; i < getResources().getStringArray(R.array.occupations).length ; i++){
            if(getResources().getStringArray(R.array.occupations)[i].equals(sessionDTO.getOccupation()))
                pos = i + 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.occupations));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerOccupation);
        spinner.setAdapter(adapter);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == -1)
                    occupation = "";
                else
                    occupation = getResources().getStringArray(R.array.occupations)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfile();
            }
        });

        txtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        StringBuffer dobBuffer = new StringBuffer();
                        dobBuffer.append(year + "-"+ (monthOfYear+1) +"-"+dayOfMonth);
                        SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outFmt = new SimpleDateFormat("dd-MMMM-yyyy");
                        try {
                            dob = dobBuffer.toString();
                            txtDOB.setText(outFmt.format(inFmt.parse(dobBuffer.toString())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, datePickerListener,calendar.get(Calendar.YEAR) ,calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    private void addProfile(){
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gender = toggleGender.getText().toString().charAt(0)+"";
        if(NetworkCheck.isNetworkAvailable(this)){
            if(name.equals("") || email.equals("") || phone.equals("") || occupation.equals("") || dob.equals("")){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Incomplete Information");
                messageCustomDialogDTO.setMessage("All the fields are mandatory..\nPlease fill up all the fields and try again.");
                messageCustomDialogDTO.setButton("OK");
                messageCustomDialogDTO.setContext(this);
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else if(!Validation.validateEmail(email)){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Registration Failed");
                messageCustomDialogDTO.setMessage("Please enter a valid email address");
                messageCustomDialogDTO.setContext(this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else{
                sessionDTO.setName(name);
                sessionDTO.setDob(dob);
                sessionDTO.setPhoneNumber(phone);
                sessionDTO.setEmail(email);
                sessionDTO.setGender(gender);
                sessionDTO.setOccupation(occupation);
                UpdateProfileAsynTask updateProfileAsynTask = new UpdateProfileAsynTask(this, sessionDTO);
                updateProfileAsynTask.execute();
            }
        }else{
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle("No Internet Connection");
            messageCustomDialogDTO.setMessage("No internet connection found on your device. "
                    + "Please check your internet connection and try again.");
            messageCustomDialogDTO.setButton("OK");
            messageCustomDialogDTO.setContext(this);
            MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
            messageCustomDialog.show();
        }

    }

}
