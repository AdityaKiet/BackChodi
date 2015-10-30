package imposo.com.application.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import imposo.com.application.R;
import imposo.com.application.dashboard.DashboardActivity;
import imposo.com.application.login.LoginActivity;
import io.fabric.sdk.android.Fabric;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class Splash extends ActionBarActivity {
    private SharedPreferences sharedPreferences;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        populate();
        if(sharedPreferences.getString("session", null) != null ){
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.splash);
            btnLogin = (Button) findViewById(R.id.btnLoginScreen);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void populate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

}

