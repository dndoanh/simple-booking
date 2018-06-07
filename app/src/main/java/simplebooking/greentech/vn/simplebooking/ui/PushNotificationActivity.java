package simplebooking.greentech.vn.simplebooking.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import simplebooking.greentech.vn.simplebooking.R;
import simplebooking.greentech.vn.simplebooking.services.SocketService;
import simplebooking.greentech.vn.simplebooking.ui.fragments.Notification;

public class PushNotificationActivity extends AppCompatActivity implements SocketService.OnEventListener {

    private static PushNotificationActivity inst;

    public static PushNotificationActivity instance() {
        return inst;
    }

    public ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification);
        inst = this;
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait for connecting...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        //start socket service
        Intent i = new Intent(this, SocketService.class);
        startService(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inst = null;
    }

    //called from socket service after socket connection established
    public void Check() {
        pDialog.cancel();
        if (inst != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new Notification()).commit();
        }
    }

    //called from socket service after data received from server
    @Override
    public void setData(String data, String tag) {
        Log.d("Data from Service", data);
    }
}
