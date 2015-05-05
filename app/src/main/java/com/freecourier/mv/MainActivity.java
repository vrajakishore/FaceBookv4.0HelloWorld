package com.freecourier.mv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;

    private static final String TAG = "your activity name";


    public static final int INDEX_SIMPLE_LOGIN = 0;
    public static final int INDEX_CUSTOM_LOGIN = 1;

    private static final String STATE_SELECTED_FRAGMENT_INDEX = "selected_fragment_index";
    public static final String FRAGMENT_TAG = "fragment_tag";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void send_to_menu(View view) {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        String []args = new String[2];
        args[0] = username.getText().toString().trim();
        args[1] = password.getText().toString().trim();
        new RetrieveFeedTask().execute(args);
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

        if (id == R.id.action_simple_login) {
            toggleFragment(INDEX_SIMPLE_LOGIN);
            return true;
        }
        if (id == R.id.action_custom_login) {
            toggleFragment(INDEX_CUSTOM_LOGIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFragment(int index) {
        Fragment fragment = mFragmentManager.findFragmentByTag(FRAGMENT_TAG);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (index){
            case INDEX_SIMPLE_LOGIN:
                transaction.replace(android.R.id.content, new FragmentSimpleLoginButton(),FRAGMENT_TAG);
                break;
            case INDEX_CUSTOM_LOGIN:
                transaction.replace(android.R.id.content, new FragmentCustomLoginButton(),FRAGMENT_TAG);
                break;
        }
        transaction.commit();
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String[] args) {
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/login/";
            HttpPost request = new HttpPost(url);
            String responseStr = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("email", "kishore"));
                nameValuePairs.add(new BasicNameValuePair("password", "1234"));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(request);
                Log.d(TAG, "input = " + args[0]+" - "+args[1]);
                responseStr = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "outcome = " + responseStr);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "["+responseStr+"]";
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                JSONArray json = new JSONArray( result);
                //Log.d("error out", "in onPostExecute results123 : " + result);
                JSONObject jsonobj = json.getJSONObject(0);
                String message = jsonobj.getString("message");
                Log.d("error out", "in onPostExecute message : " + message);
                if(message.equalsIgnoreCase("success")){
                    Intent intent = new Intent(MainActivity.this, Main_Navigation.class);
                    // Intent intent = new Intent(MainActivity.this, Signup_Page.class);
                    Toast.makeText(MainActivity.this,"Welcome user",Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}

