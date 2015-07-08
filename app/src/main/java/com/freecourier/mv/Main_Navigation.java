package com.freecourier.mv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.freecourier.mv.Declaration.UserSessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class Main_Navigation extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "MAIN NAVIGATION PAGE";
    private static final String FM_NOTIFICATION_ID = "1234";

    UserSessionManager session;
    Context context;
    private ArrayList<HashMap<String, String>> list;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static boolean running = false;
    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        MyTimerTask myTask = new MyTimerTask();
        myTimer = new Timer();
        myTimer.schedule(myTask, 0, 20000);
    }

    private class MyTimerTask extends TimerTask {
        public void run() {
            if(!running){
                Log.i("TAG", "NEW TIMER STARTED.");
                RetrieveServerRequest task = new RetrieveServerRequest();
                task.execute();
                running = true;
            }else{
                running = false;
            }
        }
    }

    class RetrieveServerRequest extends AsyncTask<String, Void, String> {

        private Exception exception;
        //private ArrayList<String> senderNotification;

       // public RetrieveServerRequest() {
            //senderNotification = new ArrayList<String>();
       // }
       public RetrieveServerRequest() {
           list=new ArrayList<HashMap<String,String>>();
       }

        @Override
        protected String doInBackground(String[] args) {

            session = new UserSessionManager(getApplicationContext());

            if(session.checkLogin())
                finish();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // get name
            String name = user.get(UserSessionManager.KEY_NAME);

            // get email
            String email = user.get(UserSessionManager.KEY_EMAIL);


            Log.d(TAG, "session = " +name+" "+email);

            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://freecourierservice.appspot.com/rest/user/get_booking_notification/"+email;
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
                JSONArray json = new JSONArray(result);

                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {
                    HashMap<String,String> temp = new HashMap<String, String>();
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);
                    JSONArray json1 = new JSONArray("["+val+"]");
                    JSONObject jsonobj1 = json1.getJSONObject(0);
                    String[] args = new String[6];
                    val = jsonobj1.getString("booking_id");
                    temp.put("booking_id",val);
                    args[0] = temp.get("booking_id");

                    val = jsonobj1.getString("sender_name");
                    temp.put("sender_name",val);
                    args[1] = temp.get("sender_name");

                    val = jsonobj1.getString("sender_phone");
                    temp.put("sender_phone",val);
                    args[2] = temp.get("sender_phone");

                    val = jsonobj1.getString("source");
                    temp.put("source",val);
                    args[3] = temp.get("source");

                    val = jsonobj1.getString("destination");
                    temp.put("destination",val);
                    args[4] = temp.get("destination");

                    val = jsonobj1.getString("date");
                    temp.put("date",val);
                    args[5] = temp.get("date");

                    list.add(temp);

                    Log.d("error out - Element : ", "i + " + i + args[0] + args[1] + args[2] + args[3] + args[4] + args[5]);

/*
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main_Navigation.this);
                    builder.setMessage(args[1] + " selected you to carry his parcel \nBooking ID: " + args[0] + "\n Phone No: " + args[2]);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();

*/

                    addNotification(args[0],args[1],args[2],args[3],args[4],args[5]);
                    //createNotification(args[0],args[1],args[2]);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNotification(String Id, String name, String phone, String source, String destination, String date) {



        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.notification1)
                        .setContentTitle("Hola!!!!!")
                        .setContentText("Someone selected you ");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Traveller details:");

        inboxStyle.addLine("Booking Id : " + Id);
        inboxStyle.addLine("Name : " + name);
        inboxStyle.addLine("Phone : "+phone);
        inboxStyle.addLine("Source : "+source);
        inboxStyle.addLine("Destination : "+destination);
        inboxStyle.addLine("Date : "+date);


        builder.setStyle(inboxStyle);

        Intent notificationIntent = new Intent(this, Main_Navigation.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(Integer.parseInt(FM_NOTIFICATION_ID), builder.build());
    }

    // Remove notification
    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Integer.parseInt(FM_NOTIFICATION_ID));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        android.app.Fragment objFragment = null;

        switch (position) {
            case 0:
                objFragment = new Send_Fragment();
                mTitle = "Send";
                break;
            case 1:
                objFragment = new Carry_Fragment();
                mTitle = "Carry";
                break;
            case 2:
                objFragment = new History_Fragment();
                mTitle = "History";
                break;
            case 3:
                objFragment = new Share_Fragment();
                mTitle = "Share";
                break;
            case 4:
                objFragment = new Contact_Fragment();
                mTitle = "Contact";
                break;
        }
        // update the main content by replacing fragments
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.navigation_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        session = new UserSessionManager(getApplicationContext());
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        String name = user.get(UserSessionManager.KEY_NAME);

        // get email
        String email = user.get(UserSessionManager.KEY_EMAIL);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            if(email!=null){
                session.logoutUser();}
            else {
                Profile profile = Profile.getCurrentProfile();
                if (profile.getName() != null) {
                    LoginManager.getInstance().logOut();
                }
            }
            Intent intent = new Intent(Main_Navigation.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Main_Navigation) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
