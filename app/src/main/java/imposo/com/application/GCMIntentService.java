package imposo.com.application;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import imposo.com.application.global.GlobalData;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCM Tutorial::Service";
	public static final String SENDER_ID = "87364749779";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "onRegistered: registrationId=" + registrationId);
		((GlobalData)getApplicationContext()).setGcmID(registrationId);
		Log.d(TAG, ((GlobalData)getApplicationContext()).getGcmID());
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent data){
        String message;
        message = data.getStringExtra("message");
        Log.e(TAG, "onSucyess: suc=" + message);
    }

	@Override
	protected void onError(Context arg0, String errorId) {
		Log.e(TAG, "onError: errorId=" + errorId);
	}

}