package imposo.com.application.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.gson.Gson;

import org.json.JSONObject;

import imposo.com.application.dto.MessageCustomDialogDTO;
import imposo.com.application.dto.RegisterDTO;
import imposo.com.application.otp.OTPActivity;
import imposo.com.application.ui.MessageDialog;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class JSONDecode {
    public JSONDecode() {
    }

    public void decode(Context context, String result, RegisterDTO registerDTO) throws Exception{
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
            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
            registerDTO.setId(jsonObject1.getInt("id"));
            registerDTO.setIsNew(jsonObject1.getBoolean("isNew"));
            Intent intent = new Intent(context, OTPActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("registerDTO", new Gson().toJson(registerDTO));
            intent.putExtras(bundle);
            context.startActivity(intent);
            ((ActionBarActivity)context).finish();
        }
    }
}
