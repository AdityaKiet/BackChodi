package imposo.com.application.otp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.dto.SessionDTO;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class JSONDecode {
    public void decode(Context context, RegisterDTO registerDTO, String result) throws Exception{
        JSONObject jsonObject = new JSONObject(result);
        if(jsonObject.getInt("success") == 0){
            MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
            messageCustomDialogDTO.setTitle("Network Error");
            messageCustomDialogDTO.setButton("OK");
            messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
            messageCustomDialogDTO.setContext(context);
            MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
            messageCustomDialog.show();
        }else{
            String isDone = jsonObject.getString("data");
            if(isDone.equals("false")){
                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Network Error");
                messageCustomDialogDTO.setButton("OK");
                messageCustomDialogDTO.setMessage("Some unexpected error has occured. Please try again.");
                messageCustomDialogDTO.setContext(context);
                MessageDialog messageCustomDialog = new MessageDialog(messageCustomDialogDTO);
                messageCustomDialog.show();
            }else{
                SessionDTO sessionDTO = new Gson().fromJson(jsonObject.getString("data"), SessionDTO.class);
                Log.d("log", sessionDTO.toString());
                SetGCMIDAsynTask setGCMIDAsynTask = new SetGCMIDAsynTask(context, sessionDTO);
                setGCMIDAsynTask.execute();
            }
        }
    }
}
