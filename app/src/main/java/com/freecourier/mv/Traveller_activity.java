package com.freecourier.mv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freecourier.mv.Declaration.ConnectionDetector;
import com.freecourier.mv.Declaration.ListViewAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.freecourier.mv.Declaration.Constant.FIRST_COLUMN;
import static com.freecourier.mv.Declaration.Constant.FOURTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant.SECOND_COLUMN;
import static com.freecourier.mv.Declaration.Constant.THIRD_COLUMN;


public class Traveller_activity extends ActionBarActivity {

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    private static final String TAG = "TRAVELLER PAGE";
    private ArrayList<HashMap<String, String>> list;

    public Traveller_activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveller);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(Traveller_activity.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

        String []argu = new String[3];

   /*     TextView tv = (TextView)getFragmentManager().findFragmentById(R.id.container).getView().findViewById(R.id.get_date);
        Spinner s1 = (Spinner)getFragmentManager().findFragmentById(R.id.container).getView().findViewById(R.id.spinner);
        Spinner s2 = (Spinner)getFragmentManager().findFragmentById(R.id.container).getView().findViewById(R.id.spinner2);

        argu[0]=s1.getSelectedItem().toString();
        argu[1]=s2.getSelectedItem().toString();
        argu[2]=tv.getText().toString();  */

        Intent in = getIntent();
        Bundle bundle = getIntent().getExtras();
        argu[0] = bundle.getString("src");
        argu[1] = bundle.getString("des");
        argu[2] = bundle.getString("jdate");
        Log.d(TAG, argu[0] + " " + " " + argu[1] + " " + argu[2]);
        new RetrieveFeedTask().execute(argu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_traveller_activity, menu);
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
        //private ArrayList<HashMap<String, String>> list;

        public RetrieveFeedTask() {
            list=new ArrayList<HashMap<String,String>>();
        }

        @Override
        protected String doInBackground(String[] args) {
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://freecourierservice.appspot.com/rest/user/get_travel_users/";
            HttpPost request = new HttpPost(url);
            String responseStr = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("source", args[0]));
                nameValuePairs.add(new BasicNameValuePair("des", args[1]));
                nameValuePairs.add(new BasicNameValuePair("date", args[2]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(request);
                Log.d(TAG, "input = " + args[0]+" - "+args[1]+" - "+args[2]) ;
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
                JSONArray json = new JSONArray(result);

                Log.d("error out", "in onPostExecute results123 : " + result);

                HashMap<String,String> temp = new HashMap<String, String>();
                temp.put(FIRST_COLUMN, "EMAIL");
                temp.put(SECOND_COLUMN, "NAME");
                temp.put(THIRD_COLUMN, "DATE");
                temp.put(FOURTH_COLUMN, "TIME");
                list.add(temp);

                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {

                    HashMap<String,String> temp1 = new HashMap<String, String>();
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);

                    JSONArray json1 = new JSONArray("["+val+"]");

                    JSONObject jsonobj1 = json1.getJSONObject(0);
                    val = jsonobj1.getString("email");
                    temp1.put(FIRST_COLUMN, val);
                    val = jsonobj1.getString("name");
                    temp1.put(SECOND_COLUMN, val);
                    val = jsonobj1.getString("journey_date");
                    temp1.put(THIRD_COLUMN, val);
                    val = jsonobj1.getString("journey_time");
                    temp1.put(FOURTH_COLUMN, val);

                    list.add(temp1);

                    Log.d("error out", "in onPostExecute message : " + "list working");

                    // Log.d("error out - Element : ", "i + " + i + " val : " + key);



                }


                Log.d("error out", "in onPostExecute message : " + "list working and size :" +list.size()+ " - "+list.toString());
                //getCities();

                ListView listView=(ListView)findViewById(R.id.listView1);
                ListViewAdapter adapter=new ListViewAdapter(Traveller_activity.this, list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                        int pos = position;
                        if(pos>0) {
                            HashMap<String,String> selectedItem = list.get(pos);
                            // Toast.makeText(getActivity(), selectedItem.toString() + " Clicked", Toast.LENGTH_SHORT).show();
                            final String email = selectedItem.get(FIRST_COLUMN);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Traveller_activity.this);
                            builder.setMessage("Do you want to select this?");
                            builder.setTitle("Confirm the traveller !!! ");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do something after confirm
                                    Toast.makeText(Traveller_activity.this, email+" Selected", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Traveller_activity.this, TravellerDetails.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email",email);
                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.create().show();
                        }
                    }

                });



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
