package com.freecourier.mv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dexafree.materialList.view.MaterialListView;

import com.freecourier.mv.Declaration.ConnectionDetector;
import com.freecourier.mv.Declaration.UserSessionManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.steamcrafted.loadtoast.LoadToast;

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
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class MainActivity extends ActionBarActivity {
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    FloatingActionsMenu menu;
    MaterialListView cardList;


    UserSessionManager session;

    private EditText username;
    private EditText password;

    private static final String TAG = "MAIN ACTIVITY";


    public static final int INDEX_SIMPLE_LOGIN = 0;
    public static final int INDEX_CUSTOM_LOGIN = 1;
    public static final int INDEX_SIGNUP = 2;

    private static final String STATE_SELECTED_FRAGMENT_INDEX = "selected_fragment_index";
    public static final String FRAGMENT_TAG = "fragment_tag";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if((getIntent().getFlags()& Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Here activity is brought to front, not created,
            // so finishing this will get you to the last viewed activity
            finish();
            return;
        }

        cd = new ConnectionDetector(getApplicationContext());

        mFragmentManager = getSupportFragmentManager();

        session = new UserSessionManager(getApplicationContext());




        cardList = (MaterialListView)findViewById(R.id.cardList);
        cardList.getLayoutManager().offsetChildrenVertical(30);
        menu = (FloatingActionsMenu)findViewById(R.id.fab1);

        ActionBar ab=getSupportActionBar();
        Resources r=getResources();
        Drawable d=r.getDrawable(R.color.royalBlue);
        ab.setBackgroundDrawable(d);

        FloatingActionButton fare = (FloatingActionButton)findViewById(R.id.login);
        fare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
               //     showAlertDialog(MainActivity.this, "Internet Connection",
                    //      "You have internet connection", true);
                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(MainActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }
                toggleFragment(INDEX_SIMPLE_LOGIN);
            }
        });
        FloatingActionButton time = (FloatingActionButton)findViewById(R.id.adduser);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                toggleFragment(INDEX_SIGNUP);
            }
        });

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
        int flag;
        if(isValidEmail(username.getText().toString().trim())){
            args[0] = username.getText().toString().trim();
            flag=1;
        }else{
            username.setError("Invalid");
            username.requestFocus();
            flag=0;
        }

        args[1] = password.getText().toString().trim();

        if(flag==1) {
            AlertDialog dialog = new SpotsDialog(MainActivity.this);
            dialog.show();
            new RetrieveFeedTask().execute(args);
            dialog.dismiss();
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == R.id.action_simple_login) {
            toggleFragment(INDEX_SIMPLE_LOGIN);
            return true;
        }

        if (id == R.id.action_signup) {
            toggleFragment(INDEX_SIGNUP);
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

            case INDEX_SIGNUP:
                transaction.replace(android.R.id.content, new Registration(),FRAGMENT_TAG);
                break;
        }
        transaction.commit();
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String[] args) {
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://freecourierservice.appspot.com/rest/user/login/";
            HttpPost request = new HttpPost(url);
            String responseStr = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("email", args[0]));
                nameValuePairs.add(new BasicNameValuePair("password", args[1]));
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

                    username = (EditText) findViewById(R.id.username);
                    String email = username.getText().toString().trim();

                    session.createUserLoginSession("session_email", email);

                    Intent intent = new Intent(MainActivity.this, Main_Navigation.class);
                    // Intent intent = new Intent(MainActivity.this, Signup_Page.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(MainActivity.this,"Welcome user",Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Username or Password is wrong!!! Try again?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do something after confirm
                           // Toast.makeText(MainActivity.this, Selected", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            //Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            //startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    builder.create().show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}

