package simplebooking.greentech.vn.simplebooking.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import simplebooking.greentech.vn.simplebooking.R;
import simplebooking.greentech.vn.simplebooking.common.Constants;
import simplebooking.greentech.vn.simplebooking.common.RequestHandler;
import simplebooking.greentech.vn.simplebooking.common.URLs;
import simplebooking.greentech.vn.simplebooking.model.Country;

public class MainActivity extends AppCompatActivity {
    Spinner countryView;
    EditText phoneView;
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryView = (Spinner) findViewById(R.id.country);
        phoneView = (EditText) findViewById(R.id.phone);
        resultView = (TextView) findViewById(R.id.result);

        getCountryList();

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                validatePhone();
            }
        });
    }

    private void getCountryList() {

        class GetCountry extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("test", "test 123");
                //returing the response
                return requestHandler.sendGetRequest(URLs.URL_GET_COUNTRY, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    JSONArray jsonArray=obj.getJSONArray("data");
                    ArrayList<Country> countryList = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject objJSON =jsonArray.getJSONObject(i);
                        Country country= new Country(objJSON.getString("co_cd"), objJSON.getString("co_nm"));
                        countryList.add(country);
                    }

                    ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, countryList);
                    countryView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        GetCountry task = new GetCountry();
        task.execute();
    }

    private void validatePhone() {
        final String countryCode = ((Country)countryView.getSelectedItem()).getId();
        final String phone =  phoneView.getText().toString().trim();

        class ValidatePhone extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("co_cd", countryCode);
                params.put("phone", phone);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_VALIDATE_PHONE, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    String result = obj.getString("code");
                    resultView.setText(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        ValidatePhone ru = new ValidatePhone();
        ru.execute();
    }
}
