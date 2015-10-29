package imposo.com.application.otp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gcm.GCMRegistrar;

import imposo.com.application.GCMIntentService;
import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.global.GlobalData;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class SetGCMIDAsynTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private SessionDTO sessionDTO;
    private MaterialDialog.Builder progressDialogBuilder;
    private MaterialDialog progressDialog;
    private CountDownTimer countDownTimer;
    private boolean isDone = false;

    public SetGCMIDAsynTask(Context context, SessionDTO sessionDTO) {
        this.context = context;
        this.sessionDTO = sessionDTO;
    }

    @Override
    protected void onPreExecute() {
        progressDialogBuilder = new MaterialDialog.Builder(context);
        progressDialogBuilder.content("Please Wait.....");
        progressDialogBuilder.cancelable(false);
        progressDialogBuilder.progress(true, 0);
        progressDialog = progressDialogBuilder.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        GCMRegistrar.register(context, GCMIntentService.SENDER_ID);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
            countDownTimer = new CountDownTimer(30000, 3000) {

                public void onTick(long millisUntilFinished) {
                    if(((GlobalData)context.getApplicationContext()).getGcmID() != null){
                        if(!isDone) {
                            progressDialog.dismiss();
                            SetGCMIDAsynTask.this.cancel(true);
                            UpdateGCMIDAsynTask updateGCMIDAsynTask = new UpdateGCMIDAsynTask(context, sessionDTO, ((GlobalData) context.getApplicationContext()).getGcmID());
                            updateGCMIDAsynTask.execute();
                            countDownTimer.cancel();
                            countDownTimer = null;
                            isDone = true;
                        }
                    }
                }



                public void onFinish() {
                    if(!isDone) {
                        progressDialog.dismiss();
                        SetGCMIDAsynTask.this.cancel(true);
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle("Network Error");
                        messageCustomDialogDTO.setButton("OK");
                        messageCustomDialogDTO.setMessage("Some unexpected error has occured.\n Please try again.");
                        messageCustomDialogDTO.setContext(context);
                        MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                        messageCustomDialog.show();
                    }
                }
            }.start();



        super.onPostExecute(result);
    }

}
