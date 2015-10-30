package imposo.com.application.login;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Random;

import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.ui.MessageDialog;
import imposo.com.application.util.NetworkCheck;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener{
    private Button btnLogin;
    private MaterialEditText etPhoneNumber;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        populate();
    }

    private void populate(){
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etPhoneNumber = (MaterialEditText) findViewById(R.id.etLoginPhone);
        btnLogin.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Mobile Number");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogin){
            if(NetworkCheck.isNetworkAvailable(this)) {
                String phone = etPhoneNumber.getText().toString().trim();
                if (phone.equals("")) {
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Login Failed");
                    messageCustomDialogDTO.setMessage("Phone Number can't be blank.");
                    messageCustomDialogDTO.setContext(LoginActivity.this);
                    messageCustomDialogDTO.setButton("OK");
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
                else if(phone.length() < 10){
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Login Failed");
                    messageCustomDialogDTO.setMessage("Phone Number can't be so small.");
                    messageCustomDialogDTO.setContext(LoginActivity.this);
                    messageCustomDialogDTO.setButton("OK");
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
                else if(phone.length() > 10){
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Login Failed");
                    messageCustomDialogDTO.setMessage("Phone Number can't be so large.");
                    messageCustomDialogDTO.setContext(LoginActivity.this);
                    messageCustomDialogDTO.setButton("OK");
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }
                else {
                    Random ran = new Random();
                    int code = (100000 + ran.nextInt(900000));
                    String otp = String.valueOf(code);
                    RegisterDTO registerDTO = new RegisterDTO();
                    registerDTO.setPhoneNumber(phone);
                    registerDTO.setOtp(otp);
                    LoginAsynTask loginAsynTask = new LoginAsynTask(this, registerDTO);
                    loginAsynTask.execute();
                }
            }else{
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Network Error");
                messageCustomDialogDTO.setMessage("No internet connection found on your device. "
                        + "Please check your internet connection and try again.");
                messageCustomDialogDTO.setContext(LoginActivity.this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }
        }
    }
}
