package simplebooking.greentech.vn.simplebooking.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    String avatar;

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

        findViewById(R.id.buttonLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                sendLocations();
            }
        });

        findViewById(R.id.buttonUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && requestCode == 0) {


            if (resultCode == RESULT_OK) {
                Uri targetUri = data.getData();

                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    avatar = ConvertBitmapToString(resizedBitmap);
//                    resultView.setText(image);
//                    Upload();
                    uploadAvatar();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public static String ConvertBitmapToString(Bitmap bitmap){
        String encodedImage = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        try {
            encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodedImage;
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

    private void sendLocations() {

        class SendLocation extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                JSONObject params = new JSONObject();
                try {
                    params.put("vehicle_id", "6c23b811-5b48-4378-88cd-1f39084499df");
                    params.put("booking_cd", "LEY16GC8");
                    JSONArray arr = new JSONArray();
                    JSONObject obj = new JSONObject();
                    obj.put("tracking_time", "2018-05-12 09:50:00");
                    obj.put("lat", "20.9103465");
                    obj.put("lng", "105.5884756");
                    arr.put(obj);
                    params.put("location_list", arr);
                    return requestHandler.sendPostRequest2(URLs.URL_SUBMIT_LOCATIONS, params.toString());
                } catch (JSONException je){
                    je.printStackTrace();
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }
                return  "";
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
        SendLocation ru = new SendLocation();
        ru.execute();
    }

    private void uploadAvatar() {

        class UploadAvatar extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                if (avatar != null && !avatar.isEmpty()) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    JSONObject params = new JSONObject();
                    try {
                        params.put("avatar", "data:image/jpeg;base64," + avatar);
                        return requestHandler.sendPostRequest2(URLs.URL_CUSTOMER_UPLOAD_AVATAR, params.toString());
                    } catch (JSONException je) {
                        je.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                return  "";
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
        UploadAvatar ru = new UploadAvatar();
        ru.execute();
    }
}
