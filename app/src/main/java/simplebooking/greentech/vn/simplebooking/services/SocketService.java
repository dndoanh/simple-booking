package simplebooking.greentech.vn.simplebooking.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import simplebooking.greentech.vn.simplebooking.SimpleApp;
import simplebooking.greentech.vn.simplebooking.common.URLs;
import simplebooking.greentech.vn.simplebooking.ui.PushNotificationActivity;

public class SocketService extends Service {

    //create object of class
    public static SocketService Single;

    //----------------------------interface object------------------//
    private OnEventListener mCallback;

    //Socket object
    Socket socket;
    private Context context;
    //socket connection status
    public boolean socket_connected = false;

    //constructor
    public SocketService() {
    }

    //-----------------------------interface for sending data from server to activity--------------------------//
    public interface OnEventListener {
        public void setData(String data, String tag);
    }

    //---------------------------------get context from activity to send data to activity----------------------------//
    public void setContext(Context c) {
        this.context = c;
        mCallback = (OnEventListener) context;
    }

    /* Static 'instance' method */
    public static SocketService getInstance() {
        return Single;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Single = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocket();
        Log.d("Start Service", "Started from Service");

        //restart Service when it killed
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Single = null;
        socket_connected = false;
        stopSelf();
    }


    //---------***********------------------Run socket for listening to various event-----*******------//
    public void initSocket() {
        //check if socket connected or not
        if (!socket_connected) {
            try {
                IO.Options opts = new IO.Options();
                opts.reconnection = true;//try to reconnect socket when it disconnected from server
                opts.reconnectionDelay = 0;//reconnection delay time in milliseconds
                opts.reconnectionAttempts = 5;//no of attempts to reconnect socket
                //opts.forceNew = true;
                socket = IO.socket(URLs.URL_SOCKET_SEVER, opts);//server ip address
                //Methods for listening to events.
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("socket id", socket.id());
                        Log.d("Socket Status", "socket connected");
                        socket_connected = true;
                        //call main activity method to tell socket connected
                        PushNotificationActivity pushActivity = PushNotificationActivity.instance();
                        if (pushActivity != null) {
                            pushActivity.Check();
                        }
                    }

                }).on("send_response", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                        //***********------------------send data from service to activity----------------********//
                        try {

                            mCallback.setData(args[0].toString(), "send_response");
                            System.out.println("send_response=======>" + args[0].toString());
                        } catch (Exception e) {

                            System.out.println("send_response=======>" + e.toString());
                        }
                    }
                }).on("noti_rcv_message", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        try {
                            //***********------------------Generate notification----------------********//
                            callBroad(args[0].toString());
                            System.out.println("notification=======>" + args[0].toString());
                        } catch (Exception e) {
                            System.out.println("notification=======>" + e.toString());
                        }
                    }
                }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("EVENT_RECONNECT_FAILED", "yes");
                        socket_connected = false;
                        Log.d("Socket Status", "socket disconnected");

                    }

                }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("EVENT_RECONNECT", "yes");
                        socket_connected = true;
                        Log.d("Socket Status", "socket reconnected");

                    }

                }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("EVENT_CONNECT_TIMEOUT", "yes");

                    }

                }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("EVENT_RECONNECT_ATTEMPT", "yes");

                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        Log.d("EVENT_DISCONNECT", "yes");
                    }

                });
                socket.connect();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            //call main activity method to tell socket already connected
            PushNotificationActivity pushActivity = PushNotificationActivity.instance();
            if (pushActivity != null) {
                pushActivity.Check();
            }
        }
    }

    public void emit(String key, String data) {
        socket.emit(key, data);
        System.out.println(key + "@@@@@@@@@ emitting");
    }

    //call broadcast receiver to generate notifications
    public void callBroad(String message) {
        SimpleApp playForever = SimpleApp.getInstance();
        Intent intent = new Intent();
        intent.putExtra("message", message);
        intent.setAction("simple.NOTIFICATION");
        playForever.context.sendBroadcast(intent);
    }
    //-----------------------**********************End of Socket code***************-------------------//
}