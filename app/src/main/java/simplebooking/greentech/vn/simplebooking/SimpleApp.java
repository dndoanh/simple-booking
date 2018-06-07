package simplebooking.greentech.vn.simplebooking;

import android.app.Application;
import android.content.Context;

public class SimpleApp extends Application {


    private static SimpleApp instance;
    public Context context;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        context = this;
    }

    public static synchronized SimpleApp getInstance() {
        return instance;
    }

}
