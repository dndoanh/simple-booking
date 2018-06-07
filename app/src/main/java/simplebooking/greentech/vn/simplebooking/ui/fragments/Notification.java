package simplebooking.greentech.vn.simplebooking.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import simplebooking.greentech.vn.simplebooking.R;
import simplebooking.greentech.vn.simplebooking.services.SocketService;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment {

    SocketService socketService = SocketService.getInstance();

    public Notification() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        Button send_notification_all = (Button) view.findViewById(R.id.send_notification_all);

        socketService.setContext(getActivity());

        final EditText input = (EditText) view.findViewById(R.id.input);
        send_notification_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input.getText().toString().isEmpty()) {
                    socketService.emit("noti_snd_message", input.getText().toString());
                    input.setText("");
                }
            }
        });

        return view;
    }
}
