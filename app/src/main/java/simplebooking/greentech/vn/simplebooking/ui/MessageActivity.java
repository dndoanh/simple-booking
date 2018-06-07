package simplebooking.greentech.vn.simplebooking.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import simplebooking.greentech.vn.simplebooking.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle b = getIntent().getExtras();

        TextView message = (TextView) findViewById(R.id.message);
        message.setText(b.get("message").toString());

        FrameLayout back = (FrameLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, PushNotificationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
