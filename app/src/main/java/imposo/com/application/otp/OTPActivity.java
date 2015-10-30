package imposo.com.application.otp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import imposo.com.application.R;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.splash.Splash;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class OTPActivity extends ActionBarActivity implements View.OnClickListener{
    private RegisterDTO registerDTO;
    private EditText etOTP;
    private Button btnConfirmOTP;
    private Toolbar toolbar;
    private TextView tvResendOTP, tvCounter;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_otp);
        Gson gson = new Gson();
        Bundle bundle = getIntent().getExtras();
        registerDTO = gson.fromJson(bundle.getString("registerDTO"), RegisterDTO.class);
        bundle = null;
        populate();
        SendRegistrationOTPAsynTask registrationOTPAsynTask = new SendRegistrationOTPAsynTask(registerDTO);
        registrationOTPAsynTask.execute();
        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
        messageCustomDialogDTO.setTitle("Sucess");
        messageCustomDialogDTO.setMessage("One time password has been sent to your mobile number and email."
                + " Kindly enter the OTP to activate your account.");
        messageCustomDialogDTO.setContext(OTPActivity.this);
        messageCustomDialogDTO.setButton("OK");
        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
        messageCustomDialog.show();
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
        getSupportActionBar().setTitle("OTP");
        etOTP = (EditText)findViewById(R.id.etOTP);
        btnConfirmOTP = (Button)findViewById(R.id.btnConfirmOTP);
        tvResendOTP = (TextView)findViewById(R.id.txtResendOTP);
        tvCounter = (TextView)findViewById(R.id.txttimeCounterOTP);
        btnConfirmOTP.setOnClickListener(this);
        tvResendOTP.setOnClickListener(this);
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = ClockUpdate.clock(millisUntilFinished);
                tvCounter.setText(time + "  Seconds");
                if(((GlobalData)getApplicationContext()).getOtp() != null){
                    etOTP.setText(((GlobalData)getApplicationContext()).getOtp());
                    countDownTimer.cancel();
                    tvCounter.setText("");
                    ((GlobalData)getApplicationContext()).setOtp(null);;
                }
            }
            @Override
            public void onFinish() {
                tvCounter.setVisibility(View.GONE);
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
    }


    public void onBackPressed() {
        goBack();
    }

    private void goBack(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("Warning");
        builder.content("OTP confimation is a one time activity."
                + "Please confirm your OTP to get access to your account.");
        builder.positiveText("Go Back");
        builder.negativeText("Cancel");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                Intent intent = new Intent(OTPActivity.this, Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                OTPActivity.this.finish();
            }
        });
        builder.show();


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnConfirmOTP:
                String otp = etOTP.getText().toString();
                if(!otp.equals(registerDTO.getOtp())){
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Incorrect OTP");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage("You have filled an incorrect OTP. Kindly use correct OTP to activate your account.");
                    messageCustomDialogDTO.setContext(OTPActivity.this);
                    MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                    messageCustomDialog.show();
                }else{
                    countDownTimer.cancel();
                    tvCounter.setText("");
                    ((GlobalData)getApplicationContext()).setGcmID(null);
                    ConfirmOTPAsynTask confirmRegistrationOTPAsynTask = new ConfirmOTPAsynTask(OTPActivity.this, registerDTO);
                    confirmRegistrationOTPAsynTask.execute();
                }
                break;
            case R.id.txtResendOTP:
                SendRegistrationOTPAsynTask registrationOTPAsynTask = new SendRegistrationOTPAsynTask(registerDTO);
                registrationOTPAsynTask.execute();
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Sucess");
                messageCustomDialogDTO.setMessage("Your OTP has been sent again to your mobile number. Kindly enter your OTP to activate your account.");
                messageCustomDialogDTO.setContext(OTPActivity.this);
                messageCustomDialogDTO.setButton("OK");
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
                countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String time = ClockUpdate.clock(millisUntilFinished);
                        tvCounter.setText(time + "  Seconds");
                    }
                    @Override
                    public void onFinish() {
                        tvCounter.setVisibility(View.GONE);
                        tvResendOTP.setVisibility(View.VISIBLE);
                    }
                };
                countDownTimer.start();
                tvCounter.setVisibility(View.VISIBLE);
                tvResendOTP.setVisibility(View.GONE);
                break;
        }
    }
}
