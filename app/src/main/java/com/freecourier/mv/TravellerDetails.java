package com.freecourier.mv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freecourier.mv.Declaration.ConnectionDetector;
import com.freecourier.mv.Declaration.UserSessionManager;

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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;


public class TravellerDetails extends ActionBarActivity {
    private static final String TAG = "TRAVELLER DETAILS PAGE";
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    UserSessionManager session;
    private static boolean running = false;
    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller_details);
        cd = new ConnectionDetector(getApplicationContext());
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
            showAlertDialog(TravellerDetails.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }
        AlertDialog dialog = new SpotsDialog(TravellerDetails.this);
        dialog.show();
        RetrieveFeedTask obj = new RetrieveFeedTask();
        obj.execute();
        dialog.dismiss();
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

    public void booking(View v){
        RetrieveFeedTask2 obj1 = new RetrieveFeedTask2();
        obj1.execute();
    }



    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private ArrayList<String> traveller;

        public RetrieveFeedTask() {
            traveller = new ArrayList<String>();
        }

        AlertDialog dialog = new SpotsDialog(TravellerDetails.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String[] args) {
            Bundle bundle = getIntent().getExtras();
            String argu = bundle.getString("email");

            Log.d(TAG, "execute1 "+argu);
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://freecourierservice.appspot.com/rest/user/get_traveller_details/"+argu;
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
            dialog.dismiss();
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

    class RetrieveFeedTask2 extends AsyncTask<String, Void, String> {

        AlertDialog dialog = new SpotsDialog(TravellerDetails.this);
        private Exception exception;
        private ArrayList<String> travellerDetails;

        public RetrieveFeedTask2() {
            travellerDetails = new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String[] args) {
            Bundle bundle = getIntent().getExtras();
            String argu = bundle.getString("email");

            session = new UserSessionManager(getApplicationContext());

            if (session.checkLogin())
                finish();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // get name
            String name = user.get(UserSessionManager.KEY_NAME);

            // get email
            String email = user.get(UserSessionManager.KEY_EMAIL);


            Log.d(TAG, "session = " + name + " " + email);

            Log.d(TAG, "execute1 " + argu);
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://freecourierservice.appspot.com/rest/user/insert_booking_info/" + email + "/" + argu;
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
            dialog.dismiss();
            try {
                JSONArray json = new JSONArray(result);

                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);
                    travellerDetails.add(val);
                    Log.d("error out - Element : ", "i + " + i + " val : " + val);
                }

                final String bookingID = travellerDetails.get(0);

                if (bookingID.equals("fail")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(TravellerDetails.this);
                    builder.setMessage("This person is already selected\nSelect someone else....");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            }
                    });
                    builder.create().show();
                } else {
                    Toast.makeText(TravellerDetails.this, "Booking Confirmed with ID " + bookingID, Toast.LENGTH_SHORT).show();

                    session = new UserSessionManager(getApplicationContext());

                    if (session.checkLogin())
                        finish();

                    // get user data from session
                    HashMap<String, String> user = session.getUserDetails();

                    // get name
                    String name = user.get(UserSessionManager.KEY_NAME);

                    // get email
                    String email = user.get(UserSessionManager.KEY_EMAIL);
                    String[] args = new String[2];
                    args[0] = email;
                    args[1] = bookingID;
                    new BookServerRequest().execute(args);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        class BookServerRequest extends AsyncTask<String, Void, String> {

            private Exception exception;
            private ArrayList<String> BookRequest;

            public BookServerRequest() {
                BookRequest = new ArrayList<String>();
            }

            @Override
            protected String doInBackground(String[] args) {

                DefaultHttpClient client = new DefaultHttpClient();
                String url = "http://freecourierservice.appspot.com/rest/user/booking_notification/";
                HttpPost request = new HttpPost(url);
                String responseStr = "";
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("email_id", args[0]));
                    nameValuePairs.add(new BasicNameValuePair("booking_id", args[1]));
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(request);
                    Log.d("Notification", "input = " + args[0] + " - " + args[1]);

                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.d("Notification", "outcome = " + responseStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "[" + responseStr + "]";
            }

            protected void onPostExecute(String result) {
                // TODO: check this.exception
                // TODO: do something with the feed
                try {
                    JSONArray json = new JSONArray(result);

                    JSONObject jsonobj = json.getJSONObject(0);
                    for (int i = 0; i < jsonobj.names().length(); i++) {
                        String key = (String) jsonobj.names().get(i);
                        String val = jsonobj.getString(key);
                        BookRequest.add(val);
                        Log.d("error out - Element : ", "i + " + i + " val : " + val);
                    }

                    final String bookingID = BookRequest.get(0);

                    if (bookingID.equals("fail")) {

                        // Log.d("EPUDAINA  : ", "i + "  + " val : " + bookingID);
                        AlertDialog.Builder builder = new AlertDialog.Builder(TravellerDetails.this);
                        builder.setMessage("This person is already selected\nSelect someone else....");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                           /* Intent intent = new Intent(TravellerDetails.this, Traveller_activity.class);
                            startActivity(intent); */
                            }
                        });
                  /*  builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }); */

                        builder.create().show();
                    } else {
                        Toast.makeText(TravellerDetails.this, "Booking Confirmed with ID " + bookingID, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
