package com.freecourier.mv;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class TravellerDetails extends ActionBarActivity {
    private static final String TAG = "TRAVELLER DETAILS PAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_details);

        RetrieveFeedTask obj = new RetrieveFeedTask();
        obj.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_traveller_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private ArrayList<String> traveller;

        public RetrieveFeedTask() {
            traveller = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String[] args) {
            Bundle bundle = getIntent().getExtras();
            String argu = bundle.getString("email");

            Log.d(TAG, "execute1 "+argu);
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/get_traveller_details/"+argu;
            HttpGet request = new HttpGet(url);
            String responseStr = "";
            try {

                HttpResponse response = client.execute(request);
                responseStr = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "outcome = " + responseStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "[" + responseStr + "]";
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                JSONArray json = new JSONArray( result);

                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);
                    traveller.add(val);
                    Log.d("error out - Element : ", "i + " + i + " val : " + val);

                }
                String value = traveller.get(0);
                String value1 = traveller.get(1);
                String value2 = traveller.get(2);
                String value3 = traveller.get(3);
                String value4 = traveller.get(4);

                final TextView tv = (TextView) findViewById(R.id.email_ttd);
                final TextView tv1 = (TextView) findViewById(R.id.name_ttd);
                final TextView tv2 = (TextView) findViewById(R.id.phone_ttd);
                final TextView tv3 = (TextView) findViewById(R.id.gender_ttd);
                final TextView tv4 = (TextView) findViewById(R.id.city_ttd);

                tv.setText(value);
                tv1.setText(value1);
                tv2.setText(value2);
                tv3.setText(value3);
                tv4.setText(value4);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




    }
}
